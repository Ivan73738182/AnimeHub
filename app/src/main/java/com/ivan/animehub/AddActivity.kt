package com.ivan.animehub

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

class AddActivity : AppCompatActivity() {

    private var selectedImageUri: Uri? = null
    private var selectedVideoUri: Uri? = null
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        val etTitle = findViewById<EditText>(R.id.etTitle)
        val etDescription = findViewById<EditText>(R.id.etDescription)
        val btnPickImage = findViewById<Button>(R.id.btnPickImage)
        val btnPickVideo = findViewById<Button>(R.id.btnPickVideo)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val tvStatus = findViewById<TextView>(R.id.tvStatus)
        val frameImage = findViewById<FrameLayout>(R.id.frameImage)
        val ivPreview = findViewById<ImageView>(R.id.ivPreview)
        val tvPlus = findViewById<TextView>(R.id.tvPlus)

        // Нажатие на квадрат = выбор фото
        frameImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, 1001)
        }
        
        btnPickImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, 1001)
        }

        btnPickVideo.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "video/*"
            startActivityForResult(intent, 1002)
        }

        btnSave.setOnClickListener {
            val title = etTitle.text.toString()
            val desc = etDescription.text.toString()

            if (title.isEmpty() || selectedImageUri == null || selectedVideoUri == null) {
                Toast.makeText(this, "Заполните все поля и выберите файлы", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            tvStatus.text = "Начинаем загрузку..."
            btnSave.isEnabled = false

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val imageUrl = uploadFile(selectedImageUri!!, "image")
                    updateStatus("Картинка загружена. Грузим видео...")

                    val videoUrl = uploadFile(selectedVideoUri!!, "video")
                    updateStatus("Видео загружено. Сохраняем данные...")

                    saveVideoInfo(title, desc, videoUrl!!, imageUrl!!)

                    runOnUiThread {
                        Toast.makeText(this@AddActivity, "Успешно опубликовано!", Toast.LENGTH_LONG).show()
                        finish()
                    }

                } catch (e: Exception) {
                    runOnUiThread {
                        tvStatus.text = "Ошибка: ${e.message}"
                        btnSave.isEnabled = true
                    }
                }
            }
        }
    }

    private fun uploadFile(uri: Uri, type: String): String? {
        val json = JSONObject()
        json.put("filename", "file_${System.currentTimeMillis()}")
        json.put("file_type", type)
        
        val reqBody = json.toString().toRequestBody("application/json".toMediaType())
        val req = Request.Builder().url("http://node.waifly.com:27211/get-upload-url").post(reqBody).build()
        val res = client.newCall(req).execute()
        
        if (!res.isSuccessful) throw Exception("Сервер не дал ссылку")
        
        val resJson = JSONObject(res.body?.string() ?: "")
        val uploadUrl = resJson.getString("upload_url")
        val publicUrl = resJson.getString("public_url")

        val inputStream = contentResolver.openInputStream(uri)
        val tempFile = File(cacheDir, "temp_${type}")
        val outputStream = FileOutputStream(tempFile)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()

        val mediaType = if (type == "image") "image/*".toMediaType() else "video/*".toMediaType()
        val fileBody = tempFile.asRequestBody(mediaType)
        
        val uploadReq = Request.Builder().url(uploadUrl).put(fileBody).build()
        val uploadRes = client.newCall(uploadReq).execute()
        
        if (!uploadRes.isSuccessful) throw Exception("Не удалось загрузить $type в облако")
        
        tempFile.delete()
        return publicUrl
    }

    private fun saveVideoInfo(title: String, desc: String, videoUrl: String, imageUrl: String) {
        val session = SessionManager(this)
        val phone = session.getPhone() ?: "unknown"

        val json = JSONObject()
        json.put("title", title)
        json.put("description", desc)
        json.put("author_phone", phone)
        json.put("video_url", videoUrl)
        json.put("image_url", imageUrl)

        val body = json.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("http://node.waifly.com:27211/save-video")
            .post(body)
            .build()

        val response = client.newCall(request).execute()
        if (!response.isSuccessful) throw Exception("Ошибка сохранения данных")
    }

    private fun updateStatus(text: String) {
        runOnUiThread {
            findViewById<TextView>(R.id.tvStatus).text = text
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            val uri = data.data
            if (uri != null) {
                if (requestCode == 1001) {
                    selectedImageUri = uri
                    // Показываем картинку в квадрате
                    findViewById<ImageView>(R.id.ivPreview).setImageURI(uri)
                    findViewById<TextView>(R.id.tvPlus).visibility = android.view.View.GONE
                    findViewById<TextView>(R.id.tvStatus).text = "Аватарка выбрана ✓"
                } else if (requestCode == 1002) {
                    selectedVideoUri = uri
                    findViewById<TextView>(R.id.tvStatus).text = "Видео выбрано ✓"
                }
            }
        }
    }
}
