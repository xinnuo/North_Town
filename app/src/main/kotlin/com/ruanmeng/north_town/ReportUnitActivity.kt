package com.ruanmeng.north_town

import android.os.Bundle
import android.view.View
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.load_Linear
import com.ruanmeng.model.CommonData
import kotlinx.android.synthetic.main.activity_report_unit.*
import net.idik.lib.slimadapter.SlimAdapter
import java.util.ArrayList

class ReportUnitActivity : BaseActivity() {

    private lateinit var list: ArrayList<CommonData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_unit)
        init_title("选择工作单位")
    }

    override fun init_title() {
        super.init_title()
        unit_list.load_Linear(baseContext)

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_job_list) { data, injector ->
                    injector.visibility(R.id.item_job_divider1, if (list.indexOf(data) == list.size - 1) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_job_divider2, if (list.indexOf(data) != list.size - 1) View.GONE else View.VISIBLE)

                            .clicked(R.id.item_job) { }
                }
                .attachTo(unit_list)
    }
}
