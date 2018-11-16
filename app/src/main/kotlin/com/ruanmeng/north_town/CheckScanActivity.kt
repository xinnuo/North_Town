package com.ruanmeng.north_town

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.lzg.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getString
import com.ruanmeng.base.toNotDouble
import com.ruanmeng.share.BaseHttp
import kotlinx.android.synthetic.main.activity_check_scan.*
import org.json.JSONObject
import java.text.DecimalFormat

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

                    @SuppressLint("SetTextI18n")
                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        val obj = JSONObject(response.body())
                                .optJSONObject("object") ?: JSONObject()

                        val investType = obj.optString("investType")
                        val previousAmount = obj.optString("previousAmount")
                        val previousFlowAmount = obj.optString("previousFlowAmount")
                        val stock = obj.optString("stock")
                        val previousPurchaseBeginDate = obj.optString("previousPurchaseBeginDate")
                        val previousPurchaseEndDate = obj.optString("previousPurchaseEndDate")
                        val previousPurchaseReceiptNo = obj.optString("previousPurchaseReceiptNo")
                        val previousPurchaseUserName = obj.optString("previousPurchaseUserName")

                        val receivedCash = obj.optString("xianJin")
                        val receivedBank = obj.optString("yinLian")
                        val receivedTran = obj.optString("zhuanZhang")
                        val receivedOther = obj.optString("qiTa")
                        val receivedAmount = obj.optString("receivedAmount")

                        scan_cash.setRightString(DecimalFormat("0.##").format(receivedCash.toNotDouble()))
                        scan_bank.setRightString(DecimalFormat("0.##").format(receivedBank.toNotDouble()))
                        scan_tran.setRightString(DecimalFormat("0.##").format(receivedTran.toNotDouble()))
                        scan_other.setRightString(DecimalFormat("0.##").format(receivedOther.toNotDouble()))
                        scan_total.setRightString(DecimalFormat("0.##").format(receivedAmount.toNotDouble()))

                        if (investType in "1,2") {
                            scan_pre_ll.visibility = View.VISIBLE
                            scan_prehint.text = "老票${if (investType == "1") "转" else "续"}投金额(元)"
                            scan_pretotal.text = previousAmount
                            scan_pretran.text = previousFlowAmount
                            scan_prebuy.text = DecimalFormat("0.##").format(stock.toNotDouble())
                            scan_prestart.text = previousPurchaseBeginDate
                            scan_preend.text = previousPurchaseEndDate
                            scan_precode.text = previousPurchaseReceiptNo
                            scan_prename.text = previousPurchaseUserName
                        }

                        scan_code.setRightString(obj.optString("receiptNo"))
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
