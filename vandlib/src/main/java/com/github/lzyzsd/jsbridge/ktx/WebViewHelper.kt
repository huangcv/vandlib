package com.github.lzyzsd.jsbridge.ktx

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView

/**
 * @author : chunwei
 * @date : 2021/2/4
 * @description : WebView助手工具
 *
 */
class WebViewHelper {

    companion object {
        @SuppressLint("SetJavaScriptEnabled")
        @JvmStatic
        fun initWebSetting(webView: WebView?, userAgent: String = "", skipCache: Boolean = false) {
            webView?.let {
                val settings = it.settings
                if (userAgent.isNotEmpty()) {
                    settings.userAgentString = settings.userAgentString.plus(userAgent)
                }
                //设置WebView是否支持使用屏幕控件或手势进行缩放，默认是true，支持缩放。
                settings.setSupportZoom(false)
                //设置WebView是否使用其内置的变焦机制，该机制集合屏幕缩放控件使用，默认是false，不使用内置变焦机制。
                settings.builtInZoomControls = false
                //设置WebView使用内置缩放机制时，是否展现在屏幕缩放控件上，默认true，展现在控件上。
                settings.displayZoomControls = false
                //设置在WebView内部是否允许访问文件，默认允许访问true。
                settings.allowFileAccess = true
                settings.allowContentAccess = true
                //设置WebView是否使用预览模式加载界面,默认false
                settings.loadWithOverviewMode = true
                //设置WebView是否使用viewport，当该属性被设置为false时，加载页面的宽度总是适应WebView控件宽度；
                // 当被设置为true，当前页面包含viewport属性标签，在标签中指定宽度值生效，
                // 如果页面不包含viewport标签，无法提供一个宽度值，这个时候该方法将被使用。
                settings.useWideViewPort = true
                //设置WebView是否支持多屏窗口，参考WebChromeClient#onCreateWindow，默认false，不支持
                settings.setSupportMultipleWindows(false)
                //设置WebView底层的布局算法，参考WebSettings.LayoutAlgorithm，将会重新生成WebView布局
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING
//            } else {
//                settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
//            }
                //设置是否阻塞图片加载,页面加载完成之后,需要再次设置为false,让页面再次加载图片,达到优先加载内容后加载图片的目的
                settings.blockNetworkImage = false
                //是否启用保存密码的功能
                settings.savePassword = false
                //是否允许执行JavaScript代码,默认false
                settings.javaScriptEnabled = true
                //是否开启数据库API存储权限,默认false
                settings.databaseEnabled = true
                //是否开启DOM存储API权限,默认false,开启后,WebView能够使用DOM storage API
                settings.domStorageEnabled = true
                //是否开启定位功能, 默认true
                settings.setGeolocationEnabled(true)
                //是否允许JS自动打开弹窗,默认false
                settings.javaScriptCanOpenWindowsAutomatically = true
                //开启缓存
                settings.setAppCacheEnabled(true)
                settings.setAppCacheMaxSize(Long.MAX_VALUE)
                //设置WebView的缓存策略
                if (skipCache) {
                    settings.cacheMode = WebSettings.LOAD_NO_CACHE
                } else {
                    settings.cacheMode = WebSettings.LOAD_DEFAULT
                }
                //设置当一个安全站点企图加载来自一个不安全站点资源时WebView的行为
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                }
                //设置下载监听
                webView.setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
                    val uri = Uri.parse(url)
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    webView.context.startActivity(intent)
                }
                //设置滚动条样式
                webView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
            }
        }
    }

}