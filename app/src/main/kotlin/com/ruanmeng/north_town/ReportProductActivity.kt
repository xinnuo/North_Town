package com.ruanmeng.north_town

import android.os.Bundle
import com.lzg.extend.BaseResponse
import com.lzg.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.addItems
import com.ruanmeng.base.getString
import com.ruanmeng.fragment.OnFragmentItemSelectListener
import com.ruanmeng.fragment.ProductFragment
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.ProductModel
import com.ruanmeng.model.ReportMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import org.greenrobot.eventbus.EventBus
import java.util.ArrayList

class ReportProductActivity : BaseActivity(), OnFragmentItemSelectListener {

    private val list_product = ArrayList<CommonData>()
    private val list_year = ArrayList<CommonData>()
    private val list_rate = ArrayList<CommonData>()

    private lateinit var first: ProductFragment
    private lateinit var second: ProductFragment

    private var productId = ""
    private var productName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_product)
        init_title("选择产品")

        supportFragmentManager.addOnBackStackChangedListener {
            tvTitle.text = when (supportFragmentManager.backStackEntryCount) {
                0 -> "选择产品"
                else -> "选择投资期限"
            }
        }

        getData()
    }

    override fun onitemSelected(type: String, id: String, name: String) {
        when (type) {
            "产品" -> {
                productId = id
                productName = name
                getYears(id)
            }
            "期限" -> {
                val items = ArrayList<CommonData>()
                list_rate.mapTo(items) {
                    val rates = it.rate.split(",")
                    val mYears = it.years.split(",")

                    CommonData().apply {
                        min = it.min
                        max = it.max
                        years = name
                        rate = rates[mYears.indexOf(name)]
                    }
                }

                EventBus.getDefault().post(ReportMessageEvent(
                        productId,
                        "$productName(${name}年)",
                        "产品",
                        items))

                ActivityStack.screenManager.popActivities(this@ReportProductActivity::class.java)
            }
        }
    }

    override fun getData() {
        OkGo.post<BaseResponse<ArrayList<CommonData>>>(BaseHttp.product_list)
                .tag(this@ReportProductActivity)
                .headers("token", getString("token"))
                .execute(object : JacksonDialogCallback<BaseResponse<ArrayList<CommonData>>>(baseContext, true) {

                    override fun onSuccess(response: Response<BaseResponse<ArrayList<CommonData>>>) {
                        list_product.apply {
                            clear()
                            addItems(response.body().`object`)
                        }

                        if (list_product.isNotEmpty()) {

                            first = ProductFragment()
                            first.arguments = Bundle().apply { putSerializable("list", list_product) }

                            supportFragmentManager
                                    .beginTransaction()
                                    .add(R.id.product_container, first)
                                    .commit()
                        }
                    }

                })
    }

    private fun getYears(id: String) {
        OkGo.post<BaseResponse<ProductModel>>(BaseHttp.get_product)
                .tag(this@ReportProductActivity)
                .headers("token", getString("token"))
                .params("productId", id)
                .execute(object : JacksonDialogCallback<BaseResponse<ProductModel>>(baseContext, true) {

                    override fun onSuccess(response: Response<BaseResponse<ProductModel>>) {

                        list_rate.clear()
                        list_rate.addItems(response.body().`object`.rateList)
                        val minYears = list_rate.first().years.split(",")
                        minYears.mapTo(list_year.apply { clear() }) { CommonData().apply { productName = "${it}年" } }

                        if (list_year.isNotEmpty()) {

                            second = ProductFragment()
                            second.arguments = Bundle().apply {
                                putSerializable("list", list_year)
                                putBoolean("isArrow", true)
                            }

                            supportFragmentManager.beginTransaction()
                                    .setCustomAnimations(
                                            R.anim.push_left_in,
                                            R.anim.push_left_out,
                                            R.anim.push_right_in,
                                            R.anim.push_right_out)
                                    /**
                                     * .add(R.id.job_container, second) 当前页面有动画效果，上个页面没有
                                     * .replace(R.id.job_container, second) 在点击返回按钮的时候会
                                     * 重新 onstart 和 onresume，可以更新页面，而且上个页面返回有动画效果
                                     */
                                    .replace(R.id.product_container, second)
                                    .addToBackStack(null)
                                    .commit()
                        }
                    }

                })
    }
}
