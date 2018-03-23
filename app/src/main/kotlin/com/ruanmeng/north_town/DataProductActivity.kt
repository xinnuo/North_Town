package com.ruanmeng.north_town

import android.annotation.SuppressLint
import android.content.Intent
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

    private var productId = ""
    private var productName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_product)
        init_title("订单详情")

        getData()
    }

    override fun init_title() {
        super.init_title()

        product_detail.setOnClickListener {
            if (productId.isEmpty()) return@setOnClickListener

            val intent = Intent(baseContext, ManageDetailActivity::class.java)
            intent.putExtra("title", productName)
            intent.putExtra("productId", productId)
            startActivity(intent)
        }
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

                        productId = obj.optString("productId")
                        productName = obj.optString("productName")

                        product_type.text = productName
                        product_name.setRightString(obj.optString("productName"))
                        product_year.text = obj.optString("years", "0")
                        product_start.setRightString(obj.optString("beginDate"))
                        product_end.setRightString(obj.optString("endDate"))

                        val amount = obj.optString("amount", "0")
                        product_money.text = DecimalFormat(",##0.##").format(amount.toInt() / 10000.0)

                        val rate = obj.optString("rate", "0")
                        product_range.text = "${DecimalFormat("0.##").format(rate.toDouble())}%"

                        val profit = obj.optString("profit", "0")
                        product_income.setRightString("+${DecimalFormat("0.##").format(profit.toDouble())}(元)")
                    }

                })
    }
}
