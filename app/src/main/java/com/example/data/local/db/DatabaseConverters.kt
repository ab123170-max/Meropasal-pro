package com.example.data.local.db

import androidx.room.TypeConverter
import com.example.domain.model.AppLanguage
import com.example.domain.model.CurrencyPreference
import com.example.domain.model.ThemeMode

class DatabaseConverters {
  @TypeConverter
  fun fromThemeMode(mode: ThemeMode): String = mode.storageKey

  @TypeConverter
  fun toThemeMode(value: String?): ThemeMode = ThemeMode.fromKey(value)

  @TypeConverter
  fun fromAppLanguage(language: AppLanguage): String = language.storageKey

  @TypeConverter
  fun toAppLanguage(value: String?): AppLanguage = AppLanguage.fromKey(value)

  @TypeConverter
  fun fromCurrencyPreference(currency: CurrencyPreference): String = currency.code

  @TypeConverter
  fun toCurrencyPreference(value: String?): CurrencyPreference = CurrencyPreference.fromCode(value)
}
