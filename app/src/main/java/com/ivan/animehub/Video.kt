package com.ivan.animehub

data class Video(
    val id: String,
    val title: String,
    val description: String,
    val author_phone: String,
    val video_url: String,
    val image_url: String,
    val status: String,
    var likes: Int = 0,
    var views: Int = 0
)
