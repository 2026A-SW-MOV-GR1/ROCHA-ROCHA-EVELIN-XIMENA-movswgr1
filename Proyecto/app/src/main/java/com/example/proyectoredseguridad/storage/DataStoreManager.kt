package com.example.proyectoredseguridad.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "data_store")

class DataStoreManager(private val context: Context) {

    suspend fun save(key: String, value: String) {
        val prefKey = stringPreferencesKey(key)
        context.dataStore.edit { preferences ->
            preferences[prefKey] = value
        }
    }

    suspend fun retrieve(key: String): String? {
        val prefKey = stringPreferencesKey(key)
        return context.dataStore.data
            .map { preferences -> preferences[prefKey] }
            .first()
    }
}
