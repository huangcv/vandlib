package com.cwand.lib.sample

import android.os.Bundle
import com.cwand.lib.ktx.BaseTitleActivity
import com.cwand.lib.ktx.entity.MenuEntity
import com.cwand.lib.ktx.ext.delayRun
import com.cwand.lib.ktx.utils.ActManager

/**
 * @author : chunwei
 * @date : 2020/12/3
 * @description : 测试
 *
 */
class Test4 : BaseTitleActivity() {
    override fun titleTextRes(): Int {
        return R.string.app_name
    }

    override fun initViews(savedInstanceState: Bundle?) {
        delayRun({
            ActManager.finishByAlias("Test3")
        }, 5000)
    }

    override fun addActivityToStack() {
        ActManager.add(this, "Test4")
    }

    override fun removeActivityFromStack() {
        ActManager.remove(this)
    }
}