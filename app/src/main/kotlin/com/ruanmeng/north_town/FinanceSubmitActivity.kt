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

class FinanceSubmitActivity : BaseActivity() {

    private var payTypeId = ""
    private var receiptTypeId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finance_submit)
        init_title("录入收款信息")

        EventBus.getDefault().register(this@FinanceSubmitActivity)
    }

    override fun init_title() {
        super.init_title()
        val userName = intent.getStringExtra("userName") ?: ""
        val cardNo = intent.getStringExtra("cardNo") ?: ""
        val amount = intent.getStringExtra("amount") ?: ""
        val remark = intent.getStringExtra("remark") ?: ""

        et_num.setText(amount)
        et_name.setText(userName)
        et_idcard.setText(cardNo)
        et_memo.setText(remark)

        if (cardNo.isNotEmpty()) {
            et_name.isFocusable = false
            et_idcard.isFocusable = false
        }

        finance_yin.setRightString(getString("userName"))
        finance_tel.setRightString(getString("mobile"))

        finance_submit.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
        finance_submit.isClickable = false

        finance_get.addTextChangedListener(this@FinanceSubmitActivity)
        et_code.addTextChangedListener(this@FinanceSubmitActivity)
        et_num.addTextChangedListener(this@FinanceSubmitActivity)
        finance_shou.addTextChangedListener(this@FinanceSubmitActivity)
        et_name.addTextChangedListener(this@FinanceSubmitActivity)
        et_idcard.addTextChangedListener(this@FinanceSubmitActivity)
        et_memo.addTextChangedListener(this@FinanceSubmitActivity)
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.finance_get_ll -> startActivityEx<FinanceSelectActivity>("title" to "收款方式")
            R.id.finance_shou_ll -> startActivityEx<FinanceSelectActivity>("title" to "收据类型")
            R.id.finance_submit -> {
                if (!CommonUtil.IDCardValidate(et_idcard.text.trim().toString())) {
                    et_idcard.requestFocus()
                    et_idcard.setText("")
                    showToast("请输入正确的身份证号")
                    return
                }

                OkGo.post<String>(BaseHttp.purchase_auditing_add)
                        .tag(this@FinanceSubmitActivity)
                        .isMultipart(true)
                        .headers("token", getString("token"))
                        .apply {
                            params("payTypeId", payTypeId)
                            params("receiptNo", et_code.text.trim().toString())
                            params("amount", et_num.text.trim().toString())
                            params("receiptTypeId", receiptTypeId)
                            params("userName", et_name.text.trim().toString())
                            params("cardNo", et_idcard.text.trim().toString())
                            params("houseNumber", et_num.text.trim().toString())
                            params("remark", et_memo.text.trim().toString())
                        }
                        .execute(object : StringDialogCallback(baseContext) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                                showToast("财务录入信息添加成功！")
                                EventBus.getDefault().post(ReportMessageEvent("", "", "财务录入"))
                                ActivityStack.screenManager.popActivities(this@FinanceSubmitActivity::class.java)
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
                && et_memo.text.isNotBlank()) {
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
        }
    }
}
