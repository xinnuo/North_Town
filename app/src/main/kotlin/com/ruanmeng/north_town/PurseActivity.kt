package com.ruanmeng.north_town

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.lzg.extend.BaseResponse
import com.lzg.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.CommonModel
import com.ruanmeng.share.BaseHttp
import kotlinx.android.synthetic.main.activity_purse.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter

class PurseActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_purse)
        init_title("我的钱包")

        swipe_refresh.isRefreshing = true
        getData(pageNum)
    }

    override fun init_title() {
        super.init_title()
        empty_hint.text = "暂无相关交易信息！"

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
                            .text(R.id.item_purse_yong, "￥${if (data.amount.isEmpty()) "0" else data.amount}")

                            .clicked(R.id.item_purse) {
                                val intent = Intent(baseContext, FundsDetailActivity::class.java)
                                intent.putExtra("purchaseId", data.purchaseId)
                                startActivity(intent)
                            }
                }
                .attachTo(recycle_list)
    }

    override fun getData(pindex: Int) {
        OkGo.post<BaseResponse<CommonModel>>(BaseHttp.my_balance_staff)
                .tag(this@PurseActivity)
                .headers("token", getString("token"))
                .params("page", pindex)
                .execute(object : JacksonDialogCallback<BaseResponse<CommonModel>>(baseContext) {

                    @SuppressLint("SetTextI18n")
                    override fun onSuccess(response: Response<BaseResponse<CommonModel>>) {

                        list.apply {
                            if (pindex == 1) {
                                clear()
                                pageNum = pindex
                            }
                            addItems(response.body().`object`.balanceLogList)
                            if (count(response.body().`object`.balanceLogList) > 0) pageNum++
                        }
                        if (count(response.body().`object`.balanceLogList) > 0) mAdapter.updateData(list)

                        purse_total.text = response.body().`object`.balance
                        purse_withdraw.text = "￥" + response.body().`object`.withdrawDeposit
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
