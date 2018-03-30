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
import com.ruanmeng.share.BaseHttp
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter
import java.text.DecimalFormat

class DataCheckActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_check)
        init_title("客户订单")

        swipe_refresh.isRefreshing = true
        getData(pageNum)
    }

    override fun init_title() {
        super.init_title()
        empty_hint.text = "暂无相关订单信息！"
        swipe_refresh.refresh { getData(1) }
        recycle_list.load_Linear(baseContext, swipe_refresh) {
            if (!isLoadingMore) {
                isLoadingMore = true
                getData(pageNum)
            }
        }

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_check_list) { data, injector ->
                    injector.text(R.id.item_check_name, data.productName)
                            .text(R.id.item_check_limit, "${data.beginDate} ~ ${data.endDate}")
                            .text(R.id.item_check_range, "${data.rate}%")
                            .text(R.id.item_check_money, "${DecimalFormat(",##0.##").format(data.amount.toInt() / 10000.0)}万")
                            .text(R.id.item_check_long, "${data.years}年")
                            .visibility(R.id.item_check_divider, if (list.indexOf(data) == 0) View.VISIBLE else View.GONE)

                            .clicked(R.id.item_check) {
                                val intent = Intent(baseContext, DataProductActivity::class.java)
                                intent.putExtra("purchaseId", data.purchaseId)
                                startActivity(intent)
                            }
                }
                .attachTo(recycle_list)
    }

    override fun getData(pindex: Int) {
        OkGo.post<BaseResponse<ArrayList<CommonData>>>(BaseHttp.customer_purchase_list)
                .apply {
                    tag(this@DataCheckActivity)
                    headers("token", getString("token"))
                    params("accountInfoId", intent.getStringExtra("accountInfoId"))
                    params("page", pindex)

                    if (intent.getBooleanExtra("isClient", false))
                        params("managerInfoId", getString("token"))
                }
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
}
