package com.cwand.lib.ktx

import androidx.lifecycle.*
import com.cwand.lib.ktx.BooleanLiveData
import com.cwand.lib.ktx.entity.BaseResp

open class BaseViewModel : ViewModel(), DefaultLifecycleObserver {

    val loadingState: BooleanLiveData by lazy { BooleanLiveData() }
}

/**
 * 创建viewModel
 * @receiver ViewModelStoreOwner
 * @return V
 */
inline fun <reified V : ViewModel> ViewModelStoreOwner.getViewModel(): V {
    return ViewModelProvider(this).get(V::class.java)
}

//fun <T> BaseViewModel.request(block: suspend () -> BaseResp<T>, )
