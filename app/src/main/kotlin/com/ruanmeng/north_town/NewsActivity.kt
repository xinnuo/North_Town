package com.ruanmeng.north_town

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
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.KeyboardHelper
import kotlinx.android.synthetic.main.activity_news.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import kotlinx.android.synthetic.main.layout_title_filter.*
import net.idik.lib.slimadapter.SlimAdapter
import java.text.DecimalFormat

class NewsActivity : BaseActivity() {

    private val list = ArrayList<Any>()
    private var keyWord = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)
        setToolbarVisibility(false)
        init_title()

        swipe_refresh.isRefreshing = true
        getData(pageNum)
    }

    override fun init_title() {
        filter_check.setOnClickListener {
            when (news_expand.isExpanded) {
                true -> news_expand.collapse()
                else -> news_expand.expand()
            }
        }

        empty_hint.text = "暂无相关客户信息！"
        swipe_refresh.refresh { getData(1) }
        recycle_list.load_Linear(baseContext, swipe_refresh) {
            if (!isLoadingMore) {
                isLoadingMore = true
                getData(pageNum)
            }
        }

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_data_list) { data, injector ->
                    injector.text(R.id.item_data_name, getColorText(data.userName, keyWord))
                            .text(R.id.item_data_phone, getColorText("手机 ${data.telephone}", keyWord))
                            .text(R.id.item_data_idcard, getColorText("身份证号 ${data.cardNo}", keyWord))
                            .text(R.id.item_data_num, DecimalFormat(",##0.##").format(data.amount.toInt() / 10000.0))

                            .visibility(R.id.item_data_divider1, if (list.indexOf(data) == list.size - 1) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_data_divider2, if (list.indexOf(data) != list.size - 1) View.GONE else View.VISIBLE)

                            .with<RoundedImageView>(R.id.item_data_img) { view ->
                                Glide.with(baseContext)
                                        .load(BaseHttp.baseImg + data.userhead)
                                        .apply(RequestOptions
                                                .centerCropTransform()
                                                .placeholder(R.mipmap.default_user)
                                                .error(R.mipmap.default_user)
                                                .dontAnimate())
                                        .into(view)
                            }

                            .clicked(R.id.item_data) {
                                val intent = Intent(baseContext, NewsDetailActivity::class.java)
                                intent.putExtra("accountInfoId", data.accountInfoId)
                                startActivity(intent)
                            }
                }
                .attachTo(recycle_list)

        filter_edit.addTextChangedListener(this@NewsActivity)
        filter_edit.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                KeyboardHelper.hideSoftInput(baseContext) //隐藏软键盘

                if (filter_edit.text.toString().isBlank()) {
                    showToast("请输入关键字")
                } else {
                    keyWord = filter_edit.text.toString()
                    updateList()
                }
            }
            return@setOnEditorActionListener false
        }

        filter_close.setOnClickListener { filter_edit.setText("") }
    }

    override fun getData(pindex: Int) {
        OkGo.post<BaseResponse<ArrayList<CommonData>>>(BaseHttp.customer_data_list)
                .tag(this@NewsActivity)
                .isMultipart(true)
                .headers("token", getString("token"))
                .params("searchar", keyWord)
                .params("startDate", "")
                .params("endDate", "")
                .params("min", "")
                .params("max", "")
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

                        if (count(response.body().`object`) > 0) mAdapter.updateData(list)
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
            mAdapter.notifyDataSetChanged()
        }

        pageNum = 1
        getData(pageNum)
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        filter_close.visibility = if (s.isEmpty()) View.GONE else View.VISIBLE
        if (s.isEmpty() && keyWord.isNotEmpty()) {
            keyWord = ""
            updateList()
        }
    }
}
