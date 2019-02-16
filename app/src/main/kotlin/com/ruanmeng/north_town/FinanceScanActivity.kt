package com.ruanmeng.north_town

import android.os.Bundle
import com.lzg.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getString
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.getDateFormat
import kotlinx.android.synthetic.main.activity_finance_scan.*
import org.json.JSONObject

class FinanceScanActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finance_scan)
        init_title("合同信息")

        getData()
    }

    override fun getData() {
        OkGo.post<String>(BaseHttp.customer_purchase_details)
                .tag(this@FinanceScanActivity)
                .headers("token", getString("token"))
                .params("purchaseId", intent.getStringExtra("purchaseId"))
                .execute(object : StringDialogCallback(baseContext) {

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        val obj = JSONObject(response.body())
                                .optJSONObject("object") ?: JSONObject()

                        scan_product.setRightString(obj.optString("productName"))
                        scan_money.setRightString(obj.optString("amount"))
                        scan_year.setRightString(obj.optString("years").getDateFormat())
                        scan_begin.setRightString(obj.optString("beginDate"))
                        scan_end.setRightString(obj.optString("endDate"))

                        val compName = obj.optString("compName")
                        val vipNo = obj.optString("vipNo")
                        scan_company.setLeftString(if (vipNo.isEmpty()) "企业名称" else "VIP编号")
                        scan_company.setRightString(if (vipNo.isEmpty()) compName else vipNo)

                        scan_name.setRightString(obj.optString("userName"))
                        scan_idcard.setRightString(obj.optString("cardNo"))
                        scan_bank.setRightString(obj.optString("bank"))
                        scan_account.setRightString(obj.optString("bankcard"))
                        scan_tel.setRightString(obj.optString("phone"))
                        scan_addr.text = obj.optString("address")
                        scan_fax.setRightString(obj.optString("fax"))
                        scan_none.setRightString(obj.optString("nonManagerName"))
                        scan_memo.text = obj.optString("remark")
                    }

                })
    }
}
