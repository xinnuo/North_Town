package com.ruanmeng.north_town

import android.os.Bundle
import android.view.View
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.startActivityEx

class NewsSelectActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_select)
        init_title("客户数据", "历史投资")
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.tv_nav_right -> startActivityEx<DataHistoryActivity>()
            R.id.news_customer -> startActivityEx<NewsActivity>(
                    "title" to "客户统计",
                    "type" to "1")
            R.id.news_order -> startActivityEx<NewsActivity>(
                    "title" to "投资订单统计",
                    "type" to "2")
        }
    }
}
