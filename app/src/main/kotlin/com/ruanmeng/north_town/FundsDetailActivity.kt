package com.ruanmeng.north_town

import android.annotation.SuppressLint
import android.os.Bundle
import com.lzg.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getString
import com.ruanmeng.share.BaseHttp
import kotlinx.android.synthetic.main.activity_funds_detail.*
import org.json.JSONObject
import java.text.DecimalFormat

class FundsDetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_funds_detail)
        init_title("订单详情")

        getData()
    }

    override fun init_title() {
        super.init_title()
        val isLead = intent.getBooleanExtra("isLead", false)
        if (isLead) funds_expand.expand()
    }

    override fun getData() {
        OkGo.post<String>(BaseHttp.customer_purchase_details)
                .tag(this@FundsDetailActivity)
                .headers("token", getString("token"))
                .params("purchaseId", intent.getStringExtra("purchaseId"))
                .execute(object : StringDialogCallback(baseContext) {

                    @SuppressLint("SetTextI18n")
                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        val obj = JSONObject(response.body()).getJSONObject("object")

                        val rate = obj.optString("rate", "0")
                        val productName = obj.optString("productName")
                        funds_product.text = "$productName(${DecimalFormat("0.##").format(rate.toDouble())}%)"

                        val amount = obj.optString("amount", "0")
                        funds_money.text = DecimalFormat(",##0.##").format(amount.toInt() / 10000.0)

                        funds_year.text = obj.optString("years", "0")
                        funds_name.setRightString(obj.optString("userName"))
                        funds_tel.setRightString(obj.optString("telephone"))
                        funds_idcard.setRightString(obj.optString("cardNo"))
                        funds_attribute.setRightString(obj.optString("investTypeName"))
                        funds_start.setRightString(obj.optString("beginDate"))
                        funds_end.setRightString(obj.optString("endDate"))

                        val profit = obj.optString("profit", "0")
                        funds_get.setRightString("￥ ${DecimalFormat("0.##").format(profit.toDouble())}")
                    }

                })
    }
}
