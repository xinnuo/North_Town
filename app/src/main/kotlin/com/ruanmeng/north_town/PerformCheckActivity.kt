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
import com.ruanmeng.utils.DialogHelper
import com.ruanmeng.utils.TimeHelper
import kotlinx.android.synthetic.main.activity_perform_check.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter
import java.text.DecimalFormat
import java.util.*

class PerformCheckActivity : BaseActivity() {

    private val list = ArrayList<Any>()
    private var date_start = ""
    private var date_end = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perform_check)
        init_title("业绩统计")

        date_start = intent.getStringExtra("start")
        date_end = intent.getStringExtra("end")
        perform_start.text = date_start
        perform_end.text = date_end

        swipe_refresh.isRefreshing = true
        getData(pageNum)
    }

    @SuppressLint("SetTextI18n")
    override fun init_title() {
        super.init_title()
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
                            .text(R.id.item_perform_money, "${DecimalFormat(",##0.##").format(data.amount.toDouble() / 10000.0)}万")
                            .text(R.id.item_perform_limit, "${data.years}年")
                            .text(R.id.item_perform_date, data.createDate)
                            .text(R.id.item_perform_type, if (data.status == "0") "退会" else when (data.investType) {
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

        perform_start.setOnClickListener {
            val year_now = Calendar.getInstance().get(Calendar.YEAR)
            DialogHelper.showDateDialog(this@PerformCheckActivity,
                    year_now - 50,
                    year_now,
                    3,
                    "选择起始日期",
                    true,
                    false, { _, _, _, _, _, date ->
                if (perform_end.text.isNotEmpty()) {
                    val days = TimeHelper.getInstance().getDays(date, perform_end.text.toString())
                    if (days < 0) {
                        showToast("起始日期不能大于结束日期")
                        return@showDateDialog
                    }
                }

                perform_start.text = date
            })
        }

        perform_end.setOnClickListener {
            val year_now = Calendar.getInstance().get(Calendar.YEAR)
            DialogHelper.showDateDialog(this@PerformCheckActivity,
                    year_now - 50,
                    year_now,
                    3,
                    "选择结束日期",
                    true,
                    false, { _, _, _, _, _, date ->
                if (perform_start.text.isNotEmpty()) {
                    val days = TimeHelper.getInstance().getDays(perform_start.text.toString(), date)
                    if (days < 0) {
                        showToast("结束日期不能小于起始日期")
                        return@showDateDialog
                    }
                }

                perform_end.text = date
            })
        }

        perform_filter.setOnClickListener {

            if (perform_start.text.isNotEmpty() && perform_end.text.isEmpty()) {
                showToast("请选择结束日期")
                return@setOnClickListener
            }

            if (perform_start.text.isEmpty() && perform_end.text.isNotEmpty()) {
                showToast("请选择起始日期")
                return@setOnClickListener
            }

            if (perform_start.text.isNotEmpty() && perform_end.text.isNotEmpty()) {
                date_start = perform_start.text.toString()
                date_end = perform_end.text.toString()
            }

            updateList()
        }
    }

    override fun getData(pindex: Int) {
        OkGo.post<BaseResponse<PurchaseModel>>(BaseHttp.achievement_date_list)
                .tag(this@PerformCheckActivity)
                .headers("token", getString("token"))
                .params("managerInfoId", intent.getStringExtra("managerInfoId"))
                .params("beginDate", date_start)
                .params("endDate", date_end)
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
                        mAdapter.updateData(list)

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
