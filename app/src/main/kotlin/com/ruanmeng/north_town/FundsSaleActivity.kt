package com.ruanmeng.north_town

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.lzg.extend.BaseResponse
import com.lzg.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.model.CommissionModel
import com.ruanmeng.model.CommonData
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.DialogHelper
import com.ruanmeng.utils.TimeHelper
import com.ruanmeng.view.DropPopWindow
import kotlinx.android.synthetic.main.activity_funds_sale.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter
import java.util.*

class FundsSaleActivity : BaseActivity() {

    private val list = ArrayList<Any>()
    private val list_depart = ArrayList<CommonData>()

    private var departmentId = ""
    private var date_start = ""
    private var date_end = ""
    private var money_min = ""
    private var money_max = ""

    private lateinit var dropPopWindowLeft: DropPopWindow
    private lateinit var dropPopWindowCenter: DropPopWindow
    private var dropPopWindowRight: DropPopWindow? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_funds_sale)
        init_title("销售部佣金", "佣金规则")

        swipe_refresh.isRefreshing = true
        getData(pageNum)
    }

    override fun init_title() {
        super.init_title()
        empty_hint.text = "暂无相关佣金信息！"
        swipe_refresh.refresh { getData(1) }
        recycle_list.load_Linear(baseContext, swipe_refresh) {
            if (!isLoadingMore) {
                isLoadingMore = true
                getData(pageNum)
            }
        }

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_purse_list) { data, injector ->
                    injector.text(R.id.item_purse_name, data.userName)
                            .text(R.id.item_purse_product, data.productName)
                            .text(R.id.item_purse_date, data.createDate)
                            .text(R.id.item_purse_agent, data.managerInfoName)
                            .text(R.id.item_purse_yong, "￥${if (data.commission.isEmpty()) "0" else data.commission}")

                            .clicked(R.id.item_purse) {
                                startActivityEx<FundsDetailActivity>("purchaseId" to data.purchaseId)
                            }
                }
                .attachTo(recycle_list)

        tvRight.setOnClickListener {
            startActivityEx<WebActivity>("title" to "佣金规则")
        }

        dropPopWindowLeft = object : DropPopWindow(
                baseContext,
                R.layout.popu_layout_date,
                funds_date_arrow) {

            override fun afterInitView(view: View) {
                val pop_start = view.findViewById<TextView>(R.id.pop_start)
                val pop_end = view.findViewById<TextView>(R.id.pop_end)
                val pop_filter = view.findViewById<TextView>(R.id.pop_filter)
                val pop_reset = view.findViewById<TextView>(R.id.pop_reset)
                val year_now = Calendar.getInstance().get(Calendar.YEAR)

                pop_start.setOnClickListener {

                    DialogHelper.showDateDialog(baseContext,
                            year_now - 50,
                            year_now,
                            3,
                            "选择起始日期",
                            true,
                            false, { _, _, _, _, _, date ->

                        if (pop_end.text.isNotEmpty()) {
                            val days = TimeHelper.getInstance().getDays(date, pop_end.text.toString())
                            if (days < 0) {
                                showToast("起始日期不能大于结束日期")
                                return@showDateDialog
                            }
                        }

                        pop_start.text = date
                    })
                }

                pop_end.setOnClickListener {

                    DialogHelper.showDateDialog(baseContext,
                            year_now - 50,
                            year_now,
                            3,
                            "选择结束日期",
                            true,
                            false, { _, _, _, _, _, date ->
                        if (pop_start.text.isNotEmpty()) {
                            val days = TimeHelper.getInstance().getDays(pop_start.text.toString(), date)
                            if (days < 0) {
                                showToast("结束日期不能小于起始日期")
                                return@showDateDialog
                            }
                        }

                        pop_end.text = date
                    })
                }

                pop_filter.setOnClickListener {
                    if (pop_start.text.isNotEmpty() && pop_end.text.isEmpty()) {
                        showToast("请选择结束日期")
                        return@setOnClickListener
                    }

                    if (pop_start.text.isEmpty() && pop_end.text.isNotEmpty()) {
                        showToast("请选择起始日期")
                        return@setOnClickListener
                    }

                    if (pop_start.text.isNotEmpty() && pop_end.text.isNotEmpty()) {
                        date_start = pop_start.text.toString()
                        date_end = pop_end.text.toString()

                        dismiss()
                        window.decorView.postDelayed({ runOnUiThread { updateList() } }, 350)
                    }
                }

                pop_reset.setOnClickListener {
                    pop_start.text = ""
                    pop_end.text = ""

                    if (date_start.isNotEmpty()
                            || date_end.isNotEmpty()) {
                        date_start = ""
                        date_end = ""

                        window.decorView.postDelayed({ runOnUiThread { updateList() } }, 350)
                    }

                    dismiss()
                }
            }

        }

        dropPopWindowCenter = object : DropPopWindow(
                baseContext,
                R.layout.popu_layout_money,
                funds_money_arrow) {

            override fun afterInitView(view: View) {
                val pop_min = view.findViewById<EditText>(R.id.pop_min)
                val pop_max = view.findViewById<EditText>(R.id.pop_max)
                val pop_filter = view.findViewById<TextView>(R.id.pop_filter)
                val pop_reset = view.findViewById<TextView>(R.id.pop_reset)

                pop_filter.setOnClickListener {
                    if (pop_min.text.isEmpty() && pop_max.text.isNotEmpty()) {
                        showToast("请输入最低金额")
                        return@setOnClickListener
                    }
                    if (pop_min.text.isNotEmpty() && pop_max.text.isEmpty()) {
                        showToast("请输入最高金额")
                        return@setOnClickListener
                    }
                    if (pop_min.text.isNotEmpty() && pop_max.text.isNotEmpty()) {
                        if (pop_min.text.toString().toInt() > pop_max.text.toString().toInt()) {
                            showToast("最低金额不能大于最高金额")
                            return@setOnClickListener
                        }

                        money_min = pop_min.text.toString()
                        money_max = pop_max.text.toString()

                        dismiss()
                        window.decorView.postDelayed({ runOnUiThread { updateList() } }, 350)
                    }
                }

                pop_reset.setOnClickListener {
                    pop_min.setText("")
                    pop_max.setText("")

                    if (money_min.isNotEmpty()
                            || money_max.isNotEmpty()) {
                        money_min = ""
                        money_max = ""

                        window.decorView.postDelayed({ runOnUiThread { updateList() } }, 350)
                    }

                    dismiss()
                }
            }

        }
    }

    override fun getData(pindex: Int) {
        OkGo.post<BaseResponse<CommissionModel>>(BaseHttp.department_commission_list)
                .tag(this@FundsSaleActivity)
                .headers("token", getString("token"))
                .params("startDate", date_start)
                .params("endDate", date_end)
                .params("min", money_min)
                .params("max", money_max)
                .params("page", pindex)
                .execute(object : JacksonDialogCallback<BaseResponse<CommissionModel>>(baseContext) {

                    override fun onSuccess(response: Response<BaseResponse<CommissionModel>>) {

                        list.apply {
                            if (pindex == 1) {
                                clear()
                                pageNum = pindex
                            }
                            addItems(response.body().`object`.purchaseList)
                            if (count(response.body().`object`.purchaseList) > 0) pageNum++
                        }
                        mAdapter.updateData(list)

                        funds_total.text = response.body().`object`.commissionSum
                    }

                    override fun onFinish() {
                        super.onFinish()
                        swipe_refresh.isRefreshing = false
                        isLoadingMore = false

                        empty_view.visibility = if (list.size > 0) View.GONE else View.VISIBLE
                    }

                })
    }

    @Suppress("DEPRECATION")
    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.funds_date -> {
                if (dropPopWindowLeft.isShowing) dropPopWindowLeft.dismiss()
                else dropPopWindowLeft.showAsDropDown(funds_divider)
            }
            R.id.funds_money -> {
                if (dropPopWindowCenter.isShowing) dropPopWindowCenter.dismiss()
                else dropPopWindowCenter.showAsDropDown(funds_divider)
            }
            R.id.funds_depart -> {
                if (dropPopWindowRight == null
                        || list_depart.isEmpty()) {
                    OkGo.post<BaseResponse<ArrayList<CommonData>>>(BaseHttp.department_list)
                            .tag(this@FundsSaleActivity)
                            .headers("token", getString("token"))
                            .execute(object : JacksonDialogCallback<BaseResponse<ArrayList<CommonData>>>(baseContext, true) {

                                override fun onSuccess(response: Response<BaseResponse<ArrayList<CommonData>>>) {
                                    list_depart.apply {
                                        clear()
                                        addItems(response.body().`object`)
                                    }

                                    if (list_depart.isNotEmpty()) {
                                        dropPopWindowRight = object : DropPopWindow(
                                                baseContext,
                                                R.layout.popu_layout_depart,
                                                funds_depart_arrow) {

                                            override fun afterInitView(view: View) {
                                                val recyclerView = view.findViewById<RecyclerView>(R.id.pop_container)
                                                recyclerView.apply {
                                                    load_Linear(baseContext)
                                                    adapter = SlimAdapter.create()
                                                            .register<CommonData>(R.layout.item_depart_list) { data, injector ->
                                                                injector.text(R.id.item_depart, data.departmentName)
                                                                        .textColor(R.id.item_depart, resources.getColor(if (data.isChecked) R.color.colorAccent else R.color.black_dark))

                                                                        .visibility(
                                                                                R.id.item_depart_divider1,
                                                                                if (list_depart.indexOf(data) == list_depart.size - 1) View.GONE else View.VISIBLE)
                                                                        .visibility(
                                                                                R.id.item_depart_divider2,
                                                                                if (list_depart.indexOf(data) != list_depart.size - 1) View.GONE else View.VISIBLE)

                                                                        .clicked(R.id.item_depart) { _ ->
                                                                            list_depart.filter { it.isChecked }.forEach { it.isChecked = false }
                                                                            data.isChecked = true
                                                                            funds_depart_hint.text = data.departmentName

                                                                            if (departmentId != data.departmentId) {
                                                                                departmentId = data.departmentId
                                                                                (adapter as SlimAdapter).notifyDataSetChanged()
                                                                                window.decorView.postDelayed({ runOnUiThread { updateList() } }, 350)
                                                                            }

                                                                            dismiss()
                                                                        }
                                                            }
                                                            .attachTo(this)

                                                    (adapter as SlimAdapter).updateData(list_depart)
                                                }

                                            }
                                        }

                                        dropPopWindowRight!!.showAsDropDown(funds_divider)
                                    }
                                }

                            })
                } else {
                    if (dropPopWindowRight!!.isShowing) dropPopWindowRight!!.dismiss()
                    else dropPopWindowRight!!.showAsDropDown(funds_divider)
                }
            }
        }
    }

    fun updateList() {
        OkGo.getInstance().cancelTag(this@FundsSaleActivity)
        swipe_refresh.isRefreshing = true

        empty_view.visibility = View.GONE
        if (list.isNotEmpty()) {
            funds_total.text = "0"

            list.clear()
            mAdapter.notifyDataSetChanged()
        }

        pageNum = 1
        getData(pageNum)
    }
}
