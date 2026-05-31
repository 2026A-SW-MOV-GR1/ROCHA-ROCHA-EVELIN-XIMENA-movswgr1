package com.example.dualpersistenceapp.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.dualpersistenceapp.data.model.Item
import com.example.dualpersistenceapp.util.AppLogger
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.first

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "items_store")

class DataStoreItemRepository(private val context: Context) : ItemRepository {

    private val gson = Gson()
    private val itemsKey = stringPreferencesKey("items_json")

    override suspend fun getAll(): List<Item> {
        AppLogger.debug("DataStoreRepo", "getAll()")
        val prefs = context.dataStore.data.first()
        val json = prefs[itemsKey] ?: return emptyList()
        val type = object : TypeToken<List<Item>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    override suspend fun insert(item: Item) {
        AppLogger.info("DataStoreRepo", "insert: ${item.title}")
        val current = getAll().toMutableList()
        val newId = if (current.isEmpty()) 1 else current.maxOf { it.id } + 1
        current.add(item.copy(id = newId))
        saveAll(current)
    }

    override suspend fun update(item: Item) {
        AppLogger.info("DataStoreRepo", "update: ${item.title}")
        val current = getAll().toMutableList()
        val index = current.indexOfFirst { it.id == item.id }
        if (index >= 0) {
            current[index] = item
            saveAll(current)
        }
    }

    override suspend fun delete(item: Item) {
        AppLogger.info("DataStoreRepo", "delete: ${item.title}")
        val current = getAll().toMutableList()
        current.removeAll { it.id == item.id }
        saveAll(current)
    }

    private suspend fun saveAll(items: List<Item>) {
        context.dataStore.edit { prefs ->
            prefs[itemsKey] = gson.toJson(items)
        }
    }
}
