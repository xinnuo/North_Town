package com.ruanmeng.fragment

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ruanmeng.base.BaseFragment
import com.ruanmeng.base.getString
import com.ruanmeng.base.startActivity
import com.ruanmeng.north_town.*
import com.ruanmeng.utils.TopDecoration
import kotlinx.android.synthetic.main.fragment_main_first.*
import kotlinx.android.synthetic.main.layout_title_main.*
import net.idik.lib.slimadapter.SlimAdapter

class MainFirstFragment : BaseFragment() {

    //调用这个方法切换时不会释放掉Fragment
    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (this.view != null)
            this.view!!.visibility = if (menuVisible) View.VISIBLE else View.GONE
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init_title()
    }

    override fun init_title() {
        main_title.text = "首页"
        val list = when (getString("accountType")) {
            "App_Staff" -> listOf("客户报备", "客户资料", "客户数据", "财务审核")
            "App_Staff_Finance" -> listOf("客户报备", "财务对账", "客户数据", "业绩统计", "佣金统计", "理财产品")
            "App_Staff_Service" -> listOf("客户报备", "客户资料", "客户数据", "业绩统计", "佣金统计", "财务审核")
            else -> listOf("客户报备", "客户资料", "财务对账", "客户数据", "业绩统计", "佣金统计", "理财产品", "财务审核")
        }

        first_list.apply {
            layoutManager = GridLayoutManager(activity, 3)
            addItemDecoration(TopDecoration(15))

            adapter = SlimAdapter.create()
                    .register<String>(R.layout.item_first_grid) { data, injector ->
                        injector.text(R.id.item_first_name, data)
                                .image(R.id.item_first_img, when (data) {
                                    "客户报备" -> R.mipmap.list1
                                    "客户资料" -> R.mipmap.list2
                                    "财务对账" -> R.mipmap.list3
                                    "客户数据" -> R.mipmap.list4
                                    "业绩统计" -> R.mipmap.list5
                                    "佣金统计" -> R.mipmap.list6
                                    "理财产品" -> R.mipmap.list7
                                    "财务审核" -> R.mipmap.list8
                                    else -> R.mipmap.default_logo
                                })

                                .clicked(R.id.item_first) {
                                    when (data) {
                                        "客户报备" -> startActivity<ReportActivity>()
                                        "客户资料" -> startActivity<DataActivity>()
                                        "财务对账" -> startActivity<FinanceActivity>()
                                        "客户数据" -> startActivity<NewsActivity>()
                                        "业绩统计" -> startActivity<PerformActivity>()
                                        "佣金统计" -> startActivity<FundsActivity>()
                                        "理财产品" -> startActivity<ManageActivity>()
                                        "财务审核" -> startActivity<CheckActivity>()
                                    }
                                }
                    }
                    .attachTo(this)
        }

        (first_list.adapter as SlimAdapter).updateData(list)
    }
}
