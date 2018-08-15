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
import com.ruanmeng.model.ReportMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.KeyboardHelper
import kotlinx.android.synthetic.main.activity_report_agent.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import kotlinx.android.synthetic.main.layout_search.*
import net.idik.lib.slimadapter.SlimAdapter
import org.greenrobot.eventbus.EventBus

class ReportAgentActivity : BaseActivity() {

    private val list = ArrayList<Any>()
    private var keyWord = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_agent)
        init_title(intent.getStringExtra("title"))

        swipe_refresh.isRefreshing = true
        getData(pageNum)
    }

    override fun init_title() {
        super.init_title()
        when (intent.getStringExtra("type")) {
            "1", "3" -> {
                search_edit.hint = "请输入经纪人姓名或手机号或身份证号"
                empty_hint.text = "暂无相关经纪人信息！"
            }
            "2" -> {
                search_edit.hint = "请输入收银员姓名或手机号或身份证号"
                empty_hint.text = "暂无相关收银员信息！"
            }
        }

        swipe_refresh.refresh { getData(1) }
        recycle_list.load_Linear(baseContext, swipe_refresh) {
            if (!isLoadingMore) {
                isLoadingMore = true
                getData(pageNum)
            }
        }

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_client_list) { data, injector ->
                    injector.text(R.id.item_client_name, getColorText(data.userName, keyWord))
                            .visibility(R.id.item_client_divider1, if (list.indexOf(data) == list.size - 1) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_client_divider2, if (list.indexOf(data) != list.size - 1) View.GONE else View.VISIBLE)

                            .with<RoundedImageView>(R.id.item_client_img) { view ->
                                view.setImageURL(when (intent.getStringExtra("type")) {
                                    "1", "2", "3" -> BaseHttp.baseImg + data.userHead
                                    else -> BaseHttp.baseImg + data.userhead
                                }, R.mipmap.default_user)
                            }

                            .clicked(R.id.item_client) {
                                EventBus.getDefault().post(ReportMessageEvent(
                                        data.accountInfoId,
                                        data.userName,
                                        when (intent.getStringExtra("type")) {
                                            "1" -> "非基金"
                                            "2" -> "收银员"
                                            "3" -> "经纪人"
                                            else -> ""
                                        },
                                        data.telephone))

                                ActivityStack.screenManager.popActivities(this@ReportAgentActivity::class.java)
                            }
                }
                .attachTo(recycle_list)

        search_edit.addTextChangedListener(this@ReportAgentActivity)
        search_edit.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                KeyboardHelper.hideSoftInput(baseContext) //隐藏软键盘

                if (search_edit.text.toString().isBlank()) {
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
        when (intent.getStringExtra("type")) {
            "1" -> OkGo.post<BaseResponse<ArrayList<CommonData>>>(BaseHttp.non_manager_list)
                    .tag(this@ReportAgentActivity)
                    .isMultipart(true)
                    .headers("token", getString("token"))
                    .params("keyWord", keyWord)
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
                            reprot_divider.visibility = if (list.isNotEmpty()) View.VISIBLE else View.GONE
                        }

                    })
            "2", "3" -> OkGo.post<BaseResponse<ArrayList<CommonData>>>(BaseHttp.staff_type_list)
                    .tag(this@ReportAgentActivity)
                    .isMultipart(true)
                    .headers("token", getString("token"))
                    .params("keyWord", keyWord)
                    .params("page", pindex)
                    .params("accountType", when (intent.getStringExtra("type")) {
                        "2" -> "App_Staff_Finance_Collect"
                        "3" -> "App_Staff"
                        else -> ""
                    })
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
                            if (count(response.body().`object`) > 0) mAdapter.updateData(list)
                        }

                        override fun onFinish() {
                            super.onFinish()
                            swipe_refresh.isRefreshing = false
                            isLoadingMore = false

                            empty_view.visibility = if (list.isNotEmpty()) View.GONE else View.VISIBLE
                            reprot_divider.visibility = if (list.isNotEmpty()) View.VISIBLE else View.GONE
                        }

                    })
            else -> OkGo.post<BaseResponse<CommonModel>>(BaseHttp.customer_list)
                    .tag(this@ReportAgentActivity)
                    .isMultipart(true)
                    .headers("token", getString("token"))
                    .params("searchar", keyWord)
                    .params("accountType", "3")
                    .params("page", pindex)
                    .execute(object : JacksonDialogCallback<BaseResponse<CommonModel>>(baseContext) {

                        override fun onSuccess(response: Response<BaseResponse<CommonModel>>) {

                            list.apply {
                                if (pindex == 1) {
                                    clear()
                                    pageNum = pindex
                                }
                                addItems(response.body().`object`.accountInfoList)
                                if (count(response.body().`object`.accountInfoList) > 0) pageNum++
                            }
                            if (count(response.body().`object`.accountInfoList) > 0) mAdapter.updateData(list)
                        }

                        override fun onFinish() {
                            super.onFinish()
                            swipe_refresh.isRefreshing = false
                            isLoadingMore = false

                            empty_view.visibility = if (list.isNotEmpty()) View.GONE else View.VISIBLE
                            reprot_divider.visibility = if (list.isNotEmpty()) View.VISIBLE else View.GONE
                        }

                    })
        }
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
}
