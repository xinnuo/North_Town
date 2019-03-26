package com.ruanmeng.north_town

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import com.lzg.extend.BaseResponse
import com.lzg.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.makeramen.roundedimageview.RoundedImageView
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.CommonModel
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.KeyboardHelper
import com.ruanmeng.utils.NumberHelper
import com.ruanmeng.utils.getDateFormat
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import kotlinx.android.synthetic.main.layout_result.*
import kotlinx.android.synthetic.main.layout_search.*
import net.idik.lib.slimadapter.SlimAdapter
import java.text.DecimalFormat

class FinanceHistoryActivity : BaseActivity() {

    private val list = ArrayList<Any>()
    private var keyWord = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finance_history)
        init_title("历史审核")

        swipe_refresh.isRefreshing = true
        getData(pageNum)
    }

    override fun init_title() {
        super.init_title()
        when (intent.getStringExtra("type")) {
            "1" -> search_edit.hint = "请输入客户姓名"
            "2" -> search_edit.hint = "请输入客户或经纪人姓名"
        }
        empty_hint.text = "暂无相关历史审核信息！"

        swipe_refresh.refresh { getData(1) }
        recycle_list.load_Linear(baseContext, swipe_refresh) {
            if (!isLoadingMore) {
                isLoadingMore = true
                getData(pageNum)
            }
        }

        mAdapter = SlimAdapter.create()
                .apply {
                    when (intent.getStringExtra("type")) {
                        "1" -> register<CommonData>(R.layout.item_review_list) { data, injector ->
                            injector.text(R.id.item_review_name, getColorText(data.userName, keyWord))
                                    .text(R.id.item_review_time, data.createDate)
                                    .text(R.id.item_review_product, data.productName)
                                    .text(R.id.item_review_year, data.years.getDateFormat())
                                    .text(R.id.item_review_money,  "${DecimalFormat(",##0.##").format(data.amount.toInt() / 10000.0)}万")
                                    .text(R.id.item_review_pay, data.paytypeName.trimEnd('、'))
                                    .gone(R.id.item_review_divider)

                                    .with<RoundedImageView>(R.id.item_review_img) { view ->
                                        view.setImageURL(BaseHttp.baseImg + data.userhead, R.mipmap.default_user)
                                    }

                                    .clicked(R.id.item_review) {
                                        startActivityEx<DataProductActivity>("purchaseId" to data.purchaseId)
                                    }
                        }
                        "2" -> register<CommonData>(R.layout.item_finance_list) { data, injector ->
                            injector.text(R.id.item_finance_name, data.productName)
                                    .text(R.id.item_finance_time, data.createDate)
                                    .text(R.id.item_finance_time2, data.serviceCheckDate)
                                    .text(R.id.item_finance_put, getColorText(data.userName, keyWord))
                                    .text(R.id.item_finance_manage, getColorText(data.managerInfoName, keyWord))
                                    .text(R.id.item_finance_money, NumberHelper.fmtMicrometer(data.amount))
                                    .gone(R.id.item_finance_divider)
                                    .visibility(R.id.item_finance_ll, if (data.serviceCheckDate.isNotEmpty()) View.VISIBLE else View.GONE)

                                    .clicked(R.id.item_finance) {
                                        startActivityEx<DataProductActivity>("purchaseId" to data.purchaseId)
                                    }
                        }
                    }
                }
                .attachTo(recycle_list)

        search_edit.addTextChangedListener(this@FinanceHistoryActivity)
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
        OkGo.post<BaseResponse<CommonModel>>(BaseHttp.purchase_past_auditing_list)
                .tag(this@FinanceHistoryActivity)
                .isMultipart(true)
                .headers("token", getString("token"))
                .params("searchar", keyWord)
                .params("page", pindex)
                .execute(object : JacksonDialogCallback<BaseResponse<CommonModel>>(baseContext) {

                    override fun onSuccess(response: Response<BaseResponse<CommonModel>>) {

                        list.apply {
                            if (pindex == 1) {
                                clear()
                                pageNum = pindex
                            }
                            addItems(response.body().`object`.maps)
                            if (count(response.body().`object`.maps) > 0) pageNum++
                        }

                        list_result.text = response.body().`object`?.count ?: "0"

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

        if (s.isEmpty()) {
            if (keyWord.isNotEmpty()) {
                keyWord = ""
                updateList()
            }
        } else {
            keyWord = s.toString()
            updateList()
        }
    }

}
