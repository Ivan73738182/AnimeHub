package com.ivan.animehub

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddActivity : AppCompatActivity() {

    private var selectedImageUri: Uri? = null
    private var selectedVideoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        val etTitle = findViewById<EditText>(R.id.etTitle)
        val etDescription = findViewById<EditText>(R.id.etDescription)
        val btnPickImage = findViewById<Button>(R.id.btnPickImage)
        val btnPickVideo = findViewById<Button>(R.id.btnPickVideo)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val tvStatus = findViewById<TextView>(R.id.tvStatus)

        // Выбор картинки (аватарки)
        btnPickImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, 1001)
        }

        // Выбор видео
        btnPickVideo.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "video/*"
            startActivityForResult(intent, 1002)
        }

        // Сохранение
        btnSave.setOnClickListener {
            val title = etTitle.text.toString()
            val desc = etDescription.text.toString()

            if (title.isEmpty()) {
                Toast.makeText(this, "Введите название", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (selectedImageUri == null) {
                Toast.makeText(this, "Выберите аватарку", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (selectedVideoUri == null) {
                Toast.makeText(this, "Выберите видео", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Здесь позже мы добавим отправку файлов на сервер Upstash
            Toast.makeText(this, "Всё выбрано! Скоро загрузим на сервер.", Toast.LENGTH_LONG).show()
        }
    }

    // Обработка выбора файлов из галереи
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            val uri = data.data
            if (uri != null) {
                if (requestCode == 1001) {
                    selectedImageUri = uri
                    findViewById<TextView>(R.id.tvStatus).text = "Аватарка выбрана ✓"
                } else if (requestCode == 1002) {
                    selectedVideoUri = uri
                    findViewById<TextView>(R.id.tvStatus).text = "Видео выбрано ✓"
                }
            }
        }
    }
}
