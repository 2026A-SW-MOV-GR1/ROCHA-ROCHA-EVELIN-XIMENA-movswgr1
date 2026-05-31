package com.example.dualpersistenceapp.data.model

data class Item(
    val id: Int = 0,
    val title: String,
    val description: String,
    val createdAt: Long = System.currentTimeMillis()
)
