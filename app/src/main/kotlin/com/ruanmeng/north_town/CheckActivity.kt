package com.ruanmeng.north_town

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.lzg.extend.BaseResponse
import com.lzg.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.makeramen.roundedimageview.RoundedImageView
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.ReportMessageEvent
import com.ruanmeng.share.BaseHttp
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.text.DecimalFormat

class CheckActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check)
        init_title("审核对账")

        EventBus.getDefault().register(this@CheckActivity)

        swipe_refresh.isRefreshing = true
        getData(pageNum)
    }

    override fun init_title() {
        super.init_title()
        empty_hint.text = "暂无相关对账信息！"
        swipe_refresh.refresh { getData(1) }
        recycle_list.load_Linear(baseContext, swipe_refresh) {
            if (!isLoadingMore) {
                isLoadingMore = true
                getData(pageNum)
            }
        }

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_review_list) { data, injector ->
                    injector.text(R.id.item_review_name, data.userName)
                            .text(R.id.item_review_time, data.createDate)
                            .text(R.id.item_review_product, data.productName)
                            .text(R.id.item_review_year, "${data.years}年")
                            .text(R.id.item_review_money,  "${DecimalFormat(",##0.##").format(data.amount.toInt() / 10000.0)}万")
                            .text(R.id.item_review_pay, data.paytypeName)
                            .visibility(R.id.item_review_divider, if (list.indexOf(data) == 0) View.VISIBLE else View.GONE)

                            .with<RoundedImageView>(R.id.item_review_img) { view ->
                                Glide.with(baseContext)
                                        .load(BaseHttp.baseImg + data.userhead)
                                        .apply(RequestOptions
                                                .centerCropTransform()
                                                .placeholder(R.mipmap.default_user)
                                                .error(R.mipmap.default_user)
                                                .dontAnimate())
                                        .into(view)
                            }

                            .clicked(R.id.item_review) {
                                val intent = Intent(baseContext, CheckDetailActivity::class.java)
                                intent.putExtra("purchaseId", data.purchaseId)
                                startActivity(intent)
                            }
                }
                .attachTo(recycle_list)
    }

    override fun getData(pindex: Int) {
        OkGo.post<BaseResponse<ArrayList<CommonData>>>(BaseHttp.purchase_auditing_list)
                .tag(this@CheckActivity)
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

                        empty_view.visibility = if (list.size > 0) View.GONE else View.VISIBLE
                    }

                })
    }

    override fun finish() {
        EventBus.getDefault().unregister(this@CheckActivity)
        super.finish()
    }

    @Subscribe
    fun onMessageEvent(event: ReportMessageEvent) {
        when (event.type) {
            "审核通过" -> {
                swipe_refresh.isRefreshing = true
                pageNum = 1
                getData(pageNum)
            }
        }
    }
}
