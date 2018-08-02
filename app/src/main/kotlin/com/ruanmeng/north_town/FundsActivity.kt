package com.ruanmeng.north_town

import android.os.Bundle
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.startActivityEx
import kotlinx.android.synthetic.main.activity_funds.*

class FundsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_funds)
        init_title("佣金统计")
    }

    override fun init_title() {
        super.init_title()

        funds_agent.setOnClickListener { startActivityEx<FundsAgentActivity>() }
        funds_lead.setOnClickListener { startActivityEx<FundsLeadActivity>() }
        funds_sale.setOnClickListener { startActivityEx<FundsSaleActivity>() }
    }
}
