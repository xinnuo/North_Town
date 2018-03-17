package com.ruanmeng.north_town

import android.os.Bundle
import android.view.View
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.startActivity
import com.ruanmeng.model.ReportMessageEvent
import com.ruanmeng.utils.DialogHelper
import kotlinx.android.synthetic.main.activity_report_order.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.*

class ReportOrderActivity : BaseActivity() {

    private var bankId = ""

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
            R.id.report_product_ll -> { }
            R.id.report_start_ll -> {
                val year_now = Calendar.getInstance().get(Calendar.YEAR)

                DialogHelper.showDateDialog(this@ReportOrderActivity,
                        year_now,
                        year_now + 20,
                        3,
                        "选择出资日期",
                        true,
                        false, { _, _, _, _, _, date ->
                    report_start.text = date
                })
            }
            R.id.report_end_ll -> {
                val year_now = Calendar.getInstance().get(Calendar.YEAR)

                DialogHelper.showDateDialog(this@ReportOrderActivity,
                        year_now,
                        year_now + 20,
                        3,
                        "选择到期日期",
                        true,
                        false, { _, _, _, _, _, date ->
                    report_start.text = date
                })
            }
            R.id.report_bank_ll -> startActivity(ReportBankActivity::class.java)
            R.id.report_agent_ll -> startActivity(ReportAgentActivity::class.java)
            R.id.report_up_ll -> { }
            R.id.report_relation_ll -> { }
            R.id.report_submit -> { }
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
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
    }

    override fun finish() {
        EventBus.getDefault().unregister(this@ReportOrderActivity)
        super.finish()
    }

    @Subscribe
    fun onMessageEvent(event: ReportMessageEvent) {
        when (event.type) {
            "银行" -> {
                bankId = event.id
                report_bank.text = event.name
            }
        }
    }
}
