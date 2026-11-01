package com.ivan.animehub

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

class PlayerActivity : AppCompatActivity() {
    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val videoUrl = intent.getStringExtra("VIDEO_URL")
        val playerView = findViewById<PlayerView>(R.id.playerView)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)

        // Инициализация плеера
        player = ExoPlayer.Builder(this).build()
        playerView.player = player

        if (videoUrl != null) {
            val mediaItem = MediaItem.fromUri(videoUrl)
            player?.setMediaItem(mediaItem)
            player?.prepare()
            player?.playWhenReady = true // Начинаем воспроизведение сразу
        }

        btnBack.setOnClickListener {
            finish() // Закрываем плеер
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release() // Освобождаем ресурсы при закрытии
        player = null
    }
}
