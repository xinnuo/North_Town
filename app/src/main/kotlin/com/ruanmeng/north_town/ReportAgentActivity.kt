package com.ruanmeng.north_town

import android.os.Bundle
import android.view.View
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.load_Linear
import com.ruanmeng.model.CommonData
import kotlinx.android.synthetic.main.activity_report_agent.*
import net.idik.lib.slimadapter.SlimAdapter

class ReportAgentActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_agent)
        init_title("选择经纪人")

        list.add(CommonData("1"))
        list.add(CommonData("2"))
        list.add(CommonData("3"))
        list.add(CommonData("4"))
        list.add(CommonData("5"))
        list.add(CommonData("6"))
        mAdapter.updateData(list)
    }

    override fun init_title() {
        super.init_title()
        agent_list.load_Linear(baseContext)

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_client_list) { data, injector ->
                    injector.visibility(R.id.item_client_divider1, if (list.indexOf(data) == list.size - 1) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_client_divider2, if (list.indexOf(data) != list.size - 1) View.GONE else View.VISIBLE)
                }
                .attachTo(agent_list)
    }
}
