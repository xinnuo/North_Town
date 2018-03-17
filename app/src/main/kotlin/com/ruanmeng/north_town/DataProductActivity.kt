package com.ruanmeng.north_town

import android.annotation.SuppressLint
import android.os.Bundle
import com.lzg.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getString
import com.ruanmeng.share.BaseHttp
import kotlinx.android.synthetic.main.activity_data_product.*
import org.json.JSONObject
import java.text.DecimalFormat

class DataProductActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_product)
        init_title("订单详情")

        getData()
    }

    override fun getData() {
        OkGo.post<String>(BaseHttp.customer_purchase_details)
                .tag(this@DataProductActivity)
                .headers("token", getString("token"))
                .params("purchaseId", intent.getStringExtra("purchaseId"))
                .execute(object : StringDialogCallback(baseContext) {

                    @SuppressLint("SetTextI18n")
                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        val obj = JSONObject(response.body()).getJSONObject("object")

                        product_type.text = obj.getString("productName")
                        product_name.setRightString(obj.getString("productName"))
                        product_year.text = obj.getString("years") ?: "0"
                        product_start.setRightString(obj.getString("beginDate"))
                        product_end.setRightString(obj.getString("endDate"))

                        val amount = obj?.getString("amount") ?: "0"
                        product_money.text = DecimalFormat(",##0.##").format(amount.toInt() / 10000.0)

                        val rate = obj?.getString("rate") ?: "0"
                        product_range.text = "${DecimalFormat("0.##").format(rate.toDouble())}%"

                        val profit = obj?.getString("profit") ?: "0"
                        product_income.setRightString("+${DecimalFormat("0.##").format(profit.toDouble())}(元)")
                    }

                })
    }
}
