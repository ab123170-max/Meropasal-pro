package com.example

import com.example.core.currency.CurrencyFormatter
import com.example.data.local.db.DatabaseConverters
import com.example.domain.model.AppLanguage
import com.example.domain.model.CurrencyPreference
import com.example.domain.model.SuiteRoadmap
import com.example.domain.model.ThemeMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class ExampleUnitTest {

  private val converters = DatabaseConverters()

  @Test
  fun currencyFormatter_returnsPlaceholderWhenNoLedgerValueExists() {
    assertEquals("—", CurrencyFormatter.formatOrPlaceholder(null, CurrencyPreference.NPR))
    assertEquals("NPR (रू)", CurrencyFormatter.displayBadge(CurrencyPreference.NPR))
    assertEquals("USD ($)", CurrencyFormatter.displayBadge(CurrencyPreference.USD))
  }

  @Test
  fun currencyFormatter_formatsNumericAmountWithSelectedCurrencySymbol() {
    assertEquals("रू 1,250.50", CurrencyFormatter.formatOrPlaceholder(1250.5, CurrencyPreference.NPR))
    assertEquals("$ 42.00", CurrencyFormatter.formatOrPlaceholder(42.0, CurrencyPreference.USD))
  }

  @Test
  fun databaseConverters_roundTripAllEnumsSafely() {
    ThemeMode.entries.forEach { mode ->
      assertEquals(mode, converters.toThemeMode(converters.fromThemeMode(mode)))
    }
    AppLanguage.entries.forEach { lang ->
      assertEquals(lang, converters.toAppLanguage(converters.fromAppLanguage(lang)))
    }
    CurrencyPreference.entries.forEach { currency ->
      assertEquals(currency, converters.toCurrencyPreference(converters.fromCurrencyPreference(currency)))
    }
  }

  @Test
  fun databaseConverters_fallbackGracefullyOnUnknownKeys() {
    assertEquals(ThemeMode.SYSTEM, converters.toThemeMode("invalid"))
    assertEquals(AppLanguage.ENGLISH, converters.toAppLanguage(null))
    assertEquals(CurrencyPreference.NPR, converters.toCurrencyPreference("XYZ"))
  }

  @Test
  fun roadmapModules_areAllDisabledInPart01() {
    assertFalse(SuiteRoadmap.plannedModules.isEmpty())
    SuiteRoadmap.plannedModules.forEach { module ->
      assertFalse("Module ${module.id} must be disabled in Part 01", module.isAvailable)
    }
  }
}
