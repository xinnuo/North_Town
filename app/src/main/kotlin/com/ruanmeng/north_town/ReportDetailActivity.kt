package com.ruanmeng.north_town

import android.os.Bundle
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.startActivity
import kotlinx.android.synthetic.main.activity_report_detail.*

class ReportDetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_detail)
        init_title("客户详情")
    }

    override fun init_title() {
        super.init_title()
        val isData = intent.getBooleanExtra("isData", false)
        if (isData) report_input.text = "查看客户投资"

        report_input.setOnClickListener {
            when (isData) {
               true -> startActivity(DataCheckActivity::class.java)
               false -> startActivity(ReportOrderActivity::class.java)
            }
        }
    }
}
