package com.example.ui.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.logging.AppLogger
import com.example.core.result.AppResult
import com.example.domain.usecase.EnsureAppInitializedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface SplashUiState {
  data object Initializing : SplashUiState

  data object Ready : SplashUiState

  data class Error(val message: String) : SplashUiState
}

@HiltViewModel
class SplashViewModel @Inject constructor(
  private val ensureAppInitializedUseCase: EnsureAppInitializedUseCase,
) : ViewModel() {

  private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Initializing)
  val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

  init {
    bootstrap()
  }

  fun bootstrap(minDisplayMs: Long = DEFAULT_MIN_DISPLAY_MS) {
    viewModelScope.launch {
      _uiState.value = SplashUiState.Initializing
      val start = System.currentTimeMillis()
      when (val result = ensureAppInitializedUseCase()) {
        is AppResult.Success -> {
          val elapsed = System.currentTimeMillis() - start
          if (elapsed < minDisplayMs) {
            delay(minDisplayMs - elapsed)
          }
          AppLogger.i(TAG, "Splash bootstrap succeeded")
          _uiState.value = SplashUiState.Ready
        }
        is AppResult.Error -> {
          AppLogger.e(TAG, "Splash bootstrap failed: ${result.error.message}")
          _uiState.value = SplashUiState.Error(result.error.message)
        }
        is AppResult.Loading -> Unit
      }
    }
  }

  private companion object {
    const val TAG = "SplashViewModel"
    const val DEFAULT_MIN_DISPLAY_MS = 950L
  }
}
