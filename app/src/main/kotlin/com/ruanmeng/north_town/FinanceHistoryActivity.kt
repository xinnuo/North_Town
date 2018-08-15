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
import com.ruanmeng.utils.NumberHelper
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter
import java.text.DecimalFormat

class FinanceHistoryActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finance_history)
        init_title("历史审核")

        swipe_refresh.isRefreshing = true
        getData(pageNum)
    }

    override fun init_title() {
        super.init_title()
        empty_hint.text = "暂无相关历史审核信息！"
        swipe_refresh.refresh { getData(1) }
        recycle_list.load_Linear(baseContext, swipe_refresh) {
            if (!isLoadingMore) {
                isLoadingMore = true
                getData(pageNum)
            }
        }

        mAdapter = SlimAdapter.create()
                .apply {
                    when (intent.getStringExtra("type")) {
                        "1" -> register<CommonData>(R.layout.item_review_list) { data, injector ->
                            injector.text(R.id.item_review_name, data.userName)
                                    .text(R.id.item_review_time, data.createDate)
                                    .text(R.id.item_review_product, data.productName)
                                    .text(R.id.item_review_year, "${data.years}年")
                                    .text(R.id.item_review_money,  "${DecimalFormat(",##0.##").format(data.amount.toInt() / 10000.0)}万")
                                    .text(R.id.item_review_pay, data.paytypeName)
                                    .visibility(R.id.item_review_divider, if (list.indexOf(data) == 0) View.VISIBLE else View.GONE)

                                    .with<RoundedImageView>(R.id.item_review_img) { view ->
                                        view.setImageURL(BaseHttp.baseImg + data.userhead, R.mipmap.default_user)
                                    }

                                    .clicked(R.id.item_review) {
                                        startActivityEx<DataProductActivity>("purchaseId" to data.purchaseId)
                                    }
                        }
                        "2" -> register<CommonData>(R.layout.item_finance_list) { data, injector ->
                            injector.text(R.id.item_finance_name, data.productName)
                                    .text(R.id.item_finance_time, data.createDate)
                                    .text(R.id.item_finance_put, data.userName)
                                    .text(R.id.item_finance_manage, data.managerInfoName)
                                    .text(R.id.item_finance_money, NumberHelper.fmtMicrometer(data.amount))
                                    .visibility(R.id.item_finance_divider, if (list.indexOf(data) == 0) View.VISIBLE else View.GONE)

                                    .clicked(R.id.item_finance) {
                                        startActivityEx<DataProductActivity>("purchaseId" to data.purchaseId)
                                    }
                        }
                    }
                }
                .attachTo(recycle_list)
    }

    override fun getData(pindex: Int) {
        OkGo.post<BaseResponse<ArrayList<CommonData>>>(BaseHttp.purchase_past_auditing_list)
                .tag(this@FinanceHistoryActivity)
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
