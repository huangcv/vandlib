package com.cwand.lib.ktx.widgets

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.cwand.lib.ktx.R


open class LoadingDialog : DialogFragment() {

    companion object {
        val SHOW_TAG = "LoadingDialogFragment"
        val KEY_CANCELABLE = ":Key_cancelable"
        val KEY_MSG = ":Key_msg"

        fun get(cancelable: Boolean): LoadingDialog {
            return get(cancelable, null)
        }

        fun get(cancelable: Boolean, msg: String?): LoadingDialog {
            val loadingDialog = LoadingDialog()
            val args = Bundle()
            args.putBoolean(KEY_CANCELABLE, cancelable)
            args.putString(KEY_MSG, msg)
            loadingDialog.arguments = args
            return loadingDialog
        }
    }

    private var titleView: TextView? = null
    private var loadingView: LoadingView? = null
    private var dialogCanCancel: Boolean = false

    private var tip: CharSequence? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        setStyle(STYLE_NO_TITLE, R.style.VandLoadingDialogStyle)
        val dialog: Dialog = super.onCreateDialog(savedInstanceState)
        //获取初始化数据
        if (arguments != null) {
            dialogCanCancel = arguments?.getBoolean(KEY_CANCELABLE, false) ?: false
            tip = arguments?.getString(KEY_MSG, "")
        }
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(dialogCanCancel)
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.and_lib_base_loading_view, container)
        initView(view)
        return view
    }

    protected open fun initView(layoutView: View) {
        loadingView = layoutView.findViewById(R.id.lv_and_lib_base_loading_dialog_loading_view)
        loadingView?.showOverAnim = false
        titleView = layoutView.findViewById(R.id.tv_and_lib_base_loading_dialog_tip)
        tip?.let {
            titleView?.text = it
            titleView?.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        loadingView?.switch()
    }

    override fun onPause() {
        super.onPause()
        loadingView?.switch()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        loadingView?.resetAnim()
    }

    open fun updateTitle(title: CharSequence) {
        this.tip = title
        titleView?.let { tv ->
            tv.visibility = if (TextUtils.isEmpty(tip)) View.GONE else View.VISIBLE
            tip?.let {
                tv.text = it
            }
        }
    }

    open fun showLoading(fragmentManager: FragmentManager) {
        //解决: Can not perform this action after onSaveInstanceState
        val ft = fragmentManager.beginTransaction()
        ft.add(this, SHOW_TAG)
        ft.commitAllowingStateLoss()
    }

    open fun hideLoading() {
        if (isAdded && dialog != null && dialog!!.isShowing) {
            super.dismissAllowingStateLoss() //避免偶发的不能 dismiss 或状态异常出现
        }
    }

    private class InnerDialog : Dialog {

        constructor(context: Context, bundle: Bundle?) : this(
            context,
            bundle,
            R.style.VandTransparentDialog
        )

        constructor(context: Context, bundle: Bundle?, themeStyle: Int) : super(
            context,
            themeStyle
        )
    }

}