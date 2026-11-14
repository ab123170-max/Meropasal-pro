package com.example.worker

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.core.logging.AppLogger
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkScheduler @Inject constructor(
  @ApplicationContext private val context: Context,
) {
  fun scheduleFoundationCheck() {
    try {
      val request = OneTimeWorkRequestBuilder<FoundationHealthWorker>().build()
      WorkManager.getInstance(context).enqueueUniqueWork(
        FoundationHealthWorker.UNIQUE_WORK_NAME,
        ExistingWorkPolicy.KEEP,
        request,
      )
      AppLogger.d(TAG, "Enqueued foundation health check worker")
    } catch (e: Exception) {
      AppLogger.w(TAG, "WorkManager scheduling skipped safely", e)
    }
  }

  private companion object {
    const val TAG = "WorkScheduler"
  }
}
