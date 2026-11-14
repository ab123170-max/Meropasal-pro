package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.locale.ProvideAppLocale
import com.example.ui.MainViewModel
import com.example.ui.navigation.MeethoPasalNavHost
import com.example.ui.theme.MeethoPasalTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

  private val mainViewModel: MainViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val settings by mainViewModel.appSettings.collectAsStateWithLifecycle()
      ProvideAppLocale(language = settings.language) {
        MeethoPasalTheme(themeMode = settings.themeMode) {
          MeethoPasalNavHost()
        }
      }
    }
  }
}
