package com.example.proyectoredseguridad.storage

import android.content.Context

class SharedPreferencesManager(context: Context) {

    private val prefs = context.getSharedPreferences("shared_prefs_store", Context.MODE_PRIVATE)

    fun save(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    fun retrieve(key: String): String? = prefs.getString(key, null)
}
