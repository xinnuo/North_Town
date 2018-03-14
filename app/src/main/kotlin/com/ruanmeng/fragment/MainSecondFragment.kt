package com.ruanmeng.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ruanmeng.base.BaseFragment
import com.ruanmeng.base.GlideApp
import com.ruanmeng.base.getString
import com.ruanmeng.base.startActivity
import com.ruanmeng.north_town.*
import com.ruanmeng.share.BaseHttp
import kotlinx.android.synthetic.main.fragment_main_second.*
import kotlinx.android.synthetic.main.layout_title_main.*

class MainSecondFragment : BaseFragment() {

    //调用这个方法切换时不会释放掉Fragment
    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (this.view != null)
            this.view!!.visibility = if (menuVisible) View.VISIBLE else View.GONE
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main_second, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init_title()
    }

    override fun onStart() {
        super.onStart()
        second_name.text = getString("userName")
        second_tel.text = getString("mobile")

        activity?.let {
            GlideApp.with(it)
                .load(BaseHttp.baseImg + getString("userhead"))
                .placeholder(R.mipmap.default_user)
                .error(R.mipmap.default_user)
                .dontAnimate()
                .into(second_img)
        }
    }

    override fun init_title() {
        main_title.text = "个人中心"

        second_info.setOnClickListener { startActivity(InfoActivity::class.java) }
        second_fold.setOnClickListener { startActivity(PurseActivity::class.java) }
        second_customer.setOnClickListener { startActivity(ClientActivity::class.java) }
        second_setting.setOnClickListener { startActivity(SettingActivity::class.java) }
    }
}
