package com.ruanmeng.north_town

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.lzg.extend.BaseResponse
import com.lzg.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.makeramen.roundedimageview.RoundedImageView
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.share.BaseHttp
import kotlinx.android.synthetic.main.activity_client.*
import net.idik.lib.slimadapter.SlimAdapter

class ClientActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client)
        init_title("我的客户")

        getData()
    }

    override fun init_title() {
        super.init_title()
        recycle_list.load_Linear(baseContext)

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_client_list) { data, injector ->
                    injector.text(R.id.item_client_name, data.userName)

                            .visibility(R.id.item_client_divider1, if (list.indexOf(data) == list.size - 1) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_client_divider2, if (list.indexOf(data) != list.size - 1) View.GONE else View.VISIBLE)

                            .with<RoundedImageView>(R.id.item_client_img) { view ->
                                view.setImageURL(BaseHttp.baseImg + data.userhead, R.mipmap.default_user)
                            }

                            .clicked(R.id.item_client) {
                                val intent = Intent(baseContext, NewsDetailActivity::class.java)
                                intent.putExtra("accountInfoId", data.accountInfoId)
                                intent.putExtra("userName", data.userName)
                                intent.putExtra("cardNo", data.cardNo)
                                startActivity(intent)
                            }
                }
                .attachTo(recycle_list)
    }

    override fun getData() {
        OkGo.post<BaseResponse<java.util.ArrayList<CommonData>>>(BaseHttp.my_customer_list)
                .tag(this@ClientActivity)
                .headers("token", getString("token"))
                .execute(object : JacksonDialogCallback<BaseResponse<java.util.ArrayList<CommonData>>>(baseContext, true) {

                    override fun onSuccess(response: Response<BaseResponse<java.util.ArrayList<CommonData>>>) {
                        list.apply {
                            clear()
                            addItems(response.body().`object`)
                        }
                        mAdapter.updateData(list)
                    }

                })
    }
}
