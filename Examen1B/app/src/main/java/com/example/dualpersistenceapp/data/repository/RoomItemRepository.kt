package com.example.dualpersistenceapp.data.repository

import com.example.dualpersistenceapp.data.local.room.ItemDao
import com.example.dualpersistenceapp.data.local.room.ItemEntity
import com.example.dualpersistenceapp.data.model.Item
import com.example.dualpersistenceapp.util.AppLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class RoomItemRepository(private val dao: ItemDao) : ItemRepository {

    override suspend fun getAll(): List<Item> {
        AppLogger.debug("RoomRepo", "getAll()")
        return dao.getAllItems().first().map { it.toItem() }
    }

    fun getAllAsFlow(): Flow<List<Item>> {
        AppLogger.debug("RoomRepo", "getAllAsFlow()")
        return dao.getAllItems().map { items ->
            items.map { it.toItem() }
        }
    }

    override suspend fun insert(item: Item) {
        AppLogger.info("RoomRepo", "insert: ${item.title}")
        dao.insert(item.toEntity())
    }

    override suspend fun update(item: Item) {
        AppLogger.info("RoomRepo", "update: ${item.title}")
        dao.update(item.toEntity())
    }

    override suspend fun delete(item: Item) {
        AppLogger.info("RoomRepo", "delete: ${item.title}")
        dao.delete(item.toEntity())
    }

    private fun ItemEntity.toItem() = Item(id, title, description, createdAt)
    private fun Item.toEntity() = ItemEntity(id, title, description, createdAt)
}
