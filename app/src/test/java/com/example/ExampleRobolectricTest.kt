package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import com.example.core.dispatcher.DispatcherProvider
import com.example.data.remote.NoOpRemoteDataSource
import com.example.data.repository.SettingsRepositoryImpl
import com.example.domain.model.AppLanguage
import com.example.domain.model.CurrencyPreference
import com.example.domain.model.ThemeMode
import com.example.worker.FoundationHealthWorker
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun appMetadata_matchesMeethoPasalProRequirements() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("MeethoPasal PRO", appName)
    assertEquals("com.meethopasal.pro", BuildConfig.APPLICATION_ID)
    assertTrue(appName.length <= 30)
  }

  @Test
  fun foundationHealthWorker_succeedsOfflineWithRoomDatabase() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = androidx.room.Room.inMemoryDatabaseBuilder(
      context,
      com.example.data.local.db.MeethoPasalDatabase::class.java,
    ).allowMainThreadQueries().build()

    try {
      val repo = SettingsRepositoryImpl(
        settingsDao = db.appSettingsDao(),
        remoteDataSource = NoOpRemoteDataSource(),
        dispatchers = object : DispatcherProvider {
          override val main: CoroutineDispatcher = Dispatchers.Unconfined
          override val io: CoroutineDispatcher = Dispatchers.Unconfined
          override val default: CoroutineDispatcher = Dispatchers.Unconfined
        },
      )

      val worker = TestListenableWorkerBuilder<FoundationHealthWorker>(context)
        .setWorkerFactory(
          object : androidx.work.WorkerFactory() {
            override fun createWorker(
              appContext: Context,
              workerClassName: String,
              workerParameters: androidx.work.WorkerParameters,
            ): ListenableWorker = FoundationHealthWorker(appContext, workerParameters, repo)
          }
        )
        .build()

      val result = worker.doWork()
      assertEquals(ListenableWorker.Result.success(), result)

      val seeded = db.appSettingsDao().getSettingsOnce()
      assertEquals(ThemeMode.SYSTEM, seeded?.themeMode)
      assertEquals(AppLanguage.ENGLISH, seeded?.language)
      assertEquals(CurrencyPreference.NPR, seeded?.currency)
    } finally {
      db.close()
    }
  }
}
