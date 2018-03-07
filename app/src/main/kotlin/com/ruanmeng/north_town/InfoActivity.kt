package com.ruanmeng.north_town

import android.os.Bundle
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.startActivity
import kotlinx.android.synthetic.main.activity_info.*

class InfoActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)
        init_title("个人信息")
    }

    override fun init_title() {
        super.init_title()

        info_img_ll.setOnClickListener {  }
        info_name.setOnClickListener { startActivity(ModifyNameActivity::class.java) }
        info_pwd.setOnClickListener { startActivity(PasswordActivity::class.java) }
    }
}
