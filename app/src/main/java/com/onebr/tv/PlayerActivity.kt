package com.onebr.tv

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class PlayerActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar
    private lateinit var serverSpinner: Spinner
    private lateinit var rotateBtn: Button

    private var tmdbId: Int = 0
    private var mediaType: String = "movie"
    private var currentServerIndex = 0
    private var isLandscape = true

    private val handler = Handler(Looper.getMainLooper())
    private var failoverRunnable: Runnable? = null

    // أفضل وأقوى السيرفرات المتاحة حالياً التي تشمل الدراما الآسيوية والتركية والعالمية
    private val serverNames = arrayOf(
        "سيرفر 1 (VidSrc Pro)",
        "سيرفر 2 (AutoEmbed Fast)",
        "سيرفر 3 (EmbedSu HD)",
        "سيرفر 4 (MultiEmbed Multi-Lang)",
        "سيرفر 5 (VidLink Super)",
        "سيرفر 6 (SmashyStream Asia/Global)"
    )

    private fun getEmbedUrl(index: Int): String {
        return when (index) {
            0 -> if (mediaType == "tv") "https://vidsrc.to/embed/tv/$tmdbId/1/1" else "https://vidsrc.to/embed/movie/$tmdbId"
            1 -> if (mediaType == "tv") "https://player.autoembed.cc/embed/tv/$tmdbId/1/1" else "https://player.autoembed.cc/embed/movie/$tmdbId"
            2 -> if (mediaType == "tv") "https://embed.su/embed/tv/$tmdbId/1/1" else "https://embed.su/embed/movie/$tmdbId"
            3 -> if (mediaType == "tv") "https://multiembed.mov/?video_id=$tmdbId&tmdb=1&s=1&e=1" else "https://multiembed.mov/?video_id=$tmdbId&tmdb=1"
            4 -> if (mediaType == "tv") "https://vidlink.pro/tv/$tmdbId/1/1" else "https://vidlink.pro/movie/$tmdbId"
            else -> if (mediaType == "tv") "https://player.smashystream.xyz/tv/$tmdbId?s=1&e=1" else "https://player.smashystream.xyz/movie/$tmdbId"
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // تفعيل ملء الشاشة
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_FULLSCREEN
        )

        tmdbId = intent.getIntExtra("TMDB_ID", 0)
        mediaType = intent.getStringExtra("MEDIA_TYPE") ?: "movie"

        val root = FrameLayout(this).apply {
            setBackgroundColor(Color.BLACK)
        }

        webView = WebView(this)
        progressBar = ProgressBar(this)

        // شريط تحكم علوي شفاف
        val topBar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(20, 20, 20, 20)
            setBackgroundColor(Color.parseColor("#99000000"))
        }

        // زر تدوير الشاشة الاختياري
        rotateBtn = Button(this).apply {
            text = "🔄 تدوير"
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.parseColor("#333333"))
            setOnClickListener {
                toggleRotation()
            }
        }

        // قائمة اختيار السيرفر يدوياً
        serverSpinner = Spinner(this).apply {
            val spinnerAdapter = ArrayAdapter(this@PlayerActivity, android.R.layout.simple_spinner_dropdown_item, serverNames)
            adapter = spinnerAdapter
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if (position != currentServerIndex) {
                        currentServerIndex = position
                        loadServer(currentServerIndex)
                    }
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }

        topBar.addView(rotateBtn)
        topBar.addView(serverSpinner)

        val topParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT).apply {
            gravity = Gravity.TOP
        }
        val progressParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT).apply {
            gravity = Gravity.CENTER
        }

        root.addView(webView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        root.addView(topBar, topParams)
        root.addView(progressBar, progressParams)
        setContentView(root)

        setupWebView()
        loadServer(0)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        cookieManager.setAcceptThirdPartyCookies(webView, true)

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            mediaPlaybackRequiresUserGesture = false
            allowFileAccess = true
            allowContentAccess = true
            useWideViewPort = true
            loadWithOverviewMode = true
            javaScriptCanOpenWindowsAutomatically = false
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            userAgentString = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36"
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                if (newProgress > 60) {
                    progressBar.visibility = View.GONE
                }
            }
        }

        webView.webViewClient = object : WebViewClient() {
            // منع خروج المتصفح للإعلانات المنبثقة
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url?.toString() ?: ""
                return !(url.startsWith("http://") || url.startsWith("https://"))
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                if (request?.isForMainFrame == true) {
                    triggerAutoFailover()
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                // فحص ما إذا كانت الصفحة هي صفحة تعطل Cloudflare
                view?.evaluateJavascript(
                    "(function() { return document.body.innerText.includes('522') || document.body.innerText.includes('timed out') || document.body.innerText.includes('unavailable'); })();"
                ) { result ->
                    if (result == "true") {
                        triggerAutoFailover()
                    }
                }
            }
        }
    }

    private fun loadServer(index: Int) {
        cancelFailoverTimer()
        progressBar.visibility = View.VISIBLE
        serverSpinner.setSelection(index)
        webView.loadUrl(getEmbedUrl(index))

        // مؤقت أمان: إذا لم يعمل السيرفر خلال 7 ثوانٍ يتم الانتقال تلقائياً للسيرفر التالي
        failoverRunnable = Runnable {
            triggerAutoFailover()
        }
        handler.postDelayed(failoverRunnable!!, 7000)
    }

    private fun triggerAutoFailover() {
        cancelFailoverTimer()
        if (currentServerIndex < serverNames.size - 1) {
            currentServerIndex++
            Toast.makeText(this, "جاري التبديل إلى ${serverNames[currentServerIndex]}...", Toast.LENGTH_SHORT).show()
            loadServer(currentServerIndex)
        } else {
            progressBar.visibility = View.GONE
            Toast.makeText(this, "تعذر تشغيل العمل على كافة السيرفرات", Toast.LENGTH_LONG).show()
        }
    }

    private fun cancelFailoverTimer() {
        failoverRunnable?.let { handler.removeCallbacks(it) }
        failoverRunnable = null
    }

    private fun toggleRotation() {
        isLandscape = !isLandscape
        requestedOrientation = if (isLandscape) {
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        } else {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    override fun onDestroy() {
        cancelFailoverTimer()
        webView.destroy()
        super.onDestroy()
    }
}
