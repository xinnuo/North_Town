package com.ruanmeng.north_town

import android.os.Bundle
import android.view.View
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.startActivity
import com.ruanmeng.model.ReportMessageEvent
import kotlinx.android.synthetic.main.activity_report_add.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class ReportAddActivity : BaseActivity() {

    private var preferenceId = ""
    private var preferenceName = ""
    private var industryId = ""
    private var industryName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_add)
        init_title("添加客户信息")

        EventBus.getDefault().register(this@ReportAddActivity)
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

    override fun finish() {
        EventBus.getDefault().unregister(this@ReportAddActivity)
        super.finish()
    }

    @Subscribe
    fun onMessageEvent(event: ReportMessageEvent) {
        when (event.type) {
            "偏好" -> {
                preferenceId = event.id
                preferenceName = event.name
                report_like.text = event.name
            }
            "职业" -> {
                industryId = event.id
                industryName = event.name
                report_work.text = event.name
            }
            "工作单位" -> { }
        }
    }
}
