package com.ruanmeng.north_town

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.load_Linear
import com.ruanmeng.model.CommonData
import kotlinx.android.synthetic.main.activity_finance_select.*
import net.idik.lib.slimadapter.SlimAdapter
import java.util.ArrayList

class FinanceSelectActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finance_select)
        init_title()

        list.add(CommonData("新单"))
        list.add(CommonData("旧单"))
        list.add(CommonData("转单"))
        list.add(CommonData("基金"))
        mAdapter.updateData(list)
    }

    @SuppressLint("SetTextI18n")
    override fun init_title() {
        super.init_title()
        val title = intent.getStringExtra("title")
        tvTitle.text = "选择$title"

        finance_list.load_Linear(baseContext)
        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_job_list) { data, injector ->
                    injector.visibility(R.id.item_job_divider1, if (list.indexOf(data) == list.size - 1) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_job_divider2, if (list.indexOf(data) != list.size - 1) View.GONE else View.VISIBLE)

                            .clicked(R.id.item_job) { }
                }
                .attachTo(finance_list)
    }
}
