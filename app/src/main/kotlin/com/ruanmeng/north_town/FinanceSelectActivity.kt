package com.ruanmeng.north_town

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.lzg.extend.BaseResponse
import com.lzg.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.addItems
import com.ruanmeng.base.getString
import com.ruanmeng.base.load_Linear
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.ReportMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import kotlinx.android.synthetic.main.activity_finance_select.*
import net.idik.lib.slimadapter.SlimAdapter
import org.greenrobot.eventbus.EventBus
import java.util.ArrayList

class FinanceSelectActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finance_select)
        init_title()

        getData()
    }

    @SuppressLint("SetTextI18n")
    override fun init_title() {
        super.init_title()
        val title = intent.getStringExtra("title")
        tvTitle.text = "选择$title"

        finance_list.load_Linear(baseContext)
        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_job_list) { data, injector ->
                    injector.gone(R.id.item_job_arrow)
                            .text(R.id.item_job_name, when (intent.getStringExtra("title")) {
                                "收款方式" -> data.payTypeName
                                "收据类型" -> data.receiptTypeName
                                "投资类型" -> data.investTypeName
                                else -> ""
                            })

                            .visibility(R.id.item_job_divider1, if (list.indexOf(data) == list.size - 1) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_job_divider2, if (list.indexOf(data) != list.size - 1) View.GONE else View.VISIBLE)

                            .clicked(R.id.item_job) {
                                when (intent.getStringExtra("title")) {
                                    "收款方式" -> EventBus.getDefault().post(ReportMessageEvent(
                                            data.payTypeId,
                                            data.payTypeName,
                                            "收款"))
                                    "收据类型" -> EventBus.getDefault().post(ReportMessageEvent(
                                            data.receiptTypeId,
                                            data.receiptTypeName,
                                            "收据"))
                                    "投资类型" -> EventBus.getDefault().post(ReportMessageEvent(
                                            data.investTypeId,
                                            data.investTypeName,
                                            "投资"))
                                }

                                ActivityStack.screenManager.popActivities(this@FinanceSelectActivity::class.java)
                            }
                }
                .attachTo(finance_list)
    }

    override fun getData() {
        OkGo.post<BaseResponse<ArrayList<CommonData>>>(when (intent.getStringExtra("title")) {
            "收款方式" -> BaseHttp.paytype_list
            "收据类型" -> BaseHttp.receipttype_list
            "投资类型" -> BaseHttp.investtype_list
            else -> ""
        })
                .tag(this@FinanceSelectActivity)
                .headers("token", getString("token"))
                .execute(object : JacksonDialogCallback<BaseResponse<ArrayList<CommonData>>>(baseContext, true) {

                    override fun onSuccess(response: Response<BaseResponse<ArrayList<CommonData>>>) {
                        list.apply {
                            clear()
                            addItems(response.body().`object`)
                        }
                        mAdapter.updateData(list)
                    }

                })
    }
}
