package com.ruanmeng.north_town

import android.os.Bundle
import android.view.View
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.utils.ActivityStack
import kotlinx.android.synthetic.main.activity_report.*
import kotlinx.android.synthetic.main.layout_empty_add.*
import net.idik.lib.slimadapter.SlimAdapter

class ReportActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)
        setToolbarVisibility(false)
        init_title()

        list.add(CommonData())
        list.add(CommonData())
        list.add(CommonData())
        list.add(CommonData())
        mAdapter.updateData(list)
    }

    @Suppress("DEPRECATION")
    override fun init_title() {
        swipe_refresh.refresh { getData(1) }
        recycle_list.load_Linear(baseContext, swipe_refresh) {
            if (!isLoadingMore) {
                isLoadingMore = true
                getData(pageNum)
            }
        }

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_report_list) { data, injector ->
                    injector.text(R.id.item_report_name, getColorText("姓名", "姓"))
                            .clicked(R.id.item_report) {
                        startActivity(ReportDetailActivity::class.java)
                    }
                }
                .attachTo(recycle_list)
    }
    
    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.search_cancel -> ActivityStack.screenManager.popActivities(this@ReportActivity::class.java) 
            R.id.empty_add -> startActivity(ReportAddActivity::class.java)
        }
    }
}
