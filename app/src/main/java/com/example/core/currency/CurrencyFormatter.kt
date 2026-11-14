package com.example.core.currency

import com.example.domain.model.CurrencyPreference
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

/**
 * Formats verified monetary amounts for the selected [CurrencyPreference].
 * Returns an explicit placeholder when no ledger amount exists yet.
 */
object CurrencyFormatter {
  fun formatOrPlaceholder(
    amountMinorOrNull: Double?,
    currency: CurrencyPreference,
    placeholder: String = "—",
  ): String {
    if (amountMinorOrNull == null) return placeholder
    val symbols = DecimalFormatSymbols(Locale.US)
    val formatter = DecimalFormat("#,##0.00", symbols)
    return "${currency.symbol} ${formatter.format(amountMinorOrNull)}"
  }

  fun displayBadge(currency: CurrencyPreference): String =
    "${currency.code} (${currency.symbol})"
}
