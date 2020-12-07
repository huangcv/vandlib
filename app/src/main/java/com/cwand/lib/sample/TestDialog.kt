package com.cwand.lib.sample

import android.view.Gravity
import com.cwand.lib.ktx.widgets.BFDialog

/**
 * @author : chunwei
 * @date : 2020/12/7
 * @description : 测试弹窗
 *
 */
class TestDialog : BFDialog() {

    override fun getGravity(): Int {
        return Gravity.CENTER
    }
}