package com.cwand.lib.ktx.widgets

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatDialogFragment
import com.cwand.lib.ktx.R

/**
 * @author : chunwei
 * @date : 2020/12/7
 * @description : 弹窗基类
 *
 */
open class BFDialog : AppCompatDialogFragment() {

    private val paramsBuilder: ParamsBuilder by lazy {
        ParamsBuilder()
    }

    private var customContentView: View? = null

    @LayoutRes
    open fun bindLayout(): Int = -1

    protected fun initBundle(bundle: Bundle?) {

    }

    fun hideTitle(hide: Boolean): BFDialog {
        paramsBuilder.hideTitle = hide
        return this@BFDialog
    }

    fun hideCancel(hide: Boolean): BFDialog {
        paramsBuilder.hideCancel = hide
        return this@BFDialog
    }

    fun hideConfirm(hide: Boolean): BFDialog {
        paramsBuilder.hideConfirm = hide
        return this@BFDialog
    }

    fun title(title: CharSequence): BFDialog {
        paramsBuilder.title = title
        return this@BFDialog
    }

    fun cancelAction(cancelAction: Action): BFDialog {
        paramsBuilder.cancelClickListener = cancelAction
        return this@BFDialog
    }

    fun confirmAction(confirmAction: Action): BFDialog {
        paramsBuilder.confirmClickListener = confirmAction
        return this@BFDialog
    }

    fun autoClickCancelDismiss(dismiss: Boolean): BFDialog {
        paramsBuilder.autoCancelClickDismiss = dismiss
        return this@BFDialog
    }

    fun autoClickConfirmDismiss(dismiss: Boolean): BFDialog {
        paramsBuilder.autoConfirmClickDismiss = dismiss
        return this@BFDialog
    }

    fun confirmText(confirm: CharSequence): BFDialog {
        paramsBuilder.confirmText = confirm
        return this@BFDialog
    }

    fun cancelText(cancel: CharSequence): BFDialog {
        paramsBuilder.cancelText = cancel
        return this@BFDialog
    }

    fun apply() {
        refreshBaseUI()
    }

    fun updateContent(content: CharSequence) {
        paramsBuilder.contentText = content
        contentTextView?.text = paramsBuilder.contentText
    }

    fun updateTitle(title: CharSequence) {
        paramsBuilder.title = title
        titleView?.text = paramsBuilder.title
    }

    fun updateCancelText(cancel: CharSequence) {
        paramsBuilder.cancelText = cancel
        cancelView?.text = paramsBuilder.cancelText
    }

    fun updateConfirmText(confirm: CharSequence) {
        paramsBuilder.confirmText = confirm
        confirmView?.text = paramsBuilder.confirmText
    }

    protected var titleView: TextView? = null
    protected var contentTextView: TextView? = null
    protected var cancelView: TextView? = null
    protected var confirmView: TextView? = null
    protected var contentRootView: FrameLayout? = null
    protected var verticalLineView: View? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), R.style.BFDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.and_lib_base_dialog_fragment, null, false)
        if (bindLayout() != -1) {
            val contentView = rootView.findViewById<FrameLayout>(R.id.fl_and_lib_base_dialog_fragment_content)
            contentView.removeAllViews()
            customContentView = inflater.inflate(bindLayout(), contentView, true)
        }
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
    }

    private fun initViews(contentView: View) {
        findViews(contentView)
        refreshBaseUI()
        customContentView?.let {
            initCustomView(it)
        }
    }

    private fun refreshBaseUI() {
        //标题
        titleView?.let {
            it.text = paramsBuilder.title
            it.visibility = if (paramsBuilder.hideTitle) View.GONE else View.VISIBLE
        }
        //内容
        if (customContentView == null) {
            contentTextView?.text = paramsBuilder.contentText
        }
        //取消按钮
        cancelView?.let {
            it.text = paramsBuilder.cancelText
            it.visibility = if (paramsBuilder.hideCancel) View.GONE else View.VISIBLE
        }
        //确定按钮
        confirmView?.let {
            it.text = paramsBuilder.confirmText
            it.visibility = if (paramsBuilder.hideConfirm) View.GONE else View.VISIBLE
        }
        //中间竖线
        verticalLineView?.visibility = if (paramsBuilder.hideCancel || paramsBuilder.hideConfirm) View.GONE else View.VISIBLE
    }

    private fun findViews(contentView: View) {
        if (titleView == null) {
            titleView = contentView.findViewById(R.id.tv_and_lib_base_dialog_fragment_title)
        }
        if (contentRootView == null) {
            contentRootView = contentView.findViewById(R.id.fl_and_lib_base_dialog_fragment_content)
        }
        if (contentTextView == null && customContentView == null) {
            contentTextView = contentView.findViewById(R.id.tv_and_lib_base_dialog_fragment_content)
        }
        if (cancelView == null) {
            cancelView = contentView.findViewById(R.id.tv_and_lib_base_dialog_fragment_cancel)
        }
        if (confirmView == null) {
            confirmView = contentView.findViewById(R.id.tv_and_lib_base_dialog_fragment_confirm)
        }
        if (verticalLineView == null) {
            verticalLineView = contentView.findViewById(R.id.tv_and_lib_base_dialog_fragment_vertical_line)
        }
        cancelView?.setOnClickListener {
            if (paramsBuilder.autoCancelClickDismiss) {
                dismiss()
            }
            paramsBuilder.cancelClickListener?.onAction()
        }
        confirmView?.setOnClickListener {
            if (paramsBuilder.autoConfirmClickDismiss) {
                dismiss()
            }
            paramsBuilder.confirmClickListener?.onAction()
        }
    }

    open fun initCustomView(customContentView: View) {

    }

    override fun onStart() {
        super.onStart()
        dialog?.let { d ->
            configLayout(d)
        }
    }

    protected open fun configLayout(dialog: Dialog) {
        val wm = this.requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val dm = DisplayMetrics()
        dialog.window?.let {
            it.setGravity(getGravity())
            wm.defaultDisplay.getMetrics(dm)
            val width = dm.widthPixels * 0.773f
            it.setLayout(width.toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    protected open fun getGravity():Int{
        return Gravity.CENTER
    }

    inner class ParamsBuilder {
        var cancelOnTouchOutside = true
        var gravity = Gravity.CENTER
        var canCancelable = true
        var hideTitle = false
        var hideCancel = false
        var hideConfirm = false
        var title: CharSequence = "提示"
        var cancelText: CharSequence = "取消"
        var confirmText: CharSequence = "确定"
        var contentText: CharSequence = ""
        var cancelClickListener: Action? = null
        var confirmClickListener: Action? = null
        var fullScreenWidth = false
        var fullScreenHeight = false
        var autoCancelClickDismiss = true
        var autoConfirmClickDismiss = true
        var showEffect = 0
    }

    open interface Action {
        fun onAction()
    }

    @Target(AnnotationTarget.FIELD, AnnotationTarget.FUNCTION, AnnotationTarget.LOCAL_VARIABLE)
    @Retention(value = AnnotationRetention.RUNTIME)
    annotation class ShowEffect {
        companion object {
            const val left = 0x0000
            const val top = 0x0001
            const val right = 0x0010
            const val bottom = 0x0011
            const val in_ = 0x0100
            const val out_ = 0x0101
        }
    }

}