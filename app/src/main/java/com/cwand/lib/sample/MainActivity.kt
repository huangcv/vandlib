package com.cwand.lib.sample

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.lifecycle.Observer
import com.cwand.lib.ktx.*
import com.cwand.lib.ktx.entity.MenuEntity
import com.cwand.lib.ktx.ext.launcher
import com.cwand.lib.ktx.ext.logD
import com.cwand.lib.ktx.interceptors.log.LogInterceptor
import com.cwand.lib.ktx.utils.LanguageType
import com.cwand.lib.ktx.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : BaseVMActivity<TestViewModel>() {

    override fun titleTextRes(): Int {
        return R.string.app_name
    }

    override fun showBackIcon(): Boolean {
        return true
    }

    override fun bindLayout(): Int {
        return R.layout.activity_main
    }

    override fun initViews(savedInstanceState: Bundle?) {
        addMenu(MenuEntity("设置语言"))
//        "我是测试logD".logD()
//        "我是测试logE".logE()
//        "我是测试logW".logW()
//        "我是测试logI".logI()
//        "我是测试logV".logV()
//        val list = mutableListOf<String>()
//        list.logD()
//        Logger.logD("Logger")
////        loading_view.postDelayed(Runnable {
////            addMenu(MenuEntity("1", titleColor =  Color.YELLOW), MenuEntity("2", android.R.drawable.ic_menu_close_clear_cancel), MenuEntity("3"))
////        }, 3000)
////
//        loading_view.postDelayed(Runnable {
//            addMenu(MenuEntity("1", titleColor =  Color.RED))
//            toolbarElevation = 30f
//        }, 6000)
        putFragment(TestFragment.newInstance(), R.id.fragment_root)
//        loading_view?.onClick{
//        }
//
//        var test1:String? = null
//        var test2 = "huangchunwei@163.com"
//        println(test1.isEmail())
//        println(test2.isEmail())
    }

    override fun onMenuClicked(id: Int, title: CharSequence) {
//        toast(title)
//        val dialog = TestDialog()
//        dialog.show(supportFragmentManager, "TestDialog")
        startActivity(Intent(this, SwitchLanguageActivity::class.java))
    }


    override fun initListeners() {

        //https://run.mocky.io/v3/f7540cf9-95ee-4ccd-88f5-c9a9f297a476
//        val client = OkHttpClient.Builder()
//            .addInterceptor(LogInterceptor.Builder().build())
//            .build()
//        val retrofitBuilder = Retrofit.Builder().baseUrl("https://run.mocky.io/").client(client)
//        retrofitBuilder.addConverterFactory(GsonConverterFactory.create())
//        val retrofit = retrofitBuilder.build()
//        val testApi = retrofit.create(TestApi::class.java)
//        val params = mutableMapOf("client" to "Android", "md5" to "dslfjadfl231dsfjauj12'WEFdofha")
//        testApi.test(params)?.enqueue(object : Callback<TestData?> {
//            override fun onResponse(call: Call<TestData?>, response: Response<TestData?>) {
//
//            }
//
//            override fun onFailure(call: Call<TestData?>, t: Throwable) {
//            }
//        })
//        RepositoryConfig.networkRepositoryConfig =
//            NetworkRepositoryConfig.Builder().readTimeout(1).writeTimeout(1).connectTimeout(1)
//                .build()
//        nativeRetrofitRequest()
    }

//    private fun nativeRetrofitRequest() {
//        val params = mutableMapOf("client" to "Android", "md5" to "dslfjadfl231dsfjauj12'WEFdofha")
//        RetrofitManager
//            .createService(TestApi::class.java)
//            .test(params)
//            ?.enqueue(object : Callback<TestData?> {
//                override fun onResponse(call: Call<TestData?>, response: Response<TestData?>) {
//
//                }
//
//                override fun onFailure(call: Call<TestData?>, t: Throwable) {
//                }
//            })
//    }

    override fun initData() {
        GlobalScope.launch(Dispatchers.IO) {
            NetworkUtils.networkConnected().logD()
        }.start()
    }

    override fun createViewModel(): TestViewModel = getViewModel()

    override fun initObservers() {
        viewModel.testLiveData.observe(this, Observer {
            println("收到请求:$it")
        })
    }

}