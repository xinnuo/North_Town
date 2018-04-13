package com.ruanmeng.north_town

import android.content.Intent
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
import com.ruanmeng.model.ReportMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.DialogHelper
import com.ruanmeng.utils.KeyboardHelper
import com.ruanmeng.utils.TimeHelper
import kotlinx.android.synthetic.main.activity_news.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import kotlinx.android.synthetic.main.layout_title_filter.*
import net.idik.lib.slimadapter.SlimAdapter
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.text.DecimalFormat
import java.util.*

class NewsActivity : BaseActivity() {

    private val list = ArrayList<Any>()
    private var keyWord = ""
    private var date_start = ""
    private var date_end = ""
    private var money_min = ""
    private var money_max = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)
        setToolbarVisibility(false)
        init_title()

        EventBus.getDefault().register(this@NewsActivity)

        swipe_refresh.isRefreshing = true
        getData(pageNum)
    }

    override fun init_title() {
        filter_check.setOnClickListener {
            when (news_expand.isExpanded) {
                true -> {
                    news_expand.collapse()

                    news_start.text = ""
                    news_end.text = ""
                    news_min.setText("")
                    news_max.setText("")

                    if (date_start.isNotEmpty()
                            || date_end.isNotEmpty()
                            || money_min.isNotEmpty()
                            || money_max.isNotEmpty()) {
                        date_start = ""
                        date_end = ""
                        money_min = ""
                        money_max = ""

                        window.decorView.postDelayed({ runOnUiThread { updateList() } }, 300)
                    }

                    filter_check.text = "筛选"
                }
                else -> {
                    news_expand.expand()
                    filter_check.text = "取消"
                }
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
                                view.setImageURL(BaseHttp.baseImg + data.userhead, R.mipmap.default_user)
                            }

                            .clicked(R.id.item_data) {
                                val intent = Intent(baseContext, NewsDetailActivity::class.java)
                                intent.putExtra("accountInfoId", data.accountInfoId)
                                intent.putExtra("userName", data.userName)
                                intent.putExtra("cardNo", data.cardNo)
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

        news_start.setOnClickListener {
            val year_now = Calendar.getInstance().get(Calendar.YEAR)
            DialogHelper.showDateDialog(this@NewsActivity,
                    year_now - 50,
                    year_now,
                    3,
                    "选择起始日期",
                    true,
                    false, { _, _, _, _, _, date ->
                if (news_end.text.isNotEmpty()) {
                    val days = TimeHelper.getInstance().getDays(date, news_end.text.toString())
                    if (days < 0) {
                        showToast("起始日期不能大于结束日期")
                        return@showDateDialog
                    }
                }

                news_start.text = date
            })
        }

        news_end.setOnClickListener {
            val year_now = Calendar.getInstance().get(Calendar.YEAR)
            DialogHelper.showDateDialog(this@NewsActivity,
                    year_now - 50,
                    year_now,
                    3,
                    "选择结束日期",
                    true,
                    false, { _, _, _, _, _, date ->
                if (news_start.text.isNotEmpty()) {
                    val days = TimeHelper.getInstance().getDays(news_start.text.toString(), date)
                    if (days < 0) {
                        showToast("结束日期不能小于起始日期")
                        return@showDateDialog
                    }
                }

                news_end.text = date
            })
        }

        news_filter.setOnClickListener {
            money_min = ""
            money_max = ""

            if (news_start.text.isNotEmpty() && news_end.text.isEmpty()) {
                showToast("请选择结束日期")
                return@setOnClickListener
            }
            if (news_start.text.isEmpty() && news_end.text.isNotEmpty()) {
                showToast("请选择起始日期")
                return@setOnClickListener
            }
            if (news_min.text.isEmpty() && news_max.text.isNotEmpty()) {
                showToast("请输入最低金额")
                return@setOnClickListener
            }
            if (news_min.text.isNotEmpty() && news_max.text.isEmpty()) {
                showToast("请输入最高金额")
                return@setOnClickListener
            }

            if (news_start.text.isNotEmpty() && news_end.text.isNotEmpty()) {
                date_start = news_start.text.toString()
                date_end = news_end.text.toString()
            }
            if (news_min.text.isNotEmpty() && news_max.text.isNotEmpty()) {
                if (news_min.text.toString().toInt() > news_max.text.toString().toInt()) {
                    showToast("最低金额不能大于最高金额")
                    return@setOnClickListener
                }

                money_min = news_min.text.toString()
                money_max = news_max.text.toString()
            }

            if (date_start.isNotEmpty()
                    || date_end.isNotEmpty()
                    || money_min.isNotEmpty()
                    || money_max.isNotEmpty()) {
                updateList()
            }
        }
    }

    override fun getData(pindex: Int) {
        OkGo.post<BaseResponse<ArrayList<CommonData>>>(BaseHttp.customer_data_list)
                .tag(this@NewsActivity)
                .isMultipart(true)
                .headers("token", getString("token"))
                .params("accountType", getString("accountType"))
                .params("searchar", keyWord)
                .params("startDate", date_start)
                .params("endDate", date_end)
                .params("min", money_min)
                .params("max", money_max)
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

    override fun finish() {
        EventBus.getDefault().unregister(this@NewsActivity)
        super.finish()
    }

    @Subscribe
    fun onMessageEvent(event: ReportMessageEvent) {
        when (event.type) {
            "添加订单" -> updateList()
        }
    }
}
