package com.cwand.lib.ktx.web

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.webkit.WebView
import com.github.lzyzsd.jsbridge.ktx.BridgeWebView
import com.github.lzyzsd.jsbridge.ktx.BridgeWebViewClient

/**
 * @author : chunwei
 * @date : 2021/2/18
 * @description :
 *
 */
open class CommonWebViewClient(private val webView: BridgeWebView) : BridgeWebViewClient(webView) {
    @SuppressLint("QueryPermissionsNeeded")
    override fun onCustomShouldOverrideUrlLoading(webView: WebView, url: String?): Boolean {
        return url?.let {
            if (it.startsWith("http:") || it.startsWith("https:")) {
                return@let super.onCustomShouldOverrideUrlLoading(webView, url)
            } else {
                val uri = Uri.parse(url)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                if (intent.resolveActivity(webView.context.packageManager) != null) {
                    webView.context.startActivity(intent)
                    return@let true
                }
                return@let super.onCustomShouldOverrideUrlLoading(webView, url)
            }
        } ?: return super.onCustomShouldOverrideUrlLoading(webView, url)
    }
}