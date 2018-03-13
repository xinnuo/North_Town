package com.ruanmeng.north_town

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.lzg.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.showToast
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.CommonUtil
import kotlinx.android.synthetic.main.activity_forget.*
import org.json.JSONObject

class ForgetActivity : BaseActivity() {

    private var time_count: Int = 180
    private lateinit var thread: Runnable
    private var YZM: String = ""
    private var mTel: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget)
        init_title("忘记密码")
    }

    override fun init_title() {
        super.init_title()
        btn_sure.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
        btn_sure.isClickable = false

        et_tel.addTextChangedListener(this@ForgetActivity)
        et_yzm.addTextChangedListener(this@ForgetActivity)
        et_pwd.addTextChangedListener(this@ForgetActivity)
    }

    @SuppressLint("SetTextI18n")
    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.bt_yzm -> {
                if (et_tel.text.isBlank()) {
                    et_tel.requestFocus()
                    showToast("请输入手机号")
                    return
                }

                if (!CommonUtil.isMobileNumber(et_tel.text.toString())) {
                    et_tel.requestFocus()
                    et_tel.setText("")
                    showToast("手机号码格式错误，请重新输入")
                    return
                }

                thread = Runnable {
                    bt_yzm.text = "${time_count}秒后重发"
                    if (time_count > 0) {
                        bt_yzm.postDelayed(thread, 1000)
                        time_count--
                    } else {
                        bt_yzm.text = "发送验证码"
                        bt_yzm.isClickable = true
                        bt_yzm.setTextColor(Color.parseColor("#FF7F00"))
                        bt_yzm.setBackgroundResource(R.drawable.rec_bg_trans_stroke_orange)
                        time_count = 180
                    }
                }

                OkGo.post<String>(BaseHttp.get_smscode)
                        .tag(this@ForgetActivity)
                        .params("mobile", et_tel.text.trim().toString())
                        .params("accountType", "App_Staff")
                        .params("time", System.currentTimeMillis())
                        .params("smsKey", "hOWt3hiakXHrePCqDKUsPz5T6f7j8P")
                        .params("isReg", "false")
                        .execute(object : StringDialogCallback(baseContext) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                                YZM = JSONObject(response.body()).getString("object")
                                mTel = et_tel.text.trim().toString()
                                if (BuildConfig.LOG_DEBUG) et_yzm.setText(YZM)

                                bt_yzm.isClickable = false
                                bt_yzm.setTextColor(Color.parseColor("#D0D0D0"))
                                bt_yzm.setBackgroundResource(R.drawable.rec_bg_trans_stroke_d0d0d0)
                                time_count = 180
                                bt_yzm.post(thread)
                            }

                        })
            }
            R.id.btn_sure -> {
                if (!CommonUtil.isMobileNumber(et_tel.text.toString())) {
                    et_tel.requestFocus()
                    et_tel.setText("")
                    showToast("手机号码格式错误，请重新输入")
                    return
                }

                if (et_tel.text.toString() != mTel) {
                    showToast("手机号码不匹配，请重新获取验证码")
                    return
                }

                if (et_yzm.text.trim().toString() != YZM) {
                    et_yzm.requestFocus()
                    et_yzm.setText("")
                    showToast("验证码错误，请重新输入")
                    return
                }

                if (et_pwd.text.length < 6) {
                    showToast("新密码长度不少于6位")
                    return
                }

                OkGo.post<String>(BaseHttp.update_pwd)
                        .tag(this@ForgetActivity)
                        .params("mobile", et_tel.text.trim().toString())
                        .params("smscode", et_yzm.text.trim().toString())
                        .params("newpwd", et_pwd.text.trim().toString())
                        .params("accountType", "App_Staff")
                        .execute(object : StringDialogCallback(this@ForgetActivity) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                                showToast(msg)
                                ActivityStack.screenManager.popActivities(this@ForgetActivity::class.java)
                            }

                        })
            }
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (et_tel.text.isNotBlank()
                && et_yzm.text.isNotBlank()
                && et_pwd.text.isNotBlank()) {
            btn_sure.setBackgroundResource(R.drawable.rec_bg_red)
            btn_sure.isClickable = true
        } else {
            btn_sure.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
            btn_sure.isClickable = false
        }
    }
}
