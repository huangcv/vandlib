package com.cwand.lib.ktx.web

import android.graphics.Bitmap
import android.net.Uri
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import java.lang.ref.WeakReference

/**
 * @author : chunwei
 * @date : 2021/2/7
 * @description :
 *
 */
class CommonWebChromeClient : WebChromeClient {

    private var commonWebViewRefer: WeakReference<CommonWebView>? = null
    private var receivedTitleListener: OnReceivedTitleListener? = null

    constructor(webView: CommonWebView) : super() {
        this.commonWebViewRefer = WeakReference(webView)
    }

    override fun onProgressChanged(view: WebView, newProgress: Int) {
        super.onProgressChanged(view, newProgress)
    }

    override fun onReceivedIcon(view: WebView, icon: Bitmap?) {
        super.onReceivedIcon(view, icon)
    }

    override fun onReceivedTitle(view: WebView, title: String?) {
        super.onReceivedTitle(view, title)
        this.receivedTitleListener?.onReceivedTitle(view, title)
    }

    override fun onShowFileChooser(
        webView: WebView?,
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: FileChooserParams?
    ): Boolean {
        return super.onShowFileChooser(webView, filePathCallback, fileChooserParams)
    }

    fun onReceivedTitleListener(listener: OnReceivedTitleListener) = apply {
        this.receivedTitleListener = listener
    }

    interface OnReceivedTitleListener {
        fun onReceivedTitle(webView: WebView, title: String?)
    }


}