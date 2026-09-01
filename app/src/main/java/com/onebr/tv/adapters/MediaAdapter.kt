package com.onebr.tv.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.onebr.tv.R
import com.onebr.tv.models.MediaItem

class MediaAdapter(
    private var items: List<MediaItem>,
    private val onItemClick: (MediaItem) -> Unit
) : RecyclerView.Adapter<MediaAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgPoster: ImageView = view.findViewById(R.id.imgPoster)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.imgPoster.load(item.poster) {
            crossfade(true)
            placeholder(android.R.drawable.ic_menu_gallery)
            error(android.R.drawable.stat_notify_error)
        }
        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<MediaItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}
