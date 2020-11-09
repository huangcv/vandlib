package com.cwand.lib.sample

import android.graphics.Color
import android.os.Bundle
import com.cwand.lib.ktx.BaseTitleActivity
import com.cwand.lib.ktx.entity.MenuEntity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseTitleActivity() {


    override fun bindLayout(): Int {
        return R.layout.activity_main
    }

    override fun initViews(savedInstanceState: Bundle?) {
        loading_view.postDelayed(Runnable {
            addMenu(MenuEntity("1", titleColor =  Color.YELLOW), MenuEntity("2", android.R.drawable.ic_menu_close_clear_cancel), MenuEntity("3"))
        }, 3000)

        loading_view.postDelayed(Runnable {
            addMenu(MenuEntity("1", titleColor =  Color.RED))
        }, 6000)
    }

    override fun onMenuClicked(index: Int, title: CharSequence) {
        toast(title)
    }

    override fun initListeners() {
    }

    override fun initData() {
    }

}