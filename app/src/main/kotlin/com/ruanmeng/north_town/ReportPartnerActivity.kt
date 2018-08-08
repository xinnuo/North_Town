package com.ruanmeng.north_town

import android.os.Bundle
import android.view.View
import com.lzg.extend.BaseResponse
import com.lzg.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.CommonModel
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter

class ReportPartnerActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_partner)
        init_title("有限合伙人企业")

        swipe_refresh.isRefreshing = true
        getData()
    }

    override fun init_title() {
        super.init_title()
        swipe_refresh.refresh { getData() }
        recycle_list.load_Linear(baseContext, swipe_refresh)

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_partner_list) { data, injector ->

                    val index = list.indexOf(data)

                    injector.text(R.id.item_partner_name, data.compName)
                            .text(R.id.item_partner_need, data.amount)
                            .text(R.id.item_partner_put, data.numPeople)
                            .text(R.id.item_partner_left, data.surplus)
                            .visibility(R.id.item_partner_divider, if (index == 0) View.VISIBLE else View.GONE)

                            .clicked(R.id.item_partner) {
                                val count = data.surplus.toInt()
                                if (count < 1) {
                                    showToast("投资人数已满，请选择其他合伙企业")
                                    return@clicked
                                }

                                startActivityEx<ReportOrderActivity>(
                                        "accountInfoId" to intent.getStringExtra("accountInfoId"),
                                        "productId" to intent.getStringExtra("productId"),
                                        "userName" to intent.getStringExtra("userName"),
                                        "companyId" to data.companyId,
                                        "companyName" to data.compName,
                                        "companySkipName" to data.commponyName,
                                        "legalMan" to data.legalMan,
                                        "amount" to data.amount,
                                        "put" to data.numPeople,
                                        "left" to data.surplus,
                                        "type" to "合伙人")

                                ActivityStack.screenManager.popActivities(this@ReportPartnerActivity::class.java)
                            }
                }
                .attachTo(recycle_list)
    }

    override fun getData() {
        OkGo.post<BaseResponse<CommonModel>>(BaseHttp.get_company_list)
                .tag(this@ReportPartnerActivity)
                .headers("token", getString("token"))
                .execute(object : JacksonDialogCallback<BaseResponse<CommonModel>>(baseContext) {

                    override fun onSuccess(response: Response<BaseResponse<CommonModel>>) {

                        list.clear()
                        list.addItems(response.body().`object`.companyList)
                        mAdapter.updateData(list)
                    }

                    override fun onFinish() {
                        super.onFinish()
                        swipe_refresh.isRefreshing = false
                    }

                })
    }
}
