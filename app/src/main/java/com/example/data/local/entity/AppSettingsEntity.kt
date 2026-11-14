package com.example.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.AppLanguage
import com.example.domain.model.AppSettings
import com.example.domain.model.CurrencyPreference
import com.example.domain.model.ThemeMode

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
  @PrimaryKey
  @ColumnInfo(name = "id")
  val id: Int = AppSettings.SINGLETON_ID,
  @ColumnInfo(name = "theme_mode")
  val themeMode: ThemeMode = ThemeMode.SYSTEM,
  @ColumnInfo(name = "language")
  val language: AppLanguage = AppLanguage.ENGLISH,
  @ColumnInfo(name = "currency")
  val currency: CurrencyPreference = CurrencyPreference.NPR,
  @ColumnInfo(name = "restaurant_name")
  val restaurantName: String = AppSettings.DEFAULT_RESTAURANT_NAME,
  @ColumnInfo(name = "branch_code")
  val branchCode: String = AppSettings.DEFAULT_BRANCH_CODE,
  @ColumnInfo(name = "updated_at_epoch_ms")
  val updatedAtEpochMs: Long = 0L,
)
