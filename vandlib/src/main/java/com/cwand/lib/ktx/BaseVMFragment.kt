package com.cwand.lib.ktx

import android.os.Bundle
import android.view.View

abstract class BaseVMFragment<VM : BaseViewModel> : BaseTitleFragment() {
    val viewModel: VM by lazy {
        createViewModel().apply {
            lifecycle.addObserver(this)
            loadingState.observe(this@BaseVMFragment, { showLoading ->
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

    override fun innerInit(arguments: Bundle?, view: View) {
        initObservers()
        super.innerInit(arguments, view)
    }

}