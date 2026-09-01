package com.onebr.tv

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private val apiService = ApiService.create()

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
                val response = apiService.getTrendingMedia(page = 1)
                val items = response.results ?: emptyList()
                recyclerView.adapter = MediaAdapter(items)
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "خطأ في الاتصال بالخادم", Toast.LENGTH_SHORT).show()
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }
}
