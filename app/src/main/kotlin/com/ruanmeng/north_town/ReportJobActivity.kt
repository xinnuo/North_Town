package com.ruanmeng.north_town

import android.os.Bundle
import com.ruanmeng.base.BaseActivity

class ReportJobActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_job)
        init_title("选择行业")
    }
}