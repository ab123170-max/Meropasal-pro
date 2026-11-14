package com.example

import android.os.Looper
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import java.util.concurrent.TimeUnit
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w411dp-h915dp-xxhdpi", application = HiltTestApplication::class)
class MeethoPasalCujRobolectricTest {

  @get:Rule(order = 0)
  val hiltRule = HiltAndroidRule(this)

  @get:Rule(order = 1)
  val composeTestRule = createAndroidComposeRule<MainActivity>()

  @Test
  fun splashTransitionsToDashboard_navigationAndSettingsPersistenceWorkOffline() {
    hiltRule.inject()

    // Confirm Splash screen renders on startup.
    composeTestRule.onNodeWithTag("splash_screen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("splash_brand_title").assertIsDisplayed()

    // Advance Robolectric main looper & Compose clock so Room IO + splash transition complete.
    composeTestRule.waitUntil(timeoutMillis = 8_000L) {
      Shadows.shadowOf(Looper.getMainLooper()).idleFor(150, TimeUnit.MILLISECONDS)
      composeTestRule.mainClock.advanceTimeBy(150)
      composeTestRule.onAllNodesWithTag("dashboard_welcome_card").fetchSemanticsNodes().isNotEmpty()
    }
    composeTestRule.waitForIdle()

    // Verify Dashboard shell & zero fake financial figures.
    composeTestRule.onNodeWithTag("dashboard_welcome_card").assertIsDisplayed()
    composeTestRule.onNodeWithTag("dashboard_business_overview_card").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("dashboard_sales_summary_card").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("sales_empty_figure").assertIsDisplayed()
    composeTestRule.onNodeWithTag("module_placeholder_pos_billing").performScrollTo().assertIsDisplayed()

    // Navigate to Settings.
    composeTestRule.onNodeWithTag("nav_settings").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("settings_screen").assertIsDisplayed()

    // Change Theme to Dark, Language to Nepali, Currency to USD.
    composeTestRule.onNodeWithTag("theme_option_dark").performScrollTo().performClick()
    composeTestRule.waitUntil(timeoutMillis = 3_000L) {
      Shadows.shadowOf(Looper.getMainLooper()).idleFor(50, TimeUnit.MILLISECONDS)
      composeTestRule.onAllNodesWithTag("settings_saved_banner").fetchSemanticsNodes().isNotEmpty()
    }

    composeTestRule.onNodeWithTag("language_option_ne").performScrollTo().performClick()
    composeTestRule.waitUntil(timeoutMillis = 3_000L) {
      Shadows.shadowOf(Looper.getMainLooper()).idleFor(50, TimeUnit.MILLISECONDS)
      composeTestRule.onAllNodesWithText("प्रणाली सेटिङ्स").fetchSemanticsNodes().isNotEmpty()
    }
    composeTestRule.onNodeWithText("प्रणाली सेटिङ्स").assertIsDisplayed()

    composeTestRule.onNodeWithTag("currency_option_usd").performScrollTo().performClick()
    composeTestRule.waitForIdle()

    // Navigate to Info screen.
    composeTestRule.onNodeWithTag("nav_info").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("info_screen").assertIsDisplayed()
    composeTestRule.onNodeWithText("com.meethopasal.pro").performScrollTo().assertIsDisplayed()

    // Navigate back to Dashboard and verify selected currency USD ($) is reflected.
    composeTestRule.onNodeWithTag("nav_dashboard").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("dashboard_sales_summary_card").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithText("USD ($)").assertIsDisplayed()
  }
}
