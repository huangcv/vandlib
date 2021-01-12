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
        putFragment(FunctionNavigationFragment.newInstance(), R.id.fragment_root)
    }

    override fun onMenuClicked(menu: MenuItem, menuId: Int, title: CharSequence) {
        if (menuId == 1) {
            startActivity(Intent(this, SwitchLanguageActivity::class.java))
        }else if (menuId == 2) {
            startActivity(Intent(this, Test3::class.java))
        }
    }

    override fun initListeners() {
    }
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