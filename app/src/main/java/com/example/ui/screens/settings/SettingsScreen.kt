package com.example.ui.screens.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.BuildConfig
import com.example.R
import com.example.core.locale.localizedString
import com.example.domain.model.AppLanguage
import com.example.domain.model.CurrencyPreference
import com.example.domain.model.ThemeMode
import com.example.ui.components.ErrorStateCard
import com.example.ui.components.LoadingStateCard
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusPill
import com.example.ui.state.UiState
import com.example.ui.theme.MonospaceMetaTextStyle
import kotlinx.coroutines.delay

@Composable
fun SettingsScreen(
  contentPadding: PaddingValues = PaddingValues(0.dp),
  viewModel: SettingsViewModel = hiltViewModel(),
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  SettingsContent(
    uiState = uiState,
    contentPadding = contentPadding,
    onThemeSelected = viewModel::onThemeSelected,
    onLanguageSelected = viewModel::onLanguageSelected,
    onCurrencySelected = viewModel::onCurrencySelected,
    onVerifyDatabase = viewModel::verifyDatabase,
    onResetConfirmed = viewModel::onResetConfirmed,
    onDismissBanner = viewModel::dismissBanner,
  )
}

@Composable
fun SettingsContent(
  uiState: UiState<SettingsContentModel>,
  contentPadding: PaddingValues = PaddingValues(0.dp),
  onThemeSelected: (ThemeMode) -> Unit,
  onLanguageSelected: (AppLanguage) -> Unit,
  onCurrencySelected: (CurrencyPreference) -> Unit,
  onVerifyDatabase: () -> Unit,
  onResetConfirmed: () -> Unit,
  onDismissBanner: () -> Unit,
) {
  var showResetDialog by remember { mutableStateOf(false) }

  if (showResetDialog) {
    AlertDialog(
      onDismissRequest = { showResetDialog = false },
      title = { Text(localizedString(R.string.settings_reset_confirm_title)) },
      text = { Text(localizedString(R.string.settings_reset_confirm_message)) },
      confirmButton = {
        Button(
          onClick = {
            showResetDialog = false
            onResetConfirmed()
          },
          modifier = Modifier.testTag("settings_confirm_reset_button"),
        ) {
          Text(localizedString(R.string.settings_action_confirm))
        }
      },
      dismissButton = {
        TextButton(
          onClick = { showResetDialog = false },
          modifier = Modifier.testTag("settings_cancel_reset_button"),
        ) {
          Text(localizedString(R.string.settings_action_cancel))
        }
      },
    )
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .testTag("settings_screen"),
    contentAlignment = Alignment.TopCenter,
  ) {
    LazyColumn(
      modifier = Modifier
        .widthIn(max = 840.dp)
        .fillMaxSize(),
      contentPadding = PaddingValues(
        start = 16.dp,
        end = 16.dp,
        top = contentPadding.calculateTopPadding() + 16.dp,
        bottom = contentPadding.calculateBottomPadding() + 24.dp,
      ),
      verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
      item {
        SectionHeader(
          title = localizedString(R.string.settings_title),
          subtitle = localizedString(R.string.settings_subtitle),
        )
      }

      when (uiState) {
        is UiState.Loading -> {
          item { LoadingStateCard() }
        }
        is UiState.Error -> {
          item {
            ErrorStateCard(
              message = uiState.error.message,
              onRetry = onVerifyDatabase,
            )
          }
        }
        is UiState.Empty -> {
          item { LoadingStateCard(message = uiState.message) }
        }
        is UiState.Success -> {
          val model = uiState.data
          val settings = model.settings

          if (model.statusBannerMessage != null) {
            item {
              LaunchedEffect(model.statusBannerMessage) {
                delay(2_200)
                onDismissBanner()
              }
              AnimatedVisibility(visible = true) {
                Surface(
                  modifier = Modifier
                    .fillMaxWidth()
                    .testTag("settings_saved_banner"),
                  shape = MaterialTheme.shapes.medium,
                  color = MaterialTheme.colorScheme.secondaryContainer,
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                  ) {
                    Icon(
                      imageVector = Icons.Rounded.CheckCircle,
                      contentDescription = null,
                      tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                    Text(
                      text = if (model.statusBannerMessage == "saved") {
                        localizedString(R.string.settings_saved_banner)
                      } else {
                        model.statusBannerMessage
                      },
                      style = MaterialTheme.typography.labelLarge,
                      color = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                  }
                }
              }
            }
          }

          // Theme Selection Card
          item {
            SettingsSectionCard(
              title = localizedString(R.string.settings_section_appearance),
              icon = Icons.Outlined.DarkMode,
            ) {
              ThemeOptionRow(
                label = localizedString(R.string.settings_theme_system),
                icon = Icons.Outlined.PhoneAndroid,
                selected = settings.themeMode == ThemeMode.SYSTEM,
                testTag = "theme_option_system",
                onClick = { onThemeSelected(ThemeMode.SYSTEM) },
              )
              HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
              ThemeOptionRow(
                label = localizedString(R.string.settings_theme_light),
                icon = Icons.Outlined.LightMode,
                selected = settings.themeMode == ThemeMode.LIGHT,
                testTag = "theme_option_light",
                onClick = { onThemeSelected(ThemeMode.LIGHT) },
              )
              HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
              ThemeOptionRow(
                label = localizedString(R.string.settings_theme_dark),
                icon = Icons.Outlined.DarkMode,
                selected = settings.themeMode == ThemeMode.DARK,
                testTag = "theme_option_dark",
                onClick = { onThemeSelected(ThemeMode.DARK) },
              )
            }
          }

          // Language Selection Card
          item {
            SettingsSectionCard(
              title = localizedString(R.string.settings_section_localization),
              icon = Icons.Outlined.Language,
            ) {
              AppLanguage.entries.forEachIndexed { index, language ->
                if (index > 0) {
                  HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
                SelectablePreferenceRow(
                  title = language.nativeLabel,
                  subtitle = "${language.englishLabel} (${language.bcp47Tag.uppercase()})",
                  selected = settings.language == language,
                  testTag = "language_option_${language.storageKey}",
                  onClick = { onLanguageSelected(language) },
                )
              }
            }
          }

          // Currency Selection Card
          item {
            SettingsSectionCard(
              title = localizedString(R.string.settings_section_currency),
              icon = Icons.Outlined.Payments,
            ) {
              CurrencyPreference.entries.forEachIndexed { index, currency ->
                if (index > 0) {
                  HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
                SelectablePreferenceRow(
                  title = "${currency.code} • ${currency.symbol}",
                  subtitle = currency.displayName,
                  selected = settings.currency == currency,
                  testTag = "currency_option_${currency.code.lowercase()}",
                  onClick = { onCurrencySelected(currency) },
                )
              }
            }
          }

          // App Information Summary Card
          item {
            SettingsSectionCard(
              title = localizedString(R.string.settings_section_app_info),
              icon = Icons.Rounded.VerifiedUser,
            ) {
              Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                KeyValueRow(
                  label = localizedString(R.string.info_label_app_name),
                  value = localizedString(R.string.app_name),
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                KeyValueRow(
                  label = localizedString(R.string.info_label_app_id),
                  value = BuildConfig.APPLICATION_ID,
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                KeyValueRow(
                  label = localizedString(R.string.info_label_version),
                  value = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                )
              }
            }
          }

          // Database Diagnostics Card
          item {
            SettingsSectionCard(
              title = localizedString(R.string.settings_section_database),
              icon = Icons.Outlined.Storage,
            ) {
              val health = model.databaseHealth
              Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                KeyValueRow(
                  label = localizedString(R.string.settings_db_name_label),
                  value = health?.databaseName ?: "meethopasal_pro.db",
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                KeyValueRow(
                  label = localizedString(R.string.settings_db_version_label),
                  value = "v${health?.schemaVersion ?: 1}",
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                KeyValueRow(
                  label = localizedString(R.string.settings_db_records_label),
                  value = "${health?.settingsRecordCount ?: 1}",
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically,
                ) {
                  Text(
                    text = localizedString(R.string.settings_db_status_label),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                  )
                  StatusPill(
                    text = health?.statusLabel ?: "Checking…",
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                  )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                  OutlinedButton(
                    onClick = onVerifyDatabase,
                    modifier = Modifier
                      .weight(1f)
                      .minimumInteractiveComponentSize()
                      .testTag("settings_verify_db_button"),
                  ) {
                    Text(localizedString(R.string.settings_action_health_check))
                  }

                  Button(
                    onClick = { showResetDialog = true },
                    modifier = Modifier
                      .weight(1f)
                      .minimumInteractiveComponentSize()
                      .testTag("settings_reset_button"),
                    colors = ButtonDefaults.buttonColors(
                      containerColor = MaterialTheme.colorScheme.primary,
                      contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                  ) {
                    Icon(
                      imageVector = Icons.Outlined.RestartAlt,
                      contentDescription = null,
                      modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(localizedString(R.string.settings_action_reset))
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun SettingsSectionCard(
  title: String,
  icon: ImageVector,
  content: @Composable () -> Unit,
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = MaterialTheme.shapes.medium,
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(18.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(20.dp),
        )
        Text(
          text = title,
          style = MaterialTheme.typography.titleMedium,
          color = MaterialTheme.colorScheme.onSurface,
        )
      }
      content()
    }
  }
}

@Composable
private fun ThemeOptionRow(
  label: String,
  icon: ImageVector,
  selected: Boolean,
  testTag: String,
  onClick: () -> Unit,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .minimumInteractiveComponentSize()
      .clickable(onClick = onClick)
      .padding(vertical = 8.dp)
      .testTag(testTag),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = if (selected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant,
      )
      Text(
        text = label,
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
        color = MaterialTheme.colorScheme.onSurface,
      )
    }
    RadioButton(
      selected = selected,
      onClick = null,
    )
  }
}

@Composable
private fun SelectablePreferenceRow(
  title: String,
  subtitle: String,
  selected: Boolean,
  testTag: String,
  onClick: () -> Unit,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .minimumInteractiveComponentSize()
      .clickable(onClick = onClick)
      .padding(vertical = 8.dp)
      .testTag(testTag),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
        color = MaterialTheme.colorScheme.onSurface,
      )
      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
    RadioButton(
      selected = selected,
      onClick = null,
    )
  }
}

@Composable
private fun KeyValueRow(
  label: String,
  value: String,
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Text(
      text = value,
      style = MonospaceMetaTextStyle,
      color = MaterialTheme.colorScheme.onSurface,
    )
  }
}
