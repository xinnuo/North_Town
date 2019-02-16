package com.ruanmeng.north_town

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.view.KeyEvent
import android.widget.CompoundButton
import com.lzg.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getString
import com.ruanmeng.base.showToast
import com.ruanmeng.base.startActivityEx
import com.ruanmeng.fragment.MainFirstFragment
import com.ruanmeng.fragment.MainSecondFragment
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.DeviceHelper
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class MainActivity : BaseActivity() {

    private val mCompositeDisposable by lazy { CompositeDisposable() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setToolbarVisibility(false)
        init_title()

        main_check1.performClick()

        checkState()
    }

    override fun init_title() {
        main_check1.setOnCheckedChangeListener(this)
        main_check2.setOnCheckedChangeListener(this)
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        // instantiateItem从FragmentManager中查找Fragment，找不到就getItem新建一个，
        // setPrimaryItem设置隐藏和显示，最后finishUpdate提交事务。
        if (isChecked) {
            val fragment = mFragmentPagerAdapter
                    .instantiateItem(main_container, buttonView.id) as Fragment
            mFragmentPagerAdapter.setPrimaryItem(main_container, 0, fragment)
            mFragmentPagerAdapter.finishUpdate(main_container)
        }
    }

    private val mFragmentPagerAdapter = object : FragmentPagerAdapter(
            supportFragmentManager) {

        override fun getItem(position: Int): Fragment = when (position) {
            R.id.main_check1 -> MainFirstFragment()
            R.id.main_check2 -> MainSecondFragment()
            else -> MainFirstFragment()
        }

        override fun getCount(): Int = 2
    }

    private fun checkState() {
        mCompositeDisposable.add(
                Observable.interval(5, 5, TimeUnit.SECONDS)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            OkGo.post<String>(BaseHttp.equipment_check)
                                    .tag(this@MainActivity)
                                    .headers("token", getString("token"))
                                    .params("equipment", DeviceHelper.getDeviceIdIMEI(baseContext))
                                    .execute(object : StringDialogCallback(baseContext, false) {

                                        override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                            val obj = JSONObject(response.body()).optString("object")
                                            if (obj == "1")  startActivityEx<LoginActivity>("offLine" to true)
                                        }

                                    })
                        }
        )
    }

    override fun finish() {
        super.finish()
        mCompositeDisposable.clear()
    }

    private var exitTime: Long = 0
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - exitTime > 2000) {
                showToast("再按一次退出程序")
                exitTime = System.currentTimeMillis()
            } else {
                onBackPressed()
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}
