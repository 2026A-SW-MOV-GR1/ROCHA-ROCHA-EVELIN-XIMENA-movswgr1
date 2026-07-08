package com.example.androidnativowebview.data.model

enum class LessonType { LESSON, CHECKPOINT, PRACTICE }

data class Lesson(
    val id: Int,
    val title: String,
    val emoji: String,
    val type: LessonType,
    val xpReward: Int,
    val isCompleted: Boolean,
    val isLocked: Boolean,
    val currentProgress: Int = 0,
    val totalProgress: Int = 5
)
