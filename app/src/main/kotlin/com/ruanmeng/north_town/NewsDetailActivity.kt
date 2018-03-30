package com.ruanmeng.north_town

import android.os.Bundle
import com.lzg.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getString
import com.ruanmeng.base.setImageURL
import com.ruanmeng.share.BaseHttp
import kotlinx.android.synthetic.main.activity_news_detail.*
import org.json.JSONObject

class NewsDetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_detail)
        init_title("客户详情", "添加订单")

        getData()
    }

    @Suppress("DEPRECATION")
    override fun init_title() {
        super.init_title()
        val drawable = resources.getDrawable(R.mipmap.nav_add)
        // 这一步必须要做,否则不会显示
        drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
        tvRight.setCompoundDrawables(drawable, null, null, null)

        tvRight.setOnClickListener {
            intent.setClass(baseContext, ReportOrderActivity::class.java)
            startActivity(intent)
        }
        news_look.setOnClickListener {
            intent.setClass(baseContext, DataCheckActivity::class.java)
            startActivity(intent)
        }
    }

    override fun getData() {
        OkGo.post<String>(BaseHttp.customer_details)
                .tag(this@NewsDetailActivity)
                .headers("token", getString("token"))
                .params("accountInfoId", intent.getStringExtra("accountInfoId"))
                .execute(object : StringDialogCallback(baseContext) {

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        val obj = JSONObject(response.body()).getJSONObject("object")

                        news_img.setImageURL(BaseHttp.baseImg + obj.optString("userhead"))
                        news_name.text = obj.optString("userName", "姓名")
                        news_tel.text = obj.optString("telephone", "电话")
                        news_idcard.setRightString(obj.optString("cardNo"))
                        news_birthday.setRightString(obj.optString("birth"))
                        news_marry.setRightString(when (obj.opt("married")) {
                            "0" -> "未婚"
                            "1" -> "已婚"
                            else -> ""
                        })
                        news_work.setRightString(obj.optString("industryName"))
                        news_get.setRightString(obj.optString("income"))
                        news_owner.setRightString(when (obj.opt("isOwner")) {
                            "0" -> "否"
                            "1" -> "是"
                            else -> ""
                        })
                        news_memo.text = obj.optString("remark")
                    }

                })
    }
}
