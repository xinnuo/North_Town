package com.ruanmeng.north_town

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import com.flyco.dialog.widget.ActionSheetDialog
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
import kotlinx.android.synthetic.main.layout_search.*
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
    private var productType = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)
        when (intent.getStringExtra("type")) {
            "1" -> init_title(intent.getStringExtra("title"))
            "2" -> init_title(intent.getStringExtra("title"), "类别")
        }

        EventBus.getDefault().register(this@NewsActivity)

        swipe_refresh.isRefreshing = true
        getData(pageNum)
    }

    override fun init_title() {
        super.init_title()
        empty_hint.text = "暂无相关客户信息！"
        search_edit.hint = "请输入客户姓名或手机号或身份证号"
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
                        "1" -> register<CommonData>(R.layout.item_data_list) { data, injector ->
                            injector.text(R.id.item_data_name, getColorText(data.userName, keyWord))
                                    .text(R.id.item_data_phone, getColorText("手机 ${data.telephone}", keyWord))
                                    .text(R.id.item_data_idcard, getColorText("身份证号 ${data.cardNo}", keyWord))
                                    .text(R.id.item_data_num, DecimalFormat(",##0.##").format(data.amount.toInt() / 10000.0))
                                    .text(R.id.item_data_vip, "VIP号：${data.vipNo}")

                                    .visibility(R.id.item_data_vip, if (data.vipNo.isEmpty()) View.GONE else View.VISIBLE)
                                    .visibility(R.id.item_data_divider1, if (list.indexOf(data) == list.size - 1) View.GONE else View.VISIBLE)
                                    .visibility(R.id.item_data_divider2, if (list.indexOf(data) != list.size - 1) View.GONE else View.VISIBLE)

                                    .with<RoundedImageView>(R.id.item_data_img) { view ->
                                        view.setImageURL(BaseHttp.baseImg + data.userhead, R.mipmap.default_user)
                                    }

                                    .clicked(R.id.item_data) {
                                        startActivityEx<ReportDetailActivity>(
                                                "type" to "3",
                                                "accountInfoId" to data.accountInfoId)
                                    }
                        }
                        "2" -> register<CommonData>(R.layout.item_data3_list) { data, injector ->
                            injector.text(R.id.item_data_name, getColorText(data.userName, keyWord))
                                    .text(R.id.item_data_phone, "产品名称：${data.productName}")
                                    .text(R.id.item_data_idcard, "投资周期：${data.beginDate} ~ ${data.endDate}")
                                    .text(R.id.item_data_num, data.amount)
                                    .text(R.id.item_data_trans, when (data.investType) {
                                        "1" -> {
                                            val stockAmount = DecimalFormat("##0.##").format(data.stock.toDouble())
                                            "转投金额：${stockAmount}元"
                                        }
                                        "2" -> {
                                            val stockAmount = DecimalFormat("##0.##").format(data.stock.toDouble())
                                            "续投金额：${stockAmount}元"
                                        }
                                        else -> ""
                                    })

                                    .visibility(R.id.item_data_trans,
                                            when (data.investType) {
                                                "1", "2" -> View.VISIBLE
                                                else -> View.GONE
                                            })
                                    .visibility(R.id.item_data_divider1, if (list.indexOf(data) == list.size - 1) View.GONE else View.VISIBLE)
                                    .visibility(R.id.item_data_divider2, if (list.indexOf(data) != list.size - 1) View.GONE else View.VISIBLE)

                                    .with<RoundedImageView>(R.id.item_data_img) { view ->
                                        view.setImageURL(BaseHttp.baseImg + data.userhead, R.mipmap.default_user)
                                    }

                                    .clicked(R.id.item_data) {
                                        startActivityEx<DataProductActivity>("purchaseId" to data.purchaseId)
                                    }
                        }
                    }
                }
                .attachTo(recycle_list)

        search_edit.addTextChangedListener(this@NewsActivity)
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
                if (news_min.text.toNoInt() > news_max.text.toNoInt()) {
                    showToast("最低金额不能大于最高金额")
                    return@setOnClickListener
                }

                money_min = news_min.text.toString()
                money_max = news_max.text.toString()
            }

            updateList()
        }

        tvRight.setOnClickListener {
            val arrHint = arrayOf("会员卡", "有限合伙人")
            val dialog = ActionSheetDialog(this, arrHint, null)
            @Suppress("DEPRECATION")
            dialog.isTitleShow(false)
                    .lvBgColor(resources.getColor(R.color.white))
                    .dividerColor(resources.getColor(R.color.divider))
                    .dividerHeight(0.5f)
                    .itemTextColor(resources.getColor(R.color.black_dark))
                    .itemHeight(40f)
                    .itemTextSize(15f)
                    .cancelText(resources.getColor(R.color.light))
                    .cancelTextSize(15f)
                    .layoutAnimation(null)
                    .show()
            dialog.setOnOperItemClickL { _, _, position, _ ->
                dialog.dismiss()

                productType = "${position + 1}"
                tvRight.text = arrHint[position]
                updateList()
            }
        }
    }

    override fun getData(pindex: Int) {
        OkGo.post<BaseResponse<ArrayList<CommonData>>>(when (intent.getStringExtra("type")) {
            "1" -> BaseHttp.customer_data_list
            "2" -> BaseHttp.customer_purchase_data_list
            else -> ""
        })
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
                .params("productType", productType)
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
        search_close.visibility = if (s.isEmpty()) View.GONE else View.VISIBLE
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
