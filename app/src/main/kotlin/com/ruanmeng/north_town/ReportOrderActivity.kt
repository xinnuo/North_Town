package com.ruanmeng.north_town

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputFilter
import android.view.View
import com.lzg.extend.BaseResponse
import com.lzg.extend.StringDialogCallback
import com.lzg.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.OrderData
import com.ruanmeng.model.ProductModel
import com.ruanmeng.model.ReportMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.*
import com.ruanmeng.view.OnTextWatcher
import kotlinx.android.synthetic.main.activity_report_order.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.json.JSONObject
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList

class ReportOrderActivity : BaseActivity() {

    private val listRate = ArrayList<CommonData>()
    private val listYear = ArrayList<String>()
    private val items = ArrayList<CommonData>()
    private val listOrder = ArrayList<OrderData>()

    private var putType = ""
    private var productId = ""
    private var accountInfoId = ""
    private var previousPurchaseId = ""
    private var previousFlowAmount = ""
    private var previousPurchasePos = 0
    private var managerInfoId = ""
    private var introducerInfoId = ""
    private var relationshipId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_order)
        init_title()

        EventBus.getDefault().register(this@ReportOrderActivity)

        getData()
        getMessage()
        getProductDetail()
        if (putType == "会员卡") getVipMessage()
    }

    @SuppressLint("SetTextI18n")
    override fun init_title() {
        super.init_title()
        putType = intent.getStringExtra("type")
        productId = intent.getStringExtra("productId")
        accountInfoId = intent.getStringExtra("accountInfoId")
        report_name.text = intent.getStringExtra("userName")

        report_submit.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
        report_submit.isClickable = false

        when (putType) {
            "会员卡" -> {
                order_expand.collapse()
                vip_expand.expand()
                back_ll.visibility = View.VISIBLE
                tvTitle.text = "会员卡"

                vip_name.addTextChangedListener(this@ReportOrderActivity)
                vip_num.addTextChangedListener(this@ReportOrderActivity)
                vip_card.addTextChangedListener(this@ReportOrderActivity)
                vip_addr.addTextChangedListener(this@ReportOrderActivity)
            }
            else -> {
                order_expand.expand()
                vip_expand.collapse()
                back_ll.visibility = View.GONE
                tvTitle.text = "有限合伙人"
                report_company.text = intent.getStringExtra("companyName")
                report_partner.text = intent.getStringExtra("legalMan")
                report_need.text = "${intent.getStringExtra("amount")}人"
                report_put.text = "${intent.getStringExtra("put")}人"
                report_left.text = "${intent.getStringExtra("left")}人"

                et_receipt.addTextChangedListener(this@ReportOrderActivity)
            }
        }

        et_money.filters = arrayOf<InputFilter>(DecimalNumberFilter())

        et_money.addTextChangedListener(this@ReportOrderActivity)
        report_start.addTextChangedListener(this@ReportOrderActivity)
        report_end.addTextChangedListener(this@ReportOrderActivity)
        et_card.addTextChangedListener(this@ReportOrderActivity)
        et_phone.addTextChangedListener(this@ReportOrderActivity)

        val watcher = object : OnTextWatcher() {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()) report_expect2.text = "预期收入 ￥0"
                else {
                    if (et_money.text.isNotBlank()) calculatedValue(doubleToLong(et_money.text))
                }
            }
        }
        et_profit.addTextChangedListener(watcher)
    }

    @SuppressLint("SetTextI18n")
    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.report_product_ll -> {
                DialogHelper.showItemDialog(
                        baseContext,
                        "请选择",
                        previousPurchasePos,
                        listOf("新增", "转投", "续投")) { position, name ->
                    previousPurchasePos = position
                    when (position) {
                        0 -> {
                            listOrder.clear()
                            previousPurchaseId = ""
                            previousFlowAmount = ""
                            report_product.text = name
                            report_total.text = ""
                        }
                        1, 2 -> startActivityEx<ReportListActivity>(
                                "productId" to productId,
                                "previousId" to previousPurchaseId,
                                "previousType" to position.toString())
                    }
                }
            }
            R.id.report_start_ll, R.id.report_end_ll -> {
                if (v.id == R.id.report_end_ll && report_end.text.isNotEmpty()) return

                val year_now = Calendar.getInstance().get(Calendar.YEAR)
                DialogHelper.showDateDialog(
                        baseContext,
                        year_now,
                        year_now + 20,
                        3,
                        "选择出资日期",
                        true,
                        false, { _, _, _, _, _, date ->
                    report_start.text = date
                    report_end.text = TimeHelper.getInstance().getAnyYear(date, items.first().years.toInt())
                })
            }
            R.id.report_bank_ll -> startActivityEx<ReportBankActivity>()
            R.id.report_agent_ll -> startActivityEx<ReportAgentActivity>(
                    "title" to "选择非基金经纪人",
                    "type" to "1")
            R.id.report_up_ll -> startActivityEx<ReportUpActivity>()
            R.id.report_year_ll -> {
                if (listYear.size < 2) return

                DialogHelper.showItemDialog(
                        baseContext,
                        "选择投资期限",
                        listYear.indexOf(report_year.text.toString()),
                        listYear) { position, name ->

                    report_year.text = name
                    items.clear()
                    listRate.mapTo(items) {
                        val rates = it.rate.split(",")
                        val mYears = it.years.split(",")

                        CommonData().apply {
                            min = it.min
                            max = it.max
                            years = mYears[position]
                            rate = rates[position]
                        }
                    }

                    if (et_money.text.isNotBlank()) {
                        calculatedValue(doubleToLong(et_money.text))
                    } else {
                        report_expect1.text = "利率 ${items.first().rate}%（起投 ${items.first().min}万）"
                    }

                    if (report_start.text.isNotEmpty()) {
                        report_end.text = TimeHelper.getInstance()
                                .getAnyYear(
                                        report_start.text.toString(),
                                        items.first().years.toInt())
                    }
                }
            }
            R.id.report_relation_ll -> {
                if (introducerInfoId.isEmpty()) {
                    showToast("请选择上级客户信息")
                    return
                }

                startActivityEx<ReportUnitActivity>("title" to "客户关系")
            }
            R.id.report_submit -> {
                /*if (!BankcardHelper.checkBankCard(et_card.rawText)) {
                    et_card.requestFocus()
                    et_card.setText("")
                    showToast("请输入正确的银行卡卡号")
                    return
                }*/

                if (!CommonUtil.isMobile(et_phone.text.toString())) {
                    et_phone.requestFocus()
                    et_phone.setText("")
                    showToast("请输入正确的联系电话")
                    return
                }

                if (report_total.text.isNotEmpty()) {
                    val trans = report_total.text.toNoDouble()
                    val total = et_money.text.toNoDouble()
                    if (trans > total) {
                        showToast("转投续投金额不能超过认购金额")
                        return
                    }
                }

                if (introducerInfoId.isNotEmpty() && relationshipId.isEmpty()) {
                    showToast("请选择与上级客户关系")
                    return
                }

                OkGo.post<String>(BaseHttp.purchase_sub)
                        .tag(this@ReportOrderActivity)
                        .isMultipart(true)
                        .headers("token", getString("token"))
                        .apply {
                            when (putType) {
                                "会员卡" -> {
                                    params("vipNo", vip_num.text.toString())
                                    params("cardNo", vip_card.text.toString())
                                    params("prepaidAmount", et_back.text.toString())
                                }
                                else -> {
                                    params("companyId", intent.getStringExtra("companyId"))
                                    params("commponyName", intent.getStringExtra("companySkipName"))
                                    params("cardNo", et_receipt.text.toString())
                                }
                            }
                        }
                        .params("profit", et_profit.text.toString())
                        .params("managerInfoId", getString("token"))
                        .params("productId", productId)
                        .params("accountInfoId", accountInfoId)
                        .params("previousPurchaseId", previousPurchaseId)
                        .params("previousFlowAmount", previousFlowAmount)
                        .params("stock", doubleToLong(report_total.text))
                        .params("years", report_year.text.trimEnd('年').toString())
                        .params("amount", doubleToLong(et_money.text))
                        .params("beginDate", report_start.text.toString())
                        .params("endDate", report_end.text.toString())
                        .params("bank", report_bank.text.toString())
                        .params("bankCard", et_card.rawText)
                        .params("phone", et_phone.text.toString())
                        .params("address", et_addr.text.trim().toString())
                        .params("fax", et_fax.text.toString())
                        .params("nonManagerInfoId", managerInfoId)
                        .params("introducerInfoId", introducerInfoId)
                        .params("relationshipId", relationshipId)
                        .params("remark", et_memo.text.trim().toString())
                        .execute(object : StringDialogCallback(baseContext) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                                showToast(msg)
                                val obj = JSONObject(response.body())
                                        .optJSONObject("object") ?: JSONObject()

                                if (relation_expand.isExpanded)
                                    EventBus.getDefault().post(ReportMessageEvent("", "", "添加订单"))

                                startActivityEx<ReportFinanceActivity>(
                                        "purchaseId" to obj.optString("purchaseId"),
                                        "accountName" to obj.optString("accountName"),
                                        "cardNo" to obj.optString("cardNo"))

                                ActivityStack.screenManager.popActivities(
                                        this@ReportOrderActivity::class.java,
                                        ReportSelectActivity::class.java)
                            }

                        })
            }
        }
    }

    override fun getData() {
        OkGo.post<String>(BaseHttp.customer_introducer_relationship)
                .tag(this@ReportOrderActivity)
                .headers("token", getString("token"))
                .params("accountInfoId", intent.getStringExtra("accountInfoId"))
                .execute(object : StringDialogCallback(baseContext, false) {

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        val obj = JSONObject(response.body())
                                .optJSONObject("object") ?: JSONObject()

                        introducerInfoId = obj.optString("introducerInfoId")
                        relationshipId = obj.optString("relationshipId")
                        if (relationshipId.isNotEmpty()) relation_expand.collapse()
                        else relation_expand.expand()
                    }

                })
    }

    private fun getMessage() {
        OkGo.post<String>(BaseHttp.customer_last_purchase_msg)
                .tag(this@ReportOrderActivity)
                .headers("token", getString("token"))
                .params("accountInfoId", intent.getStringExtra("accountInfoId"))
                .execute(object : StringDialogCallback(baseContext, false) {

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        val obj = JSONObject(response.body())
                                .optJSONObject("object") ?: JSONObject()

                        et_receipt.setText(obj.optString("cardNo"))
                        et_receipt.setSelection(et_receipt.text.length)
                        report_bank.text = obj.optString("bank")
                        et_card.setText(obj.optString("bankCard"))
                        et_phone.setText(obj.optString("phone"))
                        et_addr.setText(obj.optString("address"))
                        et_fax.setText(obj.optString("fax"))
                    }

                })
    }

    private fun getVipMessage() {
        OkGo.post<String>(BaseHttp.customer_vip_no)
                .tag(this@ReportOrderActivity)
                .headers("token", getString("token"))
                .params("accountInfoId", intent.getStringExtra("accountInfoId"))
                .execute(object : StringDialogCallback(baseContext, false) {

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        val obj = JSONObject(response.body())
                                .optJSONObject("object") ?: JSONObject()

                        vip_name.setText(obj.optString("userName"))
                        vip_name.setSelection(vip_name.text.length)
                        vip_num.setText(obj.optString("vipNo"))
                        vip_card.setText(obj.optString("cardNo"))
                        vip_addr.setText(obj.optString("villageName"))
                    }

                })
    }

    private fun getProductDetail() {
        OkGo.post<BaseResponse<ProductModel>>(BaseHttp.get_product)
                .tag(this@ReportOrderActivity)
                .headers("token", getString("token"))
                .params("productId", intent.getStringExtra("productId"))
                .execute(object : JacksonDialogCallback<BaseResponse<ProductModel>>(baseContext, true) {

                    @SuppressLint("SetTextI18n")
                    override fun onSuccess(response: Response<BaseResponse<ProductModel>>) {

                        listRate.clear()
                        listRate.addItems(response.body().`object`.rateList)
                        if (listRate.isNotEmpty()) {
                            val minYears = listRate.first().years.split(",")
                            minYears.mapTo(listYear.apply { clear() }) { "${it}年" }

                            if (listYear.isNotEmpty()) {
                                report_year.text = listYear.first()

                                listRate.mapTo(items) {
                                    val rates = it.rate.split(",")
                                    val mYears = it.years.split(",")

                                    CommonData().apply {
                                        min = it.min
                                        max = it.max
                                        years = mYears.first()
                                        rate = rates.first()
                                    }
                                }

                                report_expect1.text = "利率 ${items.first().rate}%（起投 ${items.first().min}万）"
                            }
                        }
                    }

                })
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        when (putType) {
            "会员卡" -> {
                if (vip_name.text.isNotBlank()
                        && vip_num.text.isNotBlank()
                        && vip_card.text.isNotBlank()
                        && vip_addr.text.isNotBlank()
                        && et_money.text.isNotBlank()
                        && report_start.text.isNotBlank()
                        && report_end.text.isNotBlank()
                        && et_card.text.isNotBlank()
                        && et_phone.text.isNotBlank()) {
                    report_submit.setBackgroundResource(R.drawable.rec_bg_red)
                    report_submit.isClickable = true
                } else {
                    report_submit.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
                    report_submit.isClickable = false
                }
            }
            else -> {
                if (et_receipt.text.isNotBlank()
                        && et_money.text.isNotBlank()
                        && report_start.text.isNotBlank()
                        && report_end.text.isNotBlank()
                        && et_card.text.isNotBlank()
                        && et_phone.text.isNotBlank()) {
                    report_submit.setBackgroundResource(R.drawable.rec_bg_red)
                    report_submit.isClickable = true
                } else {
                    report_submit.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
                    report_submit.isClickable = false
                }
            }
        }

        if (et_money.isFocused && et_money.text.isNotBlank()) calculatedValue(doubleToLong(et_money.text))
    }

    private fun doubleToLong(edit: CharSequence) = (edit.toNoDouble() * 10000).toLong()

    @SuppressLint("SetTextI18n")
    private fun calculatedValue(value: Long) {
        if (items.isEmpty() || et_profit.text.isNotEmpty()) return

        val minValue = items.first().min.toInt() * 10000

        if (value >= minValue) {
            items.forEach {
                val min = it.min.toInt() * 10000
                val max = it.max.toInt() * 10000
                val year = it.years.toInt()

                if (min < max) {
                    if (value in min..(max - 1)) {
                        report_expect1.text = "利率 ${it.rate}%（起投 ${it.min}万）"
                        val expect = DecimalFormat("###,###,##0.##").format(value * (1 + it.rate.toDouble() * year / 100.0))
                        report_expect2.text = "预期收入 ￥$expect"
                        return
                    }
                } else {
                    if (value >= min) {
                        report_expect1.text = "利率 ${it.rate}%（起投 ${it.min}万）"
                        val expect = DecimalFormat("###,###,##0.##").format(value * (1 + it.rate.toDouble() * year / 100.0))
                        report_expect2.text = "预期收入 ￥$expect"
                        return
                    }
                }
            }
        } else {
            report_expect1.text = "利率 ${items.first().rate}%（起投 ${items.first().min}万）"
            report_expect2.text = "预期收入 ￥0"
        }
    }

    override fun finish() {
        EventBus.getDefault().unregister(this@ReportOrderActivity)
        super.finish()
    }

    @SuppressLint("SetTextI18n")
    @Subscribe
    fun onMessageEvent(event: ReportMessageEvent) {
        when (event.type) {
            "转续投" -> {
                if (listOrder.any { it.orderId == event.id }) {
                    listOrder.first { it.orderId == event.id }.apply {
                        orderName = event.name
                        orderAmount = event.extend
                    }
                } else listOrder.add(OrderData(event.id, event.name, event.extend))

                val itemsName = ArrayList<String>()
                val itemsId = ArrayList<String>()
                val itemsAmount = ArrayList<String>()
                var totalAmount = 0.0

                listOrder.forEach {
                    itemsName.add(it.orderName)
                    itemsId.add(it.orderId)
                    itemsAmount.add(doubleToLong(it.orderAmount).toString())
                    totalAmount += it.orderAmount.toDouble()
                }

                previousPurchaseId = itemsId.joinToString(",")
                previousFlowAmount = itemsAmount.joinToString(",")
                report_product.text = itemsName.joinToString(", ")
                report_total.text = DecimalFormat("0.##").format(totalAmount)
            }
            "银行" -> report_bank.text = event.name
            "非基金" -> {
                managerInfoId = event.id
                report_agent.text = event.name
            }
            "上级" -> {
                introducerInfoId = event.id
                report_up.text = event.name
            }
            "关系" -> {
                relationshipId = event.id
                report_relation.text = event.name
            }
        }
    }
}
