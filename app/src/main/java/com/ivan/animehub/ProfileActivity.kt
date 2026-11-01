package com.ivan.animehub

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val tvPhone = findViewById<TextView>(R.id.tvPhone)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        // Получаем сохраненный номер
        val session = SessionManager(this)
        val phone = session.getPhone() ?: "Неизвестно"
        tvPhone.text = phone

        // Кнопка Выйти
        btnLogout.setOnClickListener {
            session.logout() // Очищаем память
            startActivity(Intent(this, MainActivity::class.java)) // На экран входа
            finish() // Закрываем профиль
        }
    }
}
