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
                true -> {
                    intent.setClass(baseContext, DataCheckActivity::class.java)
                    startActivity(intent)
                }
                false -> {
                    intent.setClass(baseContext, ReportOrderActivity::class.java)
                    startActivity(intent)
                }
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

                        val obj = JSONObject(response.body()).getJSONObject("object")

                        report_img.setImageURL(BaseHttp.baseImg + obj.optString("userhead"))
                        report_name.text = obj.optString("userName", "姓名")
                        report_tel.text = obj.optString("telephone", "电话")
                        report_idcard.setRightString(obj.optString("cardNo"))
                        report_owner.setRightString(when (obj.opt("isOwner")) {
                            "1" -> "是"
                            else -> "否"
                        })
                        report_house.text = obj.optString("villageName")
                        report_num.setRightString(obj.optString("houseNumber"))
                        report_memo.text = obj.optString("remark")
                    }

                })
    }
}
