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

    abstract fun createViewModel(): VM
    abstract fun initObservers()

    override fun innerInit(savedInstanceState: Bundle?) {
        super.innerInit(savedInstanceState)
        initObservers()
    }

}