package com.example.ui.state

import com.example.core.result.AppError

sealed interface UiState<out T> {
  data object Loading : UiState<Nothing>

  data class Empty(val message: String) : UiState<Nothing>

  data class Success<out T>(val data: T) : UiState<T>

  data class Error(val error: AppError) : UiState<Nothing>
}
