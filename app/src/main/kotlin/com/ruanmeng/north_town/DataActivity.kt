package com.ruanmeng.north_town

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.view.View
import android.view.inputmethod.EditorInfo
import com.lzg.extend.BaseResponse
import com.lzg.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.makeramen.roundedimageview.RoundedImageView
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.NewData
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.KeyboardHelper
import com.ruanmeng.utils.Tools
import kotlinx.android.synthetic.main.activity_data.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import kotlinx.android.synthetic.main.layout_search.*
import net.idik.lib.slimadapter.SlimAdapter
import java.text.DecimalFormat

class DataActivity : BaseActivity() {

    private val list = ArrayList<Any>()
    private var keyWord = ""
    private var type = "old"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data)
        init_title("客户列表")
    }

    override fun init_title() {
        super.init_title()
        search_edit.hint = "请输入客户姓名或手机号或身份证号"
        empty_hint.text = "暂无相关客户信息！"

        data_tab.apply {
            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

                override fun onTabReselected(tab: TabLayout.Tab) {}
                override fun onTabUnselected(tab: TabLayout.Tab) {}

                override fun onTabSelected(tab: TabLayout.Tab) {
                    type = if (tab.position == 0) "old" else "new"
                    OkGo.getInstance().cancelTag(this@DataActivity)

                    window.decorView.postDelayed({ runOnUiThread { updateList() } }, 300)
                }

            })

            addTab(this.newTab().setText("老客户"), true)
            addTab(this.newTab().setText("新客户"), false)

            post { Tools.setIndicator(this, 50, 50) }
        }

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
                                view.setImageURL(BaseHttp.baseImg + data.userhead, R.mipmap.default_user)
                            }

                            .clicked(R.id.item_data) {
                                intent.setClass(baseContext, ReportDetailActivity::class.java)
                                intent.putExtra("accountInfoId", data.accountInfoId)
                                intent.putExtra("isData", true)
                                intent.putExtra("isNew", type == "new")
                                startActivity(intent)
                            }
                }
                .register<NewData>(R.layout.item_data2_list) { data, injector ->
                    injector.text(R.id.item_data2_name, getColorText(data.userName, keyWord))
                            .text(R.id.item_data2_phone, getColorText("手机 ${data.telephone}", keyWord))

                            .visibility(R.id.item_data2_divider1, if (list.indexOf(data) == list.size - 1) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_data2_divider2, if (list.indexOf(data) != list.size - 1) View.GONE else View.VISIBLE)

                            .clicked(R.id.item_data2) {
                                intent.setClass(baseContext, DataNewActivity::class.java)
                                intent.putExtra("potentialCustomerId", data.potentialCustomerId)
                                startActivity(intent)
                            }
                }
                .attachTo(recycle_list)

        search_edit.addTextChangedListener(this@DataActivity)
        search_edit.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                KeyboardHelper.hideSoftInput(baseContext) //隐藏软键盘

                if (search_edit.text.isBlank()) {
                    showToast("请输入关键字")
                } else {
                    keyWord = search_edit.text.toString()
                    updateList()
                }
            }
            return@setOnEditorActionListener false
        }

        search_close.setOnClickListener { search_edit.setText("") }
    }

    override fun getData(pindex: Int) {
        when (type) {
            "old" -> {
                OkGo.post<BaseResponse<ArrayList<CommonData>>>(BaseHttp.customer_list_all)
                        .tag(this@DataActivity)
                        .isMultipart(true)
                        .headers("token", getString("token"))
                        .params("accountType", getString("accountType"))
                        .params("searchar", keyWord)
                        .params("type", type)
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

                            @SuppressLint("SetTextI18n")
                            override fun onFinish() {
                                super.onFinish()
                                swipe_refresh.isRefreshing = false
                                isLoadingMore = false

                                empty_view.visibility = if (list.size > 0) View.GONE else View.VISIBLE
                                data_divider.visibility = if (list.size > 0) View.VISIBLE else View.GONE
                            }

                        })
            }
            "new" -> {
                OkGo.post<BaseResponse<ArrayList<NewData>>>(BaseHttp.my_potentialcustomer_list)
                        .tag(this@DataActivity)
                        .isMultipart(true)
                        .headers("token", getString("token"))
                        .params("accountType", getString("accountType"))
                        .params("searchar", keyWord)
                        .params("type", type)
                        .params("page", pindex)
                        .execute(object : JacksonDialogCallback<BaseResponse<ArrayList<NewData>>>(baseContext) {

                            override fun onSuccess(response: Response<BaseResponse<ArrayList<NewData>>>) {

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

                            @SuppressLint("SetTextI18n")
                            override fun onFinish() {
                                super.onFinish()
                                swipe_refresh.isRefreshing = false
                                isLoadingMore = false

                                empty_view.visibility = if (list.size > 0) View.GONE else View.VISIBLE
                                data_divider.visibility = if (list.size > 0) View.VISIBLE else View.GONE
                            }

                        })
            }
        }
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
