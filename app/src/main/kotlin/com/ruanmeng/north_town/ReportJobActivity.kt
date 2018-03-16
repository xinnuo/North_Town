package com.ruanmeng.north_town

import android.os.Bundle
import com.lzg.extend.BaseResponse
import com.lzg.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.addItems
import com.ruanmeng.base.getString
import com.ruanmeng.fragment.JobFragment
import com.ruanmeng.fragment.OnFragmentItemSelectListener
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.ReportMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import org.greenrobot.eventbus.EventBus
import java.util.ArrayList

class ReportJobActivity : BaseActivity(), OnFragmentItemSelectListener {

    private var list_industry = ArrayList<CommonData>()
    private var list_business = ArrayList<CommonData>()

    private lateinit var first: JobFragment
    private lateinit var second: JobFragment

    private var industryId = ""
    private var industryName = ""
    private var businessId = ""
    private var businessName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_job)
        init_title("选择行业")

        supportFragmentManager.addOnBackStackChangedListener {
            tvTitle.text = when (supportFragmentManager.backStackEntryCount) {
                0 -> "选择行业"
                else -> "选择职业"
            }
        }

        getData()
    }

    override fun onitemSelected(type: String, id: String, name: String) {
        when (type) {
            "行业" -> {
                industryId = id
                industryName = name
                getBusiness(id)
            }
            "职业" -> {
                businessId = id
                businessName = name

                EventBus.getDefault().post(ReportMessageEvent(
                        businessId,
                        businessName,
                        "职业"))

                ActivityStack.screenManager.popActivities(this@ReportJobActivity::class.java)
            }
        }
    }

    override fun getData() {
        OkGo.post<BaseResponse<ArrayList<CommonData>>>(BaseHttp.industry_list)
                .tag(this@ReportJobActivity)
                .headers("token", getString("token"))
                .params("industryId", "")
                .execute(object : JacksonDialogCallback<BaseResponse<ArrayList<CommonData>>>(baseContext, true) {

                    override fun onSuccess(response: Response<BaseResponse<ArrayList<CommonData>>>) {
                        list_industry.apply {
                            clear()
                            addItems(response.body().`object`)
                        }

                        if (list_industry.isNotEmpty()) {

                            first = JobFragment()
                            first.arguments = Bundle().apply { putSerializable("list", list_industry) }

                            supportFragmentManager
                                    .beginTransaction()
                                    .add(R.id.job_container, first)
                                    .commit()
                        } else {
                            EventBus.getDefault().post(ReportMessageEvent(
                                    industryId,
                                    industryName,
                                    "职业"))

                            ActivityStack.screenManager.popActivities(this@ReportJobActivity::class.java)
                        }
                    }

                })
    }

    private fun getBusiness(id: String) {
        OkGo.post<BaseResponse<ArrayList<CommonData>>>(BaseHttp.industry_list)
                .tag(this@ReportJobActivity)
                .headers("token", getString("token"))
                .params("industryId", id)
                .execute(object : JacksonDialogCallback<BaseResponse<ArrayList<CommonData>>>(baseContext, true) {

                    override fun onSuccess(response: Response<BaseResponse<ArrayList<CommonData>>>) {
                        list_business.apply {
                            clear()
                            addItems(response.body().`object`)
                        }

                        if (list_business.isNotEmpty()) {

                            second = JobFragment()
                            second.arguments = Bundle().apply {
                                putSerializable("list", list_business)
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
                                    .replace(R.id.job_container, second)
                                    .addToBackStack(null)
                                    .commit()
                        }
                    }

                })
    }
}
