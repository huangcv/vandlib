package com.cwand.lib.sample

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import com.cwand.lib.ktx.BaseTitleActivity
import com.cwand.lib.ktx.entity.MenuEntity
import com.cwand.lib.ktx.ext.logD
import com.cwand.lib.ktx.utils.NetworkUtils

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
        addMenu(MenuEntity("中文"), MenuEntity("英文"))
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
//        toast(title)
//        val dialog = TestDialog()
//        dialog.show(supportFragmentManager, "TestDialog")
        if (title == "中文") {
            changeLanguage("zh") {
                restartActivity()
            }
        } else {
            changeLanguage("en") {
                restartActivity()
            }
        }
    }

    private fun restartActivity() {
        // 不同的版本，使用不同的重启方式，达到最好的效果
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            // 6.0 以及以下版本，使用这种方式，并给 activity 添加启动动画效果，可以规避黑屏和闪烁问题
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        } else {
            // 6.0 以上系统直接调用重新创建函数，可以达到无缝切换的效果
            recreate()
        }
    }

    override fun initListeners() {
    }

    override fun initData() {
        Thread {
            NetworkUtils.networkConnected().logD()
        }.start()
    }

}