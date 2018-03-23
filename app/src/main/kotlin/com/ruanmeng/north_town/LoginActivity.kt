package com.ruanmeng.north_town

import android.os.Bundle
import android.view.View
import com.lzg.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.CommonUtil
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.layout_title_main.*
import org.json.JSONObject

class LoginActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setToolbarVisibility(false)

        init_title()
    }

    override fun init_title() {
        main_title.text = "登录"
        main_close.visibility = View.VISIBLE

        bt_login.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
        bt_login.isClickable = false

        et_name.addTextChangedListener(this@LoginActivity)
        et_pwd.addTextChangedListener(this@LoginActivity)

        if (getString("mobile").isNotEmpty()) {
            et_name.setText(getString("mobile"))
            et_name.setSelection(et_name.text.length)
        }

        if (intent.getBooleanExtra("offLine", false)) {
            clearData()
            ActivityStack.screenManager.popAllActivityExcept(this@LoginActivity::class.java)
        }
    }

    override fun doClick(v: View) {
        when (v.id) {
            R.id.main_close -> onBackPressed()
            R.id.tv_forget -> startActivity<ForgetActivity>()
            R.id.bt_login -> {
                if (!CommonUtil.isMobile(et_name.text.toString())) {
                    et_name.requestFocus()
                    et_name.setText("")
                    showToast("手机号码格式错误，请重新输入")
                    return
                }
                if (et_pwd.text.length < 6) {
                    et_pwd.requestFocus()
                    showToast("密码长度不少于6位")
                    return
                }

                OkGo.post<String>(BaseHttp.account_login)
                        .tag(this@LoginActivity)
                        .params("accountName", et_name.text.trim().toString())
                        .params("password", et_pwd.text.trim().toString())
                        .params("accountType", "App_Staff")
                        .execute(object : StringDialogCallback(this@LoginActivity) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                val obj = JSONObject(response.body()).getJSONObject("object")

                                putBoolean("isLogin", true)
                                putString("token", obj.optString("token"))
                                putString("mobile", obj.optString("telephone"))
                                putString("userName", obj.optString("userName"))
                                putString("userhead", obj.optString("userhead"))
                                putString("cardNo", obj.optString("cardNo"))

                                startActivity<MainActivity>()
                                ActivityStack.screenManager.popActivities(this@LoginActivity::class.java)
                            }

                        })
            }
        }
    }

    private fun clearData() {
        putBoolean("isLogin", false)
        putString("token", "")

        putString("userName", "")
        putString("userhead", "")
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (et_name.text.isNotBlank()
                && et_pwd.text.isNotBlank()) {
            bt_login.setBackgroundResource(R.drawable.rec_bg_red)
            bt_login.isClickable = true
        } else {
            bt_login.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
            bt_login.isClickable = false
        }
    }
}
