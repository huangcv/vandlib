package com.cwand.lib.sample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cwand.lib.ktx.widgets.CycleLayoutManager
import com.cwand.lib.ktx.widgets.RepeatLayoutManager
import kotlinx.android.synthetic.main.activity_widgets.*

/**
 * @author : chunwei
 * @date : 2020/12/29
 * @description : 控件展示页面
 *
 */
class WidgetsActivity : AppBaseTitleActivity() {

    val data = mutableListOf<String>()
    private var myAdapter: MyAdapter? = null

    override fun titleTextRes(): Int {
        return R.string.widgets
    }

    override fun bindLayout(): Int {
        return R.layout.activity_widgets
    }

    override fun initViews(savedInstanceState: Bundle?) {
        recyclerView?.apply {
            myAdapter = MyAdapter()
            adapter = myAdapter
            layoutManager = RepeatLayoutManager(RecyclerView.HORIZONTAL)
        }
    }

    override fun initData() {
        for (i in 0 until 30) {
            data.add(i.toString())
        }
        myAdapter?.let {
            it.setData(data)
            it.notifyDataSetChanged()
        }
    }

    private class MyAdapter : RecyclerView.Adapter<VH>() {
        private var data: List<String>? = null
        private var layoutInflater : LayoutInflater? = null
        fun setData(d: List<String>) {
            this.data = d
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            if (layoutInflater == null) {
                layoutInflater = LayoutInflater.from(parent.context)
            }
            val inflaterView = layoutInflater?.inflate(R.layout.adapter_cycle_layout, parent, false)
            return VH(inflaterView!!)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
        }

        override fun getItemCount(): Int {
            return data?.size ?: 0
        }

    }

    private class VH(private val itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

}