package com.example.data.repository

import com.example.core.dispatcher.DispatcherProvider
import com.example.core.logging.AppLogger
import com.example.core.result.AppError
import com.example.core.result.AppResult
import com.example.data.local.dao.AppSettingsDao
import com.example.data.local.db.DatabaseMigrations
import com.example.data.local.entity.AppSettingsEntity
import com.example.data.local.mapper.toDomain
import com.example.data.remote.RemoteDataSource
import com.example.domain.model.AppLanguage
import com.example.domain.model.AppSettings
import com.example.domain.model.CurrencyPreference
import com.example.domain.model.DatabaseHealthInfo
import com.example.domain.model.RemoteSyncStatus
import com.example.domain.model.ThemeMode
import com.example.domain.repository.SettingsRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext

@Singleton
class SettingsRepositoryImpl @Inject constructor(
  private val settingsDao: AppSettingsDao,
  private val remoteDataSource: RemoteDataSource,
  private val dispatchers: DispatcherProvider,
) : SettingsRepository {

  override fun observeSettings(): Flow<AppResult<AppSettings>> =
    settingsDao
      .observeSettings()
      .map<AppSettingsEntity?, AppResult<AppSettings>> { entity ->
        if (entity == null) {
          val seeded = seedDefaultSettingsInternal()
          AppResult.Success(seeded.toDomain())
        } else {
          AppResult.Success(entity.toDomain())
        }
      }
      .onStart { emit(AppResult.Loading) }
      .catch { throwable ->
        if (throwable is CancellationException) throw throwable
        AppLogger.e(TAG, "Failed to observe app settings", throwable)
        emit(
          AppResult.Error(
            AppError.DatabaseError(
              message = throwable.message ?: "Failed to read local preferences",
              cause = throwable,
            )
          )
        )
      }
      .flowOn(dispatchers.io)

  override suspend fun ensureInitialized(): AppResult<AppSettings> =
    withContext(dispatchers.io) {
      try {
        val existing = settingsDao.getSettingsOnce()
        val resolved = existing ?: seedDefaultSettingsInternal()
        AppLogger.d(TAG, "Local settings initialized (updatedAt=${resolved.updatedAtEpochMs})")
        AppResult.Success(resolved.toDomain())
      } catch (e: CancellationException) {
        throw e
      } catch (e: Exception) {
        AppLogger.e(TAG, "Failed to initialize local settings", e)
        AppResult.Error(
          AppError.DatabaseError(
            message = e.message ?: "Unable to initialize local settings database",
            cause = e,
          )
        )
      }
    }

  override suspend fun getSettingsOnce(): AppResult<AppSettings> = ensureInitialized()

  override suspend fun updateThemeMode(themeMode: ThemeMode): AppResult<AppSettings> =
    updateSetting { current, now ->
      val updatedRows = settingsDao.updateThemeMode(themeMode, now)
      val next = current.copy(themeMode = themeMode, updatedAtEpochMs = now)
      if (updatedRows == 0) settingsDao.upsertSettings(next)
      next
    }

  override suspend fun updateLanguage(language: AppLanguage): AppResult<AppSettings> =
    updateSetting { current, now ->
      val updatedRows = settingsDao.updateLanguage(language, now)
      val next = current.copy(language = language, updatedAtEpochMs = now)
      if (updatedRows == 0) settingsDao.upsertSettings(next)
      next
    }

  override suspend fun updateCurrency(currency: CurrencyPreference): AppResult<AppSettings> =
    updateSetting { current, now ->
      val updatedRows = settingsDao.updateCurrency(currency, now)
      val next = current.copy(currency = currency, updatedAtEpochMs = now)
      if (updatedRows == 0) settingsDao.upsertSettings(next)
      next
    }

  override suspend fun resetToDefaults(): AppResult<AppSettings> =
    withContext(dispatchers.io) {
      try {
        val defaultEntity = AppSettingsEntity(updatedAtEpochMs = System.currentTimeMillis())
        settingsDao.upsertSettings(defaultEntity)
        AppLogger.i(TAG, "Preferences reset to defaults")
        AppResult.Success(defaultEntity.toDomain())
      } catch (e: CancellationException) {
        throw e
      } catch (e: Exception) {
        AppLogger.e(TAG, "Failed to reset preferences", e)
        AppResult.Error(
          AppError.DatabaseError(
            message = e.message ?: "Unable to reset local preferences",
            cause = e,
          )
        )
      }
    }

  override suspend fun getDatabaseHealth(): AppResult<DatabaseHealthInfo> =
    withContext(dispatchers.io) {
      try {
        val current = settingsDao.getSettingsOnce() ?: seedDefaultSettingsInternal()
        val count = settingsDao.getSettingsCount()
        val info = DatabaseHealthInfo(
          databaseName = DatabaseMigrations.DATABASE_NAME,
          schemaVersion = DatabaseMigrations.CURRENT_SCHEMA_VERSION,
          isInitialized = count > 0,
          settingsRecordCount = count,
          lastUpdatedEpochMs = current.updatedAtEpochMs,
          statusLabel = if (count > 0) "Healthy (Verified)" else "Uninitialized",
        )
        AppResult.Success(info)
      } catch (e: CancellationException) {
        throw e
      } catch (e: Exception) {
        AppLogger.e(TAG, "Database health check failed", e)
        AppResult.Error(
          AppError.DatabaseError(
            message = e.message ?: "Database verification failed",
            cause = e,
          )
        )
      }
    }

  override suspend fun getRemoteSyncStatus(): RemoteSyncStatus =
    try {
      remoteDataSource.getSyncStatus()
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      AppLogger.w(TAG, "Remote status check failed gracefully in offline mode", e)
      RemoteSyncStatus.OfflineStandby
    }

  private suspend fun updateSetting(
    block: suspend (current: AppSettingsEntity, now: Long) -> AppSettingsEntity
  ): AppResult<AppSettings> =
    withContext(dispatchers.io) {
      try {
        val current = settingsDao.getSettingsOnce() ?: seedDefaultSettingsInternal()
        val now = System.currentTimeMillis()
        val updated = block(current, now)
        AppLogger.d(TAG, "Updated local preference: theme=${updated.themeMode}, lang=${updated.language}, currency=${updated.currency}")
        AppResult.Success(updated.toDomain())
      } catch (e: CancellationException) {
        throw e
      } catch (e: Exception) {
        AppLogger.e(TAG, "Failed to update local preference", e)
        AppResult.Error(
          AppError.DatabaseError(
            message = e.message ?: "Failed to save preference locally",
            cause = e,
          )
        )
      }
    }

  private suspend fun seedDefaultSettingsInternal(): AppSettingsEntity {
    val defaultEntity = AppSettingsEntity(updatedAtEpochMs = System.currentTimeMillis())
    settingsDao.upsertSettings(defaultEntity)
    return defaultEntity
  }

  private companion object {
    const val TAG = "SettingsRepository"
  }
}
