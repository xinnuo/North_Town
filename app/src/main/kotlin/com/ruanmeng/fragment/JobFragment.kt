package com.ruanmeng.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ruanmeng.base.BaseFragment
import com.ruanmeng.base.load_Linear
import com.ruanmeng.model.CommonData
import com.ruanmeng.north_town.R
import kotlinx.android.synthetic.main.fragment_job_first.*
import net.idik.lib.slimadapter.SlimAdapter
import java.util.*

class JobFragment : BaseFragment() {

    private lateinit var list: ArrayList<CommonData>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_job_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init_title()
    }

    override fun init_title() {
        activity?.let { job_list.load_Linear(it) }

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_job_list) { data, injector ->
                    injector.visibility(R.id.item_job_divider1, if (list.indexOf(data) == list.size - 1) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_job_divider2, if (list.indexOf(data) != list.size - 1) View.GONE else View.VISIBLE)

                            .clicked(R.id.item_job) { }
                }
                .attachTo(job_list)
    }
}
