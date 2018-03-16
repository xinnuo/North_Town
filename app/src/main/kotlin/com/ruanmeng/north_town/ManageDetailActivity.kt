package com.ruanmeng.north_town

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import android.widget.TextView
import com.lzg.extend.BaseResponse
import com.lzg.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.addItems
import com.ruanmeng.base.getString
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.ProductModel
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.view.FullyLinearLayoutManager
import kotlinx.android.synthetic.main.activity_manage_detail.*
import net.idik.lib.slimadapter.SlimAdapter

class ManageDetailActivity : BaseActivity() {

    private val list = ArrayList<CommonData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_detail)
        init_title()

        getData()
    }

    @SuppressLint("SetTextI18n", "SetJavaScriptEnabled")
    override fun init_title() {
        super.init_title()
        tvTitle.text = "${intent.getStringExtra("title")}详情"

        manage_info.apply {
            //支持javascript
            settings.javaScriptEnabled = true
            //设置可以支持缩放
            settings.setSupportZoom(true)
            //自适应屏幕
            settings.loadWithOverviewMode = true
            settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
            isHorizontalScrollBarEnabled = false

            //设置出现缩放工具
            settings.builtInZoomControls = true
            settings.displayZoomControls = false
        }

        manage_list.apply {
            layoutManager = FullyLinearLayoutManager(baseContext)

            adapter = SlimAdapter.create()
                    .register<CommonData>(R.layout.item_manage_list) { data, injector ->
                        injector.visibility(R.id.item_manage_divider1, if (list.indexOf(data) == list.size - 1) View.GONE else View.VISIBLE)
                                .visibility(R.id.item_manage_divider2, if (list.indexOf(data) != list.size - 1) View.GONE else View.VISIBLE)

                                .with<TextView>(R.id.item_manage_title) { view ->
                                    val min = if (data.min.isNotEmpty()) data.min.toInt() else 0
                                    val max = if (data.max.isNotEmpty()) data.max.toInt() else 0
                                    if (min >= max) view.text = "${min}万(含)以上"
                                    else view.text = "${min}万~${max}万(不含)"
                                }

                                .with<TextView>(R.id.item_manage_right) { view ->
                                    val rates = data.rate.split(",")
                                    view.visibility = if (rates.size > 1) View.GONE else View.VISIBLE
                                    view.text = "利率 ${rates.first()}%"
                                }

                                .with<TextView>(R.id.item_manage_down) { view ->
                                    val rates = data.rate.split(",")
                                    val years = data.years.split(",")
                                    view.visibility = if (rates.size > 1) View.VISIBLE else View.GONE

                                    var hint = "利率"
                                    (0 until rates.size).forEach { hint += "  ${years[it]}年(${rates[it]}%)" }
                                    view.text = hint
                                }
                    }
                    .attachTo(this)
        }
    }

    override fun getData() {
        OkGo.post<BaseResponse<ProductModel>>(BaseHttp.get_product)
                .tag(this@ManageDetailActivity)
                .headers("token", getString("token"))
                .params("productId", intent.getStringExtra("productId"))
                .execute(object : JacksonDialogCallback<BaseResponse<ProductModel>>(baseContext, true) {

                    @SuppressLint("SetTextI18n")
                    override fun onSuccess(response: Response<BaseResponse<ProductModel>>) {
                        list.apply {
                            clear()
                            addItems(response.body().`object`.rateList)
                        }
                        (manage_list.adapter as SlimAdapter).updateData(list)

                        val data = response.body().`object`.product

                        val minYears = list.first().years.split(",")
                        val minRates = list.first().rate.split(",")
                        val maxRates = list.last().rate.split(",")

                        manage_percent.text = "${minRates.first()}%~${maxRates.last()}%"
                        manage_year.text = minYears.first()
                        manage_money.text = data.minAmount

                        val str = "<meta " +
                                "name=\"viewport\" " +
                                "content=\"width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no\">" +
                                "<style>" +
                                ".con{ width:100%; margin:0 auto; color:#fff; color:#666; padding:0.5em 0; overflow:hidden; display:block; font-size:0.92em; line-height:1.8em;}\n" +
                                ".con h1,h2,h3,h4,h5,h6{ font-size:1em; }\n " +
                                "img{ max-width: 100% !important; display:block; height:auto !important; }" +
                                "*{ max-width:100% !important; }\n" +
                                "</style>"
                        manage_info.loadDataWithBaseURL(
                                BaseHttp.baseImg,
                                "$str<div class=\"con\">${data.productRemark}</div>",
                                "text/html",
                                "utf-8",
                                "")
                    }

                })
    }

    override fun onResume() {
        super.onResume()
        manage_info.onResume()
    }

    override fun onPause() {
        super.onPause()
        manage_info.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        manage_info.destroy()
    }
}
