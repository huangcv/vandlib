package com.cwand.lib.sample

import android.os.Bundle
import android.view.View
import com.cwand.lib.ktx.AbsFragment

class TestFragment : AbsFragment() {

    companion object {
        fun newInstance(): TestFragment {
            val args = Bundle()
            val fragment = TestFragment()
            fragment.arguments = args
            return fragment
        }
    }


    override fun bindLayout(): Int {
        return R.layout.fragment_test
    }

    override fun initViews(savedInstanceState: Bundle?, contentView: View) {
    }

    override fun initListeners() {
    }

    override fun lazyInit() {
        toast("懒加载数据")
    }
}