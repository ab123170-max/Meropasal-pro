package com.example.ui.navigation

import kotlinx.serialization.Serializable

sealed interface AppRoute {
  @Serializable
  data object Splash : AppRoute

  @Serializable
  data object Dashboard : AppRoute

  @Serializable
  data object Settings : AppRoute

  @Serializable
  data object Info : AppRoute
}
