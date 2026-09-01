package com.onebr.tv

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PlayerActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar
    private lateinit var switchServerBtn: Button
    
    private var currentServerIndex = 0
    private var tmdbId: Int = 0
    private var mediaType: String = "movie"

    // قائمة 16 سيرفر بث عالمي
    private fun getServerUrl(index: Int): String {
        return when (index % 16) {
            0 -> if (mediaType == "tv") "https://vidsrc.to/embed/tv/$tmdbId/1/1" else "https://vidsrc.to/embed/movie/$tmdbId"
            1 -> if (mediaType == "tv") "https://player.autoembed.cc/embed/tv/$tmdbId/1/1" else "https://player.autoembed.cc/embed/movie/$tmdbId"
            2 -> if (mediaType == "tv") "https://vidsrc.me/embed/tv?tmdb=$tmdbId&season=1&episode=1" else "https://vidsrc.me/embed/movie?tmdb=$tmdbId"
            3 -> if (mediaType == "tv") "https://embed.su/embed/tv/$tmdbId/1/1" else "https://embed.su/embed/movie/$tmdbId"
            4 -> if (mediaType == "tv") "https://multiembed.mov/?video_id=$tmdbId&tmdb=1&s=1&e=1" else "https://multiembed.mov/?video_id=$tmdbId&tmdb=1"
            5 -> if (mediaType == "tv") "https://moviesapi.club/tv/$tmdbId-1-1" else "https://moviesapi.club/movie/$tmdbId"
            6 -> if (mediaType == "tv") "https://vidsrc.xyz/embed/tv?tmdb=$tmdbId&season=1&episode=1" else "https://vidsrc.xyz/embed/movie?tmdb=$tmdbId"
            7 -> if (mediaType == "tv") "https://2embed.cc/embedtv/$tmdbId&s=1&e=1" else "https://2embed.cc/embed/$tmdbId"
            8 -> if (mediaType == "tv") "https://smashystream.xyz/embed/tv/$tmdbId/1/1" else "https://smashystream.xyz/embed/movie/$tmdbId"
            9 -> if (mediaType == "tv") "https://vidlink.pro/tv/$tmdbId/1/1" else "https://vidlink.pro/movie/$tmdbId"
            10 -> if (mediaType == "tv") "https://frembed.top/api/serie.php?id=$tmdbId&s=1&e=1" else "https://frembed.top/api/film.php?id=$tmdbId"
            11 -> if (mediaType == "tv") "https://nontonembed.com/tv/$tmdbId/1/1" else "https://nontonembed.com/movie/$tmdbId"
            12 -> if (mediaType == "tv") "https://blackvid.space/embed?tmdb=$tmdbId&season=1&episode=1" else "https://blackvid.space/embed?tmdb=$tmdbId"
            13 -> if (mediaType == "tv") "https://autoembed.to/tv/tmdb/$tmdbId-1-1" else "https://autoembed.to/movie/tmdb/$tmdbId"
            14 -> if (mediaType == "tv") "https://watch.streamflix.one/tv/$tmdbId/1/1" else "https://watch.streamflix.one/movie/$tmdbId"
            else -> if (mediaType == "tv") "https://vidsrc.in/embed/tv?tmdb=$tmdbId&season=1&episode=1" else "https://vidsrc.in/embed/movie?tmdb=$tmdbId"
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_FULLSCREEN
        )

        val rootLayout = FrameLayout(this).apply {
            setBackgroundColor(Color.BLACK)
        }

        tmdbId = intent.getIntExtra("TMDB_ID", 0)
        mediaType = intent.getStringExtra("MEDIA_TYPE") ?: "movie"

        webView = WebView(this)
        progressBar = ProgressBar(this).apply {
            visibility = View.VISIBLE
        }
        val pParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT).apply {
            gravity = Gravity.CENTER
        }

        // زر لتبديل السيرفر يدوياً أيضاً
        switchServerBtn = Button(this).apply {
            text = "تغيير السيرفر (1/16)"
            textSize = 10f
            setBackgroundColor(Color.parseColor("#80000000"))
            setTextColor(Color.WHITE)
            setOnClickListener {
                nextServer()
            }
        }
        val btnParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT).apply {
            gravity = Gravity.TOP or Gravity.END
            setMargins(16, 16, 16, 16)
        }

        rootLayout.addView(webView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        rootLayout.addView(progressBar, pParams)
        rootLayout.addView(switchServerBtn, btnParams)
        setContentView(rootLayout)

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
            javaScriptCanOpenWindowsAutomatically = false // حظر فتح نوافذ إعلانية تلقائياً
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            userAgentString = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36"
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                if (newProgress > 70) progressBar.visibility = View.GONE
            }
        }

        webView.webViewClient = object : WebViewClient() {
            // حظر روابط الإعلانات المنبثقة والخروج من المشغل
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url?.toString() ?: ""
                val currentServerHost = getServerUrl(currentServerIndex)
                // إذا كان الرابط لا يتبع سيرفر التشغيل الأساسي، امنعه (حجب إعلانات)
                return !url.contains("embed") && !url.contains("vidsrc") && !url.contains("autoembed")
            }

            // التبديل التلقائي في حال تعطل السيرفر (Error 522 / 404 / connection timeout)
            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                if (request?.isForMainFrame == true) {
                    nextServer()
                }
            }
        }

        loadCurrentServer()
    }

    private fun loadCurrentServer() {
        progressBar.visibility = View.VISIBLE
        switchServerBtn.text = "سيرفر ${currentServerIndex + 1}/16"
        val url = getServerUrl(currentServerIndex)
        webView.loadUrl(url)
    }

    private fun nextServer() {
        if (currentServerIndex < 15) {
            currentServerIndex++
            Toast.makeText(this, "جاري الانتقال لسيرفر بديل ${currentServerIndex + 1}...", Toast.LENGTH_SHORT).show()
            loadCurrentServer()
        } else {
            Toast.makeText(this, "تم تجربة كافة السيرفرات المتاحة لهذا العمل", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        webView.destroy()
        super.onDestroy()
    }
}
