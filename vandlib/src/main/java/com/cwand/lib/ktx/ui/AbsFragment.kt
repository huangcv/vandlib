package com.cwand.lib.ktx.ui

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.IntDef
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.cwand.lib.ktx.R
import com.cwand.lib.ktx.livedata.OnEventAction
import com.cwand.lib.ktx.utils.ToastUtils
import com.cwand.lib.ktx.widgets.LoadingDialog

abstract class AbsFragment : Fragment(), OnEventAction {

    //默认主题状态栏颜色
    @ColorInt
    protected var themeStatusBarBgColor = Color.WHITE

    //默认主题工具栏颜色
    @ColorInt
    protected var themeToolbarBgColor = Color.WHITE

    //默认主题导航栏颜色
    @ColorInt
    protected var themeNavigationBarBgColor = Color.WHITE

    //状态栏背景颜色,默认灰色
    @ColorInt
    var statusBarBgColor: Int = Color.WHITE
        set(value) {
            if (value != field) {
                field = value
                configStatusBarColor(value)
            }
        }

    //底部导航栏背景颜色,默认灰色
    @ColorInt
    var navigationBarBgColor: Int = Color.WHITE
        set(value) {
            if (value != field) {
                field = value
                configNavigationBarColor(value)
            }
        }

    private fun configStatusBarColor(color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity?.window?.statusBarColor = color
        }
    }

    private fun configNavigationBarColor(color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity?.window?.navigationBarColor = color
        }
    }

    //页面是否全屏,已适配>=P的异形刘海屏
    var fullScreen: Boolean = false
        set(value) {
            if (value != field) {
                field = value
                if (field) {
                    this@AbsFragment.activity?.let {
                        if (it is AbsActivity) {
                            it.fullScreen()
                        }
                    }
                } else {
                    this@AbsFragment.activity?.let {
                        if (it is AbsActivity) {
                            it.exitFullScreen()
                        }
                    }
                }
            }
        }

    private val STATE_SAVE_IS_HIDDEN = "STATE_SAVE_IS_HIDDEN"

    @IntDef(Toast.LENGTH_SHORT, Toast.LENGTH_LONG)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    internal annotation class Duration

    private var loadingDialog: DialogFragment? = null

    private var viewInit = false
    private var firstInit = true
    protected var rootView: View? = null

    @LayoutRes
    abstract fun bindLayout(): Int
    abstract fun initViews(savedInstanceState: Bundle?, contentView: View)
    abstract fun initListeners()
    abstract fun lazyInit()

    open fun initLoading(title: String?, cancelable: Boolean = true): DialogFragment {
        return LoadingDialog.get(cancelable, title)
    }

    private fun initThemeValue() {
        val typedValue = TypedValue()
        themeStatusBarBgColor = if (theme.resolveAttribute(R.attr.statusBarBgColor,
                typedValue,
                true)
        ) typedValue.data else Color.WHITE
        themeToolbarBgColor = if (theme.resolveAttribute(R.attr.toolbarBgColor,
                typedValue,
                true)
        ) typedValue.data else Color.WHITE
        themeNavigationBarBgColor = if (theme.resolveAttribute(R.attr.navigationBarBgColor,
                typedValue,
                true)
        ) typedValue.data else Color.WHITE
        checkThemeColor()
    }

    /**
     * 校验主题颜色,这里可以统一处理主题颜色
     */
    open fun checkThemeColor() {

    }

    protected fun showLoading() {
        showLoading(null)
    }

    protected fun showLoading(tip: String?) {
        showLoading(tip, canCancel = true)
    }

    protected fun showLoading(tip: String?, canCancel: Boolean = true) {
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
                    ld.showLoading(childFragmentManager)
                } else {
                    df.show(childFragmentManager, "LoadingDialog")
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

    protected fun toast(tip: String) {
        ToastUtils.toast(requireContext(), tip)
    }

    protected fun toast(@StringRes res: Int) {
        ToastUtils.toast(requireContext(), res)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initThemeValue()
        if (savedInstanceState != null) {
            try {
                val isHidden =
                    savedInstanceState.getBoolean(STATE_SAVE_IS_HIDDEN)
                if (fragmentManager != null) {
                    if (isDetached) {
                        val ft = fragmentManager!!.beginTransaction()
                        if (isHidden) {
                            ft.hide(this)
                        } else {
                            ft.show(this)
                        }
                        ft.commitNowAllowingStateLoss()
                    }
                } else {
                    if (isDetached) {
                        val ft = childFragmentManager.beginTransaction()
                        if (isHidden) {
                            ft.hide(this)
                        } else {
                            ft.show(this)
                        }
                        ft.commitNowAllowingStateLoss()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(STATE_SAVE_IS_HIDDEN, isHidden)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        rootView = createViews(inflater, container, savedInstanceState)
        viewInit = true
        return rootView
    }

    open fun createViews(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        if (rootView == null) {
            this.rootView = inflater.inflate(bindLayout(), container, false)
        } else {
            (rootView!!.parent as ViewGroup).removeAllViewsInLayout()
        }
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        innerInit(arguments, view)
    }

    open fun innerInit(arguments: Bundle?, view: View) {
        initViews(arguments, view)
        initListeners()
    }

    override fun onResume() {
        super.onResume()
        if (viewInit && firstInit) {
            lazyInit()
            firstInit = false
        }
    }

    override fun onDetach() {
        super.onDetach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onDestroyOptionsMenu() {
        super.onDestroyOptionsMenu()
    }

    override fun onDestroy() {
        super.onDestroy()
        hideLoading()
        closeKeyboard()
    }

    /**
     * 关闭系统键盘
     */
    open fun closeKeyboard() {
        val inputMethodManager =
            requireContext().getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
            requireActivity().window.decorView.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

    protected fun getStatusBarHeight(): Int {
        var result = 0
        val resId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resId > 0) {
            result = resources.getDimensionPixelSize(resId)
        }
        return result
    }

    /**
     * 发送动作到Activity
     */
    protected fun sendEventToActivity(id: Int, extraData: Any? = null) {
        activity?.let {
            if (it is OnEventAction) {
                it.onEventAction(id, extraData)
            }
        }
    }

    /**
     * 接受来自Fragment发送过来的时间
     */
    override fun onEventAction(id: Int, extraData: Any?) {
    }

}

val Fragment.theme: Resources.Theme
    get() = requireActivity().theme