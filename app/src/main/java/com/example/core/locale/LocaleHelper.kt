package com.example.core.locale

import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import com.example.domain.model.AppLanguage
import java.util.Locale

/**
 * Provides a locale-configured [Context] for string resource resolution so language changes
 * apply immediately across Compose screens without recreating the Activity.
 */
val LocalLocalizedContext = staticCompositionLocalOf<Context> {
  error("LocalLocalizedContext not provided")
}

object LocaleHelper {
  fun createLocalizedContext(baseContext: Context, language: AppLanguage): Context {
    val locale = Locale.forLanguageTag(language.bcp47Tag)
    val config = Configuration(baseContext.resources.configuration)
    config.setLocale(locale)
    return baseContext.createConfigurationContext(config)
  }
}

@Composable
fun ProvideAppLocale(
  language: AppLanguage,
  content: @Composable () -> Unit,
) {
  val baseContext = LocalContext.current
  val localizedContext = remember(baseContext, language) {
    LocaleHelper.createLocalizedContext(baseContext, language)
  }
  CompositionLocalProvider(LocalLocalizedContext provides localizedContext) {
    content()
  }
}

@Composable
fun localizedString(resId: Int): String {
  val context = LocalLocalizedContext.current
  return context.getString(resId)
}

@Composable
fun localizedString(resId: Int, vararg formatArgs: Any): String {
  val context = LocalLocalizedContext.current
  return context.getString(resId, *formatArgs)
}
