package com.onebr.tv

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class PlayerActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        // إخفاء أشرطة النظام لتشغيل الفيديو بملء الشاشة
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_FULLSCREEN
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        )

        val mediaId = intent.getLongExtra("MEDIA_ID", 0)
        val mediaType = intent.getStringExtra("MEDIA_TYPE") ?: "movie"

        webView = findViewById(R.id.playerWebView)
        val settings: WebSettings = webView.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.mediaPlaybackRequiresUserGesture = false

        webView.webChromeClient = WebChromeClient()
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                // منع فتح أي نوافذ منبثقة أو تحويلات إعلانية خارج نطاق المشغل
                val isEmbed = url?.contains("vidsrc") == true || url?.contains("autoembed") == true
                return !isEmbed
            }
        }

        val streamUrl = if (mediaType == "tv") {
            "https://vidsrc.to/embed/tv/$mediaId/1/1"
        } else {
            "https://vidsrc.to/embed/movie/$mediaId"
        }

        webView.loadUrl(streamUrl)
    }

    override fun onDestroy() {
        webView.destroy()
        super.onDestroy()
    }
}
