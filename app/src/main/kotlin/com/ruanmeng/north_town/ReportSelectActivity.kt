package com.ruanmeng.north_town

import android.os.Bundle
import android.view.View
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.startActivityEx

class ReportSelectActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_select)
        init_title("合同报备")
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.report_partner -> startActivityEx<ReportPartnerActivity>()
            R.id.report_vip -> startActivityEx<ReportOrderActivity>()
        }
    }
}
