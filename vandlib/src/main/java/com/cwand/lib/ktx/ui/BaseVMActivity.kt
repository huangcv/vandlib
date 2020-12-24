package com.cwand.lib.ktx.ui

import android.os.Bundle
import com.cwand.lib.ktx.viewmodel.BaseViewModel

/**
 * Activity基类
 */
abstract class BaseVMActivity<VM : BaseViewModel> : BaseTitleActivity() {

    val viewModel: VM by lazy {
        createViewModel().apply {
            lifecycle.addObserver(this)
            loadingState.showLoading.observe(this@BaseVMActivity) {
                showLoading(it)
            }
            loadingState.hideLoading.observe(this@BaseVMActivity) {
                hideLoading()
            }
        }
    }

    abstract fun createViewModel(): VM
    abstract fun initObservers()

    override fun innerInit(savedInstanceState: Bundle?) {
        super.innerInit(savedInstanceState)
        initObservers()
    }

}