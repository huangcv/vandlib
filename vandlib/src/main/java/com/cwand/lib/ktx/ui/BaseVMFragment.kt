package com.cwand.lib.ktx.ui

import android.os.Bundle
import android.view.View
import com.cwand.lib.ktx.viewmodel.BaseViewModel

abstract class BaseVMFragment<VM : BaseViewModel> : BaseTitleFragment() {
    val viewModel: VM by lazy {
        createViewModel().apply {
            lifecycle.addObserver(this)
            loadingState.showLoading.observe(this@BaseVMFragment){
                showLoading(it)
            }
            loadingState.hideLoading.observe(this@BaseVMFragment){
                hideLoading()
            }
        }
    }

    abstract fun createViewModel(): VM


}