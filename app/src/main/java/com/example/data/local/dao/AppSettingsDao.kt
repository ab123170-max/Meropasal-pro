package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.AppSettingsEntity
import com.example.domain.model.AppLanguage
import com.example.domain.model.AppSettings
import com.example.domain.model.CurrencyPreference
import com.example.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow

@Dao
interface AppSettingsDao {
  @Query("SELECT * FROM app_settings WHERE id = :id LIMIT 1")
  fun observeSettings(id: Int = AppSettings.SINGLETON_ID): Flow<AppSettingsEntity?>

  @Query("SELECT * FROM app_settings WHERE id = :id LIMIT 1")
  suspend fun getSettingsOnce(id: Int = AppSettings.SINGLETON_ID): AppSettingsEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertSettings(entity: AppSettingsEntity)

  @Query(
    "UPDATE app_settings SET theme_mode = :themeMode, updated_at_epoch_ms = :updatedAt WHERE id = :id"
  )
  suspend fun updateThemeMode(
    themeMode: ThemeMode,
    updatedAt: Long,
    id: Int = AppSettings.SINGLETON_ID,
  ): Int

  @Query(
    "UPDATE app_settings SET language = :language, updated_at_epoch_ms = :updatedAt WHERE id = :id"
  )
  suspend fun updateLanguage(
    language: AppLanguage,
    updatedAt: Long,
    id: Int = AppSettings.SINGLETON_ID,
  ): Int

  @Query(
    "UPDATE app_settings SET currency = :currency, updated_at_epoch_ms = :updatedAt WHERE id = :id"
  )
  suspend fun updateCurrency(
    currency: CurrencyPreference,
    updatedAt: Long,
    id: Int = AppSettings.SINGLETON_ID,
  ): Int

  @Query("SELECT COUNT(*) FROM app_settings")
  suspend fun getSettingsCount(): Int

  @Query("DELETE FROM app_settings")
  suspend fun clearAll()
}
