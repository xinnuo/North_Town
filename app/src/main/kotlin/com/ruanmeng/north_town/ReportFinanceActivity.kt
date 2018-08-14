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
import kotlinx.android.synthetic.main.activity_report_finance.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class ReportFinanceActivity : BaseActivity() {

    private var payTypeId = ""
    private var receiptTypeId = ""
    private var cashierInfoId = ""
    private var managerInfoId = ""
    private var nonManagerInfoId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_finance)
        init_title("财务信息")

        EventBus.getDefault().register(this@ReportFinanceActivity)
    }

    override fun init_title() {
        super.init_title()
        val accountName = intent.getStringExtra("accountName") ?: ""
        val cardNo = intent.getStringExtra("cardNo") ?: ""

        et_name.setText(accountName)
        et_idcard.setText(cardNo)

        finance_submit.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
        finance_submit.isClickable = false

        finance_get.addTextChangedListener(this@ReportFinanceActivity)
        et_code.addTextChangedListener(this@ReportFinanceActivity)
        et_num.addTextChangedListener(this@ReportFinanceActivity)
        finance_shou.addTextChangedListener(this@ReportFinanceActivity)
        et_name.addTextChangedListener(this@ReportFinanceActivity)
        et_idcard.addTextChangedListener(this@ReportFinanceActivity)
        finance_yin.addTextChangedListener(this@ReportFinanceActivity)
        finance_manager.addTextChangedListener(this@ReportFinanceActivity)
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.finance_get_ll -> startActivityEx<FinanceSelectActivity>("title" to "收款方式")
            R.id.finance_shou_ll -> startActivityEx<FinanceSelectActivity>("title" to "收据类型")
            R.id.finance_yin_ll -> startActivityEx<ReportAgentActivity>("type" to "2")
            R.id.finance_manager_ll -> startActivityEx<ReportAgentActivity>("type" to "3")
            R.id.finance_non_ll -> startActivityEx<ReportAgentActivity>("type" to "1")
            R.id.finance_submit -> {
                if (!CommonUtil.IDCardValidate(et_idcard.text.trim().toString())) {
                    et_idcard.requestFocus()
                    et_idcard.setText("")
                    showToast("请输入正确的身份证号")
                    return
                }

                OkGo.post<String>(BaseHttp.purchase_auditing_add)
                        .tag(this@ReportFinanceActivity)
                        .isMultipart(true)
                        .headers("token", getString("token"))
                        .apply {
                            params("purchaseId", intent.getStringExtra("purchaseId"))
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
                                ActivityStack.screenManager.popActivities(this@ReportFinanceActivity::class.java)
                            }

                        })
            }
        }
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
        EventBus.getDefault().unregister(this@ReportFinanceActivity)
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
            }
            "经纪人" -> {
                managerInfoId = event.id
                finance_manager.text = event.name
            }
        }
    }
}
