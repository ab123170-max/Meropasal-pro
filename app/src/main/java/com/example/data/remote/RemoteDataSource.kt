package com.example.data.remote

import com.example.core.result.AppResult
import com.example.domain.model.AppSettings
import com.example.domain.model.RemoteSyncStatus

/**
 * Decoupled remote backend contract for future Supabase integration.
 *
 * Security & offline rules:
 * - Application startup must NEVER block on this interface.
 * - Service-role keys or private API secrets must NEVER be embedded in the Android client.
 * - Future cloud sync will authenticate via user session tokens and Row-Level Security (RLS).
 */
interface RemoteDataSource {
  suspend fun getSyncStatus(): RemoteSyncStatus

  suspend fun pushSettingsSnapshot(settings: AppSettings): AppResult<Unit>

  suspend fun pullSettingsSnapshot(): AppResult<AppSettings?>
}
