package com.cwand.lib.ktx

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.ColorInt
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle
import com.cwand.lib.ktx.widgets.LoadingDialog


abstract class AbsActivity : AppCompatActivity() {

    var clickHideSoftMethodEnable: Boolean = false

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

    open fun fullScreen() {
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

    open fun exitFullScreen() {
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

    private var loadingDialog: DialogFragment? = null

    @LayoutRes
    abstract fun bindLayout(): Int
    abstract fun initViews(savedInstanceState: Bundle?)
    abstract fun initListeners()
    abstract fun initData()

    protected fun showLoading() {
        showLoading(null)
    }

    protected fun showLoading(tip: String?) {
        showLoading(null, canCancel = true)
    }

    protected fun showLoading(tip: String?, canCancel: Boolean = false) {
        if (loadingDialog == null) {
            loadingDialog = initLoading(tip, canCancel)
        }
        loadingDialog?.let { df ->
            df.isCancelable = canCancel
            val dialog = df.dialog
            if (dialog == null || (!dialog.isShowing)) {
                if (loadingDialog is LoadingDialog) {
                    val ld = loadingDialog as LoadingDialog
                    tip?.let {
                        ld.updateTitle(it)
                    }
                    ld.showLoading(supportFragmentManager)
                } else {
                    df.show(supportFragmentManager, "LoadingDialog")
                }
            }
        }
    }

    protected fun hideLoading() {
        loadingDialog?.let { df ->
            if (df is LoadingDialog) {
                df.hideLoading()
            } else {
                df.dismiss()
            }
        }
    }

    open fun initLoading(title: String?, cancelable: Boolean = true): DialogFragment {
        return LoadingDialog.get(cancelable, title)
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

    protected fun toast(tip: CharSequence) {
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

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (clickHideSoftMethodEnable) {
            ev?.let {
                //把操作放在用户点击的时候 得到当前页面的焦点,ps:有输入框的页面焦点一般会被输入框占据
                if (it.action == MotionEvent.ACTION_MOVE || it.action == MotionEvent.ACTION_UP) {
                    val focusView = currentFocus
                    //判断用户点击的是否是输入框以外的区域
                    if (isShouldHideKeyboard(focusView, it)) {
                        closeKeyboard()
                        (focusView as EditText).clearFocus()
                    }
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun isShouldHideKeyboard(view: View?, event: MotionEvent): Boolean {
        view?.let {
            //判断得到的焦点控件是否包含EditText
            if ((view is EditText)) {
                val l = IntArray(2)
                view.getLocationInWindow(l)
                //得到输入框在屏幕中上下左右的位置
                val left = l[0]
                val top = l[1]
                val bottom = top + view.getHeight()
                val right = left + view.getWidth()
                return !(event.x > left && event.x < right
                        && event.y > top && event.y < bottom)
            }
        }
        // 如果焦点不是EditText则忽略
        return false
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