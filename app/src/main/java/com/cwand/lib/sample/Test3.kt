package com.cwand.lib.sample

import android.content.Intent
import android.os.Bundle
import com.cwand.lib.ktx.ui.BaseTitleActivity
import com.cwand.lib.ktx.entity.MenuEntity
import com.cwand.lib.ktx.utils.ActManager

/**
 * @author : chunwei
 * @date : 2020/12/3
 * @description : 测试3
 *
 */
class Test3 : BaseTitleActivity() {
    override fun titleTextRes(): Int {
        return R.string.app_name
    }

    override fun initViews(savedInstanceState: Bundle?) {
        addMenu(MenuEntity(title = "打开页面4"))
        showLoading()
    }

    override fun onMenuClicked(id: Int, title: CharSequence) {
        startActivity(Intent(this, Test4::class.java))
    }

    override fun addActivityToStack() {
        ActManager.add(this, "Test3")
    }

    override fun removeActivityFromStack() {
        ActManager.removeByAlias("Test3")
    }

}