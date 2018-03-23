package com.ruanmeng.north_town

import android.os.Bundle
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.load_Linear
import com.ruanmeng.base.refresh
import com.ruanmeng.base.startActivity
import com.ruanmeng.model.CommonData
import kotlinx.android.synthetic.main.activity_news.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import kotlinx.android.synthetic.main.layout_title_filter.*
import net.idik.lib.slimadapter.SlimAdapter

class NewsActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)
        setToolbarVisibility(false)
        init_title()

        list.add(CommonData())
        list.add(CommonData())
        list.add(CommonData())
        list.add(CommonData())
        list.add(CommonData())
        list.add(CommonData())
        mAdapter.updateData(list)
    }

    override fun init_title() {
        filter_check.setOnClickListener {
            when (news_expand.isExpanded) {
                true -> news_expand.collapse()
                else -> news_expand.expand()
            }
        }

        empty_hint.text = "暂无相关客户信息！"
        swipe_refresh.refresh { getData(mPosition) }
        recycle_list.load_Linear(baseContext, swipe_refresh) {
            if (!isLoadingMore) {
                isLoadingMore = true
                getData(pageNum)
            }
        }

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_data_list) { data, injector ->
                    injector.clicked(R.id.item_data) { startActivity<NewsDetailActivity>() }
                }
                .attachTo(recycle_list)
    }
}
