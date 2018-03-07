package com.ruanmeng.north_town

import android.os.Bundle
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

        funds_agent.setOnClickListener { startActivity(FundsAgentActivity::class.java) }
        funds_lead.setOnClickListener { startActivity(FundsLeadActivity::class.java) }
        funds_sale.setOnClickListener { startActivity(FundsSaleActivity::class.java) }
    }
}
