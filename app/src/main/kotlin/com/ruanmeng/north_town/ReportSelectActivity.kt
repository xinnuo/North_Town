package com.ruanmeng.north_town

import android.os.Bundle
import android.view.View
import com.lzg.extend.BaseResponse
import com.lzg.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.addItems
import com.ruanmeng.base.getString
import com.ruanmeng.base.startActivityEx
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.CommonModel
import com.ruanmeng.share.BaseHttp
import java.util.ArrayList

class ReportSelectActivity : BaseActivity() {

    private val list = ArrayList<CommonData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_select)
        init_title("合同报备")

        getData()
    }

    override fun doClick(v: View) {
        super.doClick(v)
        val accountInfoId = intent.getStringExtra("accountInfoId")
        when (v.id) {
            R.id.report_partner -> {
                if (list.any { it.productName == "有限合伙人投资" }) {
                    val productId = list.first { it.productName == "有限合伙人投资" }.productId

                    startActivityEx<ReportPartnerActivity>(
                            "accountInfoId" to accountInfoId,
                            "productId" to productId,
                            "userName" to intent.getStringExtra("userName"))
                }
            }
            R.id.report_vip -> {
                if (list.any { it.productName == "会员卡投资" }) {
                    val productId = list.first { it.productName == "会员卡投资" }.productId

                    startActivityEx<ReportOrderActivity>(
                            "accountInfoId" to accountInfoId,
                            "productId" to productId,
                            "type" to "会员卡")
                }
            }
        }
    }

    override fun getData() {
        OkGo.post<BaseResponse<CommonModel>>(BaseHttp.product_list)
                .tag(this@ReportSelectActivity)
                .headers("token", getString("token"))
                .execute(object : JacksonDialogCallback<BaseResponse<CommonModel>>(baseContext, true) {

                    override fun onSuccess(response: Response<BaseResponse<CommonModel>>) {
                        list.apply {
                            clear()
                            addItems(response.body().`object`.bl)
                        }
                    }

                })
    }
}