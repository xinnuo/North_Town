package com.ruanmeng.north_town

import android.os.Bundle
import android.view.View
import com.lzg.extend.BaseResponse
import com.lzg.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.ReportMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import kotlinx.android.synthetic.main.activity_report_like.*
import net.idik.lib.slimadapter.SlimAdapter
import org.greenrobot.eventbus.EventBus
import java.util.ArrayList

class ReportLikeActivity : BaseActivity() {

    private val list = ArrayList<CommonData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_like)
        init_title("选择偏好", "确定")

        getData()
    }

    override fun init_title() {
        super.init_title()
        recycle_list.load_Linear(baseContext)

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_like_list) { data, injector ->
                    injector.text(R.id.item_like_name, data.preferenceName)
                            .checked(R.id.item_like_check, data.isChecked)

                            .visibility(R.id.item_like_divider1, if (list.indexOf(data) == list.size - 1) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_like_divider2, if (list.indexOf(data) != list.size - 1) View.GONE else View.VISIBLE)

                            .clicked(R.id.item_like) {
                                data.isChecked = !data.isChecked
                                mAdapter.notifyDataSetChanged()
                            }
                }
                .attachTo(recycle_list)

        tvRight.setOnClickListener {
            var preferenceId = ""
            var preferenceName = ""

            list.filter { it.isChecked }.forEach {
                preferenceId += it.preferenceId + ","
                preferenceName += it.preferenceName + ","
            }

            EventBus.getDefault().post(ReportMessageEvent(
                    if (preferenceId.isEmpty()) "" else preferenceId.trimEnd(','),
                    if (preferenceName.isEmpty()) "" else preferenceName.trimEnd(','),
                    "偏好"))

            ActivityStack.screenManager.popActivities(this@ReportLikeActivity::class.java)
        }
    }

    override fun getData() {
        OkGo.post<BaseResponse<ArrayList<CommonData>>>(BaseHttp.preference_list)
                .tag(this@ReportLikeActivity)
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
