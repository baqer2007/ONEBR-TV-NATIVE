package com.onebr.tv

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.onebr.tv.adapters.MediaAdapter
import com.onebr.tv.network.ApiService
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: MediaAdapter
    private val apiService = ApiService.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progressBar = findViewById(R.id.progressBar)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 3)

        adapter = MediaAdapter(emptyList()) { selectedMedia ->
            val intent = Intent(this, PlayerActivity::class.java).apply {
                putExtra("MEDIA_ID", selectedMedia.id)
                putExtra("MEDIA_TYPE", selectedMedia.mediaType)
            }
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        fetchTrendingMedia()
    }

    private fun fetchTrendingMedia() {
        progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val response = apiService.getTrending()
                if (response.success) {
                    adapter.updateData(response.data)
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "خطأ في الاتصال: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }
}
