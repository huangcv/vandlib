package com.cwand.lib.sample

import android.graphics.Color
import android.os.Bundle
import com.cwand.lib.ktx.BaseTitleActivity
import com.cwand.lib.ktx.entity.MenuEntity
import com.cwand.lib.ktx.ext.*
import com.cwand.lib.ktx.utils.Logger
import com.cwand.lib.ktx.widgets.BFDialog

class MainActivity : BaseTitleActivity() {
    override fun titleTextRes(): Int {
        return R.string.app_name
    }

    override fun showBackIcon(): Boolean {
        return true
    }

    override fun bindLayout(): Int {
        return R.layout.activity_main
    }

    override fun initViews(savedInstanceState: Bundle?) {
        addMenu(MenuEntity("显示弹窗", titleColor = Color.WHITE))
//        "我是测试logD".logD()
//        "我是测试logE".logE()
//        "我是测试logW".logW()
//        "我是测试logI".logI()
//        "我是测试logV".logV()
//        val list = mutableListOf<String>()
//        list.logD()
//        Logger.logD("Logger")
////        loading_view.postDelayed(Runnable {
////            addMenu(MenuEntity("1", titleColor =  Color.YELLOW), MenuEntity("2", android.R.drawable.ic_menu_close_clear_cancel), MenuEntity("3"))
////        }, 3000)
////
//        loading_view.postDelayed(Runnable {
//            addMenu(MenuEntity("1", titleColor =  Color.RED))
//            toolbarElevation = 30f
//        }, 6000)
        putFragment(TestFragment.newInstance(), R.id.fragment_root)
//        loading_view?.onClick{
//        }
//
//        var test1:String? = null
//        var test2 = "huangchunwei@163.com"
//        println(test1.isEmail())
//        println(test2.isEmail())
    }

    override fun onMenuClicked(id: Int, title: CharSequence) {
        toast(title)
        val dialog = TestDialog()
        dialog.show(supportFragmentManager, "TestDialog")
    }

    override fun initListeners() {
    }

    override fun initData() {
    }

}