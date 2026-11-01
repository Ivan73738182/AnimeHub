package com.ivan.animehub

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val bottomAppBar = findViewById<BottomAppBar>(R.id.bottomAppBar)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAdd)

        // Добавляем вкладки в меню
        bottomAppBar.replaceMenu(R.menu.bottom_nav_menu)

        // Нажатие на кнопку "Плюс"
        fabAdd.setOnClickListener {
            startActivity(Intent(this, AddActivity::class.java))
        }

        // Переключение вкладок
        bottomAppBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    Toast.makeText(this, "Вы на главной", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_profile -> {
                    Toast.makeText(this, "Выход...", Toast.LENGTH_SHORT).show()
                    val session = SessionManager(this)
                    session.logout()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }
}
