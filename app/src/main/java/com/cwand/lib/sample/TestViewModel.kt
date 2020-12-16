package com.cwand.lib.sample

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.cwand.lib.ktx.AndLib
import com.cwand.lib.ktx.BaseViewModel
import com.cwand.lib.ktx.ToastUtils
import com.cwand.lib.ktx.ext.Launcher

/**
 * @author : chunwei
 * @date : 2020/12/15
 * @description : 测试ViewModel
 *
 */
class TestViewModel : BaseViewModel() {

    val testLiveData = MutableLiveData<List<TestBean>>()


    val repository = TestRepository()

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        refreshData()
    }

    private fun refreshData() {
        launcher {
            testLiveData.value = repository.getTestData()
        }.onException {
            ToastUtils.toast(AndLib.getAppContext(), it.message ?: "")
            println("hate:onException ${it.message}")
        }.onFinally {
            ToastUtils.toast(AndLib.getAppContext(), "请求结束")
            println("hate:onFinally")
        }.start()
    }

}