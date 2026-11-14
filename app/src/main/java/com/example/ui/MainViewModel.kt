package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.result.AppResult
import com.example.domain.model.AppSettings
import com.example.domain.usecase.ObserveAppSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class MainViewModel @Inject constructor(
  observeAppSettingsUseCase: ObserveAppSettingsUseCase,
) : ViewModel() {

  val appSettings: StateFlow<AppSettings> =
    observeAppSettingsUseCase()
      .map { result ->
        when (result) {
          is AppResult.Success -> result.data
          else -> AppSettings()
        }
      }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AppSettings(),
      )
}
