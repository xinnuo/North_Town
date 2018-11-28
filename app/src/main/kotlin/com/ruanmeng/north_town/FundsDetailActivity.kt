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
import kotlinx.android.synthetic.main.activity_funds_detail.*
import org.json.JSONObject
import java.text.DecimalFormat

class FundsDetailActivity : BaseActivity() {

    private var previousPurchaseId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_funds_detail)
        init_title("订单详情")

        getData()
    }

    override fun init_title() {
        super.init_title()
        val isLead = intent.getBooleanExtra("isLead", false)
        if (isLead) {
            funds_expand.expand()
            getUpData()
        }

        tvRight.setOnClickListener {
            startActivityEx<FundsDetailActivity>("purchaseId" to previousPurchaseId)
        }
    }

    override fun getData() {
        OkGo.post<String>(BaseHttp.customer_purchase_details)
                .tag(this@FundsDetailActivity)
                .headers("token", getString("token"))
                .params("purchaseId", intent.getStringExtra("purchaseId"))
                .execute(object : StringDialogCallback(baseContext) {

                    @SuppressLint("SetTextI18n")
                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        val obj = JSONObject(response.body())
                                .optJSONObject("object") ?: JSONObject()

                        previousPurchaseId = obj.optString("previousPurchaseId")
                        if (previousPurchaseId.isNotEmpty()) tvRight.text = "历史订单"

                        val rate = obj.optString("rate", "0")
                        val productName = obj.optString("productName")
                        funds_product.text = "$productName(${DecimalFormat("0.##").format(rate.toDouble())}%)"

                        val amount = obj.optString("amount", "0")
                        funds_money.text = DecimalFormat(",##0.##").format(amount.toDouble() / 10000.0)

                        funds_year.text = obj.optString("years", "0")
                        funds_name.setRightString(obj.optString("userName"))
                        funds_tel.setRightString(obj.optString("telephone"))
                        funds_idcard.setRightString(obj.optString("cardNo"))
                        when (obj.optString("investType")) {
                            "1" -> {
                                funds_attribute.setRightString("转投")
                                funds_trans.visibility = View.VISIBLE
                                funds_trans.setLeftString("转投金额(元)")

                                val stock = obj.optString("stock", "0")
                                val strAmount = DecimalFormat(",##0.##").format(stock.toDouble())
                                funds_trans.setRightString(strAmount)
                            }
                            "2" -> {
                                funds_attribute.setRightString("续投")
                                funds_trans.visibility = View.VISIBLE
                                funds_trans.setLeftString("续投金额(元)")

                                val stock = obj.optString("stock", "0")
                                val strAmount = DecimalFormat(",##0.##").format(stock.toDouble())
                                funds_trans.setRightString(strAmount)
                            }
                            "3" -> funds_attribute.setRightString("新增")
                        }
                        funds_start.setRightString(obj.optString("beginDate"))
                        funds_end.setRightString(obj.optString("endDate"))

                        val profit = obj.optString("profit", "0")
                        funds_get.setRightString("￥ ${DecimalFormat(",##0.##").format(profit.toDouble())}")

                        val muchYears = obj.optString("muchYears", "1")
                        funds_count.setRightString("第${muchYears}年")
                    }

                })
    }

    private fun getUpData() {
        OkGo.post<String>(BaseHttp.introducer_purchase_details)
                .tag(this@FundsDetailActivity)
                .headers("token", getString("token"))
                .params("purchaseId", intent.getStringExtra("purchaseId"))
                .execute(object : StringDialogCallback(baseContext, false) {

                    @SuppressLint("SetTextI18n")
                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        val obj = JSONObject(response.body()).getJSONObject("object") ?: JSONObject()

                        val data = obj.getJSONObject("introducer") ?: JSONObject()
                        funds_name2.setRightString(data.optString("introducerName"))
                        funds_tel2.setRightString(data.optString("introducerTelephone"))
                        funds_idcard2.setRightString(data.optString("introducerCardNo"))

                        val rate = obj.optString("introducerlv", "0")
                        funds_detail2.setRightString("收益 = 投资金额 * 老带新利率($rate%)")

                        val profit = obj.optString("introducerProfit", "0")
                        funds_get2.setRightString(DecimalFormat(",##0.##").format(profit.toDouble()))
                    }

                })
    }
}
