package com.cwand.lib.sample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cwand.lib.ktx.widgets.*
import kotlinx.android.synthetic.main.activity_widgets.*

/**
 * @author : chunwei
 * @date : 2020/12/29
 * @description : 控件展示页面
 *
 */
class WidgetsActivity : AppBaseTitleActivity() {

    private lateinit var stateHolder: StateHolder
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
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context))
        }
        stateHolder =
            NiceLoading
                .bind(this, R.id.fl_widget_content_root)
                .defaultState(State.LOADING)
                .noNetworkClick {
                    toast("暂无网络")
                }
                .emptyClick(R.id.tv_empty) {
                    toast("空数据")
                }
                .build()
    }

    override fun initData() {
        for (i in 0 until 30) {
            data.add("第 $i 个")
        }
        myAdapter?.let {
            it.setData(data)
            it.notifyDataSetChanged()
        }
    }

    fun rootClick(view: View) {
        toast("root clicked")
    }

    fun showLoading(view: View) {
        stateHolder.showLoading()
    }

    fun showError(view: View) {
        stateHolder.showError()
    }

    fun showEmpty(view: View) {
        stateHolder.showEmpty()
    }

    fun showNoNetwork(view: View) {
        stateHolder.showNoNetwork()
    }

    fun showContent(view: View) {
        stateHolder.showContent()
    }

    private class MyAdapter : RecyclerView.Adapter<VH>() {
        private var data: List<String>? = null
        private var layoutInflater: LayoutInflater? = null
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
            data?.let {
                holder.setText(it[position])
            }
        }

        override fun getItemCount(): Int {
            return data?.size ?: 0
        }

    }

    private class VH(private val itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView

        init {
            textView = itemView.findViewById(R.id.item_title)
        }

        fun setText(text: CharSequence) {
            textView.text = text
        }
    }

}