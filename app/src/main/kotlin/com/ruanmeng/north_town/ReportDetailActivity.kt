package com.ruanmeng.north_town

import android.os.Bundle
import android.view.View
import com.lzg.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getString
import com.ruanmeng.base.setImageURL
import com.ruanmeng.share.BaseHttp
import kotlinx.android.synthetic.main.activity_report_detail.*
import org.json.JSONObject

class ReportDetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_detail)
        init_title("客户详情")

        getData()
    }

    override fun init_title() {
        super.init_title()
        val isData = intent.getBooleanExtra("isData", false)
        if (isData) {
            report_input.text = "查看客户投资"

            if (intent.getBooleanExtra("isNew", false))
                report_input.visibility = View.GONE
        }

        report_input.setOnClickListener {
            when (isData) {
            //客户资料
                true -> startActivity(intent.apply { setClass(baseContext, DataCheckActivity::class.java) })
            //客户报备
                false -> startActivity(intent.apply { setClass(baseContext, ReportSelectActivity::class.java) })
            }
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

                        report_img.setImageURL(BaseHttp.baseImg + obj.optString("userhead"))
                        report_name.text = obj.optString("userName", "姓名")
                        report_tel.text = obj.optString("telephone", "电话")
                        report_idcard.setRightString(obj.optString("cardNo"))
                        report_owner.setRightString(when (obj.opt("isOwner")) {
                            "1" -> "是"
                            else -> "否"
                        })
                        report_house.text = obj.optString("villageName")
                        report_type.setRightString(obj.optString("villageTypeName"))
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
}
