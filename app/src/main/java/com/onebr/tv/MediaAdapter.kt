package com.onebr.tv

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load

class MediaAdapter(private val mediaList: List<MediaItem>) :
    RecyclerView.Adapter<MediaAdapter.MediaViewHolder>() {

    class MediaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val posterImageView: ImageView = itemView.findViewById(R.id.posterImageView)
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false)
        return MediaViewHolder(view)
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        val item = mediaList[position]
        holder.titleTextView.text = item.title ?: item.name ?: ""

        val posterUrl = "https://image.tmdb.org/t/p/w500${item.poster_path}"
        holder.posterImageView.load(posterUrl)

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, PlayerActivity::class.java).apply {
                putExtra("TMDB_ID", item.id)
                putExtra("MEDIA_TYPE", item.media_type ?: "movie")
            }
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = mediaList.size
}
