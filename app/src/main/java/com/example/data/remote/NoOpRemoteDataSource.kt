package com.example.data.remote

import com.example.core.result.AppError
import com.example.core.result.AppResult
import com.example.domain.model.AppSettings
import com.example.domain.model.RemoteSyncStatus
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Offline-first Part 01 implementation of [RemoteDataSource].
 * Performs zero network calls and keeps the app 100% operational without internet access.
 */
@Singleton
class NoOpRemoteDataSource @Inject constructor() : RemoteDataSource {
  override suspend fun getSyncStatus(): RemoteSyncStatus = RemoteSyncStatus.OfflineStandby

  override suspend fun pushSettingsSnapshot(settings: AppSettings): AppResult<Unit> =
    AppResult.Error(AppError.NetworkUnavailableError())

  override suspend fun pullSettingsSnapshot(): AppResult<AppSettings?> =
    AppResult.Success(null)
}
