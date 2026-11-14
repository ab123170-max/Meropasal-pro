package com.example.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.core.logging.AppLogger
import com.example.core.result.AppResult
import com.example.domain.repository.SettingsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Offline-safe WorkManager foundation worker that verifies local Room database health
 * and ensures default application preferences exist without requiring network connectivity.
 */
@HiltWorker
class FoundationHealthWorker @AssistedInject constructor(
  @Assisted appContext: Context,
  @Assisted workerParams: WorkerParameters,
  private val settingsRepository: SettingsRepository,
) : CoroutineWorker(appContext, workerParams) {

  override suspend fun doWork(): Result {
    AppLogger.d(TAG, "Running offline foundation health check (attempt=$runAttemptCount)")
    return when (val initResult = settingsRepository.ensureInitialized()) {
      is AppResult.Success -> {
        when (val healthResult = settingsRepository.getDatabaseHealth()) {
          is AppResult.Success -> {
            AppLogger.i(
              TAG,
              "Foundation check passed: db=${healthResult.data.databaseName} v${healthResult.data.schemaVersion} records=${healthResult.data.settingsRecordCount}",
            )
            Result.success()
          }
          is AppResult.Error -> {
            AppLogger.w(TAG, "Health check error: ${healthResult.error.message}")
            if (runAttemptCount < MAX_RETRIES) Result.retry() else Result.failure()
          }
          is AppResult.Loading -> Result.retry()
        }
      }
      is AppResult.Error -> {
        AppLogger.e(TAG, "Database initialization error in worker: ${initResult.error.message}")
        if (runAttemptCount < MAX_RETRIES) Result.retry() else Result.failure()
      }
      is AppResult.Loading -> Result.retry()
    }
  }

  companion object {
    const val UNIQUE_WORK_NAME = "meethopasal_foundation_health_check"
    private const val TAG = "FoundationWorker"
    private const val MAX_RETRIES = 2
  }
}
