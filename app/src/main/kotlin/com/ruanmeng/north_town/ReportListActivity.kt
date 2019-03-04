package com.ruanmeng.north_town

import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.view.View
import android.view.inputmethod.EditorInfo
import com.lzg.extend.BaseResponse
import com.lzg.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.ReportMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.DialogHelper
import com.ruanmeng.utils.KeyboardHelper
import com.ruanmeng.utils.getDateFormat
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import kotlinx.android.synthetic.main.layout_search.*
import net.idik.lib.slimadapter.SlimAdapter
import org.greenrobot.eventbus.EventBus
import java.text.DecimalFormat

class ReportListActivity : BaseActivity() {

    private val list = ArrayList<Any>()
    private var previousType = ""
    private var previousId = ""
    private var keyWord = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_list)
        init_title("订单列表")

        swipe_refresh.isRefreshing = true
        getData(pageNum)
    }

    override fun init_title() {
        super.init_title()
        previousType = intent.getStringExtra("previousType")
        previousId = intent.getStringExtra("previousId")

        empty_hint.text = "暂无相关订单信息！"
        swipe_refresh.refresh { getData(1) }
        recycle_list.load_Linear(baseContext, swipe_refresh) {
            if (!isLoadingMore) {
                isLoadingMore = true
                getData(pageNum)
            }
        }

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_check1_list) { data, injector ->
                    injector.text(R.id.item_check_name,
                            data.productName + when (data.investType) {
                                "1" -> {
                                    val stockAmount = DecimalFormat(",##0.##").format(data.stock.toDouble() / 10000)
                                    "（转投:${stockAmount}万）"
                                }
                                "2" -> {
                                    val stockAmount = DecimalFormat(",##0.##").format(data.stock.toDouble() / 10000)
                                    "（续投:${stockAmount}万）"
                                }
                                else -> ""
                            })
                            .text(R.id.item_check_limit, "${data.beginDate} ~ ${data.endDate}")
                            .text(R.id.item_check_range, "${data.rate}%")
                            .text(R.id.item_check_money, "${DecimalFormat(",##0.##").format(data.amount.toDouble() / 10000.0)}万")
                            .text(R.id.item_check_long, data.years.getDateFormat())
                            .text(R.id.item_check_user, data.userName)
                            .gone(R.id.item_check_divider)

                            .clicked(R.id.item_check) {
                                if (data.purchaseId in previousId) {
                                    showToast("已选择当前产品，可修改投资金额")
                                }

                                DialogHelper.showInputDialog(
                                        baseContext,
                                        "${if (previousType == "1") "转" else "续"}投金额（万元）",
                                        "请输入${if (previousType == "1") "转" else "续"}投金额（万元）",
                                        data.amount) { input ->

                                    EventBus.getDefault().post(ReportMessageEvent(
                                            data.purchaseId,
                                            "${data.productName}(${DecimalFormat("0.##").format(input.toDouble())}万)",
                                            "转续投",
                                            input))

                                    ActivityStack.screenManager.popActivities(this@ReportListActivity::class.java)
                                }
                            }
                }
                .attachTo(recycle_list)

        search_edit.apply {
            hint = "请输入客户身份证号"
            inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL
            keyListener = DigitsKeyListener.getInstance("1234567890xX")
            filters = arrayOf<InputFilter>(InputFilter.LengthFilter(18))
        }
        search_edit.addTextChangedListener(this@ReportListActivity)
        search_edit.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                KeyboardHelper.hideSoftInput(baseContext) //隐藏软键盘

                if (search_edit.text.isBlank()) {
                    showToast("请输入关键字")
                } else {
                    keyWord = search_edit.text.trim().toString()
                    updateList()
                }
            }
            return@setOnEditorActionListener false
        }

        search_close.setOnClickListener { search_edit.setText("") }
    }

    override fun getData(pindex: Int) {
        OkGo.post<BaseResponse<ArrayList<CommonData>>>(BaseHttp.past_due_purchase_list_by_type)
                .tag(this@ReportListActivity)
                .headers("token", getString("token"))
                .params("cardNo", keyWord)
                .params("productId", intent.getStringExtra("productId"))
                .params("investType", previousType)
                .params("page", pindex)
                .execute(object : JacksonDialogCallback<BaseResponse<ArrayList<CommonData>>>(baseContext) {

                    override fun onSuccess(response: Response<BaseResponse<ArrayList<CommonData>>>) {
                        list.apply {
                            if (pindex == 1) {
                                clear()
                                pageNum = pindex
                            }
                            addItems(response.body().`object`)
                            if (count(response.body().`object`) > 0) pageNum++
                        }

                        mAdapter.updateData(list)
                    }

                    override fun onFinish() {
                        super.onFinish()
                        swipe_refresh.isRefreshing = false
                        isLoadingMore = false

                        empty_view.visibility = if (list.isNotEmpty()) View.GONE else View.VISIBLE
                    }

                })
    }

    private fun updateList() {
        swipe_refresh.isRefreshing = true

        empty_view.visibility = View.GONE
        if (list.isNotEmpty()) {
            list.clear()
            mAdapter.notifyDataSetChanged()
        }

        pageNum = 1
        getData(pageNum)
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        search_close.visibility = if (s.isEmpty()) View.GONE else View.VISIBLE
        if (s.isEmpty() && keyWord.isNotEmpty()) {
            keyWord = ""
            updateList()
        }
    }
}
