package com.example.androidnativowebview.data.model

data class Achievement(
    val id: Int,
    val title: String,
    val description: String,
    val emoji: String,
    val isUnlocked: Boolean,
    val progress: Int = 0,
    val total: Int = 1
)
