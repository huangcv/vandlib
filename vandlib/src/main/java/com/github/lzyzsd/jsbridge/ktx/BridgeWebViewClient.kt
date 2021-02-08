package com.github.lzyzsd.jsbridge.ktx

import android.graphics.Bitmap
import android.os.Build
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.github.lzyzsd.jsbridge.ktx.BridgeUtil.Companion.webViewLoadLocalJs
import java.io.UnsupportedEncodingException
import java.net.URLDecoder

/**
 * @author : chunwei
 * @date : 2021/2/7
 * @description :
 *
 */
class BridgeWebViewClient(private val bridgeWebView: BridgeWebView) : WebViewClient() {

    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        var tempUrl = url
        try {
            tempUrl = URLDecoder.decode(tempUrl, "UTF-8")
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

        return if (tempUrl.startsWith(BridgeUtil.YY_RETURN_DATA)) { // 如果是返回数据
            bridgeWebView.handleReturnData(tempUrl)
            true
        } else if (tempUrl.startsWith(BridgeUtil.YY_OVERRIDE_SCHEMA)) { //
            bridgeWebView.flushMessageQueue()
            true
        } else {
            if (this.onCustomShouldOverrideUrlLoading(
                    view,
                    tempUrl
                )
            ) true else super.shouldOverrideUrlLoading(view, tempUrl)
        }
    }

    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            var url = request.url.toString()
            try {
                url = URLDecoder.decode(url, "UTF-8")
            } catch (ex: UnsupportedEncodingException) {
                ex.printStackTrace()
            }
            if (url.startsWith(BridgeUtil.YY_RETURN_DATA)) { // 如果是返回数据
                bridgeWebView.handleReturnData(url)
                true
            } else if (url.startsWith(BridgeUtil.YY_OVERRIDE_SCHEMA)) { //
                bridgeWebView.flushMessageQueue()
                true
            } else {
                if (onCustomShouldOverrideUrlLoading(
                        view,
                        url
                    )
                ) true else super.shouldOverrideUrlLoading(view, request)
            }
        } else {
            super.shouldOverrideUrlLoading(view, request)
        }
    }

    override fun onPageStarted(view: WebView, url: String, favicon: Bitmap) {
        super.onPageStarted(view, url, favicon)
    }

    override fun onPageFinished(view: WebView, url: String) {
        super.onPageFinished(view, url)
        webViewLoadLocalJs(view, BridgeWebView.localJsFile)
        bridgeWebView.startupMessage?.let {
            it.forEach { m ->
                bridgeWebView.dispatchMessage(m)
            }
            bridgeWebView.startupMessage = null
        }
        onCustomPageFinished(view, url)
    }

    open fun onCustomShouldOverrideUrlLoading(webView: WebView?, url: String?): Boolean {
        return false
    }


    open fun onCustomPageFinished(view: WebView?, url: String?) {}
}