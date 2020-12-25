package com.cwand.lib.sample

import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.cwand.lib.ktx.ui.BaseTitleFragment

/**
 * @author : chunwei
 * @date : 2020/12/25
 * @description :
 *
 */
class Test4Fragment : BaseTitleFragment() {

    companion object {
        fun newInstance(): Test4Fragment {
            val args = Bundle()
            val fragment = Test4Fragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun checkThemeColor() {
        themeToolbarBgColor = Color.GRAY
        themeStatusBarBgColor = Color.GRAY
        themeNavigationBarBgColor = Color.GRAY
    }

    override fun titleTextRes(): Int {
        return R.string.app_name
    }

    override fun bindLayout(): Int {
        return R.layout.fragment_test
    }

    override fun initViews(savedInstanceState: Bundle?, contentView: View) {
        statusBarBgColor = themeStatusBarBgColor
        navigationBarBgColor = themeNavigationBarBgColor
    }

    override fun initListeners() {
    }

    override fun lazyInit() {
    }
}