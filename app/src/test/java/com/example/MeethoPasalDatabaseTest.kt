package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.db.DatabaseMigrations
import com.example.data.local.db.MeethoPasalDatabase
import com.example.data.local.entity.AppSettingsEntity
import com.example.domain.model.AppLanguage
import com.example.domain.model.CurrencyPreference
import com.example.domain.model.ThemeMode
import java.io.File
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class MeethoPasalDatabaseTest {

  @Test
  fun roomDatabase_insertsAndRetrievesSettings() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = Room.inMemoryDatabaseBuilder(context, MeethoPasalDatabase::class.java)
      .allowMainThreadQueries()
      .build()

    try {
      val dao = db.appSettingsDao()
      val entity = AppSettingsEntity(
        themeMode = ThemeMode.DARK,
        language = AppLanguage.NEPALI,
        currency = CurrencyPreference.USD,
        updatedAtEpochMs = 123456L,
      )
      dao.upsertSettings(entity)

      val loaded = dao.observeSettings().first()
      assertNotNull(loaded)
      assertEquals(ThemeMode.DARK, loaded?.themeMode)
      assertEquals(AppLanguage.NEPALI, loaded?.language)
      assertEquals(CurrencyPreference.USD, loaded?.currency)
      assertEquals(1, dao.getSettingsCount())
    } finally {
      db.close()
    }
  }

  @Test
  fun roomDatabase_persistsThemeLanguageAndCurrencyAcrossRestart() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val dbFile = File(context.filesDir, "restart_persistence_test.db")
    if (dbFile.exists()) dbFile.delete()

    // Session 1: Create DB, persist custom theme, language, and currency, then close.
    val firstDb = Room.databaseBuilder(context, MeethoPasalDatabase::class.java, dbFile.absolutePath)
      .addMigrations(*DatabaseMigrations.ALL_MIGRATIONS)
      .allowMainThreadQueries()
      .build()

    firstDb.appSettingsDao().upsertSettings(
      AppSettingsEntity(
        themeMode = ThemeMode.LIGHT,
        language = AppLanguage.HINDI,
        currency = CurrencyPreference.EUR,
        updatedAtEpochMs = 999_000L,
      )
    )
    firstDb.close()

    // Session 2: Re-open DB simulating cold app restart.
    val restartedDb = Room.databaseBuilder(context, MeethoPasalDatabase::class.java, dbFile.absolutePath)
      .addMigrations(*DatabaseMigrations.ALL_MIGRATIONS)
      .allowMainThreadQueries()
      .build()

    try {
      val restored = restartedDb.appSettingsDao().getSettingsOnce()
      assertNotNull(restored)
      assertEquals(ThemeMode.LIGHT, restored?.themeMode)
      assertEquals(AppLanguage.HINDI, restored?.language)
      assertEquals(CurrencyPreference.EUR, restored?.currency)
      assertEquals(999_000L, restored?.updatedAtEpochMs)
    } finally {
      restartedDb.close()
      dbFile.delete()
    }
  }
}
