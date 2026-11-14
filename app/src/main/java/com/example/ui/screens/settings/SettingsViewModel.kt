package com.example.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.result.AppResult
import com.example.domain.model.AppLanguage
import com.example.domain.model.AppSettings
import com.example.domain.model.CurrencyPreference
import com.example.domain.model.DatabaseHealthInfo
import com.example.domain.model.ThemeMode
import com.example.domain.usecase.GetDatabaseHealthUseCase
import com.example.domain.usecase.ObserveAppSettingsUseCase
import com.example.domain.usecase.ResetPreferencesUseCase
import com.example.domain.usecase.UpdateCurrencyPreferenceUseCase
import com.example.domain.usecase.UpdateLanguagePreferenceUseCase
import com.example.domain.usecase.UpdateThemePreferenceUseCase
import com.example.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsContentModel(
  val settings: AppSettings,
  val databaseHealth: DatabaseHealthInfo?,
  val statusBannerMessage: String?,
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
  observeAppSettingsUseCase: ObserveAppSettingsUseCase,
  private val updateThemePreferenceUseCase: UpdateThemePreferenceUseCase,
  private val updateLanguagePreferenceUseCase: UpdateLanguagePreferenceUseCase,
  private val updateCurrencyPreferenceUseCase: UpdateCurrencyPreferenceUseCase,
  private val resetPreferencesUseCase: ResetPreferencesUseCase,
  private val getDatabaseHealthUseCase: GetDatabaseHealthUseCase,
) : ViewModel() {

  private val dbHealthFlow = MutableStateFlow<DatabaseHealthInfo?>(null)
  private val bannerFlow = MutableStateFlow<String?>(null)

  val uiState: StateFlow<UiState<SettingsContentModel>> =
    combine(observeAppSettingsUseCase(), dbHealthFlow, bannerFlow) { result, health, banner ->
      when (result) {
        is AppResult.Loading -> UiState.Loading
        is AppResult.Error -> UiState.Error(result.error)
        is AppResult.Success ->
          UiState.Success(
            SettingsContentModel(
              settings = result.data,
              databaseHealth = health,
              statusBannerMessage = banner,
            )
          )
      }
    }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Loading,
      )

  init {
    verifyDatabase()
  }

  fun onThemeSelected(themeMode: ThemeMode) {
    viewModelScope.launch {
      when (val res = updateThemePreferenceUseCase(themeMode)) {
        is AppResult.Success -> {
          bannerFlow.value = "saved"
          verifyDatabase()
        }
        is AppResult.Error -> bannerFlow.value = res.error.message
        is AppResult.Loading -> Unit
      }
    }
  }

  fun onLanguageSelected(language: AppLanguage) {
    viewModelScope.launch {
      when (val res = updateLanguagePreferenceUseCase(language)) {
        is AppResult.Success -> {
          bannerFlow.value = "saved"
          verifyDatabase()
        }
        is AppResult.Error -> bannerFlow.value = res.error.message
        is AppResult.Loading -> Unit
      }
    }
  }

  fun onCurrencySelected(currency: CurrencyPreference) {
    viewModelScope.launch {
      when (val res = updateCurrencyPreferenceUseCase(currency)) {
        is AppResult.Success -> {
          bannerFlow.value = "saved"
          verifyDatabase()
        }
        is AppResult.Error -> bannerFlow.value = res.error.message
        is AppResult.Loading -> Unit
      }
    }
  }

  fun onResetConfirmed() {
    viewModelScope.launch {
      when (val res = resetPreferencesUseCase()) {
        is AppResult.Success -> {
          bannerFlow.value = "saved"
          verifyDatabase()
        }
        is AppResult.Error -> bannerFlow.value = res.error.message
        is AppResult.Loading -> Unit
      }
    }
  }

  fun verifyDatabase() {
    viewModelScope.launch {
      when (val healthResult = getDatabaseHealthUseCase()) {
        is AppResult.Success -> dbHealthFlow.value = healthResult.data
        is AppResult.Error -> bannerFlow.value = healthResult.error.message
        is AppResult.Loading -> Unit
      }
    }
  }

  fun dismissBanner() {
    bannerFlow.value = null
  }
}
