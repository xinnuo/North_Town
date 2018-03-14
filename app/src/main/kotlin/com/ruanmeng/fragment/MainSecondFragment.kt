package com.ruanmeng.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lzg.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.cache.CacheMode
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.north_town.*
import com.ruanmeng.share.BaseHttp
import kotlinx.android.synthetic.main.fragment_main_second.*
import kotlinx.android.synthetic.main.layout_title_main.*
import org.json.JSONObject

class MainSecondFragment : BaseFragment() {

    //调用这个方法切换时不会释放掉Fragment
    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (this.view != null)
            this.view!!.visibility = if (menuVisible) View.VISIBLE else View.GONE
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.fragment_main_second, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init_title()
    }

    override fun init_title() {
        main_title.text = "个人中心"

        second_info.setOnClickListener { startActivity(InfoActivity::class.java) }
        second_fold.setOnClickListener { startActivity(PurseActivity::class.java) }
        second_customer.setOnClickListener { startActivity(ClientActivity::class.java) }
        second_setting.setOnClickListener { startActivity(SettingActivity::class.java) }
    }

    override fun onStart() {
        super.onStart()
        getData()
    }

    override fun getData() {
        OkGo.post<String>(BaseHttp.userinfo)
                .tag(activity)
                .headers("token", getString("token"))
                .cacheMode(CacheMode.REQUEST_FAILED_READ_CACHE)
                .cacheKey(BaseHttp.userinfo)
                .execute(object : StringDialogCallback(activity, false) {

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        val obj = JSONObject(response.body()).getJSONObject("object")
                        putString("userName", obj.getString("userName"))
                        putString("userhead", obj.getString("userhead"))
                        putString("cardNo", obj.getString("cardNo"))

                        second_name.text = getString("userName")
                        second_tel.text = getString("mobile")

                        if (second_img.getTag(R.id.second_img) == null) {
                            GlideApp.with(activity!!)
                                    .load(BaseHttp.baseImg + getString("userhead"))
                                    .placeholder(R.mipmap.default_user)
                                    .error(R.mipmap.default_user)
                                    .dontAnimate()
                                    .into(second_img)

                            second_img.setTag(R.id.second_img, getString("userhead"))
                        } else {
                            if (second_img.getTag(R.id.second_img) != getString("userhead")) {
                                GlideApp.with(activity!!)
                                        .load(BaseHttp.baseImg + getString("userhead"))
                                        .placeholder(R.mipmap.default_user)
                                        .error(R.mipmap.default_user)
                                        .dontAnimate()
                                        .into(second_img)

                                second_img.setTag(R.id.second_img, getString("userhead"))
                            }
                        }
                    }

                })
    }
}
