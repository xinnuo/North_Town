package com.ruanmeng.north_town

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.utils.Tools
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        init_title("设置")
    }

    override fun init_title() {
        super.init_title()
        setting_version.setRightString("v" + Tools.getVersion(baseContext))

        setting_about.setOnClickListener {
            val intent = Intent(baseContext, WebActivity::class.java)
            intent.putExtra("title", "关于我们")
            startActivity(intent)
        }
        setting_quit.setOnClickListener {
            AlertDialog.Builder(baseContext)
                    .setTitle("退出登录")
                    .setMessage("确定要退出当前账号吗？")
                    .setPositiveButton("退出") { _, _ ->
                        val intent = Intent(baseContext, LoginActivity::class.java)
                        intent.putExtra("offLine", true)
                        startActivity(intent)
                    }
                    .setNegativeButton("取消") { _, _ -> }
                    .create()
                    .show()
        }
    }
}
