package com.example.data.local.mapper

import com.example.data.local.entity.AppSettingsEntity
import com.example.domain.model.AppSettings

fun AppSettingsEntity.toDomain(): AppSettings =
  AppSettings(
    id = id,
    themeMode = themeMode,
    language = language,
    currency = currency,
    restaurantName = restaurantName,
    branchCode = branchCode,
    updatedAtEpochMs = updatedAtEpochMs,
  )

fun AppSettings.toEntity(): AppSettingsEntity =
  AppSettingsEntity(
    id = id,
    themeMode = themeMode,
    language = language,
    currency = currency,
    restaurantName = restaurantName,
    branchCode = branchCode,
    updatedAtEpochMs = updatedAtEpochMs,
  )
