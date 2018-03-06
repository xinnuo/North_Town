package com.ruanmeng.north_town

import android.os.Bundle
import com.ruanmeng.base.BaseActivity

class ClientActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client)
        init_title("我的客户")
    }
}
