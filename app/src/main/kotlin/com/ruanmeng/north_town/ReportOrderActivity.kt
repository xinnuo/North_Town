package com.ruanmeng.north_town

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.lzg.extend.BaseResponse
import com.lzg.extend.StringDialogCallback
import com.lzg.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.ReportMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.*
import kotlinx.android.synthetic.main.activity_report_order.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.text.DecimalFormat
import java.util.*

class ReportOrderActivity : BaseActivity() {

    private val list = ArrayList<CommonData>()
    private val items = ArrayList<CommonData>()

    private var bankId = ""
    private var productId = ""
    private var investTypeId = ""
    private var managerInfoId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_order)
        init_title("添加订单")

        EventBus.getDefault().register(this@ReportOrderActivity)

        getData()
    }

    override fun init_title() {
        super.init_title()
        report_submit.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
        report_submit.isClickable = false

        report_name.setRightString(intent.getStringExtra("userName"))
        report_idcard.setRightString(intent.getStringExtra("cardNo"))

        report_product.addTextChangedListener(this@ReportOrderActivity)
        et_pact.addTextChangedListener(this@ReportOrderActivity)
        if (list.isNotEmpty()) et_receipt.addTextChangedListener(this@ReportOrderActivity)
        report_tou.addTextChangedListener(this@ReportOrderActivity)
        et_money.addTextChangedListener(this@ReportOrderActivity)
        report_start.addTextChangedListener(this@ReportOrderActivity)
        report_end.addTextChangedListener(this@ReportOrderActivity)
        report_bank.addTextChangedListener(this@ReportOrderActivity)
        et_card.addTextChangedListener(this@ReportOrderActivity)
        et_phone.addTextChangedListener(this@ReportOrderActivity)
        et_addr.addTextChangedListener(this@ReportOrderActivity)
        et_fax.addTextChangedListener(this@ReportOrderActivity)
        report_agent.addTextChangedListener(this@ReportOrderActivity)
        et_memo.addTextChangedListener(this@ReportOrderActivity)
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.report_product_ll -> startActivity<ReportProductActivity>()
            R.id.report_tou_ll -> {
                val intent = Intent(baseContext, FinanceSelectActivity::class.java)
                intent.putExtra("title", "投资类型")
                startActivity(intent)
            }
            R.id.report_start_ll, R.id.report_end_ll -> {
                if (report_product.text.isEmpty()) {
                    showToast("请选择投资产品")
                    return
                }

                if (v.id == R.id.report_end_ll && report_end.text.isNotEmpty()) return

                val year_now = Calendar.getInstance().get(Calendar.YEAR)
                DialogHelper.showDateDialog(this@ReportOrderActivity,
                        year_now,
                        year_now + 20,
                        3,
                        "选择出资日期",
                        true,
                        false, { _, _, _, _, _, date ->
                    report_start.text = date
                    report_end.text = TimeHelper.getInstance().getAnyYear(date, items.first().years.toInt())
                })
            }
            R.id.report_bank_ll -> startActivity<ReportBankActivity>()
            R.id.report_agent_ll -> startActivity<ReportAgentActivity>()
            R.id.report_submit -> {
                if (list.isNotEmpty() && list.none { it.receiptNo == et_receipt.text.toString() }) {
                    showToast("请输入正确的收据编码")
                    return
                }
                if (!BankcardHelper.checkBankCard(et_card.rawText)) {
                    et_card.requestFocus()
                    et_card.setText("")
                    showToast("请输入正确的银行卡卡号")
                    return
                }

                if (BankCardUtil(et_card.rawText).bankName != report_bank.text.toString()) {
                    showToast("银行卡号与所属银行不符，请重新输入")
                    return
                }

                if (!CommonUtil.isMobile(et_phone.text.toString())) {
                    et_phone.requestFocus()
                    et_phone.setText("")
                    showToast("请输入正确的联系电话")
                    return
                }

                if (!CommonUtil.isFax(et_fax.text.toString())) {
                    et_fax.requestFocus()
                    et_fax.setText("")
                    showToast("请输入正确的传真号码")
                    return
                }

                OkGo.post<String>(BaseHttp.purchase_sub)
                        .tag(this@ReportOrderActivity)
                        .isMultipart(true)
                        .headers("token", getString("token"))
                        .params("accountInfoId", intent.getStringExtra("accountInfoId"))
                        .params("contractNo", et_pact.text.trim().toString())
                        .params("receiptNo", et_receipt.text.trim().toString())
                        .params("investTypeId", investTypeId)
                        .params("productId", productId)
                        .params("years", items.first().years)
                        .params("amount", et_money.text.trim().toString())
                        .params("beginDate", report_start.text.toString())
                        .params("endDate", report_end.text.toString())
                        .params("bank", bankId)
                        .params("bankCard", et_card.rawText)
                        .params("phone", et_phone.text.trim().toString())
                        .params("address", et_addr.text.trim().toString())
                        .params("fax", et_fax.text.trim().toString())
                        .params("managerInfoId", managerInfoId)
                        .params("remark", et_memo.text.trim().toString())
                        .execute(object : StringDialogCallback(baseContext) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                                showToast("添加订单信息成功！")
                                ActivityStack.screenManager.popActivities(this@ReportOrderActivity::class.java)
                            }

                        })
            }
        }
    }

    override fun getData() {
        OkGo.post<BaseResponse<ArrayList<CommonData>>>(BaseHttp.purchase_by_id)
                .tag(this@ReportOrderActivity)
                .headers("token", getString("token"))
                .params("accountInfoId", intent.getStringExtra("accountInfoId"))
                .execute(object : JacksonDialogCallback<BaseResponse<ArrayList<CommonData>>>(baseContext, true) {

                    override fun onSuccess(response: Response<BaseResponse<ArrayList<CommonData>>>) {
                        list.apply {
                            clear()
                            addItems(response.body().`object`)
                        }

                        if (list.isEmpty()) order_expand.collapse()
                        else {
                            et_receipt.setText(list.first().receiptNo)
                            et_money.setText(list.first().amount)
                            et_memo.setText(list.first().remark)
                        }
                    }

                })
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (report_product.text.isNotBlank()
                && et_pact.text.isNotBlank()
                && (list.isEmpty() || et_receipt.text.isNotBlank())
                && report_tou.text.isNotBlank()
                && et_money.text.isNotBlank()
                && report_start.text.isNotBlank()
                && report_end.text.isNotBlank()
                && report_bank.text.isNotBlank()
                && et_card.text.isNotBlank()
                && et_phone.text.isNotBlank()
                && et_addr.text.isNotBlank()
                && et_fax.text.isNotBlank()
                && report_agent.text.isNotBlank()
                && et_memo.text.isNotBlank()) {
            report_submit.setBackgroundResource(R.drawable.rec_bg_red)
            report_submit.isClickable = true
        } else {
            report_submit.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
            report_submit.isClickable = false
        }

        if (et_money.isFocused && et_money.text.isNotBlank()) calculatedValue(et_money.text.toString().toLong())
    }

    private fun calculatedValue(value: Long) {
        if (items.isEmpty()) return

        val minValue = items.first().min.toInt() * 10000

        if (value >= minValue) {
            items.forEach {
                val min = it.min.toInt() * 10000
                val max = it.max.toInt() * 10000

                if (min < max) {
                    if (value in min..(max - 1)) {
                        report_expect.setLeftString("利率 ${it.rate}%（起投 ${it.min}万）")
                        val expect = DecimalFormat("###,###,##0.##").format(value * (1 + it.rate.toInt() / 100.0))
                        report_expect.setRightString("预期收入 ￥$expect")
                        return
                    }
                } else {
                    if (value >= min) {
                        report_expect.setLeftString("利率 ${it.rate}%（起投 ${it.min}万）")
                        val expect = DecimalFormat("###,###,##0.##").format(value * (1 + it.rate.toInt() / 100.0))
                        report_expect.setRightString("预期收入 ￥$expect")
                        return
                    }
                }
            }
        } else {
            report_expect.setLeftString("利率 ${items.first().rate}%（起投 ${items.first().min}万）")
            report_expect.setRightString("预期收入 ￥0")
        }
    }

    override fun finish() {
        EventBus.getDefault().unregister(this@ReportOrderActivity)
        super.finish()
    }

    @Subscribe
    fun onMessageEvent(event: ReportMessageEvent) {
        when (event.type) {
            "产品" -> {
                productId = event.id
                items.clear()
                items.addAll(event.items)
                report_product.text = event.name

                if (et_money.isFocused && et_money.text.isBlank())
                    report_expect.setLeftString("利率 ${items.first().rate}%（起投 ${items.first().min}万）")

                if (!et_money.isFocused) {
                    if (et_money.text.isNotBlank()) calculatedValue(et_money.text.toString().toLong())
                    else report_expect.setLeftString("利率 ${items.first().rate}%（起投 ${items.first().min}万）")
                }
            }
            "银行" -> {
                bankId = event.id
                report_bank.text = event.name
            }
            "经纪人" -> {
                managerInfoId = event.id
                report_agent.text = event.name
            }
            "投资" -> {
                investTypeId = event.id
                report_tou.text = event.name
            }
        }
    }
}
