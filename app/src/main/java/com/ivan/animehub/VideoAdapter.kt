package com.ivan.animehub

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class VideoAdapter(private val videos: List<Video>) : RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

    class VideoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivCover: ImageView = view.findViewById(R.id.ivCover)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvDesc: TextView = view.findViewById(R.id.tvDesc)
        val btnWatch: Button = view.findViewById(R.id.btnWatch)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_video, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val video = videos[position]
        holder.tvTitle.text = video.title
        holder.tvDesc.text = video.description

        // Загружаем обложку с помощью Glide
        Glide.with(holder.itemView.context)
            .load(video.image_url)
            .placeholder(android.R.drawable.ic_menu_gallery) // Заглушка, пока грузится
            .into(holder.ivCover)

        // Кнопка Смотреть
        holder.btnWatch.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra("VIDEO_URL", video.video_url)
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = videos.size
}
