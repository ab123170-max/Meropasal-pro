package com.example.domain.repository

import com.example.core.result.AppResult
import com.example.domain.model.AppLanguage
import com.example.domain.model.AppSettings
import com.example.domain.model.CurrencyPreference
import com.example.domain.model.DatabaseHealthInfo
import com.example.domain.model.RemoteSyncStatus
import com.example.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
  fun observeSettings(): Flow<AppResult<AppSettings>>

  suspend fun ensureInitialized(): AppResult<AppSettings>

  suspend fun getSettingsOnce(): AppResult<AppSettings>

  suspend fun updateThemeMode(themeMode: ThemeMode): AppResult<AppSettings>

  suspend fun updateLanguage(language: AppLanguage): AppResult<AppSettings>

  suspend fun updateCurrency(currency: CurrencyPreference): AppResult<AppSettings>

  suspend fun resetToDefaults(): AppResult<AppSettings>

  suspend fun getDatabaseHealth(): AppResult<DatabaseHealthInfo>

  suspend fun getRemoteSyncStatus(): RemoteSyncStatus
}
