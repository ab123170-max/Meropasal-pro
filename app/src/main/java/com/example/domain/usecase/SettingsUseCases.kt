package com.example.domain.usecase

import com.example.core.result.AppResult
import com.example.domain.model.AppLanguage
import com.example.domain.model.AppSettings
import com.example.domain.model.CurrencyPreference
import com.example.domain.model.DatabaseHealthInfo
import com.example.domain.model.RemoteSyncStatus
import com.example.domain.model.ThemeMode
import com.example.domain.repository.SettingsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveAppSettingsUseCase @Inject constructor(
  private val repository: SettingsRepository,
) {
  operator fun invoke(): Flow<AppResult<AppSettings>> = repository.observeSettings()
}

class EnsureAppInitializedUseCase @Inject constructor(
  private val repository: SettingsRepository,
) {
  suspend operator fun invoke(): AppResult<AppSettings> = repository.ensureInitialized()
}

class UpdateThemePreferenceUseCase @Inject constructor(
  private val repository: SettingsRepository,
) {
  suspend operator fun invoke(themeMode: ThemeMode): AppResult<AppSettings> =
    repository.updateThemeMode(themeMode)
}

class UpdateLanguagePreferenceUseCase @Inject constructor(
  private val repository: SettingsRepository,
) {
  suspend operator fun invoke(language: AppLanguage): AppResult<AppSettings> =
    repository.updateLanguage(language)
}

class UpdateCurrencyPreferenceUseCase @Inject constructor(
  private val repository: SettingsRepository,
) {
  suspend operator fun invoke(currency: CurrencyPreference): AppResult<AppSettings> =
    repository.updateCurrency(currency)
}

class ResetPreferencesUseCase @Inject constructor(
  private val repository: SettingsRepository,
) {
  suspend operator fun invoke(): AppResult<AppSettings> = repository.resetToDefaults()
}

class GetDatabaseHealthUseCase @Inject constructor(
  private val repository: SettingsRepository,
) {
  suspend operator fun invoke(): AppResult<DatabaseHealthInfo> = repository.getDatabaseHealth()
}

class GetRemoteSyncStatusUseCase @Inject constructor(
  private val repository: SettingsRepository,
) {
  suspend operator fun invoke(): RemoteSyncStatus = repository.getRemoteSyncStatus()
}
