package com.example.domain.model

enum class ThemeMode(val storageKey: String) {
  SYSTEM("system"),
  LIGHT("light"),
  DARK("dark");

  companion object {
    fun fromKey(key: String?): ThemeMode =
      entries.firstOrNull { it.storageKey.equals(key, ignoreCase = true) } ?: SYSTEM
  }
}

enum class AppLanguage(
  val storageKey: String,
  val bcp47Tag: String,
  val nativeLabel: String,
  val englishLabel: String,
) {
  ENGLISH("en", "en", "English", "English"),
  NEPALI("ne", "ne", "नेपाली", "Nepali"),
  HINDI("hi", "hi", "हिन्दी", "Hindi");

  companion object {
    fun fromKey(key: String?): AppLanguage =
      entries.firstOrNull { it.storageKey.equals(key, ignoreCase = true) } ?: ENGLISH
  }
}

enum class CurrencyPreference(
  val code: String,
  val symbol: String,
  val displayName: String,
) {
  NPR("NPR", "रू", "Nepalese Rupee"),
  USD("USD", "$", "US Dollar"),
  INR("INR", "₹", "Indian Rupee"),
  EUR("EUR", "€", "Euro");

  companion object {
    fun fromCode(code: String?): CurrencyPreference =
      entries.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: NPR
  }
}

data class AppSettings(
  val id: Int = SINGLETON_ID,
  val themeMode: ThemeMode = ThemeMode.SYSTEM,
  val language: AppLanguage = AppLanguage.ENGLISH,
  val currency: CurrencyPreference = CurrencyPreference.NPR,
  val restaurantName: String = DEFAULT_RESTAURANT_NAME,
  val branchCode: String = DEFAULT_BRANCH_CODE,
  val updatedAtEpochMs: Long = 0L,
) {
  companion object {
    const val SINGLETON_ID = 1
    const val DEFAULT_RESTAURANT_NAME = "MeethoPasal Flagship"
    const val DEFAULT_BRANCH_CODE = "MAIN-01"
  }
}
