package com.example.dualpersistenceapp.data.local.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Query("SELECT * FROM items ORDER BY createdAt DESC")
    fun getAllItems(): Flow<List<ItemEntity>>

    @Insert
    fun insert(item: ItemEntity)

    @Update
    fun update(item: ItemEntity)

    @Delete
    fun delete(item: ItemEntity)
}
