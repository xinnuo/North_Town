package com.ruanmeng.north_town

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.lzg.extend.BaseResponse
import com.lzg.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.ReportMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.NumberHelper
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class FinanceActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finance)
        init_title("财务录入", "其他录入")

        EventBus.getDefault().register(this@FinanceActivity)

        swipe_refresh.isRefreshing = true
        getData(pageNum)
    }

    override fun init_title() {
        super.init_title()
        empty_hint.text = "暂无相关录入信息！"
        swipe_refresh.refresh { getData(1) }
        recycle_list.load_Linear(baseContext, swipe_refresh) {
            if (!isLoadingMore) {
                isLoadingMore = true
                getData(pageNum)
            }
        }

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_finance_list) { data, injector ->
                    injector.text(R.id.item_finance_name, data.productName)
                            .text(R.id.item_finance_time, data.createDate)
                            .text(R.id.item_finance_put, data.userName)
                            .text(R.id.item_finance_manage, data.managerName)
                            .text(R.id.item_finance_money, NumberHelper.fmtMicrometer(data.amount))
                            .visibility(R.id.item_finance_divider, if (list.indexOf(data) == 0) View.VISIBLE else View.GONE)

                            .clicked(R.id.item_finance) {
                                val intent = Intent(baseContext, FinanceSubmitActivity::class.java)
                                intent.putExtra("userName", data.userName)
                                intent.putExtra("cardNo", data.cardNo)
                                intent.putExtra("amount", data.amount)
                                intent.putExtra("remark", data.remark)
                                startActivity(intent)
                            }
                }
                .attachTo(recycle_list)

        tvRight.setOnClickListener { startActivity<FinanceSubmitActivity>() }
    }

    override fun getData(pindex: Int) {
        OkGo.post<BaseResponse<ArrayList<CommonData>>>(BaseHttp.purchase_nopay_list)
                .tag(this@FinanceActivity)
                .headers("token", getString("token"))
                .params("page", pindex)
                .execute(object : JacksonDialogCallback<BaseResponse<ArrayList<CommonData>>>(baseContext) {

                    override fun onSuccess(response: Response<BaseResponse<ArrayList<CommonData>>>) {

                        list.apply {
                            if (pindex == 1) {
                                clear()
                                pageNum = pindex
                            }
                            addItems(response.body().`object`)
                            if (count(response.body().`object`) > 0) pageNum++
                        }
                        if (count(response.body().`object`) > 0) mAdapter.updateData(list)
                    }

                    override fun onFinish() {
                        super.onFinish()
                        swipe_refresh.isRefreshing = false
                        isLoadingMore = false

                        empty_view.visibility = if (list.size > 0) View.GONE else View.VISIBLE
                    }

                })
    }

    override fun finish() {
        EventBus.getDefault().unregister(this@FinanceActivity)
        super.finish()
    }

    @Subscribe
    fun onMessageEvent(event: ReportMessageEvent) {
        when (event.type) {
            "财务录入" -> {
                pageNum = 1
                swipe_refresh.isRefreshing = true
                getData(pageNum)
            }
        }
    }
}
