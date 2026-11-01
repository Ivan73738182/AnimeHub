package com.ivan.animehub

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class HomeActivity : AppCompatActivity() {
    private lateinit var videoAdapter: VideoAdapter
    private val videosList = mutableListOf<Video>()
    private lateinit var swipeRefresh: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val bottomAppBar = findViewById<BottomAppBar>(R.id.bottomAppBar)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAdd)
        val rvVideos = findViewById<RecyclerView>(R.id.rvVideos)
        swipeRefresh = findViewById(R.id.swipeRefresh)

        // Настройка списка
        rvVideos.layoutManager = LinearLayoutManager(this)
        videoAdapter = VideoAdapter(videosList)
        rvVideos.adapter = videoAdapter

        bottomAppBar.replaceMenu(R.menu.bottom_nav_menu)

        fabAdd.setOnClickListener {
            startActivity(Intent(this, AddActivity::class.java))
        }

        // Загрузка ленты
        loadVideos()

        // Обновление свайпом
        swipeRefresh.setOnRefreshListener {
            loadVideos()
        }

        bottomAppBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun loadVideos() {
        swipeRefresh.isRefreshing = true
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val client = OkHttpClient()
                val request = Request.Builder().url("http://node.waifly.com:27211/videos").build()
                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val json = response.body?.string()
                    val jsonObj = JSONObject(json!!)
                    val videosArray = jsonObj.getJSONArray("videos")
                    
                    val type = object : TypeToken<List<Video>>() {}.type
                    val newVideos: List<Video> = Gson().fromJson(videosArray.toString(), type)

                    runOnUiThread {
                        videosList.clear()
                        videosList.addAll(newVideos)
                        videoAdapter.notifyDataSetChanged()
                        swipeRefresh.isRefreshing = false
                    }
                } else {
                    runOnUiThread {
                        swipeRefresh.isRefreshing = false
                        Toast.makeText(this@HomeActivity, "Ошибка загрузки", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("HomeActivity", "Ошибка: ${e.message}")
                runOnUiThread {
                    swipeRefresh.isRefreshing = false
                    Toast.makeText(this@HomeActivity, "Нет связи с сервером", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
