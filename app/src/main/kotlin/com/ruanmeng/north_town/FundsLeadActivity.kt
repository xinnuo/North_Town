package com.ruanmeng.north_town

import android.os.Bundle
import android.view.View
import com.lzg.extend.BaseResponse
import com.lzg.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.makeramen.roundedimageview.RoundedImageView
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.CommonModel
import com.ruanmeng.share.BaseHttp
import kotlinx.android.synthetic.main.activity_funds_lead.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter
import java.text.DecimalFormat

class FundsLeadActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_funds_lead)
        init_title("老带新人员统计")

        swipe_refresh.isRefreshing = true
        getData(pageNum)
    }

    override fun init_title() {
        super.init_title()
        empty_hint.text = "暂无相关新人信息！"
        swipe_refresh.refresh { getData(1) }
        recycle_list.load_Linear(baseContext, swipe_refresh) {
            if (!isLoadingMore) {
                isLoadingMore = true
                getData(pageNum)
            }
        }

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_lead_list) { data, injector ->
                    injector.text(R.id.item_lead_name, data.userName)
                            .text(R.id.item_lead_jie, data.introducerInfoName)
                            .text(R.id.item_lead_total, "${DecimalFormat(",##0.##").format(data.sumAll.toDouble() / 10000.0)}万")
                            .text(R.id.item_lead_get, "${DecimalFormat(",##0.##").format(data.profitAll.toDouble() / 10000.0)}万")

                            .visibility(R.id.item_lead_divider1, if (list.indexOf(data) == list.size - 1) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_lead_divider2, if (list.indexOf(data) != list.size - 1) View.GONE else View.VISIBLE)

                            .with<RoundedImageView>(R.id.item_lead_img) { view ->
                                view.setImageURL(BaseHttp.baseImg + data.userhead, R.mipmap.default_user)
                            }

                            .clicked(R.id.item_lead) {
                                startActivityEx<FundsProductActivity>(
                                        "accountInfoId" to data.accountInfoId,
                                        "userName" to data.userName,
                                        "userhead" to data.userhead,
                                        "introducerInfoName" to data.introducerInfoName,
                                        "sumAll" to "${DecimalFormat(",##0.##").format(data.sumAll.toDouble() / 10000.0)}万",
                                        "profitAll" to "${DecimalFormat(",##0.##").format(data.profitAll.toDouble() / 10000.0)}万")
                            }
                }
                .attachTo(recycle_list)
    }

    override fun getData(pindex: Int) {
        OkGo.post<BaseResponse<CommonModel>>(BaseHttp.introducer_list)
                .tag(this@FundsLeadActivity)
                .headers("token", getString("token"))
                .params("page", pindex)
                .execute(object : JacksonDialogCallback<BaseResponse<CommonModel>>(baseContext) {

                    override fun onSuccess(response: Response<BaseResponse<CommonModel>>) {

                        list.apply {
                            if (pindex == 1) {
                                clear()
                                pageNum = pindex
                            }
                            addItems(response.body().`object`.accountInfoList)
                            if (count(response.body().`object`.accountInfoList) > 0) pageNum++
                        }
                        mAdapter.updateData(list)


                        val profit = response.body().`object`.introducerProfit
                        funds_get.text = DecimalFormat(",##0.##").format(profit.toDouble())
                        funds_count.text = response.body().`object`.accountInfoCount
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
