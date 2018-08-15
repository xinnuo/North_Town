package com.ruanmeng.north_town

import android.os.Bundle
import android.view.View
import com.lzg.extend.BaseResponse
import com.lzg.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.share.BaseHttp
import kotlinx.android.synthetic.main.activity_funds_product.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter

class FundsProductActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_funds_product)
        init_title("老带新明细")

        swipe_refresh.isRefreshing = true
        getData(pageNum)
    }

    override fun init_title() {
        super.init_title()
        lead_name.text = intent.getStringExtra("userName")
        lead_jie.text = intent.getStringExtra("introducerInfoName")
        lead_total.text = intent.getStringExtra("sumAll")
        lead_get.text = intent.getStringExtra("profitAll")
        lead_img.setImageURL(BaseHttp.baseImg + intent.getStringExtra("userhead"))

        empty_hint.text = "暂无相关产品信息！"
        swipe_refresh.refresh { getData(1) }
        recycle_list.load_Linear(baseContext, swipe_refresh) {
            if (!isLoadingMore) {
                isLoadingMore = true
                getData(pageNum)
            }
        }

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_detail_list) { data, injector ->
                    injector.text(R.id.item_detail_name, data.productName)
                            .text(R.id.item_detail_time, data.createDate)
                            .text(R.id.item_detail_money, data.introducerProfit)

                            .visibility(R.id.item_detail_divider1, if (list.indexOf(data) == list.size - 1) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_detail_divider2, if (list.indexOf(data) != list.size - 1) View.GONE else View.VISIBLE)

                            .clicked(R.id.item_detail) {
                                startActivityEx<FundsDetailActivity>(
                                        "purchaseId" to data.purchaseId,
                                        "isLead" to true)
                            }
                }
                .attachTo(recycle_list)
    }

    override fun getData(pindex: Int) {
        OkGo.post<BaseResponse<ArrayList<CommonData>>>(BaseHttp.my_purchase_introducer_list)
                .apply {
                    tag(this@FundsProductActivity)
                    headers("token", getString("token"))
                    params("accountInfoId", intent.getStringExtra("accountInfoId"))
                    params("page", pindex)
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
                        mAdapter.updateData(list)
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
