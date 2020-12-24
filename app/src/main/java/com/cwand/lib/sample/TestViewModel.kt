package com.cwand.lib.sample

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.cwand.lib.ktx.AndLib
import com.cwand.lib.ktx.ext.logD
import com.cwand.lib.ktx.viewmodel.BaseViewModel
import com.cwand.lib.ktx.utils.ToastUtils
import com.cwand.lib.ktx.viewmodel.launcher

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
        launcher(true, "快点出来...") {
            testLiveData.value = repository.getTestData()
        }.onStart {
//            "请求开始".logD()
//            ToastUtils.toast(AndLib.getAppContext(), "请求开始")
        }.onSuccess {
//            "请求成功".logD()
//            ToastUtils.toast(AndLib.getAppContext(), "请求成功")
        }.onException {
//            "请求异常: ${it.code}, ${it.error}".logD()
//            ToastUtils.toast(AndLib.getAppContext(),
//                "${it.code}, ${it.error}")
        }.onCompleted {
//            "请求完成".logD()
//            ToastUtils.toast(AndLib.getAppContext(), "请求完成")
        }.start()

//        launcher {
//            testLiveData.value = repository.getTestData()
//        }.onStart {
//            ToastUtils.toast(AndLib.getAppContext(), "请求开始")
//        }.onSuccess {
//            ToastUtils.toast(AndLib.getAppContext(), "请求成功")
//        }.onException {
//            ToastUtils.toast(AndLib.getAppContext(),
//                "${it.code}, ${it.error}")
//        }.onCompleted {
//            ToastUtils.toast(AndLib.getAppContext(), "请求完成")
//        }.start()
    }

}