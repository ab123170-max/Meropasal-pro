package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.domain.model.ThemeMode

private val LightColorScheme = lightColorScheme(
  primary = DeepNavy,
  onPrimary = Color.White,
  primaryContainer = Color(0xFFD6E4F5),
  onPrimaryContainer = DeepNavyDark,
  secondary = EmeraldGreen,
  onSecondary = Color.White,
  secondaryContainer = EmeraldGreenContainerLight,
  onSecondaryContainer = Color(0xFF063822),
  tertiary = SlateAmber,
  onTertiary = Color.White,
  tertiaryContainer = SlateAmberContainerLight,
  onTertiaryContainer = Color(0xFF451A03),
  background = LightNeutralBg,
  onBackground = LightTextPrimary,
  surface = LightNeutralSurface,
  onSurface = LightTextPrimary,
  surfaceVariant = LightNeutralSurfaceVariant,
  onSurfaceVariant = LightTextSecondary,
  surfaceContainerLowest = Color.White,
  surfaceContainerLow = Color(0xFFF9FBFD),
  surfaceContainer = LightNeutralSurfaceVariant,
  surfaceContainerHigh = Color(0xFFE4EAEF),
  outline = Color(0xFF94A3B8),
  outlineVariant = LightNeutralOutline,
  error = ErrorRedLight,
  onError = Color.White,
  errorContainer = ErrorContainerLight,
  onErrorContainer = Color(0xFF410002),
)

private val DarkColorScheme = darkColorScheme(
  primary = Color(0xFF93C5FD),
  onPrimary = DeepNavyDark,
  primaryContainer = DeepNavyLight,
  onPrimaryContainer = Color(0xFFDCEAFE),
  secondary = EmeraldGreenLight,
  onSecondary = Color(0xFF032D1B),
  secondaryContainer = EmeraldGreenContainerDark,
  onSecondaryContainer = Color(0xFFA7F3D0),
  tertiary = Color(0xFFFBBF24),
  onTertiary = Color(0xFF451A03),
  tertiaryContainer = SlateAmberContainerDark,
  onTertiaryContainer = Color(0xFFFDE68A),
  background = DarkNeutralBg,
  onBackground = DarkTextPrimary,
  surface = DarkNeutralSurface,
  onSurface = DarkTextPrimary,
  surfaceVariant = DarkNeutralSurfaceVariant,
  onSurfaceVariant = DarkTextSecondary,
  surfaceContainerLowest = Color(0xFF07101C),
  surfaceContainerLow = Color(0xFF0E1B2D),
  surfaceContainer = DarkNeutralSurface,
  surfaceContainerHigh = DarkNeutralSurfaceVariant,
  outline = Color(0xFF476082),
  outlineVariant = DarkNeutralOutline,
  error = ErrorRedDark,
  onError = Color(0xFF690005),
  errorContainer = ErrorContainerDark,
  onErrorContainer = Color(0xFFFFDAD6),
)

@Composable
fun MeethoPasalTheme(
  themeMode: ThemeMode = ThemeMode.SYSTEM,
  content: @Composable () -> Unit,
) {
  val useDarkTheme = when (themeMode) {
    ThemeMode.SYSTEM -> isSystemInDarkTheme()
    ThemeMode.LIGHT -> false
    ThemeMode.DARK -> true
  }

  val colorScheme = if (useDarkTheme) DarkColorScheme else LightColorScheme

  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as? Activity)?.window
      if (window != null) {
        val controller = WindowCompat.getInsetsController(window, view)
        controller.isAppearanceLightStatusBars = !useDarkTheme
        controller.isAppearanceLightNavigationBars = !useDarkTheme
      }
    }
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = MeethoPasalTypography,
    shapes = MeethoPasalShapes,
    content = content,
  )
}
