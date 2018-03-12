package com.ruanmeng.north_town

import android.os.Bundle
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.load_Linear
import com.ruanmeng.model.CommonData
import kotlinx.android.synthetic.main.activity_report_bank.*
import net.idik.lib.slimadapter.SlimAdapter

class ReportBankActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_bank)
        init_title("选择开户行")

        list.add(CommonData())
        list.add(CommonData())
        list.add(CommonData())
        list.add(CommonData())
        mAdapter.updateData(list)
    }

    override fun init_title() {
        super.init_title()
        bank_list.load_Linear(baseContext)
        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_bank_list) { data, injector -> }
                .attachTo(bank_list)
    }
}
