package com.example.ui.navigation

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.R
import com.example.core.locale.localizedString
import com.example.ui.screens.dashboard.DashboardScreen
import com.example.ui.screens.info.InfoScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.screens.splash.SplashScreen

private data class TopLevelDestination(
  val route: AppRoute,
  val labelResId: Int,
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector,
  val testTag: String,
)

private val topLevelDestinations = listOf(
  TopLevelDestination(
    route = AppRoute.Dashboard,
    labelResId = R.string.nav_dashboard,
    selectedIcon = Icons.Filled.Dashboard,
    unselectedIcon = Icons.Outlined.Dashboard,
    testTag = "nav_dashboard",
  ),
  TopLevelDestination(
    route = AppRoute.Settings,
    labelResId = R.string.nav_settings,
    selectedIcon = Icons.Filled.Settings,
    unselectedIcon = Icons.Outlined.Settings,
    testTag = "nav_settings",
  ),
  TopLevelDestination(
    route = AppRoute.Info,
    labelResId = R.string.nav_info,
    selectedIcon = Icons.Filled.Info,
    unselectedIcon = Icons.Outlined.Info,
    testTag = "nav_info",
  ),
)

@Composable
fun MeethoPasalNavHost(
  navController: NavHostController = rememberNavController(),
  startDestination: AppRoute = AppRoute.Splash,
) {
  val backStackEntry by navController.currentBackStackEntryAsState()
  val currentDestination = backStackEntry?.destination
  val showNavigationChrome =
    currentDestination != null && !currentDestination.hasRoute<AppRoute.Splash>()

  fun navigateToTopLevel(route: AppRoute) {
    navController.navigate(route) {
      popUpTo(AppRoute.Dashboard) {
        saveState = true
      }
      launchSingleTop = true
      restoreState = true
    }
  }

  BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
    val useNavigationRail = maxWidth >= 600.dp

    Scaffold(
      contentWindowInsets = WindowInsets.safeDrawing,
      containerColor = MaterialTheme.colorScheme.background,
      bottomBar = {
        if (showNavigationChrome && !useNavigationRail) {
          NavigationBar(
            windowInsets = WindowInsets.navigationBars,
            containerColor = MaterialTheme.colorScheme.surface,
          ) {
            topLevelDestinations.forEach { dest ->
              val selected = currentDestination?.hasRoute(dest.route::class) == true
              val label = localizedString(dest.labelResId)
              NavigationBarItem(
                selected = selected,
                onClick = { navigateToTopLevel(dest.route) },
                icon = {
                  Icon(
                    imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                    contentDescription = label,
                  )
                },
                label = { Text(label) },
                modifier = Modifier.testTag(dest.testTag),
              )
            }
          }
        }
      },
    ) { innerPadding ->
      Row(modifier = Modifier.fillMaxSize()) {
        if (showNavigationChrome && useNavigationRail) {
          NavigationRail(
            containerColor = MaterialTheme.colorScheme.surface,
          ) {
            topLevelDestinations.forEach { dest ->
              val selected = currentDestination?.hasRoute(dest.route::class) == true
              val label = localizedString(dest.labelResId)
              NavigationRailItem(
                selected = selected,
                onClick = { navigateToTopLevel(dest.route) },
                icon = {
                  Icon(
                    imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                    contentDescription = label,
                  )
                },
                label = { Text(label) },
                modifier = Modifier.testTag(dest.testTag),
              )
            }
          }
        }

        NavHost(
          navController = navController,
          startDestination = startDestination,
          modifier = Modifier.weight(1f),
        ) {
          composable<AppRoute.Splash> {
            SplashScreen(
              onReadyToNavigate = {
                navController.navigate(AppRoute.Dashboard) {
                  popUpTo(navController.graph.findStartDestination().id) {
                    inclusive = true
                  }
                  launchSingleTop = true
                }
              },
            )
          }

          composable<AppRoute.Dashboard> {
            DashboardScreen(contentPadding = innerPadding)
          }

          composable<AppRoute.Settings> {
            SettingsScreen(contentPadding = innerPadding)
          }

          composable<AppRoute.Info> {
            InfoScreen(contentPadding = innerPadding)
          }
        }
      }
    }
  }
}
