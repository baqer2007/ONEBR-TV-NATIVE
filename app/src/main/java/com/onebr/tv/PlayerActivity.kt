package com.onebr.tv

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class PlayerActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private var isFallbackLoaded = false

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_FULLSCREEN
        )

        webView = WebView(this)
        setContentView(webView)

        val tmdbId = intent.getIntExtra("TMDB_ID", 0)
        val mediaType = intent.getStringExtra("MEDIA_TYPE") ?: "movie"

        // السيرفر الأساسي عالي الاستقرار
        val primaryUrl = if (mediaType == "tv") {
            "https://vidsrc.to/embed/tv/$tmdbId/1/1"
        } else {
            "https://vidsrc.to/embed/movie/$tmdbId"
        }

        // السيرفر الاحتياطي التلقائي في حال تعطل الأول
        val fallbackUrl = if (mediaType == "tv") {
            "https://player.autoembed.cc/embed/tv/$tmdbId/1/1"
        } else {
            "https://player.autoembed.cc/embed/movie/$tmdbId"
        }

        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        cookieManager.setAcceptThirdPartyCookies(webView, true)

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
            mediaPlaybackRequiresUserGesture = false
            allowFileAccess = true
            allowContentAccess = true
            useWideViewPort = true
            loadWithOverviewMode = true
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            userAgentString = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36"
        }

        webView.webChromeClient = WebChromeClient()
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                return false
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                if (request?.isForMainFrame == true && !isFallbackLoaded) {
                    isFallbackLoaded = true
                    view?.loadUrl(fallbackUrl)
                }
            }
        }

        webView.loadUrl(primaryUrl)
    }

    override fun onDestroy() {
        webView.destroy()
        super.onDestroy()
    }
}
