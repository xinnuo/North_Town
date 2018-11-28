package com.ruanmeng.north_town

import android.os.Bundle
import android.text.InputFilter
import android.view.View
import com.lzg.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getString
import com.ruanmeng.base.showToast
import com.ruanmeng.base.startActivityEx
import com.ruanmeng.model.ReportMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.CommonUtil
import com.ruanmeng.utils.NameLengthFilter
import kotlinx.android.synthetic.main.activity_report_add.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class ReportAddActivity : BaseActivity() {

    private var villageTypeId = ""
    private var introducerInfoId = ""
    private var relationshipId = ""
    private var preferenceId = ""
    private var preferenceName = ""
    private var industryId = ""
    private var unitTypeId = ""
    private var unitTypeName = ""
    private var isOwner = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_add)
        init_title("添加客户信息")

        EventBus.getDefault().register(this@ReportAddActivity)
    }

    override fun init_title() {
        super.init_title()
        report_submit.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
        report_submit.isClickable = false

        et_name.filters = arrayOf<InputFilter>(NameLengthFilter(12))
        et_name.addTextChangedListener(this@ReportAddActivity)
        et_phone.addTextChangedListener(this@ReportAddActivity)
        et_card.addTextChangedListener(this@ReportAddActivity)

        rg_check.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_check1 -> isOwner = "1"
                R.id.rb_check2 -> isOwner = "0"
            }
        }
        rb_check1.performClick()
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.report_like_ll -> startActivityEx<ReportLikeActivity>()
            R.id.report_work_ll -> startActivityEx<ReportJobActivity>()
            R.id.report_unit_ll -> startActivityEx<ReportUnitActivity>("title" to "工作单位")
            R.id.report_type_ll -> {
                startActivityEx<ReportUnitActivity>(
                        "title" to "住宅类型",
                        "isType" to true)
            }
            R.id.report_up_ll -> startActivityEx<ReportUpActivity>()
            R.id.report_relation_ll -> {
                if (introducerInfoId.isEmpty()) {
                    showToast("请选择上级客户信息")
                    return
                }

                startActivityEx<ReportUnitActivity>("title" to "客户关系")
            }
            R.id.report_submit -> {
                if (!CommonUtil.isMobile(et_phone.text.toString())) {
                    et_phone.requestFocus()
                    et_phone.setText("")
                    showToast("请输入正确的手机号")
                    return
                }

                if (!CommonUtil.IDCardValidate(et_card.text.trim().toString())) {
                    et_card.requestFocus()
                    et_card.setText("")
                    showToast("请输入正确的身份证号")
                    return
                }

                if (introducerInfoId.isNotEmpty() && relationshipId.isEmpty()) {
                    showToast("请选择与上级客户关系")
                    return
                }

                OkGo.post<String>(BaseHttp.customer_sub)
                        .tag(this@ReportAddActivity)
                        .isMultipart(true)
                        .headers("token", getString("token"))
                        .params("userName", et_name.text.trim().toString())
                        .params("telephone", et_phone.text.trim().toString())
                        .params("cardNo", et_card.text.trim().toString())
                        .params("isOwner", isOwner)
                        .params("villageName", report_type.text.toString())
                        .params("houseNumber", et_num.text.trim().toString())
                        .params("introducerInfoId", introducerInfoId)
                        .params("relationshipId", relationshipId)
                        .params("remark", et_memo.text.trim().toString())
                        .params("preferenceIds", preferenceId)
                        .params("preferences", preferenceName)
                        .params("industryId", industryId)
                        .params("unitTypeId", unitTypeId)
                        .params("unitName", unitTypeName)
                        .execute(object : StringDialogCallback(baseContext) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                                showToast("客户信息添加成功！")
                                EventBus.getDefault().post(ReportMessageEvent("", "", "添加客户"))
                                ActivityStack.screenManager.popActivities(this@ReportAddActivity::class.java)
                            }

                        })
            }
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (et_name.text.isNotBlank()
                && et_phone.text.isNotBlank()
                && et_card.text.isNotBlank()) {
            report_submit.setBackgroundResource(R.drawable.rec_bg_red)
            report_submit.isClickable = true
        } else {
            report_submit.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
            report_submit.isClickable = false
        }
    }

    override fun finish() {
        EventBus.getDefault().unregister(this@ReportAddActivity)
        super.finish()
    }

    @Subscribe
    fun onMessageEvent(event: ReportMessageEvent) {
        when (event.type) {
            "类型" -> {
                villageTypeId = event.id
                report_type.text = event.name
            }
            "上级" -> {
                introducerInfoId = event.id
                report_up.text = event.name
            }
            "关系" -> {
                relationshipId = event.id
                report_relation.text = event.name
            }
            "偏好" -> {
                preferenceId = event.id
                preferenceName = event.name
                report_like.text = event.name
            }
            "职业" -> {
                industryId = event.id
                report_work.text = event.name
            }
            "工作单位" -> {
                unitTypeId = event.id
                unitTypeName = event.name
                report_unit.text = event.name
            }
        }
    }
}
