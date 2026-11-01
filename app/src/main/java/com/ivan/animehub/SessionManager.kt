package com.ivan.animehub

import android.content.Context

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("AnimeHubPrefs", Context.MODE_PRIVATE)

    fun saveLogin(phone: String) {
        prefs.edit().putString("user_phone", phone).apply()
    }

    fun getPhone(): String? {
        return prefs.getString("user_phone", null)
    }

    fun logout() {
        prefs.edit().clear().apply()
    }
}
