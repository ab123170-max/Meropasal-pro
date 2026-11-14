package com.example.domain.model

sealed interface RemoteSyncStatus {
  data object OfflineStandby : RemoteSyncStatus

  data object NotConfigured : RemoteSyncStatus

  data class Connected(val endpointLabel: String) : RemoteSyncStatus

  data class SyncFailed(val reason: String) : RemoteSyncStatus
}
