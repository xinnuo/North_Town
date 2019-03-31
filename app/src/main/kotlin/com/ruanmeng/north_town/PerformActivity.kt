package com.ruanmeng.north_town

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import com.flyco.dialog.widget.ActionSheetDialog
import com.lzg.extend.BaseResponse
import com.lzg.extend.StringDialogCallback
import com.lzg.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.makeramen.roundedimageview.RoundedImageView
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.CommonModel
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.DialogHelper
import com.ruanmeng.utils.KeyboardHelper
import com.ruanmeng.utils.TimeHelper
import kotlinx.android.synthetic.main.activity_perform.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import kotlinx.android.synthetic.main.layout_result.*
import kotlinx.android.synthetic.main.layout_search.*
import net.idik.lib.slimadapter.SlimAdapter
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList

class PerformActivity : BaseActivity() {

    private val list = ArrayList<Any>()
    private var keyWord = ""
    private var date_start = ""
    private var date_end = ""

    private val listGroup = ArrayList<CommonData>()
    private var groupPostion = -1
    private var groupId = ""
    private var managerType = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perform)
        init_title("业绩统计", "类别")

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

                                OkGo.post<String>(BaseHttp.check_into_achievement)
                                        .tag(this@PerformActivity)
                                        .headers("token", getString("token"))
                                        .params("managerInfoId", data.managerInfoId)
                                        .execute(object : StringDialogCallback(baseContext) {
                                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                                startActivityEx<PerformCheckActivity>(
                                                        "managerInfoId" to data.managerInfoId,
                                                        "start" to date_start,
                                                        "end" to date_end)
                                            }
                                        })
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
                date_start = date
                updateList()
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
                date_end = date
                updateList()
            })
        }

        perform_group.setOnClickListener {
            if (listGroup.isEmpty()) {
                OkGo.post<BaseResponse<ArrayList<CommonData>>>(BaseHttp.business_group_list)
                        .tag(this@PerformActivity)
                        .headers("token", getString("token"))
                        .execute(object : JacksonDialogCallback<BaseResponse<ArrayList<CommonData>>>(baseContext, true) {

                            override fun onSuccess(response: Response<BaseResponse<ArrayList<CommonData>>>) {

                                listGroup.apply {
                                    clear()
                                    addItems(response.body().`object`)
                                }
                                showGroupDialog()
                            }

                        })
            } else showGroupDialog()
        }

        tvRight.setOnClickListener {
            val arrHint = arrayOf("基金经纪人", "非基金经纪人")
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

                managerType = "${1 - position}"
                tvRight.text = arrHint[position]
                updateList()
            }
        }
    }

    private fun showGroupDialog() {
        val items = ArrayList<String>()
        listGroup.mapTo(items) { it.name }
        DialogHelper.showItemDialog(
                baseContext,
                "选择分组",
                if (groupPostion < 0) 0 else groupPostion,
                items) { position, name ->
            groupPostion = position
            groupId = listGroup[position].businessGroupId
            perform_group.text = name
            updateList()
        }
    }

    override fun getData(pindex: Int) {
        OkGo.post<BaseResponse<CommonModel>>(BaseHttp.achievement_statistics_list)
                .tag(this@PerformActivity)
                .isMultipart(true)
                .headers("token", getString("token"))
                .params("beginDate", date_start)
                .params("endDate", date_end)
                .params("searchar", keyWord)
                .params("businessGroupId", groupId)
                .params("managerType", managerType)
                .params("page", pindex)
                .execute(object : JacksonDialogCallback<BaseResponse<CommonModel>>(baseContext) {

                    override fun onSuccess(response: Response<BaseResponse<CommonModel>>) {

                        list.apply {
                            if (pindex == 1) {
                                clear()
                                pageNum = pindex
                            }
                            addItems(response.body().`object`.purchaseList)
                            if (count(response.body().`object`.purchaseList) > 0) pageNum++
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
