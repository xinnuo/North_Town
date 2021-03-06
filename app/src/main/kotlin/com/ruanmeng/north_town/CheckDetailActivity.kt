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
import kotlinx.android.synthetic.main.activity_check_detail.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.json.JSONObject
import java.text.DecimalFormat
import java.util.*

class CheckDetailActivity : BaseActivity() {

    private var purchaseId = ""
    private var productId = ""
    private var productName = ""
    private var accountInfoId = ""
    private var mYears = ""
    private var nonManagerInfoId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_detail)
        init_title("对账详情", "浏览财务信息")

        EventBus.getDefault().register(this@CheckDetailActivity)

        getData()
    }

    override fun init_title() {
        super.init_title()
        purchaseId = intent.getStringExtra("purchaseId")

        check_pass.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
        check_pass.isClickable = false

        check_money.addTextChangedListener(this@CheckDetailActivity)
        check_begin.addTextChangedListener(this@CheckDetailActivity)
        check_end.addTextChangedListener(this@CheckDetailActivity)
        check_card.addTextChangedListener(this@CheckDetailActivity)
        check_phone.addTextChangedListener(this@CheckDetailActivity)
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.tv_nav_right -> startActivityEx<CheckScanActivity>("purchaseId" to purchaseId)
            R.id.check_begin_ll, R.id.check_end_ll -> {
                if (mYears.isEmpty()) {
                    showToast("数据获取失败")
                    return
                }

                val year_now = Calendar.getInstance().get(Calendar.YEAR)
                DialogHelper.showDateDialog(
                        baseContext,
                        year_now - 5,
                        year_now + 20,
                        3,
                        "选择出资日期",
                        true,
                        false, { _, _, _, _, _, date ->
                    check_begin.text = date
                    check_end.text = TimeHelper.getInstance().getAnyMonthAny(date, mYears.toInt())
                })
            }
            R.id.check_bank_ll -> startActivityEx<ReportBankActivity>()
            R.id.check_agent_ll -> startActivityEx<ReportAgentActivity>(
                    "title" to "选择非基金经纪人",
                    "type" to "1")
            R.id.check_pass -> {
                if (productId.isEmpty() || accountInfoId.isEmpty()) {
                    showToast("数据获取失败")
                    return
                }

                /*if (!BankcardHelper.checkBankCard(check_card.rawText)) {
                    check_card.requestFocus()
                    check_card.setText("")
                    showToast("请输入正确的银行卡卡号")
                    return
                }*/

                if (!CommonUtil.isMobile(check_phone.text.toString())) {
                    check_phone.requestFocus()
                    check_phone.setText("")
                    showToast("请输入正确的联系电话")
                    return
                }

                OkGo.post<String>(BaseHttp.purchase_auditing_sub)
                        .tag(this@CheckDetailActivity)
                        .isMultipart(true)
                        .headers("token", getString("token"))
                        .params("purchaseId", purchaseId)
                        .params("productId", productId)
                        .params("accountInfoId", accountInfoId)
                        .params("years", mYears)
                        .params("amount", doubleToLong(check_money.text.toString()))
                        .params("beginDate", check_begin.text.toString())
                        .params("endDate", check_end.text.toString())
                        .params("profit", et_profit.text.toString())
                        .params("bank", check_bank.text.toString())
                        .params("bankCard", check_card.rawText)
                        .params("phone", check_phone.text.toString())
                        .params("address", check_addr.text.trim().toString())
                        .params("fax", check_fax.text.toString())
                        .params("nonManagerInfoId", nonManagerInfoId)
                        .params("remark", check_memo.text.trim().toString()).apply {
                            if (productName == "会员卡投资") params("prepaidAmount", et_back.text.toString())
                        }
                        .execute(object : StringDialogCallback(baseContext) {

                            @SuppressLint("SetTextI18n")
                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                                showToast(msg)
                                EventBus.getDefault().post(ReportMessageEvent("", "", "合同审核"))
                                ActivityStack.screenManager.popActivities(this@CheckDetailActivity::class.java)
                            }

                        })
            }
        }
    }

    override fun getData() {
        OkGo.post<String>(BaseHttp.customer_purchase_details)
                .tag(this@CheckDetailActivity)
                .headers("token", getString("token"))
                .params("purchaseId", purchaseId)
                .execute(object : StringDialogCallback(baseContext) {

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        val obj = JSONObject(response.body())
                                .optJSONObject("object") ?: JSONObject()

                        purchaseId = obj.optString("purchaseId")
                        productId = obj.optString("productId")
                        productName = obj.optString("productName")
                        accountInfoId = obj.optString("accountInfoId")
                        mYears = obj.optString("years")
                        nonManagerInfoId = obj.optString("nonManagerInfoId")

                        check_product.setRightString(productName)
                        check_year.setRightString(mYears.getDateFormat())
                        check_money.setText((obj.optString("amount").toNotInt() / 10000).toString())
                        check_begin.text = obj.optString("beginDate")
                        check_end.text = obj.optString("endDate")

                        et_profit.setText(obj.optString("profit"))
                        if (productName == "会员卡投资") {
                            back_ll.visibility = View.VISIBLE
                            et_back.setText(obj.optString("prepaidAmount"))
                        }

                        val compName = obj.optString("compName")
                        val vipNo = obj.optString("vipNo")
                        val investType = obj.optString("investType")
                        val stock = obj.optString("stock")
                        check_company.setLeftString(if (vipNo.isEmpty()) "企业名称" else "VIP编号")
                        check_company.setRightString(if (vipNo.isEmpty()) compName else vipNo)
                        when (investType) {
                            "1" -> {
                                check_trans_money.visibility = View.VISIBLE
                                check_trans_money.setLeftString("转投金额(元)")
                            }
                            "2" -> {
                                check_trans_money.visibility = View.VISIBLE
                                check_trans_money.setLeftString("续投金额(元)")
                            }
                        }
                        check_trans_money.setRightString(DecimalFormat("0.##").format(stock.toNotDouble()))

                        check_name.setRightString(obj.optString("userName"))
                        check_idcard.setRightString(obj.optString("cardNo"))
                        check_bank.text = obj.optString("bank")
                        check_card.setText(obj.optString("bankcard"))
                        check_phone.setText(obj.optString("phone"))
                        check_addr.setText(obj.optString("address"))
                        check_fax.setText(obj.optString("fax"))
                        check_agent.text = obj.optString("nonManagerName")
                        check_memo.setText(obj.optString("remark"))
                        check_addr.setSelection(check_addr.text.length)
                    }

                })
    }

    private fun doubleToLong(edit: CharSequence) = (edit.toNoDouble() * 10000).toLong()

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (check_money.text!!.isNotBlank()
                && check_begin.text.isNotBlank()
                && check_end.text.isNotBlank()
                && check_card.text!!.isNotBlank()
                && check_phone.text.isNotBlank()) {
            check_pass.setBackgroundResource(R.drawable.rec_bg_red)
            check_pass.isClickable = true
        } else {
            check_pass.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
            check_pass.isClickable = false
        }
    }

    override fun finish() {
        EventBus.getDefault().unregister(this@CheckDetailActivity)
        super.finish()
    }

    @SuppressLint("SetTextI18n")
    @Subscribe
    fun onMessageEvent(event: ReportMessageEvent) {
        when (event.type) {
            "银行" -> check_bank.text = event.name
            "非基金" -> {
                nonManagerInfoId = event.id
                check_agent.text = event.name
            }
        }
    }
}
