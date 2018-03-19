package com.ruanmeng.north_town

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.lzg.extend.BaseResponse
import com.lzg.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.model.CommissionModel
import com.ruanmeng.model.CommonData
import com.ruanmeng.share.BaseHttp
import kotlinx.android.synthetic.main.activity_funds_sale.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter

class FundsSaleActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_funds_sale)
        init_title("销售部佣金", "佣金规则")

        swipe_refresh.isRefreshing = true
        getData(pageNum)
    }

    override fun init_title() {
        super.init_title()
        empty_hint.text = "暂无相关佣金信息！"
        swipe_refresh.refresh { getData(1) }
        recycle_list.load_Linear(baseContext, swipe_refresh) {
            if (!isLoadingMore) {
                isLoadingMore = true
                getData(pageNum)
            }
        }

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_purse_list) { data, injector ->
                    injector.text(R.id.item_purse_name, data.userName)
                            .text(R.id.item_purse_product, data.productName)
                            .text(R.id.item_purse_date, data.createDate)
                            .text(R.id.item_purse_agent, data.managerInfoName)
                            .text(R.id.item_purse_yong, "￥${data.commission}")

                            .clicked(R.id.item_purse) {
                                val intent = Intent(baseContext, FundsDetailActivity::class.java)
                                intent.putExtra("purchaseId", data.purchaseId)
                                startActivity(intent)
                            }
                }
                .attachTo(recycle_list)

        tvRight.setOnClickListener {
            val intent = Intent(baseContext, WebActivity::class.java)
            intent.putExtra("title", "佣金规则")
            startActivity(intent)
        }
    }

    override fun getData(pindex: Int) {
        OkGo.post<BaseResponse<CommissionModel>>(BaseHttp.department_commission_list)
                .tag(this@FundsSaleActivity)
                .headers("token", getString("token"))
                .params("departmentId", "")
                .params("page", pindex)
                .execute(object : JacksonDialogCallback<BaseResponse<CommissionModel>>(baseContext) {

                    override fun onSuccess(response: Response<BaseResponse<CommissionModel>>) {

                        list.apply {
                            if (pindex == 1) {
                                clear()
                                pageNum = pindex
                            }
                            addItems(response.body().`object`.purchaseList)
                            if (count(response.body().`object`.purchaseList) > 0) pageNum++
                        }
                        mAdapter.updateData(list)

                        funds_total.text = response.body().`object`.commissionSum
                    }

                    override fun onFinish() {
                        super.onFinish()
                        swipe_refresh.isRefreshing = false
                        isLoadingMore = false

                        empty_view.visibility = if (list.size > 0) View.GONE else View.VISIBLE
                    }

                })
    }

    fun updateList() {
        swipe_refresh.isRefreshing = true

        if (list.size > 0) {
            funds_total.text = "0"

            list.clear()
            mAdapter.notifyDataSetChanged()
            empty_view.visibility = View.GONE
        }

        pageNum = 1
        getData(pageNum)
    }
}
