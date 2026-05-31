package com.example.dualpersistenceapp.data.repository

import com.example.dualpersistenceapp.data.model.Item

interface ItemRepository {
    suspend fun getAll(): List<Item>
    suspend fun insert(item: Item)
    suspend fun update(item: Item)
    suspend fun delete(item: Item)
}
