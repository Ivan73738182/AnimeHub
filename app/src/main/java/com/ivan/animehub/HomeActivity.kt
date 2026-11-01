package com.ivan.animehub

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAdd)

        // Нажатие на кнопку "Плюс"
        fabAdd.setOnClickListener {
            Toast.makeText(this, "Здесь будет экран добавления видео", Toast.LENGTH_SHORT).show()
            // Позже здесь будет переход на AddVideoActivity
        }

        // Переключение вкладок в нижнем меню
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    Toast.makeText(this, "Вы на главной", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_profile -> {
                    Toast.makeText(this, "Здесь будет профиль", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }
}
