package com.cwand.lib.sample

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.cwand.lib.ktx.AbsFragment
import com.cwand.lib.ktx.BaseTitleFragment
import com.cwand.lib.ktx.entity.MenuEntity
import com.cwand.lib.ktx.ext.delayRun
import com.cwand.lib.ktx.ext.onClick
import kotlinx.android.synthetic.main.fragment_test.*

class TestFragment : BaseTitleFragment() {

    companion object {
        fun newInstance(): TestFragment {
            val args = Bundle()
            val fragment = TestFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun isShowToolbar(): Boolean {
        return false
    }

    override fun titleTextRes(): Int {
        return R.string.app_name
    }

    override fun bindLayout(): Int {
        return R.layout.fragment_test
    }

    override fun initViews(savedInstanceState: Bundle?, contentView: View) {
        lightning.onClick {
            if (!lightning.isRunning) {
                lightning.startRotate(3000)
            } else {
                lightning.stopRotate()
            }
        }
    }

    override fun onMenuClicked(index: Int, title: CharSequence) {
        startActivity(Intent(requireContext(), Test3::class.java))
    }

    override fun initListeners() {
    }

    override fun lazyInit() {
        toast("懒加载数据")
    }
}