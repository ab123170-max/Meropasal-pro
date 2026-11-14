package com.example

import com.example.core.dispatcher.DispatcherProvider
import com.example.core.result.AppError
import com.example.core.result.AppResult
import com.example.data.local.dao.AppSettingsDao
import com.example.data.local.entity.AppSettingsEntity
import com.example.data.remote.NoOpRemoteDataSource
import com.example.data.repository.SettingsRepositoryImpl
import com.example.domain.model.AppLanguage
import com.example.domain.model.CurrencyPreference
import com.example.domain.model.RemoteSyncStatus
import com.example.domain.model.ThemeMode
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SettingsRepositoryTest {

  private val unconfinedDispatchers = object : DispatcherProvider {
    override val main: CoroutineDispatcher = Dispatchers.Unconfined
    override val io: CoroutineDispatcher = Dispatchers.Unconfined
    override val default: CoroutineDispatcher = Dispatchers.Unconfined
  }

  @Test
  fun ensureInitialized_seedsDefaultsAndUpdatesPreferences() = runBlocking {
    val dao = FakeAppSettingsDao()
    val repo = SettingsRepositoryImpl(dao, NoOpRemoteDataSource(), unconfinedDispatchers)

    val initial = repo.ensureInitialized()
    assertTrue(initial is AppResult.Success)
    assertEquals(ThemeMode.SYSTEM, (initial as AppResult.Success).data.themeMode)

    val themeUpdated = repo.updateThemeMode(ThemeMode.DARK)
    assertEquals(ThemeMode.DARK, (themeUpdated as AppResult.Success).data.themeMode)

    val langUpdated = repo.updateLanguage(AppLanguage.NEPALI)
    assertEquals(AppLanguage.NEPALI, (langUpdated as AppResult.Success).data.language)

    val currencyUpdated = repo.updateCurrency(CurrencyPreference.USD)
    assertEquals(CurrencyPreference.USD, (currencyUpdated as AppResult.Success).data.currency)

    val health = repo.getDatabaseHealth()
    assertTrue(health is AppResult.Success)
    assertEquals(1, (health as AppResult.Success).data.settingsRecordCount)
    assertEquals(RemoteSyncStatus.OfflineStandby, repo.getRemoteSyncStatus())
  }

  @Test
  fun repository_returnsDatabaseErrorWhenDaoThrows() = runBlocking {
    val failingDao = object : AppSettingsDao {
      override fun observeSettings(id: Int): Flow<AppSettingsEntity?> = flow {
        throw IllegalStateException("Simulated SQLite disk I/O error")
      }

      override suspend fun getSettingsOnce(id: Int): AppSettingsEntity? {
        throw IllegalStateException("Simulated SQLite read failure")
      }

      override suspend fun upsertSettings(entity: AppSettingsEntity) {
        throw IllegalStateException("Simulated SQLite write failure")
      }

      override suspend fun updateThemeMode(themeMode: ThemeMode, updatedAt: Long, id: Int): Int = 0

      override suspend fun updateLanguage(language: AppLanguage, updatedAt: Long, id: Int): Int = 0

      override suspend fun updateCurrency(currency: CurrencyPreference, updatedAt: Long, id: Int): Int = 0

      override suspend fun getSettingsCount(): Int = 0

      override suspend fun clearAll() = Unit
    }

    val repo = SettingsRepositoryImpl(failingDao, NoOpRemoteDataSource(), unconfinedDispatchers)
    val result = repo.ensureInitialized()
    assertTrue(result is AppResult.Error)
    assertTrue((result as AppResult.Error).error is AppError.DatabaseError)
  }

  private class FakeAppSettingsDao : AppSettingsDao {
    private val store = MutableStateFlow<AppSettingsEntity?>(null)

    override fun observeSettings(id: Int): Flow<AppSettingsEntity?> = store

    override suspend fun getSettingsOnce(id: Int): AppSettingsEntity? = store.value

    override suspend fun upsertSettings(entity: AppSettingsEntity) {
      store.value = entity
    }

    override suspend fun updateThemeMode(themeMode: ThemeMode, updatedAt: Long, id: Int): Int {
      val current = store.value ?: return 0
      store.value = current.copy(themeMode = themeMode, updatedAtEpochMs = updatedAt)
      return 1
    }

    override suspend fun updateLanguage(language: AppLanguage, updatedAt: Long, id: Int): Int {
      val current = store.value ?: return 0
      store.value = current.copy(language = language, updatedAtEpochMs = updatedAt)
      return 1
    }

    override suspend fun updateCurrency(currency: CurrencyPreference, updatedAt: Long, id: Int): Int {
      val current = store.value ?: return 0
      store.value = current.copy(currency = currency, updatedAtEpochMs = updatedAt)
      return 1
    }

    override suspend fun getSettingsCount(): Int = if (store.value == null) 0 else 1

    override suspend fun clearAll() {
      store.value = null
    }
  }
}
