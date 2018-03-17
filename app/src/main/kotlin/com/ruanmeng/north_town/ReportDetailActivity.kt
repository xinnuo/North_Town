package com.ruanmeng.north_town

import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.lzg.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getString
import com.ruanmeng.base.startActivity
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
        if (isData) report_input.text = "查看客户投资"

        report_input.setOnClickListener {
            when (isData) {
                true -> {
                    intent.setClass(baseContext, DataCheckActivity::class.java)
                    startActivity(intent)
                }
                false -> startActivity(ReportOrderActivity::class.java)
            }
        }
    }

    override fun getData() {
        OkGo.post<String>(BaseHttp.other_userinfo)
                .tag(this@ReportDetailActivity)
                .headers("token", getString("token"))
                .params("accountInfoId", intent.getStringExtra("accountInfoId"))
                .execute(object : StringDialogCallback(baseContext) {

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        val obj = JSONObject(response.body()).getJSONObject("object")

                        Glide.with(baseContext)
                                .load(BaseHttp.baseImg + obj.optString("userhead"))
                                .apply(RequestOptions
                                        .centerCropTransform()
                                        .placeholder(R.mipmap.default_user)
                                        .error(R.mipmap.default_user))
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .into(report_img)

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
