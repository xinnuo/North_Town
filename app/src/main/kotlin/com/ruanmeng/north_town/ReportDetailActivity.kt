package com.ruanmeng.north_town

import android.os.Bundle
import com.lzg.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.model.ReportMessageEvent
import com.ruanmeng.share.BaseHttp
import kotlinx.android.synthetic.main.activity_report_detail.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.json.JSONObject
import java.text.DecimalFormat

class ReportDetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_detail)
        init_title("客户详情")

        EventBus.getDefault().register(this@ReportDetailActivity)

        getData()
    }

    override fun init_title() {
        super.init_title()
        val accountInfoId = intent.getStringExtra("accountInfoId")
        when (intent.getStringExtra("type")) {
            "1" -> report_input.text = "录入合同"
            "2" -> report_input.text = "查看客户投资"
            "3" -> {
                if (getString("accountType") == "App_Staff")
                    changeTitle("客户详情", "添加订单")
                report_input.text = "查看客户订单"
            }
        }

        report_input.setOnClickListener {
            when (intent.getStringExtra("type")) {
                "1" -> startActivityEx<ReportSelectActivity>(
                        "accountInfoId" to accountInfoId,
                        "userName" to report_name.text.toString())
                "2" -> startActivityEx<DataCheckActivity>("accountInfoId" to accountInfoId)
                "3" -> startActivityEx<DataCheckActivity>("accountInfoId" to accountInfoId)
            }
        }

        tvRight.setOnClickListener {
            startActivityEx<ReportSelectActivity>(
                    "accountInfoId" to accountInfoId,
                    "userName" to report_name.text.toString())
        }
    }

    override fun getData() {
        OkGo.post<String>(BaseHttp.customer_details)
                .tag(this@ReportDetailActivity)
                .headers("token", getString("token"))
                .params("accountInfoId", intent.getStringExtra("accountInfoId"))
                .execute(object : StringDialogCallback(baseContext) {

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        val obj = JSONObject(response.body())
                                .optJSONObject("object")
                                .optJSONObject("accountInfo")
                                ?: JSONObject()

                        val total = obj.optString("aggregateInvestment").toNotDouble()
                        if (total > 0) report_total.setRightString(DecimalFormat("0.##").format(total / 10000.0) + "万")

                        report_img.setImageURL(BaseHttp.baseImg + obj.optString("userhead"))
                        report_name.text = obj.optString("userName", "姓名")
                        report_tel.text = obj.optString("telephone", "电话")
                        report_gender.setRightString(when (obj.optString("sex")) {
                            "0" -> "女"
                            else -> "男"
                        })
                        report_age.setRightString(obj.optString("age"))
                        report_birth.setRightString(obj.optString("birthday"))
                        report_idcard.setRightString(obj.optString("cardNo"))
                        report_owner.setRightString(when (obj.optString("isOwner")) {
                            "1" -> "是"
                            else -> "否"
                        })
                        report_house.text = obj.optString("villageName")
                        report_num.setRightString(obj.optString("houseNumber"))
                        report_up.setRightString(obj.optString("introducerName"))
                        report_relation.setRightString(obj.optString("relationshipName"))
                        report_like.text = obj.optString("preferences")
                        report_job.setRightString(obj.optString("industryName"))
                        report_unit.setRightString(obj.optString("unitName"))
                        report_memo.text = obj.optString("remark")
                    }

                })
    }

    override fun finish() {
        EventBus.getDefault().unregister(this@ReportDetailActivity)
        super.finish()
    }

    @Subscribe
    fun onMessageEvent(event: ReportMessageEvent) {
        when (event.type) {
            "添加订单" -> getData()
        }
    }
}
