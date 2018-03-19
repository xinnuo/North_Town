package com.ruanmeng.north_town

import android.content.Intent
import android.os.Bundle
import com.ruanmeng.base.BaseActivity
import kotlinx.android.synthetic.main.activity_check_detail.*

class CheckDetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_detail)
        init_title("对账详情")
    }

    override fun init_title() {
        super.init_title()

        check_pass.setOnClickListener {
            val intent = Intent(baseContext, WebActivity::class.java)
            intent.putExtra("title", "微信支付")
            startActivity(intent)
        }
    }
}
