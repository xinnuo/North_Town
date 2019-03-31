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
        funds_expand.expand()
        getUpData()

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

                        funds_name.setRightString(obj.optString("userName"))
                        funds_tel.setRightString(obj.optString("telephone"))
                        funds_idcard.setRightString(obj.optString("cardNo"))
                        funds_company.setRightString(obj.optString("commponyName"))
                        funds_he.setRightString(obj.optString("contractNo"))
                        funds_gender.setRightString(when (obj.optString("sex")) {
                            "0" -> "女"
                            else -> "男"
                        })

                        funds_bank.setRightString(obj.optString("bank"))
                        funds_bankcard.setRightString(obj.optString("bankcard"))
                        funds_phone.setRightString(obj.optString("phone"))
                        funds_address.setRightString(obj.optString("address"))
                        funds_fax.setRightString(obj.optString("fax"))
                        funds_shou.setRightString(obj.optString("receiptNo"))
                        funds_shoutype.setRightString(obj.optString("receiptTypeName"))
                        funds_xian.setRightString(obj.optString("xianJin"))
                        funds_lian.setRightString(obj.optString("yinLian"))
                        funds_zhuan.setRightString(obj.optString("zhuanZhang"))
                        funds_other.setRightString(obj.optString("qiTa"))
                        funds_zong.setRightString(obj.optString("receivedAmount"))
                        funds_yin.setRightString(obj.optString("cashierInfoName"))
                        funds_yintel.setRightString(obj.optString("cashierInfoTelephone"))
                        funds_jing.setRightString(obj.optString("managerInfoName"))
                        funds_jingtel.setRightString(obj.optString("managerInfoTelephone"))
                        funds_jingno.setRightString(obj.optString("nonManagerName"))
                        funds_jingnotel.setRightString(obj.optString("nonManagerTelephone"))

                        val year = obj.optString("years", "0").toInt()
                        if (year < 12) {
                            funds_year.text = year.toString()
                            funds_year_hint.text = "投资周期(月)"
                        } else {
                            funds_year.text = DecimalFormat("0.##").format(year / 12.0)
                            funds_year_hint.text = "投资周期(年)"
                        }

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

                        var muchYears = obj.optString("muchYears", "1")
                        if (muchYears.isEmpty()) muchYears = "1"
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

                        val obj = JSONObject(response.body())
                                .getJSONObject("object") ?: JSONObject()

                        val data = obj.getJSONObject("introducer") ?: JSONObject()
                        funds_name2.setRightString(data.optString("introducerName"))
                        funds_tel2.setRightString(data.optString("introducerTelephone"))
                        funds_idcard2.setRightString(data.optString("introducerCardNo"))

                        val rate = obj.optString("introducerlv", "0")
                        funds_detail2.setRightString("收益 = 投资金额 * 老带新利率($rate%)")

                        val profit = obj.optString("introducerProfit")
                        if (profit.isNotEmpty() && profit.toDouble() > 0) {
                            funds_get2.setRightString(DecimalFormat(",##0.##").format(profit.toDouble()))
                        }
                    }

                })
    }
}
