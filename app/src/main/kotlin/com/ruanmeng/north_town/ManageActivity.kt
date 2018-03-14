package com.ruanmeng.north_town

import android.content.Intent
import android.os.Bundle
import com.ruanmeng.base.BaseActivity
import kotlinx.android.synthetic.main.activity_manage.*

class ManageActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage)
        init_title("理财产品")
    }

    override fun init_title() {
        super.init_title()
        manage_vip.setOnClickListener {
            val intent = Intent(baseContext, ManageDetailActivity::class.java)
            intent.putExtra("title", "会员卡")
            startActivity(intent)
        }
        manage_partner.setOnClickListener {
            val intent = Intent(baseContext, ManageDetailActivity::class.java)
            intent.putExtra("title", "有限合伙人")
            startActivity(intent)
        }
    }
}
