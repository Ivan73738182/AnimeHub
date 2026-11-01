package com.ivan.animehub

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
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
        
        val session = SessionManager(this)
        if (session.getPhone() != null) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_main)

        val etPhone = findViewById<EditText>(R.id.etPhone)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvBotLink = findViewById<TextView>(R.id.tvBotLink)
        
        // Кубики для кода
        val d1 = findViewById<EditText>(R.id.d1)
        val d2 = findViewById<EditText>(R.id.d2)
        val d3 = findViewById<EditText>(R.id.d3)
        val d4 = findViewById<EditText>(R.id.d4)
        val d5 = findViewById<EditText>(R.id.d5)
        val d6 = findViewById<EditText>(R.id.d6)

        // Автопереход между кубиками
        val boxes = arrayOf(d1, d2, d3, d4, d5, d6)
        for (i in boxes.indices) {
            boxes[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s?.length == 1 && i < boxes.size - 1) {
                        boxes[i + 1].requestFocus()
                    }
                }
                override fun afterTextChanged(s: Editable?) {}
            })
        }

        tvBotLink.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://t.me/AnimeHubHelpBot")
            startActivity(intent)
        }

        btnLogin.setOnClickListener {
            val phone = etPhone.text.toString()
            // Собираем код из кубиков
            val code = d1.text.toString() + d2.text.toString() + d3.text.toString() + 
                       d4.text.toString() + d5.text.toString() + d6.text.toString()

            if (phone.isEmpty() || code.length < 6) {
                Toast.makeText(this, "Введите номер и 6-значный код", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

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
                            session.saveLogin(phone)
                            Toast.makeText(this@MainActivity, "Успешный вход!", Toast.LENGTH_SHORT).show()
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
