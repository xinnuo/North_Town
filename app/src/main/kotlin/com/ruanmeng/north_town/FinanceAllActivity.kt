package com.ruanmeng.north_town

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.lzg.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.model.ReportMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.*
import com.ruanmeng.view.OnTextWatcher
import kotlinx.android.synthetic.main.activity_finance_all.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.json.JSONObject
import java.text.DecimalFormat

class FinanceAllActivity : BaseActivity() {

    private var purchaseId = ""
    private var receiptTypeId = ""
    private var cashierInfoId = ""
    private var managerInfoId = ""
    private var nonManagerInfoId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finance_all)
        init_title("财务对账", "浏览合同信息")

        EventBus.getDefault().register(this@FinanceAllActivity)

        getData()
    }

    override fun init_title() {
        super.init_title()
        purchaseId = intent.getStringExtra("purchaseId")

        finance_pass.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
        finance_pass.isClickable = false

        et_code.addTextChangedListener(this@FinanceAllActivity)
        finance_total.addTextChangedListener(this@FinanceAllActivity)
        et_name.addTextChangedListener(this@FinanceAllActivity)
        et_idcard.addTextChangedListener(this@FinanceAllActivity)
        finance_shou.addTextChangedListener(this@FinanceAllActivity)
        finance_yin.addTextChangedListener(this@FinanceAllActivity)
        finance_manager.addTextChangedListener(this@FinanceAllActivity)

        val watcher = object : OnTextWatcher() {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (et_cash.isFocused
                        || et_bank.isFocused
                        || et_tran.isFocused
                        || et_other.isFocused) calculatedTotal()
            }
        }

        et_cash.addTextChangedListener(watcher)
        et_bank.addTextChangedListener(watcher)
        et_tran.addTextChangedListener(watcher)
        et_other.addTextChangedListener(watcher)
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.tv_nav_right -> startActivityEx<FinanceScanActivity>("purchaseId" to purchaseId)
            // R.id.finance_get_ll -> startActivityEx<FinanceSelectActivity>("title" to "收款方式")
            R.id.finance_shou_ll -> startActivityEx<FinanceSelectActivity>("title" to "收据类型")
            R.id.finance_yin_ll -> startActivityEx<ReportAgentActivity>(
                    "title" to "选择收银员",
                    "type" to "2")
            R.id.finance_manager_ll -> startActivityEx<ReportAgentActivity>(
                    "title" to "选择经纪人",
                    "type" to "3")
            R.id.finance_non_ll -> startActivityEx<ReportAgentActivity>(
                    "title" to "选择非基金经纪人",
                    "type" to "1")
            R.id.finance_pass -> {
                if (!CommonUtil.IDCardValidate(et_idcard.text.toString())) {
                    et_idcard.requestFocus()
                    et_idcard.setText("")
                    showToast("请输入正确的身份证号")
                    return
                }

                OkGo.post<String>(BaseHttp.accountant_auditing_sub)
                        .tag(this@FinanceAllActivity)
                        .isMultipart(true)
                        .headers("token", getString("token"))
                        .apply {
                            params("purchaseId", purchaseId)
                            params("receiptNo", et_code.text.toString())
                            params("xianJin", doubleToLong(et_cash.text))
                            params("yinLian", doubleToLong(et_bank.text))
                            params("zhuanZhang", doubleToLong(et_tran.text))
                            params("qiTa", doubleToLong(et_other.text))
                            params("receivedAmount", doubleToLong(finance_total.text))
                            params("receiptTypeId", receiptTypeId)
                            params("cashierInfoId", cashierInfoId)
                            params("managerInfoId", managerInfoId)
                            params("nonManagerInfoId", nonManagerInfoId)
                            params("financeRemark", et_memo.text.trim().toString())
                        }
                        .execute(object : StringDialogCallback(baseContext) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                                showToast(msg)
                                EventBus.getDefault().post(ReportMessageEvent("", "", "财务审核"))
                                ActivityStack.screenManager.popActivities(this@FinanceAllActivity::class.java)
                            }

                        })
            }
        }
    }

    override fun getData() {
        OkGo.post<String>(BaseHttp.customer_purchase_details)
                .tag(this@FinanceAllActivity)
                .headers("token", getString("token"))
                .params("purchaseId", intent.getStringExtra("purchaseId"))
                .execute(object : StringDialogCallback(baseContext) {

                    @SuppressLint("SetTextI18n")
                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        val obj = JSONObject(response.body())
                                .optJSONObject("object") ?: JSONObject()

                        receiptTypeId = obj.optString("receiptTypeId")
                        cashierInfoId = obj.optString("cashierInfoId")
                        managerInfoId = obj.optString("managerInfoId")
                        nonManagerInfoId = obj.optString("nonManagerInfoId")

                        val investType = obj.optString("investType")
                        val previousAmount = obj.optString("previousAmount")
                        val previousFlowAmount = obj.optString("previousFlowAmount")
                        val stock = obj.optString("stock")
                        val previousPurchaseBeginDate = obj.optString("previousPurchaseBeginDate")
                        val previousPurchaseEndDate = obj.optString("previousPurchaseEndDate")
                        val previousPurchaseReceiptNo = obj.optString("previousPurchaseReceiptNo")
                        val previousPurchaseUserName = obj.optString("previousPurchaseUserName")

                        val receivedCash = obj.optString("xianJin")
                        val receivedBank = obj.optString("yinLian")
                        val receivedTran = obj.optString("zhuanZhang")
                        val receivedOther = obj.optString("qiTa")

                        et_cash.setText(DecimalFormat("0.##").format(receivedCash.toNotDouble() / 10000))
                        et_bank.setText(DecimalFormat("0.##").format(receivedBank.toNotDouble() / 10000))
                        et_tran.setText(DecimalFormat("0.##").format(receivedTran.toNotDouble() / 10000))
                        et_other.setText(DecimalFormat("0.##").format(receivedOther.toNotDouble() / 10000))
                        finance_total.text = DecimalFormat("0.##").format((
                                receivedCash.toNotDouble()
                                        + receivedBank.toNotDouble()
                                        + receivedTran.toNotDouble()
                                        + receivedOther.toNotDouble()
                                ) / 10000)

                        et_code.setText(obj.optString("receiptNo"))
                        finance_shou.text = obj.optString("receiptTypeName")
                        et_name.setText(obj.optString("userName"))
                        et_idcard.setText(obj.optString("cardNo"))
                        finance_yin.text = obj.optString("cashierInfoName")
                        finance_tel.setRightString(obj.optString("cashierInfoTelephone"))
                        finance_manager.text = obj.optString("managerInfoName")
                        finance_non.text = obj.optString("nonManagerName")
                        et_memo.setText(obj.optString("financeRemark"))
                        et_code.setSelection(et_code.text.length)

                        if (investType in "1,2") {
                            finance_pre_ll.visibility = View.VISIBLE
                            finance_prehint.text = "老票${if (investType == "1") "转" else "续"}投金额(元)"
                            finance_pretotal.text = previousAmount
                            finance_pretran.text = previousFlowAmount
                            finance_prebuy.text = DecimalFormat("0.##").format(stock.toNotDouble())
                            finance_prestart.text = previousPurchaseBeginDate
                            finance_preend.text = previousPurchaseEndDate
                            finance_precode.text = previousPurchaseReceiptNo
                            finance_prename.text = previousPurchaseUserName
                        }
                    }

                })
    }

    private fun doubleToLong(edit: CharSequence) = (edit.toNoDouble() * 10000).toLong()

    private fun calculatedTotal() {
        val cashValue = et_cash.text.toNoDouble()
        val bankValue = et_bank.text.toNoDouble()
        val tranValue = et_tran.text.toNoDouble()
        val otherValue = et_other.text.toNoDouble()

        val totalValue = cashValue + bankValue + tranValue + otherValue
        if (totalValue > 0) finance_total.text = DecimalFormat("0.##").format(totalValue)
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (finance_total.text.isNotBlank()
                && et_code.text.isNotBlank()
                && et_name.text.isNotBlank()
                && et_idcard.text.isNotBlank()
                && finance_shou.text.isNotBlank()
                && finance_yin.text.isNotBlank()
                && finance_manager.text.isNotBlank()) {
            finance_pass.setBackgroundResource(R.drawable.rec_bg_red)
            finance_pass.isClickable = true
        } else {
            finance_pass.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
            finance_pass.isClickable = false
        }
    }

    override fun finish() {
        EventBus.getDefault().unregister(this@FinanceAllActivity)
        super.finish()
    }

    @SuppressLint("SetTextI18n")
    @Subscribe
    fun onMessageEvent(event: ReportMessageEvent) {
        when (event.type) {
            "收据" -> {
                receiptTypeId = event.id
                finance_shou.text = event.name
            }
            "非基金" -> {
                nonManagerInfoId = event.id
                finance_non.text = event.name
            }
            "收银员" -> {
                cashierInfoId = event.id
                finance_yin.text = event.name
                finance_tel.setRightString(event.extend)
            }
            "经纪人" -> {
                managerInfoId = event.id
                finance_manager.text = event.name
            }
        }
    }
}
