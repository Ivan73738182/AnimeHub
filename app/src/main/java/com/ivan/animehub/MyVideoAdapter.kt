package com.ivan.animehub

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyVideoAdapter(private val videos: List<Video>) : RecyclerView.Adapter<MyVideoAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_my_video, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val video = videos[position]
        holder.tvTitle.text = video.title
        
        // Показываем статус
        when (video.status) {
            "pending" -> {
                holder.tvStatus.text = "На модерации ⏳"
                holder.tvStatus.setTextColor(android.graphics.Color.parseColor("#FFC107"))
            }
            "approved" -> {
                holder.tvStatus.text = "Опубликовано ✅"
                holder.tvStatus.setTextColor(android.graphics.Color.parseColor("#4CAF50"))
            }
            else -> {
                holder.tvStatus.text = "Отклонено ❌"
                holder.tvStatus.setTextColor(android.graphics.Color.parseColor("#F44336"))
            }
        }
    }

    override fun getItemCount() = videos.size
}
