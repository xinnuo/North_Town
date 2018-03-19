package com.ruanmeng.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ruanmeng.base.BaseFragment
import com.ruanmeng.base.load_Linear
import com.ruanmeng.model.CommonData
import com.ruanmeng.north_town.R
import kotlinx.android.synthetic.main.fragment_product.*
import net.idik.lib.slimadapter.SlimAdapter
import java.util.ArrayList

class ProductFragment : BaseFragment() {

    private lateinit var list: ArrayList<CommonData>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_product, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init_title()

        @Suppress("UNCHECKED_CAST")
        list = arguments?.getSerializable("list") as ArrayList<CommonData>
        mAdapter.updateData(list)
    }

    override fun init_title() {
        val isArrow = arguments?.getBoolean("isArrow", false) ?: false
        activity?.let { product_list.load_Linear(it) }

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_job_list) { data, injector ->
                    injector.text(R.id.item_job_name, data.productName)
                            .visibility(R.id.item_job_arrow, if (isArrow) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_job_divider1, if (list.indexOf(data) == list.size - 1) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_job_divider2, if (list.indexOf(data) != list.size - 1) View.GONE else View.VISIBLE)

                            .clicked(R.id.item_job) {
                                (activity as OnFragmentItemSelectListener).onitemSelected(
                                        if (isArrow) "期限" else "产品",
                                        data.productId,
                                        if (isArrow) data.productName.trimEnd('年') else data.productName)
                            }
                }
                .attachTo(product_list)
    }
}
