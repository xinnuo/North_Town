package com.ruanmeng.north_town

import android.os.Bundle
import com.ruanmeng.base.BaseActivity

class FinanceScanActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finance_scan)
        init_title("合同信息")
    }
}
