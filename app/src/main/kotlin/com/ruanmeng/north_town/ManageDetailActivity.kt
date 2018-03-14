package com.ruanmeng.north_town

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.model.CommonData
import com.ruanmeng.view.FullyLinearLayoutManager
import kotlinx.android.synthetic.main.activity_manage_detail.*
import net.idik.lib.slimadapter.SlimAdapter

class ManageDetailActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_detail)
        init_title()

        list.add(CommonData("1"))
        list.add(CommonData("2"))
        list.add(CommonData("3"))
        list.add(CommonData("4"))
        (manage_list.adapter as SlimAdapter).updateData(list)
    }

    @SuppressLint("SetTextI18n", "SetJavaScriptEnabled")
    override fun init_title() {
        super.init_title()
        tvTitle.text = "${intent.getStringExtra("title")}详情"

        manage_info.apply {
            //支持javascript
            settings.javaScriptEnabled = true
            //设置可以支持缩放
            settings.setSupportZoom(true)
            //自适应屏幕
            settings.loadWithOverviewMode = true
            settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
            isHorizontalScrollBarEnabled = false

            //设置出现缩放工具
            settings.builtInZoomControls = true
            settings.displayZoomControls = false
        }

        manage_list.apply {
            layoutManager = FullyLinearLayoutManager(baseContext)

            adapter = SlimAdapter.create()
                    .register<CommonData>(R.layout.item_manage_list) { data, injector ->
                        injector.visibility(R.id.item_manage_divider1, if (list.indexOf(data) == list.size - 1) View.GONE else View.VISIBLE)
                                .visibility(R.id.item_manage_divider2, if (list.indexOf(data) != list.size - 1) View.GONE else View.VISIBLE)
                    }
                    .attachTo(this)
        }
    }

    override fun onResume() {
        super.onResume()
        manage_info.onResume()
    }

    override fun onPause() {
        super.onPause()
        manage_info.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        manage_info.destroy()
    }
}
