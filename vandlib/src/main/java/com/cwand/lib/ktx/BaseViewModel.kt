package com.cwand.lib.ktx

import androidx.lifecycle.*
import com.cwand.lib.ktx.BooleanLiveData
import com.cwand.lib.ktx.entity.BaseResp
import com.cwand.lib.ktx.ext.Launcher
import com.cwand.lib.ktx.ext.launcher
import kotlinx.coroutines.CoroutineScope

open class BaseViewModel : ViewModel(), DefaultLifecycleObserver {

    val loadingState: BooleanLiveData by lazy { BooleanLiveData() }

    fun ViewModel.launcher(
        block: suspend CoroutineScope.() -> Unit,
    ): Launcher {
        return viewModelScope.launcher(block)
    }

}

/**
 * 创建viewModel
 * @receiver ViewModelStoreOwner
 * @return V
 */
inline fun <reified V : ViewModel> ViewModelStoreOwner.getViewModel(): V {
    return ViewModelProvider(this).get(V::class.java)
}




