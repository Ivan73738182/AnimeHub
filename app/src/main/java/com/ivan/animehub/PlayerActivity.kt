package com.ivan.animehub

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request

class PlayerActivity : AppCompatActivity() {
    private var player: ExoPlayer? = null
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val videoUrl = intent.getStringExtra("VIDEO_URL")
        val videoId = intent.getStringExtra("VIDEO_ID")
        val playerView = findViewById<PlayerView>(R.id.playerView)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)

        player = ExoPlayer.Builder(this).build()
        playerView.player = player

        if (videoUrl != null) {
            val mediaItem = MediaItem.fromUri(videoUrl)
            player?.setMediaItem(mediaItem)
            player?.prepare()
            player?.playWhenReady = true
        }

        // Отправляем просмотр на сервер
        if (videoId != null) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val request = Request.Builder().url("http://node.waifly.com:27211/view/$videoId").post(okhttp3.RequestBody.create(null, ByteArray(0))).build()
                    client.newCall(request).execute()
                } catch (e: Exception) {}
            }
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        player = null
    }
}
