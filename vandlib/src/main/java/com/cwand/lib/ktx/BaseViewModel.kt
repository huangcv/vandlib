package com.cwand.lib.ktx

import androidx.lifecycle.*
import com.cwand.lib.ktx.BooleanLiveData

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
