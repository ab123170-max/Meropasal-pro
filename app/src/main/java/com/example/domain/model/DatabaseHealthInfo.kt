package com.example.domain.model

data class DatabaseHealthInfo(
  val databaseName: String,
  val schemaVersion: Int,
  val isInitialized: Boolean,
  val settingsRecordCount: Int,
  val lastUpdatedEpochMs: Long,
  val statusLabel: String,
)
