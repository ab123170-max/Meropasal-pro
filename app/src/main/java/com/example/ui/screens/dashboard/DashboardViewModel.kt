package com.example.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.result.AppResult
import com.example.domain.model.AppSettings
import com.example.domain.model.ModuleDescriptor
import com.example.domain.model.RemoteSyncStatus
import com.example.domain.model.SuiteRoadmap
import com.example.domain.usecase.EnsureAppInitializedUseCase
import com.example.domain.usecase.GetRemoteSyncStatusUseCase
import com.example.domain.usecase.ObserveAppSettingsUseCase
import com.example.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DashboardContentModel(
  val settings: AppSettings,
  val remoteSyncStatus: RemoteSyncStatus,
  val plannedModules: List<ModuleDescriptor>,
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
  observeAppSettingsUseCase: ObserveAppSettingsUseCase,
  private val ensureAppInitializedUseCase: EnsureAppInitializedUseCase,
  private val getRemoteSyncStatusUseCase: GetRemoteSyncStatusUseCase,
) : ViewModel() {

  private val remoteSyncState = MutableStateFlow<RemoteSyncStatus>(RemoteSyncStatus.OfflineStandby)

  val uiState: StateFlow<UiState<DashboardContentModel>> =
    combine(observeAppSettingsUseCase(), remoteSyncState) { settingsResult, syncStatus ->
      when (settingsResult) {
        is AppResult.Loading -> UiState.Loading
        is AppResult.Error -> UiState.Error(settingsResult.error)
        is AppResult.Success ->
          UiState.Success(
            DashboardContentModel(
              settings = settingsResult.data,
              remoteSyncStatus = syncStatus,
              plannedModules = SuiteRoadmap.plannedModules,
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
    refreshStatus()
  }

  fun refreshStatus() {
    viewModelScope.launch {
      ensureAppInitializedUseCase()
      remoteSyncState.value = getRemoteSyncStatusUseCase()
    }
  }
}
