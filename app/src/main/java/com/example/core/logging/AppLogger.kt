package com.example.core.logging

import android.util.Log
import com.example.BuildConfig

/**
 * Centralized diagnostic logger for MeethoPasal PRO.
 * Guards verbose debug logs behind [BuildConfig.DEBUG] and avoids leaking sensitive payloads.
 */
object AppLogger {
  private const val ROOT_TAG = "MeethoPasal"

  fun d(tag: String, message: String) {
    if (BuildConfig.DEBUG) {
      safeLog { Log.d("$ROOT_TAG:$tag", message) }
    }
  }

  fun i(tag: String, message: String) {
    safeLog { Log.i("$ROOT_TAG:$tag", message) }
  }

  fun w(tag: String, message: String, throwable: Throwable? = null) {
    safeLog {
      if (throwable != null) {
        Log.w("$ROOT_TAG:$tag", message, throwable)
      } else {
        Log.w("$ROOT_TAG:$tag", message)
      }
    }
  }

  fun e(tag: String, message: String, throwable: Throwable? = null) {
    safeLog {
      if (throwable != null) {
        Log.e("$ROOT_TAG:$tag", message, throwable)
      } else {
        Log.e("$ROOT_TAG:$tag", message)
      }
    }
  }

  private inline fun safeLog(block: () -> Unit) {
    try {
      block()
    } catch (_: RuntimeException) {
      // Allows pure JVM unit tests without Robolectric to call AppLogger safely.
    }
  }
}
