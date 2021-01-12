package com.cwand.lib.sample

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.webkit.*
import com.cwand.lib.ktx.extensions.logD
import com.cwand.lib.ktx.utils.NetworkUtils
import kotlinx.android.synthetic.main.web_activity.*

/**
 * @author : chunwei
 * @date : 2021/1/4
 * @description : Web页面
 *
 */
class WebActivity : AppBaseTitleActivity() {
    override fun titleTextRes(): Int {
        return -1
    }

    override fun bindLayout(): Int {
        return R.layout.web_activity
    }

    override fun initViews(savedInstanceState: Bundle?) {
        webView?.apply {
            initSetting(this)
            refreshUrl()
        }
    }

    private fun refreshUrl() {
        webView?.loadUrl("https://bz.zzzmh.cn/#index")
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initSetting(webView: WebView) {
        val settings: WebSettings = webView.settings
        settings.javaScriptEnabled = true
        settings.useWideViewPort = false
//        val ua = settings.userAgentString
        //user agent 添加应用标识
//        settings.setUserAgentString(ua + ";huitou_android_" + BuildConfig.VERSION_NAME)

        //打开缓存设置
        openCacheSetting(settings)

        //不构建缩放组件
        settings.builtInZoomControls = false
        //不开始保存密码功能
        settings.savePassword = false

        //不显示WebView缩放按钮
        settings.displayZoomControls = false
        //不支持手势缩放
        settings.setSupportZoom(false)
        //阻塞图片加载,等加载完毕之后再加载图片
        settings.blockNetworkImage = false

        //可能的话使所有列的宽度不超过屏幕宽度
        settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN

        //设置加载进来的页面自适应手机屏幕
//        settings.useWideViewPort = true
//        settings.loadWithOverviewMode = true

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //让系统不屏蔽混合内容和第三方Cookie
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }

        webView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY

        webView.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
                    webView.goBack()
//                    if (mAutoGoBack) {
//                        return@OnKeyListener goBack()
//                    }
                }
            }
            false
        })
        webView.webChromeClient = WebChromeClient()
        webView.webViewClient = WebViewClient()
        webView.setDownloadListener(DownloadListener { url, _, _, mimetype, _ ->
            var uri = Uri.parse(url)
            uri.toString().logD()
            mimetype.logD()
            if (uri.toString().startsWith("blob:")) {
                val realUrl = uri.toString().replace("blob:", "")
                uri = Uri.parse(realUrl)
            }
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        })
    }

    @SuppressLint("MissingPermission")
    private fun openCacheSetting(settings: WebSettings) {
        settings.domStorageEnabled = true
        settings.databaseEnabled = true
        settings.setAppCacheEnabled(true)
        settings.setAppCacheMaxSize(Long.MAX_VALUE)
        if (NetworkUtils.isNetworkAvailable(this)) {
            settings.cacheMode = WebSettings.LOAD_NO_CACHE
        } else {
            settings.cacheMode = WebSettings.LOAD_DEFAULT
        }
    }
}