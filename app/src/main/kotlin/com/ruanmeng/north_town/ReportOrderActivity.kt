package com.ruanmeng.north_town

import android.os.Bundle
import android.view.View
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.showToast
import com.ruanmeng.base.startActivity
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.ReportMessageEvent
import com.ruanmeng.utils.DialogHelper
import com.ruanmeng.utils.TimeHelper
import kotlinx.android.synthetic.main.activity_report_order.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.text.DecimalFormat
import java.util.*

class ReportOrderActivity : BaseActivity() {

    private val items = ArrayList<CommonData>()

    private var bankId = ""
    private var productId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_order)
        init_title("添加订单")

        EventBus.getDefault().register(this@ReportOrderActivity)
    }

    override fun init_title() {
        super.init_title()
        report_submit.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
        report_submit.isClickable = false

        report_name.setRightString(intent.getStringExtra("userName"))
        report_idcard.setRightString(intent.getStringExtra("cardNo"))

        report_product.addTextChangedListener(this@ReportOrderActivity)
        et_money.addTextChangedListener(this@ReportOrderActivity)
        report_start.addTextChangedListener(this@ReportOrderActivity)
        report_end.addTextChangedListener(this@ReportOrderActivity)
        report_bank.addTextChangedListener(this@ReportOrderActivity)
        et_card.addTextChangedListener(this@ReportOrderActivity)
        et_phone.addTextChangedListener(this@ReportOrderActivity)
        et_addr.addTextChangedListener(this@ReportOrderActivity)
        et_fax.addTextChangedListener(this@ReportOrderActivity)
        report_agent.addTextChangedListener(this@ReportOrderActivity)
        report_up.addTextChangedListener(this@ReportOrderActivity)
        report_relation.addTextChangedListener(this@ReportOrderActivity)
        et_memo.addTextChangedListener(this@ReportOrderActivity)
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.report_product_ll -> startActivity(ReportProductActivity::class.java)
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
            R.id.report_bank_ll -> startActivity(ReportBankActivity::class.java)
            R.id.report_agent_ll -> startActivity(ReportAgentActivity::class.java)
            R.id.report_up_ll -> { }
            R.id.report_relation_ll -> { }
            R.id.report_submit -> { }
        }
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (report_product.text.isNotBlank()
                && et_money.text.isNotBlank()
                && report_start.text.isNotBlank()
                && report_end.text.isNotBlank()
                && report_bank.text.isNotBlank()
                && et_card.text.isNotBlank()
                && et_phone.text.isNotBlank()
                && et_addr.text.isNotBlank()
                && et_fax.text.isNotBlank()
                && report_agent.text.isNotBlank()
                && report_up.text.isNotBlank()
                && report_relation.text.isNotBlank()
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
        }
    }
}
