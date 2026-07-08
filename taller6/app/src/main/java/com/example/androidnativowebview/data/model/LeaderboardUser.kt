package com.example.androidnativowebview.data.model

data class LeaderboardUser(
    val rank: Int,
    val name: String,
    val initials: String,
    val xp: Int,
    val avatarColorRes: Int,
    val isCurrentUser: Boolean = false
)
