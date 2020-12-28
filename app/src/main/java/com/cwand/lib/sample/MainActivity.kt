package com.cwand.lib.sample

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.Observer
import com.cwand.lib.ktx.entity.MenuEntity
import com.cwand.lib.ktx.extensions.logD
import com.cwand.lib.ktx.utils.BlurUtils
import com.cwand.lib.ktx.utils.NetworkUtils
import com.cwand.lib.ktx.viewmodel.getViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppBaseVMActivity<TestViewModel>() {

    override fun onCreate(savedInstanceState: Bundle?) {
//        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        super.onCreate(savedInstanceState)
    }

    override fun titleTextRes(): Int {
        return -1
    }

    override fun showBackIcon(): Boolean {
        return false
    }

    override fun bindLayout(): Int {
        return R.layout.activity_main
    }

    override fun initViews(savedInstanceState: Bundle?) {
        statusBarBgColor = Color.TRANSPARENT
        toolbarBgColor = Color.TRANSPARENT
        navigationBarBgColor = Color.TRANSPARENT
        updateTitleIcon(R.drawable.ic_gongzai)
        GlobalScope.launch {
            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.img_1)
            BlurUtils.blur(this@MainActivity, bitmap, 20, true)
            withContext(Dispatchers.Main) {
                window.decorView.setBackgroundDrawable(BitmapDrawable(bitmap))
            }
        }
        addMenu(MenuEntity(1, "设置语言", R.drawable.ic_multi_lang))
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

    override fun onMenuClicked(menu: MenuItem, menuId: Int, title: CharSequence) {
        if (menuId == 1) {
            startActivity(Intent(this, SwitchLanguageActivity::class.java))
        }else if (menuId == 2) {
            startActivity(Intent(this, Test3::class.java))
        }
//        toast(title)
//        val dialog = TestDialog()
//        dialog.show(supportFragmentManager, "TestDialog")

//        val mScreenBitmap: Bitmap =
//            SurfaceControl.screenshot(dims.get(0) as Int, dims.get(1) as Int)
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