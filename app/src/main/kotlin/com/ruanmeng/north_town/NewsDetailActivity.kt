package com.ruanmeng.north_town

import android.os.Bundle
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.startActivity
import kotlinx.android.synthetic.main.activity_news_detail.*

class NewsDetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_detail)
        init_title("客户详情", "添加订单")
    }

    @Suppress("DEPRECATION")
    override fun init_title() {
        super.init_title()
        val drawable = resources.getDrawable(R.mipmap.nav_add)
        // 这一步必须要做,否则不会显示
        drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
        tvRight.setCompoundDrawables(drawable, null, null, null)

        tvRight.setOnClickListener { startActivity<ReportOrderActivity>() }
        news_look.setOnClickListener { startActivity<DataCheckActivity>() }
    }
}
