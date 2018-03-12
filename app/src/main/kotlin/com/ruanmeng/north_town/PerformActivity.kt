package com.ruanmeng.north_town

import android.os.Bundle
import android.support.design.widget.TabLayout
import com.lzy.okgo.OkGo
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.load_Linear
import com.ruanmeng.base.refresh
import com.ruanmeng.base.startActivity
import com.ruanmeng.model.CommonData
import com.ruanmeng.utils.Tools
import kotlinx.android.synthetic.main.activity_data.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import kotlinx.android.synthetic.main.layout_search.*
import net.idik.lib.slimadapter.SlimAdapter

class PerformActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perform)
        init_title("业绩统计")
    }

    override fun init_title() {
        super.init_title()
        search_edit.hint = "请输入业务人员姓名或手机号"
        empty_hint.text = "暂无相关业绩信息！"

        data_tab.apply {
            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

                override fun onTabReselected(tab: TabLayout.Tab) {}
                override fun onTabUnselected(tab: TabLayout.Tab) {}

                override fun onTabSelected(tab: TabLayout.Tab) {
                    mPosition = tab.position
                    OkGo.getInstance().cancelTag(this@PerformActivity)

                    window.decorView.postDelayed({ runOnUiThread { updateList() } }, 300)
                }

            })

            addTab(this.newTab().setText("日业绩"), true)
            addTab(this.newTab().setText("周业绩"), false)
            addTab(this.newTab().setText("月业绩"), false)
            addTab(this.newTab().setText("年业绩"), false)

            post { Tools.setIndicator(this, 15, 15) }
        }

        swipe_refresh.refresh { getData(mPosition) }
        recycle_list.load_Linear(baseContext, swipe_refresh) {
            if (!isLoadingMore) {
                isLoadingMore = true
                getData(pageNum)
            }
        }

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_data_list) { data, injector ->
                    injector.gone(R.id.item_data_idcard)
                            .clicked(R.id.item_data) { startActivity(PerformCheckActivity::class.java) }
                }
                .attachTo(recycle_list)
    }

    fun updateList() {
        /*swipe_refresh.isRefreshing = true
        if (list.size > 0) {
            list.clear()
            mAdapter.notifyDataSetChanged()
        }
        getData(mPosition)*/

        list.add(CommonData())
        list.add(CommonData())
        list.add(CommonData())
        list.add(CommonData())
        list.add(CommonData())
        list.add(CommonData())
        mAdapter.updateData(list)
    }
}
