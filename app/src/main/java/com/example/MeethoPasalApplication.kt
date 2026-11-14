package com.example

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.example.core.logging.AppLogger
import com.example.worker.WorkScheduler
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MeethoPasalApplication : Application(), Configuration.Provider {

  @Inject lateinit var workerFactory: HiltWorkerFactory

  @Inject lateinit var workScheduler: WorkScheduler

  override val workManagerConfiguration: Configuration
    get() =
      Configuration.Builder()
        .setWorkerFactory(workerFactory)
        .setMinimumLoggingLevel(
          if (BuildConfig.DEBUG) android.util.Log.DEBUG else android.util.Log.INFO
        )
        .build()

  override fun onCreate() {
    super.onCreate()
    AppLogger.i(TAG, "Starting MeethoPasal PRO v${BuildConfig.VERSION_NAME} (appId=${BuildConfig.APPLICATION_ID})")
    workScheduler.scheduleFoundationCheck()
  }

  private companion object {
    const val TAG = "Application"
  }
}
