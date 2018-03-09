package com.ruanmeng.north_town

import android.os.Bundle
import android.view.View
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.startActivity

class ReportAddActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_add)
        init_title("添加客户信息")
    }

    override fun init_title() {
        super.init_title()
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.report_like_ll -> startActivity(ReportLikeActivity::class.java)
            R.id.report_work_ll -> startActivity(ReportJobActivity::class.java)
            R.id.report_unit_ll -> startActivity(ReportUnitActivity::class.java)
        }
    }
}
