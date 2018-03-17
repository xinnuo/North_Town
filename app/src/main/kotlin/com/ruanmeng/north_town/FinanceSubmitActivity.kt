package com.ruanmeng.north_town

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.model.ReportMessageEvent
import kotlinx.android.synthetic.main.activity_finance_submit.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class FinanceSubmitActivity : BaseActivity() {

    private var payTypeId = ""
    private var receiptTypeId = ""
    private var investTypeId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finance_submit)
        init_title("录入收款信息")

        EventBus.getDefault().register(this@FinanceSubmitActivity)
    }

    override fun init_title() {
        super.init_title()
        finance_submit.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
        finance_submit.isClickable = false
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.finance_get_ll -> {
                val intent = Intent(baseContext, FinanceSelectActivity::class.java)
                intent.putExtra("title", "收款方式")
                startActivity(intent)
            }
            R.id.finance_shou_ll -> {
                val intent = Intent(baseContext, FinanceSelectActivity::class.java)
                intent.putExtra("title", "收据类型")
                startActivity(intent)
            }
            R.id.finance_tou_ll -> {
                val intent = Intent(baseContext, FinanceSelectActivity::class.java)
                intent.putExtra("title", "投资类型")
                startActivity(intent)
            }
            R.id.finance_submit -> { }
        }
    }

    override fun finish() {
        EventBus.getDefault().unregister(this@FinanceSubmitActivity)
        super.finish()
    }

    @Subscribe
    fun onMessageEvent(event: ReportMessageEvent) {
        when (event.type) {
            "收款" -> {
                payTypeId = event.id
                finance_get.text = event.name
            }
            "收据" -> {
                receiptTypeId = event.id
                finance_shou.text = event.name
            }
            "投资" -> {
                investTypeId = event.id
                finance_tou.text = event.name
            }
        }
    }
}
