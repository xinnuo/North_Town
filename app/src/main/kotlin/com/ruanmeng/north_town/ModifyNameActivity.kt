package com.ruanmeng.north_town

import android.os.Bundle
import com.ruanmeng.base.BaseActivity

class ModifyNameActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_name)
        init_title("修改姓名")
    }
}