package com.ruanmeng.north_town

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.lzg.extend.BaseResponse
import com.lzg.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.makeramen.roundedimageview.RoundedImageView
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.PurchaseModel
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.TimeHelper
import kotlinx.android.synthetic.main.activity_perform_check.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter
import java.text.DecimalFormat
import java.util.*

class PerformCheckActivity : BaseActivity() {

    private val list = ArrayList<Any>()
    private var mYear = 0
    private var mMonth = 0
    private var mWeek = 0
    private var mDate = ""
    private var mPatternMonth = ""
    private var mPatternDate = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perform_check)
        init_title()

        mYear = Calendar.getInstance().get(Calendar.YEAR)
        mMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
        mWeek = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)
        mDate = TimeHelper.getInstance().stringDateShort

        mPatternMonth = TimeHelper.getInstance().getNowTime("yyyy年MM月")
        mPatternDate = TimeHelper.getInstance().getNowTime("yyyy年MM月dd日")

        perform_time.text = when (intent.getStringExtra("title")) {
            "日业绩" -> mPatternDate
            "周业绩" -> "${mYear}年${mWeek}周"
            "月业绩" -> mPatternMonth
            "年业绩" -> "${mYear}年"
            else -> ""
        }

        swipe_refresh.isRefreshing = true
        getData(pageNum)
    }

    @SuppressLint("SetTextI18n")
    override fun init_title() {
        super.init_title()
        val title = intent.getStringExtra("title")
        tvTitle.text = title
        perform_hint.text = "按${title.substring(0, 1)}查询业绩"

        empty_hint.text = "暂无相关业绩信息！"
        swipe_refresh.refresh { getData(1) }
        recycle_list.load_Linear(baseContext, swipe_refresh) {
            if (!isLoadingMore) {
                isLoadingMore = true
                getData(pageNum)
            }
        }

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_perform_list) { data, injector ->
                    @Suppress("DEPRECATION")
                    injector.text(R.id.item_perform_name, data.userName)
                            .text(R.id.item_perform_tel, "(${data.telephone})")
                            .text(R.id.item_perform_product, data.productName)
                            .text(R.id.item_perform_money, "${DecimalFormat(",##0.##").format(data.amount.toInt() / 10000.0)}万")
                            .text(R.id.item_perform_limit, "${data.years}年")
                            .text(R.id.item_perform_date, data.createDate)
                            .text(R.id.item_perform_type, when (data.investType) {
                                "1" -> "转投"
                                "2" -> "续投"
                                "3" -> "新增"
                                else -> ""
                            })
                            .textColor(R.id.item_perform_type,
                                    resources.getColor(if (data.investType == "3") R.color.colorAccent else R.color.orange))
                            .background(R.id.item_perform_type,
                                    if (data.investType == "3") R.drawable.rec_bg_trans_stroke_red else R.drawable.rec_bg_trans_stroke_orange)

                            .visibility(R.id.item_perform_type, if (data.investType.isEmpty()) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_perform_divider1, if (list.indexOf(data) == list.size - 1) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_perform_divider2, if (list.indexOf(data) != list.size - 1) View.GONE else View.VISIBLE)

                            .with<RoundedImageView>(R.id.item_perform_img) {
                                it.setImageURL(BaseHttp.baseImg + data.userhead, R.mipmap.default_user)
                            }

                            .clicked(R.id.item_perform) {
                                startActivityEx<FundsDetailActivity>("purchaseId" to data.purchaseId)
                            }
                }
                .attachTo(recycle_list)

        perform_left.setOnClickListener {
            when (intent.getStringExtra("title")) {
                "日业绩" -> {
                    mDate = TimeHelper.getInstance().getNextDay(mDate, -1)
                    mPatternDate = TimeHelper.getInstance().getNextDay(mPatternDate, -1, "yyyy年MM月dd日")
                    perform_time.text = mPatternDate
                }
                "周业绩" -> {
                    mWeek = Calendar.getInstance().apply {
                        set(Calendar.WEEK_OF_YEAR, mWeek)
                        add(Calendar.WEEK_OF_YEAR, -1)
                        mYear = get(Calendar.YEAR)
                    }.get(Calendar.WEEK_OF_YEAR)
                    perform_time.text = "${mYear}年${mWeek}周"
                }
                "月业绩" -> {
                    mMonth = Calendar.getInstance().apply {
                        set(Calendar.MONTH, mMonth)
                        add(Calendar.MONTH, -1)
                        mYear = get(Calendar.YEAR)
                    }.get(Calendar.MONTH)
                    mPatternMonth = TimeHelper.getInstance().getAfterMonth(mPatternMonth, -1, "yyyy年MM月")
                    perform_time.text = mPatternMonth
                }
                "年业绩" -> {
                    mYear -= 1
                    perform_time.text = "${mYear}年"
                }
            }

            updateList()
        }
        perform_right.setOnClickListener {
            when (intent.getStringExtra("title")) {
                "日业绩" -> {
                    mDate = TimeHelper.getInstance().getNextDay(mDate, 1)
                    mPatternDate = TimeHelper.getInstance().getNextDay(mPatternDate, 1, "yyyy年MM月dd日")
                    perform_time.text = mPatternDate
                }
                "周业绩" -> {
                    mWeek = Calendar.getInstance().apply {
                        set(Calendar.WEEK_OF_YEAR, mWeek)
                        add(Calendar.WEEK_OF_YEAR, 1)
                        mYear = get(Calendar.YEAR)
                    }.get(Calendar.WEEK_OF_YEAR)
                    perform_time.text = "${mYear}年${mWeek}周"
                }
                "月业绩" -> {
                    mMonth = Calendar.getInstance().apply {
                        set(Calendar.MONTH, mMonth)
                        add(Calendar.MONTH, 1)
                        mYear = get(Calendar.YEAR)
                    }.get(Calendar.MONTH)
                    mPatternMonth = TimeHelper.getInstance().getAfterMonth(mPatternMonth, 1, "yyyy年MM月")
                    perform_time.text = mPatternMonth
                }
                "年业绩" -> {
                    mYear += 1
                    perform_time.text = "${mYear}年"
                }
            }

            updateList()
        }
    }

    override fun getData(pindex: Int) {
        OkGo.post<BaseResponse<PurchaseModel>>(when (intent.getStringExtra("title")) {
            "日业绩" -> BaseHttp.achievement_day_list
            "周业绩" -> BaseHttp.achievement_week_list
            "月业绩" -> BaseHttp.achievement_month_list
            "年业绩" -> BaseHttp.achievement_year_list
            else -> ""
        })
                .tag(this@PerformCheckActivity)
                .headers("token", getString("token"))
                .apply {
                    when (intent.getStringExtra("title")) {
                        "日业绩" -> params("date", mDate)
                        "周业绩" -> {
                            params("year", mYear)
                            params("week", mWeek)
                        }
                        "月业绩" -> {
                            params("year", mYear)
                            params("month", mMonth)
                        }
                        "年业绩" -> {
                            params("year", mYear)
                        }
                    }
                }
                .params("managerInfoId", intent.getStringExtra("managerInfoId"))
                .params("page", pindex)
                .execute(object : JacksonDialogCallback<BaseResponse<PurchaseModel>>(baseContext) {

                    @SuppressLint("SetTextI18n")
                    override fun onSuccess(response: Response<BaseResponse<PurchaseModel>>) {

                        list.apply {
                            if (pindex == 1) {
                                clear()
                                pageNum = pindex
                            }
                            addItems(response.body().`object`.purchaseList)
                            if (count(response.body().`object`.purchaseList) > 0) pageNum++
                        }
                        if (count(response.body().`object`.purchaseList) > 0) mAdapter.updateData(list)

                        val data = response.body().`object`.performanceData
                        val managerSum = if (data.managerSum.isEmpty()) "0" else data.managerSum
                        val managerContinueRate = if (data.managerContinueRate.isEmpty()) "0" else data.managerContinueRate
                        val managerQuitRate = if (data.managerQuitRate.isEmpty()) "0" else data.managerQuitRate

                        perform_total.text = DecimalFormat(",##0.00").format(managerSum.toInt() / 10000.0)
                        perform_trans.text = "$managerContinueRate%"
                        perform_quit.text = "$managerQuitRate%"
                    }

                    override fun onFinish() {
                        super.onFinish()
                        swipe_refresh.isRefreshing = false
                        isLoadingMore = false

                        empty_view.visibility = if (list.size > 0) View.GONE else View.VISIBLE
                    }

                })
    }

    @SuppressLint("SetTextI18n")
    fun updateList() {
        swipe_refresh.isRefreshing = true

        perform_total.text = "0.00"
        empty_view.visibility = View.GONE
        if (list.isNotEmpty()) {
            list.clear()
            mAdapter.notifyDataSetChanged()
        }

        pageNum = 1
        getData(pageNum)
    }
}
