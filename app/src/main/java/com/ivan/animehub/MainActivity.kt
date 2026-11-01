package com.ivan.animehub

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etPhone = findViewById<EditText>(R.id.etPhone)
        val etCode = findViewById<EditText>(R.id.etCode)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val phone = etPhone.text.toString()
            val code = etCode.text.toString()

            if (phone.isEmpty() || code.isEmpty()) {
                Toast.makeText(this, "Введите номер и код", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Отправляем запрос на наш сервер Waifly
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val client = OkHttpClient()
                    val json = JSONObject()
                    json.put("phone", phone)
                    json.put("code", code)

                    val body = json.toString().toRequestBody("application/json".toMediaType())
                    val request = Request.Builder()
                        .url("http://node.waifly.com:27211/verify-code")
                        .post(body)
                        .build()

                    val response = client.newCall(request).execute()
                    
                    runOnUiThread {
                        if (response.isSuccessful) {
                            Toast.makeText(this@MainActivity, "Успешный вход!", Toast.LENGTH_SHORT).show()
                            // Переход на главный экран (пока просто заглушка)
                            startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this@MainActivity, "Неверный код", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Ошибка сети", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
