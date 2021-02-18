package com.cwand.lib.sample

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.cwand.lib.ktx.extensions.onClick
import com.cwand.lib.ktx.ui.BaseFragment
import kotlinx.android.synthetic.main.fragment_fun_nav.*

/**
 * @author : chunwei
 * @date : 2020/12/29
 * @description :功能导航
 *
 */
class FunctionNavigationFragment : BaseFragment() {

    companion object {
        fun newInstance(): FunctionNavigationFragment {
            return FunctionNavigationFragment()
        }
    }

    override fun bindLayout(): Int {
        return R.layout.fragment_fun_nav
    }

    override fun initViews(savedInstanceState: Bundle?, contentView: View) {
//        widgets?.onClick {
//            startActivity(Intent(requireContext(), WidgetsActivity::class.java))
//        }

//        capture_screen?.onClick {
//
//        }

//        open_web?.onClick {
//            startActivity(Intent(requireContext(), WebActivity::class.java))
//        }
    }

    override fun initListeners() {
    }

    override fun lazyInit() {
    }
}