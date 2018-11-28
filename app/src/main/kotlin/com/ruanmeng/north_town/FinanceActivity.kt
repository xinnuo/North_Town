package com.ruanmeng.north_town

import android.os.Bundle
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
import com.ruanmeng.utils.KeyboardHelper
import com.ruanmeng.utils.NumberHelper
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import kotlinx.android.synthetic.main.layout_search.*
import net.idik.lib.slimadapter.SlimAdapter
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class FinanceActivity : BaseActivity() {

    private val list = ArrayList<Any>()
    private var keyWord = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finance)
        init_title("财务审核", "历史审核")

        EventBus.getDefault().register(this@FinanceActivity)

        swipe_refresh.isRefreshing = true
        getData(pageNum)
    }

    override fun init_title() {
        super.init_title()
        search_edit.hint = "请输入客户或经纪人姓名"
        empty_hint.text = "暂无相关录入信息！"

        swipe_refresh.refresh { getData(1) }
        recycle_list.load_Linear(baseContext, swipe_refresh) {
            if (!isLoadingMore) {
                isLoadingMore = true
                getData(pageNum)
            }
        }

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_finance_list) { data, injector ->
                    injector.text(R.id.item_finance_name, data.productName)
                            .text(R.id.item_finance_time, data.createDate)
                            .text(R.id.item_finance_time2, data.serviceCheckDate)
                            .text(R.id.item_finance_put, getColorText(data.userName, keyWord))
                            .text(R.id.item_finance_manage, getColorText(data.managerInfoName, keyWord))
                            .text(R.id.item_finance_money, NumberHelper.fmtMicrometer(data.amount))
                            .gone(R.id.item_finance_divider)
                            .visibility(R.id.item_finance_ll, if (data.serviceCheckDate.isNotEmpty()) View.VISIBLE else View.GONE)

                            .clicked(R.id.item_finance) {
                                when (getString("accountType")) {
                                    "App_Staff_Finance_Collect" -> startActivityEx<FinanceSubmitActivity>("purchaseId" to data.purchaseId)
                                    "App_Staff_Finance_Check" -> startActivityEx<FinanceAllActivity>("purchaseId" to data.purchaseId)
                                }
                            }
                }
                .attachTo(recycle_list)

        search_edit.addTextChangedListener(this@FinanceActivity)
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

        tvRight.setOnClickListener { startActivityEx<FinanceHistoryActivity>("type" to "2") }
    }

    override fun getData(pindex: Int) {
        OkGo.post<BaseResponse<ArrayList<CommonData>>>(BaseHttp.purchase_auditing_list)
                .tag(this@FinanceActivity)
                .isMultipart(true)
                .headers("token", getString("token"))
                .params("searchar", keyWord)
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

    fun updateList() {
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

    override fun finish() {
        EventBus.getDefault().unregister(this@FinanceActivity)
        super.finish()
    }

    @Subscribe
    fun onMessageEvent(event: ReportMessageEvent) {
        when (event.type) {
            "财务审核" -> updateList()
        }
    }
}
