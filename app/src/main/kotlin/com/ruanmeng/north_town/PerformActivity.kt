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
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.DialogHelper
import com.ruanmeng.utils.KeyboardHelper
import com.ruanmeng.utils.TimeHelper
import kotlinx.android.synthetic.main.activity_perform.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import kotlinx.android.synthetic.main.layout_search.*
import net.idik.lib.slimadapter.SlimAdapter
import java.text.DecimalFormat
import java.util.*

class PerformActivity : BaseActivity() {

    private val list = ArrayList<Any>()
    private var keyWord = ""
    private var date_start = ""
    private var date_end = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perform)
        init_title("业绩统计")

        swipe_refresh.isRefreshing = true
        getData(pageNum)
    }

    override fun init_title() {
        super.init_title()
        empty_hint.text = "暂无相关业绩信息！"
        search_edit.hint = "请输入业务人员姓名或手机号"
        swipe_refresh.refresh { getData(1) }
        recycle_list.load_Linear(baseContext, swipe_refresh) {
            if (!isLoadingMore) {
                isLoadingMore = true
                getData(pageNum)
            }
        }

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_perform1_list) { data, injector ->
                    injector.text(R.id.item_perform_name, getColorText(data.userName, keyWord))
                            .text(R.id.item_perform_phone, getColorText("手机：${data.telephone}", keyWord))
                            .text(R.id.item_perform_total, DecimalFormat(",##0.##").format(data.sum.toInt() / 10000.0))
                            .text(R.id.item_perform_new, DecimalFormat(",##0.##").format(data.incrementAmount.toInt() / 10000.0))
                            .text(R.id.item_perform_trans, DecimalFormat(",##0.##").format(data.stockAmount.toInt() / 10000.0))
                            .text(R.id.item_perform_quit, DecimalFormat(",##0.##").format(data.retreatAmount.toInt() / 10000.0))

                            .with<RoundedImageView>(R.id.item_perform_img) { view ->
                                view.setImageURL(BaseHttp.baseImg + data.userhead, R.mipmap.default_user)
                            }

                            .visibility(R.id.item_perform_divider1, if (list.indexOf(data) == list.size - 1) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_perform_divider2, if (list.indexOf(data) != list.size - 1) View.GONE else View.VISIBLE)
                            .clicked(R.id.item_perform) {
                                startActivityEx<PerformCheckActivity>(
                                        "managerInfoId" to data.managerInfoId,
                                        "start" to date_start,
                                        "end" to date_end)
                            }
                }
                .attachTo(recycle_list)

        search_edit.addTextChangedListener(this@PerformActivity)
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

        search_close.setOnClickListener { search_edit.setText("") }

        perform_start.setOnClickListener {
            val year_now = Calendar.getInstance().get(Calendar.YEAR)
            DialogHelper.showDateDialog(this@PerformActivity,
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
            DialogHelper.showDateDialog(this@PerformActivity,
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
            date_start = perform_start.text.toString()
            date_end = perform_end.text.toString()

            updateList()
        }
    }

    override fun getData(pindex: Int) {
        OkGo.post<BaseResponse<ArrayList<CommonData>>>(BaseHttp.achievement_statistics_list)
                .tag(this@PerformActivity)
                .isMultipart(true)
                .headers("token", getString("token"))
                .params("beginDate", date_start)
                .params("endDate", date_end)
                .params("searchar", keyWord)
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
                    }

                })
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
}
