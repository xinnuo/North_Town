package com.ruanmeng.north_town

import android.os.Bundle
import com.ruanmeng.base.BaseActivity

class FinanceSubmitActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finance_submit)
        init_title("录入收款信息")
    }
}
