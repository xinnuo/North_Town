package com.ruanmeng.north_town

import android.annotation.SuppressLint
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
import com.ruanmeng.model.CommonModel
import com.ruanmeng.model.ReportMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.DialogHelper
import com.ruanmeng.utils.KeyboardHelper
import com.ruanmeng.utils.TimeHelper
import kotlinx.android.synthetic.main.activity_news.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import kotlinx.android.synthetic.main.layout_result.*
import kotlinx.android.synthetic.main.layout_search.*
import net.idik.lib.slimadapter.SlimAdapter
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList

class NewsActivity : BaseActivity() {

    private val list = ArrayList<Any>()
    private var keyWord = ""
    private var date_start = ""
    private var date_end = ""
    private var money_min = ""
    private var money_max = ""
    private var productId = ""
    private var isOwner = ""
    private var years = ""
    private var villageName = ""

    private val listProduct = ArrayList<CommonData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)
        init_title(intent.getStringExtra("title"), "类别")

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
                                        startActivityEx<FundsDetailActivity>("purchaseId" to data.purchaseId)
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

        news_house.setOnClickListener {
            getHouseData { item ->
                val items = ArrayList<String>()
                item.mapTo(items) { it.villageName }

                if (item.isNotEmpty()) {
                    DialogHelper.showItemDialog(
                            baseContext,
                            "选择住宅类型",
                            0,
                            items) { _, name ->
                        news_house.text = name
                    }
                }
            }
        }

        rg_check.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_check1 -> isOwner = "1"
                R.id.rb_check2 -> isOwner = "0"
            }
        }

        news_filter.setOnClickListener {
            money_min = ""
            money_max = ""

            date_start = news_start.text.toString()
            date_end = news_end.text.toString()

            money_min = news_min.text.toString()
            money_max = news_max.text.toString()

            villageName = news_house.text.toString()
            years = news_year.text.toString()

            if (money_min.isNotEmpty() && money_max.isNotEmpty()) {
                if (money_min.toNoInt() > money_max.toNoInt()) {
                    showToast("最低金额不能大于最高金额")
                    return@setOnClickListener
                }
            }

            updateList()
        }

        tvRight.setOnClickListener {
            if (listProduct.isEmpty()) {
                OkGo.post<BaseResponse<CommonModel>>(BaseHttp.product_list)
                        .tag(this@NewsActivity)
                        .headers("token", getString("token"))
                        .execute(object : JacksonDialogCallback<BaseResponse<CommonModel>>(baseContext, true) {

                            override fun onSuccess(response: Response<BaseResponse<CommonModel>>) {
                                listProduct.apply {
                                    clear()
                                    addItems(response.body().`object`.bl)
                                }

                                showProductDialog()
                            }

                        })
            } else showProductDialog()
        }
    }

    override fun getData(pindex: Int) {
        OkGo.post<BaseResponse<CommonModel>>(when (intent.getStringExtra("type")) {
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
                .params("productId", productId)
                .params("isOwner", isOwner)
                .params("villageName", villageName)
                .params("years", years)
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
                        val amount = response.body().`object`?.amount ?: ""
                        if (amount.isNotEmpty()) {
                            list_right.visible()
                            list_money.text = DecimalFormat(",##0.##").format(amount.toInt() / 10000.0)
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

    private fun getHouseData(event: (ArrayList<CommonData>) -> Unit) {
        OkGo.post<BaseResponse<java.util.ArrayList<CommonData>>>(BaseHttp.village_list)
                .tag(this@NewsActivity)
                .headers("token", getString("token"))
                .execute(object : JacksonDialogCallback<BaseResponse<java.util.ArrayList<CommonData>>>(baseContext, true) {

                    @SuppressLint("SetTextI18n")
                    override fun onSuccess(response: Response<BaseResponse<java.util.ArrayList<CommonData>>>) {

                        val items = ArrayList<CommonData>()
                        items.apply {
                            clear()
                            addItems(response.body().`object`)
                        }
                        event(items)
                    }

                })
    }

    private fun showProductDialog() {
        val items = ArrayList<String>()
        listProduct.mapTo(items) { it.productName }
        val dialog = ActionSheetDialog(this, items.toTypedArray(), null)
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

            productId = listProduct[position].productId
            tvRight.text = items[position]
            updateList()
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
