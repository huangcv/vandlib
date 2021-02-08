package com.github.lzyzsd.jsbridge.ktx

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Looper
import android.os.SystemClock
import android.text.TextUtils
import android.util.AttributeSet
import android.webkit.WebView
import android.webkit.WebViewClient
import java.net.URLEncoder

/**
 * @author : chunwei
 * @date : 2021/2/7
 * @description :
 *
 */
class BridgeWebView : WebView, WebViewJavascriptBridge {

    companion object {
        private const val TAG = "BridgeWebView"
        const val localJsFile = "WebViewJavascriptBridge.js"
    }

    private var bridgeHandler: BridgeHandler = DefaultHandler()
    private val responseCallbacks: HashMap<String, CallBackFunction> = hashMapOf()
    private val messageHandlers: HashMap<String, BridgeHandler> = hashMapOf()
    var startupMessage: ArrayList<Message>? = ArrayList()
    private var uniqueId: Long = 0

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, 0) {
        init(context, attrs, defStyle)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun init(context: Context, attrs: AttributeSet?, defStyle: Int) {
        //准备默认设置
        isVerticalScrollBarEnabled = false
        isHorizontalScrollBarEnabled = false
        settings.javaScriptEnabled = true
        //是否允许调试
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setWebContentsDebuggingEnabled(true)
        }
        //设置默认的WebViewClient
        webViewClient = generateWebViewClient()
    }

    open fun generateWebViewClient(): WebViewClient {
        return BridgeWebViewClient(this)
    }

    fun bridgeHandler(handler: BridgeHandler) = apply {
        this.bridgeHandler = handler
    }

    /**
     * 获取到CallbackFunction data执行调用并且从数据集移除
     */
    public fun handleReturnData(url: String) {
        val functionName = BridgeUtil.getFunctionFromReturnUrl(url)
        val callbackFunction = responseCallbacks[functionName]
        val data = BridgeUtil.getDataFromReturnUrl(url)
        callbackFunction?.onCallBack(data)
        responseCallbacks.remove(functionName)
    }

    fun registerHandler(handlerName: String, handler: BridgeHandler) = apply {
        messageHandlers[handlerName] = handler
    }

    fun unregisterHandler(handlerName: String) = apply {
        messageHandlers.remove(handlerName)
    }

    fun callHandler(jsHandlerName: String, data: String, callBackFunction: CallBackFunction) =
        apply {
            doSend(jsHandlerName, data, callBackFunction)
        }

    override fun send(data: String) {
        send(data, null)
    }

    override fun send(data: String, function: CallBackFunction?) {
        doSend(null, data, function)
    }

    fun loadUrl(jsUrl: String, returnCallback: CallBackFunction) {
        loadUrl(jsUrl)
        //加入到缓存中
        responseCallbacks[BridgeUtil.parseFunctionName(jsUrl)] = returnCallback
    }

    /**
     * 保存message到消息队列
     *
     */
    private fun doSend(handlerName: String?, data: String, responseCallback: CallBackFunction?) {
        val m = Message(handlerName = handlerName, data = data)
        responseCallback?.let {
            val callbackStr = String.format(
                BridgeUtil.CALLBACK_ID_FORMAT, uniqueId.inc().toString().plus(
                    BridgeUtil.UNDERLINE_STR.plus(
                        SystemClock.currentThreadTimeMillis()
                    )
                )
            )
            responseCallbacks[callbackStr] = responseCallback
            m.callbackId = callbackStr
        }
        queueMessage(m)
    }

    private fun queueMessage(message: Message) {
        startupMessage?.add(message) ?: dispatchMessage(message)
    }

    public fun dispatchMessage(message: Message) {
        var messageJson = message.toJson()
        //escape special characters for json string  为json字符串转义特殊字符
        messageJson = messageJson.replace("(\\\\)([^utrn])".toRegex(), "\\\\\\\\$1$2")
        messageJson = messageJson.replace("(?<=[^\\\\])(\")".toRegex(), "\\\\\"")
        messageJson = messageJson.replace("(?<=[^\\\\])(\')".toRegex(), "\\\\\'")
        messageJson = messageJson.replace("%7B".toRegex(), URLEncoder.encode("%7B"))
        messageJson = messageJson.replace("%7D".toRegex(), URLEncoder.encode("%7D"))
        messageJson = messageJson.replace("%22".toRegex(), URLEncoder.encode("%22"))
        val javascriptCommand = String.format(BridgeUtil.JS_HANDLE_MESSAGE_FROM_JAVA, messageJson)
        //执行javascript,必须在主线程中执行
        if (Looper.getMainLooper().thread == Thread.currentThread()) {
            loadUrl(javascriptCommand)
        }
    }

    fun flushMessageQueue() {
        if (Thread.currentThread() == Looper.getMainLooper().thread) {
            loadUrl(BridgeUtil.JS_FETCH_QUEUE_FROM_JAVA, object : CallBackFunction {
                override fun onCallBack(data: String?) {
                    data?.let {
                        val list = Message.toArrayList(it)
                        if (list.isNotEmpty()) {
                            list.forEach { m ->
                                val responseId = m.responseId
                                if (!TextUtils.isEmpty(responseId)) {
                                    val function = responseCallbacks[responseId]
                                    val responseData = m.responseData
                                    function?.onCallBack(responseData)
                                    responseCallbacks.remove(responseId)
                                } else {
                                    var function: CallBackFunction? = null
                                    val callbackId = m.callbackId
                                    if (!TextUtils.isEmpty(callbackId)) {
                                        function = object : CallBackFunction {
                                            override fun onCallBack(data: String?) {
                                                queueMessage(
                                                    Message(
                                                        responseId = callbackId,
                                                        responseData = data
                                                    )
                                                )
                                            }
                                        }
                                    } else {
                                        function = object : CallBackFunction {
                                            override fun onCallBack(data: String?) {
                                                //do nothing
                                            }
                                        }
                                    }
                                    //执行BridgeHandler
                                    var handler: BridgeHandler? = null
                                    handler = if (!TextUtils.isEmpty(m.handlerName)) {
                                        messageHandlers[m.handlerName]
                                    } else {
                                        bridgeHandler
                                    }
                                    handler?.handler(m.data, function)
                                }
                            }
                        }
                    }
                }
            })
        }
    }

}