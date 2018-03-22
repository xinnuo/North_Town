package com.ruanmeng.north_town

import android.os.Bundle
import android.widget.ImageView
import com.lzg.extend.BaseResponse
import com.lzg.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.ReportMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import kotlinx.android.synthetic.main.activity_report_bank.*
import net.idik.lib.slimadapter.SlimAdapter
import org.greenrobot.eventbus.EventBus

class ReportBankActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_bank)
        init_title("选择开户行")

        getData()
    }

    override fun init_title() {
        super.init_title()
        bank_list.load_Linear(baseContext)
        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_bank_list) { data, injector ->
                    injector.text(R.id.item_bank_name, data.bankName)

                            .with<ImageView>(R.id.item_bank_img) { view ->
                                when (data.bankName) {
                                    "工商银行" -> view.setImageResource(R.mipmap.bank01)
                                    "农业银行" -> view.setImageResource(R.mipmap.bank02)
                                    "招商银行" -> view.setImageResource(R.mipmap.bank03)
                                    "建设银行" -> view.setImageResource(R.mipmap.bank04)
                                    "交通银行" -> view.setImageResource(R.mipmap.bank05)
                                    "中信银行" -> view.setImageResource(R.mipmap.bank06)
                                    "光大银行" -> view.setImageResource(R.mipmap.bank07)
                                    "北京银行" -> view.setImageResource(R.mipmap.bank08)
                                    "平安银行" -> view.setImageResource(R.mipmap.bank09)
                                    "中国银行" -> view.setImageResource(R.mipmap.bank10)
                                    "兴业银行" -> view.setImageResource(R.mipmap.bank11)
                                    "民生银行" -> view.setImageResource(R.mipmap.bank12)
                                    "华夏银行" -> view.setImageResource(R.mipmap.bank13)
                                    "浦发银行" -> view.setImageResource(R.mipmap.bank14)
                                    "广发银行" -> view.setImageResource(R.mipmap.bank15)
                                    "邮政储蓄" -> view.setImageResource(R.mipmap.bank16)
                                }
                            }

                            .clicked(R.id.item_bank) {
                                EventBus.getDefault().post(ReportMessageEvent(
                                        data.bankId,
                                        data.bankName,
                                        "银行"))

                                ActivityStack.screenManager.popActivities(this@ReportBankActivity::class.java)
                            }
                }
                .attachTo(bank_list)
    }

    override fun getData() {
        OkGo.post<BaseResponse<ArrayList<CommonData>>>(BaseHttp.bank_list)
                .tag(this@ReportBankActivity)
                .headers("token", getString("token"))
                .execute(object : JacksonDialogCallback<BaseResponse<ArrayList<CommonData>>>(baseContext, true) {

                    override fun onSuccess(response: Response<BaseResponse<ArrayList<CommonData>>>) {
                        list.apply {
                            clear()
                            addItems(response.body().`object`)
                        }
                        mAdapter.updateData(list)
                    }

                })
    }
}
