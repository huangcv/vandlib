package com.cwand.lib.ktx.web

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.widget.FrameLayout
import com.github.lzyzsd.jsbridge.ktx.*
import com.google.gson.Gson


/**
 * @author : chunwei
 * @date : 2021/1/28
 * @description : 通用WebView
 *
 */
class CommonWebView : FrameLayout {

    var webView: BridgeWebView? = null
        private set

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, 0) {
        initUI(context, attrs, defStyle)
    }

    private fun initUI(context: Context, attrs: AttributeSet?, defStyle: Int) {
        //添加WebView
        webView = BridgeWebView(context)
        val webLp = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        addView(webView, webLp)
        WebViewHelper.initWebSetting(webView)
        val chromeClient = CommonWebChromeClient(this)
        webView?.webChromeClient = chromeClient
        webView?.let {
            it.webViewClient = CommonWebViewClient(it)
        }
        webView?.loadUrl("https://www.baidu.com/")
    }

    fun getBridgeWebView(): BridgeWebView? {
        return this.webView
    }

    fun loadUrl(url: String) {
        loadUrl(url, emptyMap())
    }

    fun loadUrl(url: String, additionalHttpHeaders: Map<String, String>) {
        if (TextUtils.isEmpty(url)) {
            return
        }
        if (url.startsWith("http") || url.startsWith("file") || url.startsWith("https")) {
            webView?.loadUrl(url, additionalHttpHeaders)
        } else {
            loadDataWithBaseURL(url)
        }
    }

    private fun loadDataWithBaseURL(html: String) {
        webView?.let {
            it.settings.useWideViewPort = false
            it.settings.builtInZoomControls = false
            it.loadDataWithBaseURL("", html, "text/html", "utf-8", null)
        }
    }

    fun send(data: String) {
        webView?.send(data)
    }

    fun send(data: String, responseCallback: CallBackFunction?) {
        webView?.send(data, responseCallback)
    }

    fun reload() {
        stopLoad()
        webView?.reload()
    }

    fun stopLoad() {
        webView?.stopLoading()
    }

    fun setDefaultHandler(handler: BridgeHandler) {
        webView?.bridgeHandler(handler)
    }

    fun goBack(): Boolean {
        webView?.let {
            if (it.canGoBack()) {
                it.goBack()
                return true
            }
        }
        return false
    }

    inline fun <reified T : Any> registerNativeHandler(
        handlerName: String,
        handler: JsCallBack<T>
    ) {
        webView?.registerHandler(handlerName, object : BridgeHandler {
            override fun handler(data: String?, function: CallBackFunction) {
                try {
                    val genericType = getGenericType<T>()
                    if (data == null || data.isEmpty()) {
                        if (genericType == String::class.java) {
                            handler.handler(handlerName, "" as T, function)
                            return
                        }
                    } else {
                        if (genericType == String::class.java) {
                            handler.handler(handlerName, data as T, function)
                        } else {
                            val result = Gson().fromJson(data, genericType) as T
                            handler.handler(handlerName, result, function)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    inline fun <reified T : Any> callJsMethod(
        methodName: String,
        data: String,
        handler: JsCallBack<T>
    ) {
        webView?.callHandler(methodName, data, object : CallBackFunction {
            override fun onCallBack(data: String?) {
                try {
                    val genericType = getGenericType<T>()
                    if (data == null || data.isEmpty()) {
                        if (genericType == String::class.java) {
                            handler.handler(methodName, "" as T, this)
                            return
                        }
                    } else {
                        if (genericType == String::class.java) {
                            handler.handler(methodName, data as T, this)
                        } else {
                            val result = Gson().fromJson(data, genericType) as T
                            handler.handler(methodName, result, this)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    inline fun <reified T> getGenericType() = T::class.java
}