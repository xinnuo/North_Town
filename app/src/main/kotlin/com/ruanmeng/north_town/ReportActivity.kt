package com.ruanmeng.north_town

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
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import kotlinx.android.synthetic.main.activity_report.*
import kotlinx.android.synthetic.main.layout_empty_add.*
import net.idik.lib.slimadapter.SlimAdapter

class ReportActivity : BaseActivity() {

    private val list = ArrayList<Any>()
    private var keyWord = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)
        setToolbarVisibility(false)
        init_title()

        swipe_refresh.isRefreshing = true
        getData(pageNum)
    }

    @Suppress("DEPRECATION")
    override fun init_title() {
        swipe_refresh.refresh { getData(1) }
        recycle_list.load_Linear(baseContext, swipe_refresh) {
            if (!isLoadingMore) {
                isLoadingMore = true
                getData(pageNum)
            }
        }

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_report_list) { data, injector ->
                    injector.text(R.id.item_report_name, getColorText(data.userName, keyWord))
                            .text(R.id.item_report_phone, "手机 ${data.telephone}")
                            .text(R.id.item_report_idcard, "身份证号 " + getColorText(data.cardNo, keyWord))

                            .with<RoundedImageView>(R.id.item_report_img) { view ->
                                Glide.with(baseContext)
                                        .load(BaseHttp.baseImg + data.userhead)
                                        .apply(RequestOptions
                                                .centerCropTransform()
                                                .placeholder(R.mipmap.default_user)
                                                .error(R.mipmap.default_user)
                                                .dontAnimate())
                                        .into(view)
                            }

                            .clicked(R.id.item_report) {
                                startActivity(ReportDetailActivity::class.java)
                            }
                }
                .attachTo(recycle_list)
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.search_cancel -> ActivityStack.screenManager.popActivities(this@ReportActivity::class.java)
            R.id.empty_add -> startActivity(ReportAddActivity::class.java)
        }
    }

    override fun getData(pindex: Int) {
        OkGo.post<BaseResponse<ArrayList<CommonData>>>(BaseHttp.customer_list)
                .tag(this@ReportActivity)
                .headers("token", getString("token"))
                .params("search", keyWord)
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

                        empty_view.visibility = if (list.size == 0) View.VISIBLE else View.GONE
                    }

                })
    }
}
