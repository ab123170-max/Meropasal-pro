package com.example.core.result

/**
 * Domain-level error hierarchy for structured error propagation across layers.
 */
sealed interface AppError {
  val message: String
  val cause: Throwable?

  data class DatabaseError(
    override val message: String,
    override val cause: Throwable? = null,
  ) : AppError

  data class ValidationError(
    override val message: String,
    override val cause: Throwable? = null,
  ) : AppError

  data class NetworkUnavailableError(
    override val message: String = "Offline mode active. Remote backend is not required.",
    override val cause: Throwable? = null,
  ) : AppError

  data class UnknownError(
    override val message: String,
    override val cause: Throwable? = null,
  ) : AppError
}

/**
 * Typed outcome wrapper for coroutine and Flow operations.
 */
sealed interface AppResult<out T> {
  data object Loading : AppResult<Nothing>

  data class Success<out T>(val data: T) : AppResult<T>

  data class Error(val error: AppError) : AppResult<Nothing>
}

inline fun <T, R> AppResult<T>.map(transform: (T) -> R): AppResult<R> =
  when (this) {
    is AppResult.Loading -> AppResult.Loading
    is AppResult.Success -> AppResult.Success(transform(data))
    is AppResult.Error -> this
  }
