package com.ruanmeng.north_town

import android.os.Bundle
import android.view.View
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.startActivity
import kotlinx.android.synthetic.main.activity_funds.*

class FundsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_funds)
        init_title("佣金统计")
    }

    override fun init_title() {
        super.init_title()
        funds_lead.visibility = View.GONE

        funds_agent.setOnClickListener { startActivity<FundsAgentActivity>() }
        funds_lead.setOnClickListener { startActivity<FundsLeadActivity>() }
        funds_sale.setOnClickListener { startActivity<FundsSaleActivity>() }
    }
}
