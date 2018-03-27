package com.ruanmeng.north_town

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
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
import com.ruanmeng.utils.KeyboardHelper
import kotlinx.android.synthetic.main.activity_report.*
import kotlinx.android.synthetic.main.layout_empty_add.*
import kotlinx.android.synthetic.main.layout_title_search.*
import net.idik.lib.slimadapter.SlimAdapter
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class ReportActivity : BaseActivity() {

    private val list = ArrayList<Any>()
    private var keyWord = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)
        setToolbarVisibility(false)
        init_title()

        EventBus.getDefault().register(this@ReportActivity)

        swipe_refresh.isRefreshing = true
        getData(pageNum)
    }

    @Suppress("DEPRECATION")
    override fun init_title() {
        swipe_refresh.refresh { getData(1) }
        recycle_list.load_Linear(baseContext, swipe_refresh) {
            if (!isLoadingMore) {
                isLoadingMore = true
                getData(pageNum)
            }
        }

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_report_list) { data, injector ->
                    injector.text(R.id.item_report_name, getColorText(data.userName, keyWord))
                            .text(R.id.item_report_phone, getColorText("手机 ${data.telephone}", keyWord))
                            .text(R.id.item_report_idcard, getColorText("身份证号 ${data.cardNo}", keyWord))

                            .with<RoundedImageView>(R.id.item_report_img) { view ->
                                Glide.with(baseContext)
                                        .load(BaseHttp.baseImg + data.userhead)
                                        .apply(RequestOptions
                                                .centerCropTransform()
                                                .placeholder(R.mipmap.default_user)
                                                .error(R.mipmap.default_user)
                                                .dontAnimate())
                                        .into(view)
                            }

                            .clicked(R.id.item_report) {
                                val intent = Intent(baseContext, ReportDetailActivity::class.java)
                                intent.putExtra("accountInfoId", data.accountInfoId)
                                intent.putExtra("userName", data.userName)
                                intent.putExtra("cardNo", data.cardNo)
                                startActivity(intent)
                            }
                }
                .attachTo(recycle_list)

        search_edit.addTextChangedListener(this@ReportActivity)
        search_edit.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                KeyboardHelper.hideSoftInput(baseContext) //隐藏软键盘

                if (search_edit.text.toString().isBlank()) {
                    showToast("请输入关键字")
                } else {
                    keyWord = search_edit.text.toString()
                    updateList()
                }
            }
            return@setOnEditorActionListener false
        }
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.search_cancel -> search_edit.setText("")
            R.id.empty_add -> startActivity<ReportAddActivity>()
        }
    }

    override fun getData(pindex: Int) {
        OkGo.post<BaseResponse<CommonModel>>(BaseHttp.customer_list)
                .tag(this@ReportActivity)
                .isMultipart(true)
                .headers("token", getString("token"))
                .params("searchar", keyWord)
                .params("accountType", "2")
                .params("page", pindex)
                .execute(object : JacksonDialogCallback<BaseResponse<CommonModel>>(baseContext) {

                    @SuppressLint("SetTextI18n")
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

                        report_result.text = "搜索结果(${response.body().`object`.count})"
                    }

                    override fun onFinish() {
                        super.onFinish()
                        swipe_refresh.isRefreshing = false
                        isLoadingMore = false

                        empty_view.visibility = if (list.size > 0) View.GONE else View.VISIBLE
                    }

                })
    }

    fun updateList() {
        swipe_refresh.isRefreshing = true

        empty_view.visibility = View.GONE
        if (list.isNotEmpty()) {
            list.clear()
            report_result.text = "搜索结果(0)"
            mAdapter.notifyDataSetChanged()
        }

        pageNum = 1
        getData(pageNum)
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (s.isEmpty() && keyWord.isNotEmpty()) {
            keyWord = ""
            updateList()
        }
    }

    override fun finish() {
        EventBus.getDefault().unregister(this@ReportActivity)
        super.finish()
    }

    @Subscribe
    fun onMessageEvent(event: ReportMessageEvent) {
        when (event.type) {
            "添加客户" -> updateList()
        }
    }
}
