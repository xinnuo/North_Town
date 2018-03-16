package com.ruanmeng.north_town

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.lzg.extend.BaseResponse
import com.lzg.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.addItems
import com.ruanmeng.base.getString
import com.ruanmeng.base.load_Linear
import com.ruanmeng.model.CommonData
import com.ruanmeng.share.BaseHttp
import kotlinx.android.synthetic.main.activity_manage.*
import net.idik.lib.slimadapter.SlimAdapter
import java.util.ArrayList

class ManageActivity : BaseActivity() {

    private val list = ArrayList<CommonData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage)
        init_title("理财产品")

        getData()
    }

    override fun init_title() {
        super.init_title()
        manage_list.load_Linear(baseContext)

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_product_list) { data, injector ->
                    injector.text(R.id.item_product_name, data.productName)

                            .visibility(R.id.item_product_divider1, if (list.indexOf(data) == list.size - 1) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_product_divider2, if (list.indexOf(data) != list.size - 1) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_product_divider3, if (list.indexOf(data) == 0) View.VISIBLE else View.GONE)

                            .with<ImageView>(R.id.item_product_img) { view ->
                                Glide.with(baseContext)
                                        .load(BaseHttp.baseImg + data.img)
                                        .apply(RequestOptions
                                                .centerCropTransform()
                                                .placeholder(R.mipmap.default_user)
                                                .error(R.mipmap.default_user))
                                        .transition(DrawableTransitionOptions.withCrossFade())
                                        .into(view)
                            }

                            .clicked(R.id.item_product) {
                                val intent = Intent(baseContext, ManageDetailActivity::class.java)
                                intent.putExtra("title", data.productName)
                                intent.putExtra("productId", data.productId)
                                startActivity(intent)
                            }
                }
                .attachTo(manage_list)
    }

    override fun getData() {
        OkGo.post<BaseResponse<ArrayList<CommonData>>>(BaseHttp.product_list)
                .tag(this@ManageActivity)
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
