package com.cwand.lib.ktx

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

open class EventLiveData<T> : MutableLiveData<T>() {

    /**
     * 只有页面在活动的时候才会回调 {@link Observer.onChanged(T)}
     * 如果需要实时接收到消息,可以使用{@see LiveData.observeForever(Observer)} 此方法需要手动remove,防止内存泄漏
     */
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner, observer)
    }

    override fun observeForever(observer: Observer<in T>) {
        super.observeForever(observer)
    }

    override fun setValue(value: T?) {
        if (value == null) {
            return
        }
        super.setValue(value)
    }

    override fun postValue(value: T?) {
        if (value == null) {
            return
        }
        super.postValue(value)
    }

}