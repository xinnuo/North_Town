package com.ruanmeng.north_town

import android.os.Bundle
import android.text.InputFilter
import android.view.View
import com.lzg.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.NameLengthFilter
import kotlinx.android.synthetic.main.activity_modify_name.*

class ModifyNameActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_name)
        init_title("修改姓名")
    }

    override fun init_title() {
        super.init_title()
        bt_save.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
        bt_save.isClickable = false

        et_name.filters = arrayOf<InputFilter>(NameLengthFilter(12))
        et_name.addTextChangedListener(this@ModifyNameActivity)

        if (getString("userName").isNotEmpty()) {
            et_name.setText(getString("userName"))
            et_name.setSelection(et_name.text.length)
        }
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when(v.id) {
            R.id.bt_save -> {
                if (et_name.text.trim().toString() == getString("userName")) {
                    showToast("未做任何修改")
                    return
                }

                OkGo.post<String>(BaseHttp.userinfo_update)
                        .tag(this@ModifyNameActivity)
                        .isMultipart(true)
                        .headers("token", getString("token"))
                        .params("userName", et_name.text.trim().toString())
                        .execute(object : StringDialogCallback(baseContext) {
                            /*{
                                "msg": "更新成功",
                                "msgcode": 100
                            }*/
                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                                showToast(msg)
                                putString("userName", et_name.text.toString())
                                ActivityStack.screenManager.popActivities(this@ModifyNameActivity::class.java)
                            }

                        })
            }
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (et_name.text.isNotBlank()) {
            bt_save.setBackgroundResource(R.drawable.rec_bg_red)
            bt_save.isClickable = true
        } else {
            bt_save.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
            bt_save.isClickable = false
        }
    }
}
