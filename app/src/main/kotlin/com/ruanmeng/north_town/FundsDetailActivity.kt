package com.ruanmeng.north_town

import android.os.Bundle
import com.ruanmeng.base.BaseActivity
import kotlinx.android.synthetic.main.activity_funds_detail.*

class FundsDetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_funds_detail)
        init_title("订单详情")
    }

    override fun init_title() {
        super.init_title()
        val isLead = intent.getBooleanExtra("isLead", false)
        if (isLead) funds_expand.expand()
    }
}
