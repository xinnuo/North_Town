package com.ruanmeng.fragment

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ruanmeng.base.BaseFragment
import com.ruanmeng.base.getString
import com.ruanmeng.base.startActivityEx
import com.ruanmeng.north_town.*
import com.ruanmeng.utils.MultiGapDecoration
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
            "App_Staff" -> listOf(
                    "客户报备", "客户资料", "客户数据",
                    "业绩统计", "理财产品")
            "App_Staff_Service" -> listOf(
                    "客户资料", "客服审核", "客户数据",
                    "业绩统计", "佣金统计")
            "App_Staff_Finance_Collect" -> listOf(
                    "客户资料", "客户数据", "业绩统计",
                    "佣金统计", "理财产品", "财务审核(1)")
            "App_Staff_Finance_Check" -> listOf(
                    "客户资料", "客户数据", "业绩统计",
                    "佣金统计", "理财产品", "财务审核(2)")
            else -> listOf(
                    "客户报备", "客户资料", "客服审核",
                    "客户数据", "业绩统计", "佣金统计",
                    "理财产品", "财务审核(1)", "财务审核(2)")
        }

        first_list.apply {
            layoutManager = GridLayoutManager(activity, 3)
            addItemDecoration(MultiGapDecoration(15).apply { isOffsetTopEnabled = true })

            adapter = SlimAdapter.create()
                    .register<String>(R.layout.item_first_grid) { data, injector ->
                        injector.text(R.id.item_first_name, data.replace("(1)", "").replace("(2)", ""))
                                .image(R.id.item_first_img, when (data) {
                                    "客户报备" -> R.mipmap.index_icon01
                                    "客户资料" -> R.mipmap.index_icon02
                                    "客服审核" -> R.mipmap.index_icon03
                                    "客户数据" -> R.mipmap.index_icon04
                                    "业绩统计" -> R.mipmap.index_icon05
                                    "佣金统计" -> R.mipmap.index_icon06
                                    "理财产品" -> R.mipmap.index_icon07
                                    "财务审核(1)" -> R.mipmap.index_icon08
                                    "财务审核(2)" -> R.mipmap.index_icon09
                                    else -> R.mipmap.default_logo
                                })

                                .clicked(R.id.item_first) {
                                    when (data) {
                                        "客户报备" -> startActivityEx<ReportActivity>()
                                        "客户资料" -> startActivityEx<DataActivity>()
                                        "客服审核" -> startActivityEx<CheckActivity>()
                                        "客户数据" -> startActivityEx<NewsSelectActivity>()
                                        "业绩统计" -> startActivityEx<PerformActivity>()
                                        "佣金统计" -> startActivityEx<FundsActivity>()
                                        "理财产品" -> startActivityEx<ManageActivity>()
                                        "财务审核(1)" -> startActivityEx<FinanceActivity>()
                                        "财务审核(2)" -> startActivityEx<FinanceActivity>()
                                    }
                                }
                    }
                    .attachTo(this)
                    .updateData(list)
        }
    }
}
