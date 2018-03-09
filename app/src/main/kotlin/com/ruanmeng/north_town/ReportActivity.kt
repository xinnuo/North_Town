package com.ruanmeng.north_town

import android.os.Bundle
import android.view.View
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.startActivity
import com.ruanmeng.utils.ActivityStack
import kotlinx.android.synthetic.main.layout_empty_add.*

class ReportActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)
        setToolbarVisibility(false)
        init_title()
    }

    override fun init_title() {
        empty_view.visibility = View.VISIBLE
    }
    
    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.search_cancel -> ActivityStack.screenManager.popActivities(this@ReportActivity::class.java) 
            R.id.empty_add -> startActivity(ReportAddActivity::class.java)
        }
    }
}
