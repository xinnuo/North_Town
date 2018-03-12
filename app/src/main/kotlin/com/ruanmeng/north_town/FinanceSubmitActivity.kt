package com.ruanmeng.north_town

import android.os.Bundle
import com.ruanmeng.base.BaseActivity
import kotlinx.android.synthetic.main.activity_finance_submit.*

class FinanceSubmitActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finance_submit)
        init_title("录入收款信息")
    }

    override fun init_title() {
        super.init_title()

        finance_get.setOnClickListener {
            intent.setClass(baseContext, FinanceSelectActivity::class.java)
            intent.putExtra("title", "收款方式")
            startActivity(intent)
        }
        finance_shou.setOnClickListener {
            intent.setClass(baseContext, FinanceSelectActivity::class.java)
            intent.putExtra("title", "收据类型")
            startActivity(intent)
        }
        finance_submit.setOnClickListener {  }
    }
}
