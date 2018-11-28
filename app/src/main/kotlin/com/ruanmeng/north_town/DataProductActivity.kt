package com.ruanmeng.north_town

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.lzg.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getString
import com.ruanmeng.base.startActivityEx
import com.ruanmeng.share.BaseHttp
import kotlinx.android.synthetic.main.activity_data_product.*
import org.json.JSONObject
import java.text.DecimalFormat

class DataProductActivity : BaseActivity() {

    private var productId = ""
    private var productName = ""
    private var previousPurchaseId = ""

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

            startActivityEx<ManageDetailActivity>(
                    "title" to productName,
                    "productId" to productId)
        }

        tvRight.setOnClickListener {
            if (previousPurchaseId.isEmpty()) return@setOnClickListener
            startActivityEx<DataProductActivity>("purchaseId" to previousPurchaseId)
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
                        previousPurchaseId = obj.optString("previousPurchaseId")
                        if (previousPurchaseId.isNotEmpty()) tvRight.text = "历史订单"

                        product_type.text = productName
                        product_customer.setRightString(obj.optString("userName"))
                        product_tel.setRightString(obj.optString("telephone"))
                        product_idcard.setRightString(obj.optString("cardNo"))
                        product_name.setRightString(obj.optString("productName"))
                        when (obj.optString("investType")) {
                            "1" -> {
                                product_way.setRightString("转投")
                                product_trans.visibility = View.VISIBLE
                                product_trans.setLeftString("转投金额(元)")

                                val stock = obj.optString("stock", "0")
                                val strAmount = DecimalFormat(",##0.##").format(stock.toDouble())
                                product_trans.setRightString(strAmount)
                            }
                            "2" -> {
                                product_way.setRightString("续投")
                                product_trans.visibility = View.VISIBLE
                                product_trans.setLeftString("续投金额(元)")

                                val stock = obj.optString("stock", "0")
                                val strAmount = DecimalFormat(",##0.##").format(stock.toDouble())
                                product_trans.setRightString(strAmount)
                            }
                            "3" -> product_way.setRightString("新增")
                        }
                        product_year.text = obj.optString("years", "0")
                        product_start.setRightString(obj.optString("beginDate"))
                        product_end.setRightString(obj.optString("endDate"))

                        val amount = obj.optString("amount", "0")
                        product_money.text = DecimalFormat(",##0.##").format(amount.toDouble() / 10000.0)

                        val rate = obj.optString("rate", "0")
                        product_range.text = "${DecimalFormat("0.##").format(rate.toDouble())}%"

                        val profit = obj.optString("profit", "0")
                        product_income.setRightString("+${DecimalFormat("0.##").format(profit.toDouble())}(元)")

                        val muchYears = obj.optString("muchYears", "1")
                        product_count.setRightString("第${muchYears}年")
                    }

                })
    }
}
