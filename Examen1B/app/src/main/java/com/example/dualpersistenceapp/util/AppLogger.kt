package com.example.dualpersistenceapp.util

import android.util.Log

object AppLogger {
    private const val TAG = "DualPersistence"

    fun debug(tag: String = TAG, message: String) {
        safeLog { Log.d(TAG, "[DEBUG] [$tag] $message") }
    }

    fun info(tag: String = TAG, message: String) {
        safeLog { Log.i(TAG, "[INFO] [$tag] $message") }
    }

    fun error(tag: String = TAG, message: String, throwable: Throwable? = null) {
        safeLog { Log.e(TAG, "[ERROR] [$tag] $message", throwable) }
    }

    private inline fun safeLog(block: () -> Int) {
        runCatching { block() }
            .onFailure { println(it.message ?: "Logging unavailable") }
    }
}
