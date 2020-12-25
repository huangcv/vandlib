package com.cwand.lib.sample

import android.os.Bundle
import com.cwand.lib.ktx.ui.BaseTitleActivity
import com.cwand.lib.ktx.ext.delayRun
import com.cwand.lib.ktx.ui.AbsActivity
import com.cwand.lib.ktx.utils.ActManager

/**
 * @author : chunwei
 * @date : 2020/12/3
 * @description : 测试
 *
 */
class Test4 : AbsActivity() {

    override fun bindLayout(): Int {
        return R.layout.activity_test_4
    }

    override fun initViews(savedInstanceState: Bundle?) {
        putFragment(Test4Fragment.newInstance(), R.id.root_test_4)
        delayRun({
            ActManager.finishByAlias("Test3")
        }, 5000)
    }

    override fun initListeners() {
    }

    override fun initData() {
    }

    override fun addActivityToStack() {
        ActManager.add(this, "Test4")
    }

    override fun removeActivityFromStack() {
        ActManager.remove(this)
    }
}