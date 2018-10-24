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
import kotlinx.android.synthetic.main.activity_finance_all.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.json.JSONObject
import java.text.DecimalFormat
import java.util.*

class FinanceAllActivity : BaseActivity() {

    private var purchaseId = ""
    private var productId = ""
    private var accountInfoId = ""
    private var mYears = ""
    private var payTypeId = ""
    private var receiptTypeId = ""
    private var cashierInfoId = ""
    private var managerInfoId = ""
    private var nonManagerInfoId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finance_all)
        init_title("财务对账")

        EventBus.getDefault().register(this@FinanceAllActivity)

        getData()
    }

    override fun init_title() {
        super.init_title()
        purchaseId = intent.getStringExtra("purchaseId")

        finance_pass.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
        finance_pass.isClickable = false

        finance_money.addTextChangedListener(this@FinanceAllActivity)
        finance_begin.addTextChangedListener(this@FinanceAllActivity)
        finance_end.addTextChangedListener(this@FinanceAllActivity)
        finance_card.addTextChangedListener(this@FinanceAllActivity)
        finance_phone.addTextChangedListener(this@FinanceAllActivity)

        finance_get.addTextChangedListener(this@FinanceAllActivity)
        et_code.addTextChangedListener(this@FinanceAllActivity)
        et_num.addTextChangedListener(this@FinanceAllActivity)
        finance_shou.addTextChangedListener(this@FinanceAllActivity)
        finance_yin.addTextChangedListener(this@FinanceAllActivity)
        finance_manager.addTextChangedListener(this@FinanceAllActivity)
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.finance_begin_ll, R.id.finance_end_ll -> {
                if (mYears.isEmpty()) {
                    showToast("数据获取失败")
                    return
                }

                val year_now = Calendar.getInstance().get(Calendar.YEAR)
                DialogHelper.showDateDialog(
                        baseContext,
                        year_now,
                        year_now + 20,
                        3,
                        "选择出资日期",
                        true,
                        false, { _, _, _, _, _, date ->
                    finance_begin.text = date
                    finance_end.text = TimeHelper.getInstance().getAnyYear(date, mYears.toInt())
                })
            }
            R.id.finance_bank_ll -> startActivityEx<ReportBankActivity>()
            R.id.finance_get_ll -> startActivityEx<FinanceSelectActivity>("title" to "收款方式")
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
                if (productId.isEmpty() || accountInfoId.isEmpty()) {
                    showToast("数据获取失败")
                    return
                }

                /*if (!BankcardHelper.checkBankCard(finance_card.rawText)) {
                    finance_card.requestFocus()
                    finance_card.setText("")
                    showToast("请输入正确的银行卡卡号")
                    return
                }*/

                if (!CommonUtil.isMobile(finance_phone.text.toString())) {
                    finance_phone.requestFocus()
                    finance_phone.setText("")
                    showToast("请输入正确的联系电话")
                    return
                }

                OkGo.post<String>(BaseHttp.accountant_auditing_sub)
                        .tag(this@FinanceAllActivity)
                        .isMultipart(true)
                        .headers("token", getString("token"))
                        .apply {
                            params("purchaseId", purchaseId)
                            params("productId", productId)
                            params("accountInfoId", accountInfoId)
                            params("years", mYears)
                            params("amount", finance_money.text.toNoInt() * 10000)
                            params("beginDate", finance_begin.text.toString())
                            params("endDate", finance_end.text.toString())
                            params("bank", finance_bank.text.toString())
                            params("bankCard", finance_card.rawText)
                            params("phone", finance_phone.text.toString())
                            params("address", finance_addr.text.trim().toString())
                            params("fax", finance_fax.text.toString())
                            params("remark", finance_memo.text.trim().toString())
                            params("payTypeId", payTypeId)
                            params("receiptNo", et_code.text.toString())
                            params("receivedAmount", et_num.text.toNoInt() * 10000)
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

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        val obj = JSONObject(response.body())
                                .optJSONObject("object") ?: JSONObject()

                        purchaseId = obj.optString("purchaseId")
                        productId = obj.optString("productId")
                        accountInfoId = obj.optString("accountInfoId")
                        mYears = obj.optString("years")
                        payTypeId = obj.optString("payTypeId")
                        receiptTypeId = obj.optString("receiptTypeId")
                        cashierInfoId = obj.optString("cashierInfoId")
                        managerInfoId = obj.optString("managerInfoId")
                        nonManagerInfoId = obj.optString("nonManagerInfoId")

                        finance_product.setRightString(obj.optString("productName"))
                        finance_year.setRightString("${mYears}年")
                        finance_money.setText((obj.optString("amount").toNotInt() / 10000).toString())
                        finance_begin.text = obj.optString("beginDate")
                        finance_end.text = obj.optString("endDate")

                        val compName = obj.optString("compName")
                        val vipNo = obj.optString("vipNo")
                        val investType = obj.optString("investType")
                        val previousName = obj.optString("previousProductName")
                        var previousAmount = obj.optString("previousAmount")
                        val previousYears = obj.optString("previousPurchaseYears")
                        val stock = obj.optString("stock")
                        finance_company.setLeftString(if (vipNo.isEmpty()) "企业名称" else "VIP编号")
                        finance_company.setRightString(if (vipNo.isEmpty()) compName else vipNo)
                        when (investType) {
                            "1" -> {
                                finance_trans_name.visibility = View.VISIBLE
                                finance_trans_money.visibility = View.VISIBLE
                                finance_trans_name.setLeftString("转投产品")
                                finance_trans_money.setLeftString("转投金额(元)")
                            }
                            "2" -> {
                                finance_trans_name.visibility = View.VISIBLE
                                finance_trans_money.visibility = View.VISIBLE
                                finance_trans_name.setLeftString("续投产品")
                                finance_trans_money.setLeftString("续投金额(元)")
                            }
                        }
                        if (previousAmount.isEmpty()) previousAmount = "0"
                        val str = "$previousName(金额:${DecimalFormat(",##0.##").format(previousAmount.toDouble() / 10000.0)}万  期限:${previousYears}年)"
                        finance_trans_name.setRightString(str)
                        finance_trans_money.setRightString(stock)

                        finance_name.setRightString(obj.optString("userName"))
                        finance_idcard.setRightString(obj.optString("cardNo"))
                        finance_bank.text = obj.optString("bank")
                        finance_card.setText(obj.optString("bankcard"))
                        finance_phone.setText(obj.optString("phone"))
                        finance_addr.setText(obj.optString("address"))
                        finance_fax.setText(obj.optString("fax"))
                        finance_memo.setText(obj.optString("remark"))
                        finance_addr.setSelection(finance_addr.text.length)

                        finance_get.text = obj.optString("paytypeName")
                        et_code.setText(obj.optString("receiptNo"))
                        et_num.setText((obj.optString("receivedAmount").toNotInt() / 10000).toString())
                        finance_shou.text = obj.optString("receiptTypeName")
                        finance_yin.text = obj.optString("cashierInfoName")
                        finance_tel.setRightString(obj.optString("cashierInfoTelephone"))
                        finance_manager.text = obj.optString("managerInfoName")
                        finance_non.text = obj.optString("nonManagerName")
                        et_memo.setText(obj.optString("financeRemark"))
                        et_code.setSelection(et_code.text.length)
                    }

                })
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (finance_money.text.isNotBlank()
                && finance_begin.text.isNotBlank()
                && finance_end.text.isNotBlank()
                && finance_card.text.isNotBlank()
                && finance_phone.text.isNotBlank()
                && finance_get.text.isNotBlank()
                && et_code.text.isNotBlank()
                && et_num.text.isNotBlank()
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
            "银行" -> finance_bank.text = event.name
            "收款" -> {
                payTypeId = event.id
                finance_get.text = event.name
            }
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
