package com.cwand.lib.ktx.viewmodel

import androidx.lifecycle.*
import com.cwand.lib.ktx.exception.AppException
import com.cwand.lib.ktx.livedata.EventLiveData
import com.cwand.lib.ktx.ext.Launcher
import com.cwand.lib.ktx.ext.LauncherCallback
import com.cwand.lib.ktx.ext.launcher
import com.cwand.lib.ktx.utils.NetworkState
import kotlinx.coroutines.CoroutineScope

open class BaseViewModel : ViewModel(), DefaultLifecycleObserver {

    val loadingState: LoadingChange by lazy { LoadingChange() }

    val networkState: EventLiveData<NetworkState> by lazy { EventLiveData<NetworkState>() }

    inner class LoadingChange {
        /**
         * 显示加载
         */
        val showLoading by lazy { EventLiveData<String>() }

        /**
         * 隐藏加载
         */
        val hideLoading by lazy { EventLiveData<Boolean>() }
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

fun BaseViewModel.launcher(
    showLoading: Boolean = false,
    loadingMsg: String = "",
    block: suspend CoroutineScope.() -> Unit,
): Launcher {
    return viewModelScope.launcher(object : LauncherCallback {
        override fun onStart() {
            if (showLoading) {
                loadingState.showLoading.postValue(loadingMsg)
            }
        }

        override fun onException(exception: AppException) {
        }

        override fun onSuccess() {
        }

        override fun onCompleted() {
            if (showLoading) {
                loadingState.hideLoading.postValue(true)
            }
        }
    }, block)
}
