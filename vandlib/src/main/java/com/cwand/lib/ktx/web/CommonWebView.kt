package com.cwand.lib.ktx.web

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.widget.FrameLayout
import com.github.lzyzsd.jsbridge.ktx.*
import com.google.gson.reflect.TypeToken
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.WildcardType


/**
 * @author : chunwei
 * @date : 2021/1/28
 * @description : 通用WebView
 *
 */
class CommonWebView : FrameLayout {

    private var webView: BridgeWebView? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, 0) {
        initUI(context, attrs, defStyle)
    }

    private fun initUI(context: Context, attrs: AttributeSet?, defStyle: Int) {
        //添加WebView
        webView = BridgeWebView(context)
        val webLp = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        WebViewHelper.initWebSetting(webView)
        addView(webView, webLp)
        val chromeClient = CommonWebChromeClient(this)
        webView?.webChromeClient = chromeClient
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

//    fun <T> registerNativeHandler(handlerName: String, handler: JsCallBack<T>) {
//        webView?.registerHandler(handlerName, object : BridgeHandler {
//            override fun handler(data: String?, function: CallBackFunction) {
//                if (handler != null) {
//                    try {
//                        val genericType: Type = getGenericType(handler, 0)
//                        if (data == null || data.isEmpty()) {
//                            val typeToken = TypeToken.get<Any>(genericType) as TypeToken<T>
//                            if (typeToken.rawType == String::class.java) {
//                                handler.handler(handlerName!!, "" as T, function)
//                                return
//                            }
//                            return
//                        }
//                        val typeToken = TypeToken.get<Any>(genericType) as TypeToken<T>
//                        if (typeToken.rawType == String::class.java) {
//                            handler.handler(handlerName!!, data as T, function)
//                            return
//                        }
//                        val result: T = JsonUtils.getSafeGson()
//                            .fromJson(data, BeanAdapter.getGenericType(handler, 0))
//                        handler.handler(handlerName!!, result, function)
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
//                }
//            }
//        })
//    }

    @Throws(java.lang.Exception::class)
    private fun getGenericType(o: Any, index: Int): Type {
        val genType = o.javaClass.genericSuperclass
        return if (genType !is ParameterizedType) {
            Any::class.java
        } else {
            getActualType(index, genType)
        }
    }

    @Throws(java.lang.Exception::class)
    private fun getActualType(index: Int, type: ParameterizedType): Type {
        val types: Array<Type> = type.actualTypeArguments
        require(!(index < 0 || index >= types.size)) { "Index " + index + " not in range [0," + types.size + ") for " + type }
        val paramType = types[index]
        return if (paramType is WildcardType) {
            paramType.upperBounds[0]
        } else paramType
    }
}