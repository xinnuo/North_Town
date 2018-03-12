package com.ruanmeng.north_town

import android.os.Bundle
import android.view.View
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.startActivity
import kotlinx.android.synthetic.main.activity_report_order.*

class ReportOrderActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_order)
        init_title("添加订单")
    }

    override fun init_title() {
        super.init_title()

        report_start.setOnClickListener(this@ReportOrderActivity)
        report_end.setOnClickListener(this@ReportOrderActivity)
        report_bank.setOnClickListener(this@ReportOrderActivity)
        report_agent.setOnClickListener(this@ReportOrderActivity)
        report_up.setOnClickListener(this@ReportOrderActivity)
        report_relation.setOnClickListener(this@ReportOrderActivity)
        report_submit.setOnClickListener(this@ReportOrderActivity)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.report_start -> { }
            R.id.report_end -> { }
            R.id.report_bank -> { startActivity(ReportBankActivity::class.java) }
            R.id.report_agent -> { startActivity(ReportAgentActivity::class.java) }
            R.id.report_up -> { }
            R.id.report_relation -> { }
            R.id.report_submit -> { }
        }
    }
}
