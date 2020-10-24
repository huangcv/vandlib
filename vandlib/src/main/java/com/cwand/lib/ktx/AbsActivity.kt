package com.cwand.lib.ktx

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.annotation.ColorInt
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle
import com.cwand.lib.ktx.ToastUtils


abstract class AbsActivity : AppCompatActivity() {

    //页面是否全屏,已适配>=P的异形刘海屏
    var fullScreen: Boolean = false
        set(value) {
            if (value != field) {
                field = value
                if (field) {
                    fullScreen()
                } else {
                    exitFullScreen()
                }
            }
        }

    //状态栏背景颜色,默认灰色
    @ColorInt
    var statusBarBgColor: Int = Color.parseColor("#FF292D38")
        set(value) {
            if (value != field) {
                field = value
                configStatusBarColor()
            }
        }

    //状态栏字体颜色是否为亮色(即颜色为暗色),默认false
    var statusBarLightMode: Boolean = false
        set(value) {
            if (value != field) {
                field = value
                configStatusBar()
            }
        }

    //内容是否下沉到状态栏之下,默认false
    var contentImmersive: Boolean = false
        set(value) {
            if (value != field) {
                field = value
                configStatusBar()
            }
        }

    private val START = 1
    private val RESUME = 2
    private var initStatus = -1
        set(value) {
            if (field >= 2) {
                return
            }
            field = value
            canInitData()
        }

    private fun canInitData() {
        if (initStatus >= RESUME) {
            initData()
        }
    }

    private fun configStatusBar() {
        if (fullScreen)
            return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            var value = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            if (contentImmersive) {
                value = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                var modeValue = View.SYSTEM_UI_FLAG_VISIBLE
                if (statusBarLightMode) {
                    modeValue = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                }
                value = value or modeValue
            }
            window.decorView.systemUiVisibility = value
        }
    }

    private fun fullScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val decorView = window.decorView
            decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val lp = window.attributes
                lp.layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                window.attributes = lp
            }
        } else {
            //去除标题栏
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            //去除状态栏
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    private fun exitFullScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val lp = window.attributes
            lp.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT
            window.attributes = lp
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            initStatusBar()
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN or WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }

    }

    private fun configStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = statusBarBgColor
        }
    }

    val loadingDialog: Dialog by lazy { initLoading() ?: Dialog(this) }

    @LayoutRes
    abstract fun bindLayout(): Int
    abstract fun initViews(savedInstanceState: Bundle?)
    abstract fun initListeners()
    abstract fun initData()

    protected fun showLoading() {
        showLoading(null)
    }

    protected fun showLoading(tip: String?) {
        showLoading(null, canCancel = false)
    }

    protected fun showLoading(tip: String?, canCancel: Boolean = false) {
        if (!loadingDialog.isShowing) {
            loadingDialog.setCancelable(canCancel)
            loadingDialog.show()
        }
    }

    protected fun hideLoading() {
        loadingDialog.dismiss()
    }

    open fun initLoading(): Dialog? {
        return null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleBundle(savedInstanceState)
        initStatus = START
        initStatusBar()
        innerInit(savedInstanceState)
    }

    open fun initStatusBar() {
        if (fullScreen) {
            fullScreen()
            return
        }
        configStatusBarColor()
        configStatusBar()
    }

    open fun innerInit(savedInstanceState: Bundle?) {
        setContentView(bindLayout())
        initViews(savedInstanceState)
        initListeners()
    }

    open fun handleBundle(bundle: Bundle?) {

    }

    override fun onResume() {
        super.onResume()
        if (initStatus < RESUME) {
            initStatus = RESUME
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleBundle(intent?.extras)
    }

    protected fun toast(tip: String) {
        ToastUtils.toast(this, tip)
    }

    protected fun toast(@StringRes res: Int) {
        ToastUtils.toast(this, res)
    }

    protected fun putFragment(fragment: Fragment, @IdRes contentId: Int) {
        putFragment(fragment, contentId, true)
    }

    protected fun putFragment(fragment: Fragment, @IdRes contentId: Int, cleanView: Boolean) {
        try {
            if (cleanView) {
                try {
                    findViewById<ViewGroup>(contentId)?.removeAllViewsInLayout()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(contentId, fragment)
            fragmentTransaction.setMaxLifecycle(fragment, Lifecycle.State.RESUMED)
            fragmentTransaction.commitNowAllowingStateLoss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    protected fun removeFragment(fragment: Fragment) {
        if (supportFragmentManager.fragments.contains(fragment)) {
            supportFragmentManager.fragments.remove(fragment)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        hideLoading()
        closeKeyboard()
    }

    /**
     * 关闭系统键盘
     */
    protected open fun closeKeyboard() {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
            window.decorView.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }
}