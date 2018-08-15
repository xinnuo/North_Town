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
import com.ruanmeng.share.BaseHttp
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter
import java.text.DecimalFormat

class DataHistoryActivity : BaseActivity() {

    private val list = ArrayList<Any>()
    private var accountInfoId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_history)
        init_title("历史投资")

        swipe_refresh.isRefreshing = true
        getData(pageNum)
    }

    override fun init_title() {
        super.init_title()
        accountInfoId = intent.getStringExtra("accountInfoId") ?: ""

        empty_hint.text = "暂无相关历史投资信息！"
        swipe_refresh.refresh { getData(1) }
        recycle_list.load_Linear(baseContext, swipe_refresh) {
            if (!isLoadingMore) {
                isLoadingMore = true
                getData(pageNum)
            }
        }

        mAdapter = SlimAdapter.create()
                .apply {
                    if (accountInfoId.isEmpty()) {
                        register<CommonData>(R.layout.item_history_list) { data, injector ->
                            @Suppress("DEPRECATION")
                            injector.text(R.id.item_history_name, data.userName)
                                    .text(R.id.item_history_phone, "产品名称：${data.productName}")
                                    .text(R.id.item_history_idcard, "投资周期：${data.beginDate} ~ ${data.endDate}")
                                    .text(R.id.item_history_limit, data.years)
                                    .text(R.id.item_history_num, data.amount)
                                    .text(R.id.item_history_type, when (data.investType) {
                                        "1" -> "转投"
                                        "2" -> "续投"
                                        "3" -> "新增"
                                        else -> ""
                                    })
                                    .textColor(R.id.item_history_type,
                                            resources.getColor(if (data.investType == "3") R.color.colorAccent else R.color.orange))
                                    .background(R.id.item_history_type,
                                            if (data.investType == "3") R.drawable.rec_bg_trans_stroke_red else R.drawable.rec_bg_trans_stroke_orange)

                                    .visibility(R.id.item_history_type, if (data.investType.isEmpty()) View.GONE else View.VISIBLE)
                                    .visibility(R.id.item_history_divider1, if (list.indexOf(data) == list.size - 1) View.GONE else View.VISIBLE)
                                    .visibility(R.id.item_history_divider2, if (list.indexOf(data) != list.size - 1) View.GONE else View.VISIBLE)

                                    .with<RoundedImageView>(R.id.item_history_img) { view ->
                                        view.setImageURL(BaseHttp.baseImg + data.userhead, R.mipmap.default_user)
                                    }

                                    .clicked(R.id.item_history) {
                                        startActivityEx<DataProductActivity>("purchaseId" to data.purchaseId)
                                    }
                        }
                    } else {
                        register<CommonData>(R.layout.item_check_list) { data, injector ->
                            injector.text(R.id.item_check_name, data.productName)
                                    .text(R.id.item_check_limit, "${data.beginDate} ~ ${data.endDate}")
                                    .text(R.id.item_check_range, "${data.rate}%")
                                    .text(R.id.item_check_money, "${DecimalFormat(",##0.##").format(data.amount.toInt() / 10000.0)}万")
                                    .text(R.id.item_check_long, "${data.years}年")
                                    .visibility(R.id.item_check_divider, if (list.indexOf(data) == 0) View.VISIBLE else View.GONE)

                                    .clicked(R.id.item_check) {
                                        startActivityEx<DataProductActivity>("purchaseId" to data.purchaseId)
                                    }
                        }
                    }
                }
                .attachTo(recycle_list)
    }

    override fun getData(pindex: Int) {
        OkGo.post<BaseResponse<ArrayList<CommonData>>>(BaseHttp.customer_investment_past_record)
                .tag(this@DataHistoryActivity)
                .headers("token", getString("token"))
                .params("accountInfoId", accountInfoId)
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
                        mAdapter.updateData(list)
                    }

                    override fun onFinish() {
                        super.onFinish()
                        swipe_refresh.isRefreshing = false
                        isLoadingMore = false

                        empty_view.visibility = if (list.isNotEmpty()) View.GONE else View.VISIBLE
                    }

                })
    }
}
