package com.cwand.lib.sample

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.cwand.lib.ktx.ext.onClick
import com.cwand.lib.ktx.ui.BaseTitleFragment
import com.cwand.lib.ktx.utils.BlurUtils
import kotlinx.android.synthetic.main.fragment_test.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    override fun onMenuClicked(menu: MenuItem, menuId: Int, title: CharSequence) {
        startActivity(Intent(requireContext(), Test3::class.java))
    }
    override fun initListeners() {
    }

    override fun lazyInit() {
//        toast("懒加载数据")
    }
}