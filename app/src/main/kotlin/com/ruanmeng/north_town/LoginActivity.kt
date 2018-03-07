package com.ruanmeng.north_town

import android.os.Bundle
import android.view.View
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.startActivity
import com.ruanmeng.utils.ActivityStack
import kotlinx.android.synthetic.main.layout_title_main.*

class LoginActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setToolbarVisibility(false)

        init_title()
    }

    override fun init_title() {
        main_title.text = "登录"
        main_close.visibility = View.VISIBLE
    }

    override fun doClick(v: View) {
        when (v.id) {
            R.id.main_close -> onBackPressed()
            R.id.tv_forget -> startActivity(ForgetActivity::class.java)
            R.id.bt_login -> {
                startActivity(MainActivity::class.java)
                ActivityStack.screenManager.popActivities(this@LoginActivity::class.java)
            }
        }
    }
}
