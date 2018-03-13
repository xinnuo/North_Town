package com.ruanmeng.north_town

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.view.View
import com.lzy.okgo.OkGo
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.load_Linear
import com.ruanmeng.base.refresh
import com.ruanmeng.model.CommonData
import com.ruanmeng.utils.Tools
import kotlinx.android.synthetic.main.activity_data.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import kotlinx.android.synthetic.main.layout_search.*
import net.idik.lib.slimadapter.SlimAdapter

class DataActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data)
        init_title("客户列表")
    }

    override fun init_title() {
        super.init_title()
        search_edit.hint = "请输入客户姓名或身份证号"
        empty_hint.text = "暂无相关客户信息！"

        data_tab.apply {
            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

                override fun onTabReselected(tab: TabLayout.Tab) {}
                override fun onTabUnselected(tab: TabLayout.Tab) {}

                override fun onTabSelected(tab: TabLayout.Tab) {
                    mPosition = tab.position
                    OkGo.getInstance().cancelTag(this@DataActivity)

                    window.decorView.postDelayed({ runOnUiThread { updateList() } }, 300)
                }

            })

            addTab(this.newTab().setText("老客户"), true)
            addTab(this.newTab().setText("新客户"), false)

            post { Tools.setIndicator(this, 50, 50) }
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
                    injector.visibility(R.id.item_data_divider1, if (list.indexOf(data) == list.size - 1) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_data_divider2, if (list.indexOf(data) != list.size - 1) View.GONE else View.VISIBLE)
                            .clicked(R.id.item_data) {
                                intent.setClass(baseContext, ReportDetailActivity::class.java)
                                intent.putExtra("isData", true)
                                startActivity(intent)
                            }
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
