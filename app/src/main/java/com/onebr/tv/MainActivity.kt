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
    private lateinit var adapter: MediaAdapter
    private val apiService = ApiService.create()

    private val mediaList = mutableListOf<MediaItem>()
    private val loadedIds = HashSet<Int>() // لمنع تكرار أي فيلم نهائياً
    private var currentPage = 1
    private var isLoading = false
    private var hasMore = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)

        val layoutManager = GridLayoutManager(this, 3)
        recyclerView.layoutManager = layoutManager
        adapter = MediaAdapter(mediaList)
        recyclerView.adapter = adapter

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && !isLoading && hasMore) {
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 4) {
                        currentPage++
                        fetchMediaData(currentPage)
                    }
                }
            }
        })

        fetchMediaData(1)
    }

    private fun fetchMediaData(page: Int) {
        isLoading = true
        if (page == 1) progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val response = apiService.getTrendingMedia(page = page)
                val newItems = response.data ?: emptyList()

                // فلترة العناصر المكررة
                val uniqueItems = newItems.filter { item ->
                    if (!loadedIds.contains(item.id)) {
                        loadedIds.add(item.id)
                        true
                    } else {
                        false
                    }
                }

                if (uniqueItems.isNotEmpty()) {
                    val startPos = mediaList.size
                    mediaList.addAll(uniqueItems)
                    adapter.notifyItemRangeInserted(startPos, uniqueItems.size)
                } else if (page > 1) {
                    hasMore = false
                }
            } catch (e: Exception) {
                if (page == 1) {
                    Toast.makeText(this@MainActivity, "خطأ بالاتصال: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            } finally {
                isLoading = false
                progressBar.visibility = View.GONE
            }
        }
    }
}
