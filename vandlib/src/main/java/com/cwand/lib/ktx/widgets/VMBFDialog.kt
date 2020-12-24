package com.cwand.lib.ktx.widgets

import android.os.Bundle
import com.cwand.lib.ktx.viewmodel.BaseViewModel

/**
 * @author : chunwei
 * @date : 2020/12/7
 * @description : 持有ViewModel的弹窗基类
 *
 */
abstract class VMBFDialog<VM : BaseViewModel> : BFDialog() {

    val viewModel: VM by lazy {
        createViewModel().apply {
            lifecycle.addObserver(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initObservers()
    }

    abstract fun createViewModel(): VM
    abstract fun initObservers()
}