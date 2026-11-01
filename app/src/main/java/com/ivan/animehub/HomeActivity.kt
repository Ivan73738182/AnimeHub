package com.ivan.animehub

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val bottomAppBar = findViewById<BottomAppBar>(R.id.bottomAppBar)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAdd)
        val swipeRefresh = findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)

        bottomAppBar.replaceMenu(R.menu.bottom_nav_menu)

        fabAdd.setOnClickListener {
            startActivity(Intent(this, AddActivity::class.java))
        }

        // Логика обновления (Свайп вниз)
        swipeRefresh.setOnRefreshListener {
            // Имитация загрузки данных (1.5 секунды)
            Handler(Looper.getMainLooper()).postDelayed({
                swipeRefresh.isRefreshing = false // Останавливаем кружок
                Toast.makeText(this, "Лента обновлена!", Toast.LENGTH_SHORT).show()
                
                // ЗДЕСЬ ПОЗЖЕ БУДЕТ ЗАПРОС К СЕРВЕРУ ЗА НОВЫМИ ВИДЕО
            }, 1500)
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
}
