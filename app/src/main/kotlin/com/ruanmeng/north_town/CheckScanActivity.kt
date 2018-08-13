package com.ruanmeng.north_town

import android.os.Bundle
import com.lzg.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getString
import com.ruanmeng.share.BaseHttp
import kotlinx.android.synthetic.main.activity_check_scan.*
import org.json.JSONObject

class CheckScanActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_scan)
        init_title("财务信息")

        getData()
    }

    override fun getData() {
        OkGo.post<String>(BaseHttp.customer_purchase_details)
                .tag(this@CheckScanActivity)
                .headers("token", getString("token"))
                .params("purchaseId", intent.getStringExtra("purchaseId"))
                .execute(object : StringDialogCallback(baseContext) {

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        val obj = JSONObject(response.body())
                                .optJSONObject("object") ?: JSONObject()

                        scan_way.setRightString(obj.optString("paytypeName"))
                        scan_code.setRightString(obj.optString("receiptNo"))
                        scan_money.setRightString(obj.optString("receivedAmount"))
                        scan_type.setRightString(obj.optString("receiptTypeName"))
                        scan_name.setRightString(obj.optString("userName"))
                        scan_idcard.setRightString(obj.optString("cardNo"))
                        scan_shou.setRightString(obj.optString("cashierInfoName"))
                        scan_tel.setRightString(obj.optString("cashierInfoTelephone"))
                        scan_manager.setRightString(obj.optString("managerInfoName"))
                        scan_none.setRightString(obj.optString("nonManagerName"))
                        scan_memo.text = obj.optString("financeRemark")
                    }

                })
    }
}
