package com.cwand.lib.ktx

import android.os.Bundle

/**
 * Activity基类
 */
abstract class BaseVMActivity<VM : BaseViewModel> : BaseTitleActivity() {

    val viewModel: VM by lazy {
        createViewModel().apply {
            lifecycle.addObserver(this)
            loadingState.observe(this@BaseVMActivity, { showLoading ->
                if (showLoading) {
                    showLoading()
                } else {
                    hideLoading()
                }
            })
        }
    }

    abstract fun initObservers()
    abstract fun createViewModel(): VM

    override fun innerInit(savedInstanceState: Bundle?) {
        super.innerInit(savedInstanceState)
        initObservers()
    }

}