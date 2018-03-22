package com.ruanmeng.north_town

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.lzg.extend.BaseResponse
import com.lzg.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.addItems
import com.ruanmeng.base.getString
import com.ruanmeng.base.load_Linear
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.ReportMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import kotlinx.android.synthetic.main.activity_report_unit.*
import net.idik.lib.slimadapter.SlimAdapter
import org.greenrobot.eventbus.EventBus
import java.util.ArrayList

class ReportUnitActivity : BaseActivity() {

    private val list = ArrayList<CommonData>()
    private var isType = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_unit)
        init_title()

        getData()
    }

    @SuppressLint("SetTextI18n")
    override fun init_title() {
        super.init_title()
        val title = intent.getStringExtra("title")
        tvTitle.text = "选择$title"

        unit_list.load_Linear(baseContext)

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_job_list) { data, injector ->
                    injector.gone(R.id.item_job_arrow)
                            .text(R.id.item_job_name, when (intent.getStringExtra("title")) {
                                "住宅类型" -> data.villageTypeName
                                "工作单位" -> data.unitTypeName
                                "客户关系" -> data.relationshipName
                                else -> ""
                            })

                            .visibility(R.id.item_job_divider1, if (list.indexOf(data) == list.size - 1) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_job_divider2, if (list.indexOf(data) != list.size - 1) View.GONE else View.VISIBLE)

                            .clicked(R.id.item_job) {
                                when (intent.getStringExtra("title")) {
                                    "住宅类型" -> EventBus.getDefault().post(ReportMessageEvent(
                                            data.villageTypeId,
                                            data.villageTypeName,
                                            "类型"))
                                    "工作单位" -> EventBus.getDefault().post(ReportMessageEvent(
                                            data.unitTypeId,
                                            data.unitTypeName,
                                            "工作单位"))
                                    "客户关系" -> EventBus.getDefault().post(ReportMessageEvent(
                                            data.relationshipId,
                                            data.relationshipName,
                                            "关系"))
                                }

                                ActivityStack.screenManager.popActivities(this@ReportUnitActivity::class.java)
                            }
                }
                .attachTo(unit_list)
    }

    override fun getData() {
        OkGo.post<BaseResponse<ArrayList<CommonData>>>(when (intent.getStringExtra("title")) {
            "住宅类型" -> BaseHttp.villagetype_list
            "工作单位" -> BaseHttp.unittype_list
            "客户关系" -> BaseHttp.relationship_list
            else -> ""
        })
                .tag(this@ReportUnitActivity)
                .headers("token", getString("token"))
                .execute(object : JacksonDialogCallback<BaseResponse<ArrayList<CommonData>>>(baseContext, true) {

                    override fun onSuccess(response: Response<BaseResponse<ArrayList<CommonData>>>) {
                        list.apply {
                            clear()
                            addItems(response.body().`object`)
                        }
                        mAdapter.updateData(list)
                    }

                })
    }
}
