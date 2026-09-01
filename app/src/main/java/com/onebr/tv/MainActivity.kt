package com.onebr.tv

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private val apiService = ApiService.create()
    private val allMediaList = mutableListOf<MediaItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)

        recyclerView.layoutManager = GridLayoutManager(this, 3)

        fetchMediaData()
    }

    private fun fetchMediaData() {
        progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                // جلب أول 4 صفحات معاً لعرض مكتبة أعمال ضخمة
                val p1 = async { apiService.getTrendingMedia(page = 1) }
                val p2 = async { apiService.getTrendingMedia(page = 2) }
                val p3 = async { apiService.getTrendingMedia(page = 3) }
                val p4 = async { apiService.getTrendingMedia(page = 4) }

                val responses = listOf(p1.await(), p2.await(), p3.await(), p4.await())
                allMediaList.clear()
                for (res in responses) {
                    res.data?.let { allMediaList.addAll(it) }
                }

                if (allMediaList.isEmpty()) {
                    Toast.makeText(this@MainActivity, "لم يتم العثور على أعمال", Toast.LENGTH_SHORT).show()
                } else {
                    recyclerView.adapter = MediaAdapter(allMediaList)
                }
            } catch (e: Exception) {
                // في حال فشل جلب الصفحات المتعددة، يتم الاعتماد على الصفحة الأولى كخطة بديلة
                try {
                    val fallback = apiService.getTrendingMedia(page = 1)
                    fallback.data?.let {
                        allMediaList.addAll(it)
                        recyclerView.adapter = MediaAdapter(allMediaList)
                    }
                } catch (err: Exception) {
                    Toast.makeText(this@MainActivity, "خطأ بالاتصال: ${err.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }
}
