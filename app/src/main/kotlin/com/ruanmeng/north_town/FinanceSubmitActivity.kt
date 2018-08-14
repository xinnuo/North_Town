package com.ruanmeng.north_town

import android.os.Bundle
import android.view.View
import com.lzg.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getString
import com.ruanmeng.base.showToast
import com.ruanmeng.base.startActivityEx
import com.ruanmeng.model.ReportMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.CommonUtil
import kotlinx.android.synthetic.main.activity_finance_submit.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.json.JSONObject
import java.text.DecimalFormat

class FinanceSubmitActivity : BaseActivity() {

    private var purchaseId = ""
    private var payTypeId = ""
    private var receiptTypeId = ""
    private var cashierInfoId = ""
    private var managerInfoId = ""
    private var nonManagerInfoId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finance_submit)
        init_title("财务对账", "浏览合同信息")

        EventBus.getDefault().register(this@FinanceSubmitActivity)

        getData()
    }

    override fun init_title() {
        super.init_title()
        purchaseId = intent.getStringExtra("purchaseId")

        finance_submit.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
        finance_submit.isClickable = false

        finance_get.addTextChangedListener(this@FinanceSubmitActivity)
        et_code.addTextChangedListener(this@FinanceSubmitActivity)
        et_num.addTextChangedListener(this@FinanceSubmitActivity)
        finance_shou.addTextChangedListener(this@FinanceSubmitActivity)
        et_name.addTextChangedListener(this@FinanceSubmitActivity)
        et_idcard.addTextChangedListener(this@FinanceSubmitActivity)
        finance_yin.addTextChangedListener(this@FinanceSubmitActivity)
        finance_manager.addTextChangedListener(this@FinanceSubmitActivity)
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.tv_nav_right -> startActivityEx<FinanceScanActivity>("purchaseId" to purchaseId)
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
            R.id.finance_submit -> {
                if (!CommonUtil.IDCardValidate(et_idcard.text.toString())) {
                    et_idcard.requestFocus()
                    et_idcard.setText("")
                    showToast("请输入正确的身份证号")
                    return
                }

                OkGo.post<String>(BaseHttp.cashier_auditing_sub)
                        .tag(this@FinanceSubmitActivity)
                        .isMultipart(true)
                        .headers("token", getString("token"))
                        .apply {
                            params("purchaseId", purchaseId)
                            params("payTypeId", payTypeId)
                            params("receiptNo", et_code.text.toString())
                            params("receivedAmount", et_num.text.toString())
                            params("receiptTypeId", receiptTypeId)
                            params("userName", et_name.text.trim().toString())
                            params("cardNo", et_idcard.text.toString())
                            params("cashierInfoId", cashierInfoId)
                            params("managerInfoId", managerInfoId)
                            params("nonManagerInfoId", nonManagerInfoId)
                            params("financeRemark", et_memo.text.trim().toString())
                        }
                        .execute(object : StringDialogCallback(baseContext) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                                showToast(msg)
                                EventBus.getDefault().post(ReportMessageEvent("", "", "财务审核"))
                                ActivityStack.screenManager.popActivities(this@FinanceSubmitActivity::class.java)
                            }

                        })
            }
        }
    }

    override fun getData() {
        OkGo.post<String>(BaseHttp.customer_purchase_details)
                .tag(this@FinanceSubmitActivity)
                .headers("token", getString("token"))
                .params("purchaseId", intent.getStringExtra("purchaseId"))
                .execute(object : StringDialogCallback(baseContext) {

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        val obj = JSONObject(response.body())
                                .optJSONObject("object") ?: JSONObject()

                        payTypeId = obj.optString("payTypeId")
                        receiptTypeId = obj.optString("receiptTypeId")
                        cashierInfoId = obj.optString("cashierInfoId")
                        managerInfoId = obj.optString("managerInfoId")
                        nonManagerInfoId = obj.optString("nonManagerInfoId")

                        finance_get.text = obj.optString("paytypeName")
                        et_code.setText(obj.optString("receiptNo"))
                        val receivedAmount = DecimalFormat("#").format(obj.optDouble("receivedAmount", 0.0))
                        et_num.setText(receivedAmount)
                        finance_shou.text = obj.optString("receiptTypeName")
                        et_name.setText(obj.optString("userName"))
                        et_idcard.setText(obj.optString("cardNo"))
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
        if (finance_get.text.isNotBlank()
                && et_code.text.isNotBlank()
                && et_num.text.isNotBlank()
                && finance_shou.text.isNotBlank()
                && et_name.text.isNotBlank()
                && et_idcard.text.isNotBlank()
                && finance_yin.text.isNotBlank()
                && finance_manager.text.isNotBlank()) {
            finance_submit.setBackgroundResource(R.drawable.rec_bg_red)
            finance_submit.isClickable = true
        } else {
            finance_submit.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
            finance_submit.isClickable = false
        }
    }

    override fun finish() {
        EventBus.getDefault().unregister(this@FinanceSubmitActivity)
        super.finish()
    }

    @Subscribe
    fun onMessageEvent(event: ReportMessageEvent) {
        when (event.type) {
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
