package com.cwand.lib.ktx

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
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
import androidx.fragment.app.Fragment

abstract class AbsFragment : Fragment(), OnEventAction {

    //状态栏背景颜色,默认灰色
    @ColorInt
    var statusBarBgColor: Int = Color.parseColor("#FF292D38")
        set(value) {
            if (value != field) {
                field = value
                configStatusBarColor()
            }
        }

    private fun configStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            requireActivity().window.statusBarColor = statusBarBgColor
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

    val loadingDialog: Dialog by lazy { initLoading() ?: Dialog(requireContext()) }

    private var viewInit = false
    private var firstInit = true
    protected var rootView: View? = null

    @LayoutRes
    abstract fun bindLayout(): Int
    abstract fun initViews(savedInstanceState: Bundle?, contentView: View)
    abstract fun initListeners()
    abstract fun lazyInit()

    open fun initLoading(): Dialog? {
        return null
    }

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

    protected open fun createViews(
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

    protected open fun innerInit(arguments: Bundle?, view: View) {
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
    protected open fun closeKeyboard() {
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