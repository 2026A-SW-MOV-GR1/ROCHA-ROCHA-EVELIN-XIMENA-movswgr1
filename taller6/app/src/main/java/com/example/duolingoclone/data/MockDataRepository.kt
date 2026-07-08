package com.example.duolingoclone.data

import com.example.duolingoclone.R
import com.example.duolingoclone.data.model.Achievement
import com.example.duolingoclone.data.model.LeaderboardUser
import com.example.duolingoclone.data.model.Lesson
import com.example.duolingoclone.data.model.LessonType

object MockDataRepository {

    fun getLessons(): List<Lesson> = listOf(
        Lesson(1,  "Introducción",   "🌟", LessonType.LESSON,      10, isCompleted = true,  isLocked = false, 5, 5),
        Lesson(2,  "Saludos",        "👋", LessonType.LESSON,      10, isCompleted = true,  isLocked = false, 5, 5),
        Lesson(3,  "Frases Básicas", "💬", LessonType.LESSON,      10, isCompleted = true,  isLocked = false, 5, 5),
        Lesson(4,  "Checkpoint 1",   "🏆", LessonType.CHECKPOINT,  20, isCompleted = true,  isLocked = false, 5, 5),
        Lesson(5,  "Animales",       "🐾", LessonType.LESSON,      10, isCompleted = true,  isLocked = false, 5, 5),
        Lesson(6,  "Comida",         "🍎", LessonType.LESSON,      10, isCompleted = false, isLocked = false, 3, 5),
        Lesson(7,  "Familia",        "👨‍👩‍👧", LessonType.LESSON,   10, isCompleted = false, isLocked = true,  0, 5),
        Lesson(8,  "Colores",        "🎨", LessonType.LESSON,      10, isCompleted = false, isLocked = true,  0, 5),
        Lesson(9,  "Checkpoint 2",   "🏅", LessonType.CHECKPOINT,  30, isCompleted = false, isLocked = true,  0, 5),
        Lesson(10, "Números",        "🔢", LessonType.LESSON,      10, isCompleted = false, isLocked = true,  0, 5),
        Lesson(11, "Tiempo",         "⏰", LessonType.LESSON,      10, isCompleted = false, isLocked = true,  0, 5),
        Lesson(12, "Lugares",        "📍", LessonType.LESSON,      10, isCompleted = false, isLocked = true,  0, 5),
        Lesson(13, "Práctica",       "✏️", LessonType.PRACTICE,   15, isCompleted = false, isLocked = true,  0, 5),
        Lesson(14, "Checkpoint 3",   "🥇", LessonType.CHECKPOINT,  40, isCompleted = false, isLocked = true,  0, 5),
        Lesson(15, "Viajes",         "✈️", LessonType.LESSON,     10, isCompleted = false, isLocked = true,  0, 5),
        Lesson(16, "Trabajo",        "💼", LessonType.LESSON,      10, isCompleted = false, isLocked = true,  0, 5),
        Lesson(17, "Compras",        "🛒", LessonType.LESSON,      10, isCompleted = false, isLocked = true,  0, 5),
        Lesson(18, "Emociones",      "😊", LessonType.LESSON,      10, isCompleted = false, isLocked = true,  0, 5),
        Lesson(19, "Historia",       "📖", LessonType.LESSON,      10, isCompleted = false, isLocked = true,  0, 5),
        Lesson(20, "Checkpoint Final","🎓", LessonType.CHECKPOINT, 50, isCompleted = false, isLocked = true,  0, 5),
    )

    fun getLeaderboard(): List<LeaderboardUser> = listOf(
        LeaderboardUser(1,  "María García",      "MG", 4850, R.color.avatar_green),
        LeaderboardUser(2,  "Carlos López",      "CL", 3920, R.color.avatar_blue),
        LeaderboardUser(3,  "Evelin Rocha",      "ER", 3210, R.color.avatar_purple, isCurrentUser = true),
        LeaderboardUser(4,  "Ana Martínez",      "AM", 2980, R.color.avatar_orange),
        LeaderboardUser(5,  "Pedro Jiménez",     "PJ", 2750, R.color.avatar_red),
        LeaderboardUser(6,  "Lucía Fernández",   "LF", 2400, R.color.avatar_green),
        LeaderboardUser(7,  "Diego Sánchez",     "DS", 2100, R.color.avatar_blue),
        LeaderboardUser(8,  "Sofía Torres",      "ST", 1890, R.color.avatar_purple),
        LeaderboardUser(9,  "Andrés Ruiz",       "AR", 1650, R.color.avatar_orange),
        LeaderboardUser(10, "Valentina Cruz",    "VC", 1430, R.color.avatar_red),
        LeaderboardUser(11, "Mateo Rivera",      "MR", 1200, R.color.avatar_green),
        LeaderboardUser(12, "Isabella Flores",   "IF",  980, R.color.avatar_blue),
        LeaderboardUser(13, "Santiago Díaz",     "SD",  750, R.color.avatar_purple),
        LeaderboardUser(14, "Camila Morales",    "CM",  520, R.color.avatar_orange),
        LeaderboardUser(15, "Sebastián Vargas",  "SV",  310, R.color.avatar_red),
    )

    fun getAchievements(): List<Achievement> = listOf(
        Achievement(1,  "Primera Lección",     "Completa tu primera lección",         "🌟", isUnlocked = true,  1,  1),
        Achievement(2,  "Racha de 3 Días",     "Mantén una racha de 3 días",          "🔥", isUnlocked = true,  3,  3),
        Achievement(3,  "Poliglota Junior",    "Aprende 50 palabras nuevas",           "📚", isUnlocked = true,  50, 50),
        Achievement(4,  "Liga de Oro",         "Llega a la Liga de Oro",              "🏆", isUnlocked = true,  1,  1),
        Achievement(5,  "Velocista",           "Completa una lección en < 1 minuto",  "⚡", isUnlocked = true,  1,  1),
        Achievement(6,  "Perfeccionista",      "5 lecciones sin errores",             "💎", isUnlocked = false, 3,  5),
        Achievement(7,  "Racha de 7 Días",     "Mantén una racha de 7 días",          "🔥", isUnlocked = false, 4,  7),
        Achievement(8,  "Social",              "Agrega 5 amigos",                     "👥", isUnlocked = false, 2,  5),
        Achievement(9,  "Explorador",          "Completa 3 cursos diferentes",        "🗺️", isUnlocked = false, 1,  3),
        Achievement(10, "Maestro de Palabras", "Aprende 200 palabras nuevas",         "🎓", isUnlocked = false, 50, 200),
        Achievement(11, "Nocturno",            "Lección después de las 10pm",         "🌙", isUnlocked = false, 0,  1),
        Achievement(12, "Madrugador",          "Lección antes de las 7am",            "🌅", isUnlocked = false, 0,  1),
    )
}
