package com.ruanmeng.north_town

import android.annotation.SuppressLint
import android.os.Bundle
import com.lzg.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getString
import com.ruanmeng.base.showToast
import com.ruanmeng.model.ReportMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import kotlinx.android.synthetic.main.activity_check_detail.*
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import java.text.DecimalFormat

class CheckDetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_detail)
        init_title("对账详情")

        getData()
    }

    override fun init_title() {
        super.init_title()

        check_pass.setOnClickListener {
            OkGo.post<String>(BaseHttp.purchase_auditing_sub)
                    .tag(this@CheckDetailActivity)
                    .headers("token", getString("token"))
                    .params("purchaseId", intent.getStringExtra("purchaseId"))
                    .execute(object : StringDialogCallback(baseContext) {

                        @SuppressLint("SetTextI18n")
                        override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                            showToast(msg)
                            EventBus.getDefault().post(ReportMessageEvent("", "", "审核通过"))
                            ActivityStack.screenManager.popActivities(this@CheckDetailActivity::class.java)
                        }

                    })
        }
    }

    override fun getData() {
        OkGo.post<String>(BaseHttp.customer_purchase_details)
                .tag(this@CheckDetailActivity)
                .headers("token", getString("token"))
                .params("purchaseId", intent.getStringExtra("purchaseId"))
                .execute(object : StringDialogCallback(baseContext) {

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        val obj = JSONObject(response.body()).getJSONObject("object")

                        check_name.setRightString(obj.optString("userName"))
                        check_tel.setRightString(obj.optString("telephone"))
                        check_addr.text = obj.optString("address")
                        check_fax.setRightString(obj.optString("fax"))
                        check_idcard.setRightString(obj.optString("cardNo"))

                        check_get.setRightString(obj.optString("paytypeName"))
                        check_bankname.setRightString(obj.optString("userName"))
                        check_bank.setRightString(obj.optString("bankName"))
                        check_account.setRightString(obj.optString("bankcard"))
                        check_money.setRightString(obj.optString("amount"))
                        check_code.setRightString(obj.optString("receiptNo"))
                        check_type.setRightString(obj.optString("receiptTypeName"))
                        check_yin.setRightString(obj.optString("cashierInfoName"))

                        check_product.setRightString(obj.optString("productName"))
                        check_year.setRightString("${obj.optString("years")}年")
                        val amout = "${DecimalFormat(",##0.##").format(obj.optString("amount", "0").toInt() / 10000.0)}万"
                        check_total.setRightString(amout)
                    }

                })
    }
}
