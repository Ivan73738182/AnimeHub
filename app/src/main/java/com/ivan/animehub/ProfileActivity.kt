package com.ivan.animehub

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class ProfileActivity : AppCompatActivity() {
    private val client = OkHttpClient()
    private lateinit var adapter: MyVideoAdapter
    private val myVideosList = mutableListOf<Video>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val tvPhone = findViewById<TextView>(R.id.tvPhone)
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        val rvMyVideos = findViewById<RecyclerView>(R.id.rvMyVideos)

        val session = SessionManager(this)
        val phone = session.getPhone() ?: "Неизвестно"
        tvPhone.text = phone

        // Настройка списка
        rvMyVideos.layoutManager = LinearLayoutManager(this)
        adapter = MyVideoAdapter(myVideosList)
        rvMyVideos.adapter = adapter

        // Загружаем свои видео
        loadMyVideos(phone)

        btnLogout.setOnClickListener {
            session.logout()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun loadMyVideos(phone: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = Request.Builder().url("http://node.waifly.com:27211/my-videos/$phone").build()
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val json = response.body?.string()
                    val jsonObj = JSONObject(json!!)
                    val videosArray = jsonObj.getJSONArray("videos")
                    val type = object : TypeToken<List<Video>>() {}.type
                    val newVideos: List<Video> = Gson().fromJson(videosArray.toString(), type)

                    runOnUiThread {
                        myVideosList.clear()
                        myVideosList.addAll(newVideos)
                        adapter.notifyDataSetChanged()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread { Toast.makeText(this@ProfileActivity, "Ошибка загрузки", Toast.LENGTH_SHORT).show() }
            }
        }
    }
}
