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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request

class VideoAdapter(private val videos: List<Video>) : RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {
    private val client = OkHttpClient()

    class VideoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivCover: ImageView = view.findViewById(R.id.ivCover)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvDesc: TextView = view.findViewById(R.id.tvDesc)
        val btnWatch: Button = view.findViewById(R.id.btnWatch)
        val btnLike: Button = view.findViewById(R.id.btnLike)
        val tvViews: TextView = view.findViewById(R.id.tvViews)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_video, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val video = videos[position]
        holder.tvTitle.text = video.title
        holder.tvDesc.text = video.description
        holder.btnLike.text = "❤️ ${video.likes}"
        holder.tvViews.text = "👁 ${video.views} просмотров"

        Glide.with(holder.itemView.context)
            .load(video.image_url)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .into(holder.ivCover)

        // Лайк
        holder.btnLike.setOnClickListener {
            video.likes += 1
            holder.btnLike.text = "❤️ ${video.likes}"
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val request = Request.Builder().url("http://node.waifly.com:27211/like/${video.id}").post(okhttp3.RequestBody.create(null, ByteArray(0))).build()
                    client.newCall(request).execute()
                } catch (e: Exception) {}
            }
        }

        // Просмотр
        holder.btnWatch.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra("VIDEO_URL", video.video_url)
            intent.putExtra("VIDEO_ID", video.id) // Передаем ID для просмотра
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = videos.size
}
