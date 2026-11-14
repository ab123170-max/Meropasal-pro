package com.example.ui.screens.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.LockClock
import androidx.compose.material.icons.outlined.PointOfSale
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material.icons.outlined.SoupKitchen
import androidx.compose.material.icons.outlined.TableRestaurant
import androidx.compose.material.icons.rounded.CheckCircleOutline
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.TrendingFlat
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.core.currency.CurrencyFormatter
import com.example.core.locale.localizedString
import com.example.domain.model.ModuleDescriptor
import com.example.ui.components.ErrorStateCard
import com.example.ui.components.LoadingStateCard
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusPill
import com.example.ui.state.UiState
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.DeepNavyDark
import com.example.ui.theme.FinancialFigureTextStyle
import com.example.ui.theme.MonospaceMetaTextStyle

@Composable
fun DashboardScreen(
  contentPadding: PaddingValues = PaddingValues(0.dp),
  viewModel: DashboardViewModel = hiltViewModel(),
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  DashboardContent(
    uiState = uiState,
    contentPadding = contentPadding,
    onRetry = viewModel::refreshStatus,
  )
}

@Composable
fun DashboardContent(
  uiState: UiState<DashboardContentModel>,
  contentPadding: PaddingValues = PaddingValues(0.dp),
  onRetry: () -> Unit = {},
) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .testTag("dashboard_screen"),
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
      verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
      when (uiState) {
        is UiState.Loading -> {
          item { LoadingStateCard() }
        }
        is UiState.Error -> {
          item {
            ErrorStateCard(
              message = uiState.error.message,
              onRetry = onRetry,
            )
          }
        }
        is UiState.Empty -> {
          item { LoadingStateCard(message = uiState.message) }
        }
        is UiState.Success -> {
          val data = uiState.data

          item {
            WelcomeHeroCard(
              restaurantName = data.settings.restaurantName,
              branchCode = data.settings.branchCode,
            )
          }

          item {
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
              val isWide = maxWidth >= 600.dp
              if (isWide) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                  BusinessOverviewCard(
                    modifier = Modifier.weight(1f),
                  )
                  SalesSummaryPlaceholderCard(
                    currencyLabel = CurrencyFormatter.displayBadge(data.settings.currency),
                    modifier = Modifier.weight(1f),
                  )
                }
              } else {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                  BusinessOverviewCard(modifier = Modifier.fillMaxWidth())
                  SalesSummaryPlaceholderCard(
                    currencyLabel = CurrencyFormatter.displayBadge(data.settings.currency),
                    modifier = Modifier.fillMaxWidth(),
                  )
                }
              }
            }
          }

          item {
            SectionHeader(
              title = localizedString(R.string.dashboard_modules_title),
              subtitle = localizedString(R.string.dashboard_modules_subtitle),
            )
          }

          item {
            RoadmapGrid(modules = data.plannedModules)
          }
        }
      }
    }
  }
}

@Composable
private fun WelcomeHeroCard(
  restaurantName: String,
  branchCode: String,
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("dashboard_welcome_card"),
    shape = MaterialTheme.shapes.large,
    colors = CardDefaults.cardColors(containerColor = DeepNavyDark),
  ) {
    Box(modifier = Modifier.fillMaxWidth()) {
      Image(
        painter = painterResource(id = R.drawable.img_restaurant_hero),
        contentDescription = localizedString(R.string.dashboard_welcome_title),
        contentScale = ContentScale.Crop,
        modifier = Modifier
          .matchParentSize()
          .alpha(0.28f),
      )
      Box(
        modifier = Modifier
          .matchParentSize()
          .background(
            Brush.horizontalGradient(
              colors = listOf(
                DeepNavyDark.copy(alpha = 0.94f),
                DeepNavy.copy(alpha = 0.80f),
              )
            )
          )
      )

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(
            text = localizedString(R.string.dashboard_welcome_kicker).uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = Color(0xFF93C5FD),
          )
          StatusPill(
            text = branchCode,
            containerColor = Color.White.copy(alpha = 0.14f),
            contentColor = Color.White,
          )
        }

        Text(
          text = localizedString(R.string.dashboard_welcome_title),
          style = MaterialTheme.typography.headlineMedium,
          color = Color.White,
        )

        Text(
          text = "$restaurantName • ${localizedString(R.string.dashboard_welcome_subtitle)}",
          style = MaterialTheme.typography.bodyMedium,
          color = Color(0xFFCBD5E1),
        )

        Spacer(modifier = Modifier.height(4.dp))

        StatusPill(
          text = localizedString(R.string.dashboard_offline_status),
          containerColor = Color(0xFF1B8A5A).copy(alpha = 0.28f),
          contentColor = Color(0xFF6EE7B7),
          icon = Icons.Rounded.CheckCircleOutline,
        )
      }
    }
  }
}

@Composable
private fun BusinessOverviewCard(modifier: Modifier = Modifier) {
  Card(
    modifier = modifier.testTag("dashboard_business_overview_card"),
    shape = MaterialTheme.shapes.medium,
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(18.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          text = localizedString(R.string.dashboard_business_overview_title),
          style = MaterialTheme.typography.titleLarge,
          color = MaterialTheme.colorScheme.onSurface,
        )
        Icon(
          imageVector = Icons.Rounded.Storage,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
        )
      }

      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        FoundationRow(
          label = localizedString(R.string.dashboard_metric_storage_label),
          value = localizedString(R.string.dashboard_metric_storage_value),
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        FoundationRow(
          label = localizedString(R.string.dashboard_metric_sync_label),
          value = localizedString(R.string.dashboard_metric_sync_value),
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        FoundationRow(
          label = localizedString(R.string.dashboard_metric_modules_label),
          value = localizedString(R.string.dashboard_metric_modules_value),
        )
      }

      Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surfaceVariant,
      ) {
        Text(
          text = localizedString(R.string.dashboard_business_overview_empty),
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.padding(12.dp),
        )
      }
    }
  }
}

@Composable
private fun FoundationRow(
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
      style = MaterialTheme.typography.titleSmall,
      color = MaterialTheme.colorScheme.onSurface,
    )
  }
}

@Composable
private fun SalesSummaryPlaceholderCard(
  currencyLabel: String,
  modifier: Modifier = Modifier,
) {
  Card(
    modifier = modifier.testTag("dashboard_sales_summary_card"),
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
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          text = localizedString(R.string.dashboard_sales_summary_title),
          style = MaterialTheme.typography.titleLarge,
          color = MaterialTheme.colorScheme.onSurface,
        )
        StatusPill(
          text = currencyLabel,
          containerColor = MaterialTheme.colorScheme.secondaryContainer,
          contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        )
      }

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        Text(
          text = localizedString(R.string.dashboard_sales_empty_figure),
          style = FinancialFigureTextStyle,
          color = MaterialTheme.colorScheme.primary,
          modifier = Modifier.testTag("sales_empty_figure"),
        )
        Icon(
          imageVector = Icons.Rounded.TrendingFlat,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }

      Text(
        text = localizedString(R.string.dashboard_sales_empty_title),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface,
      )

      Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surfaceVariant,
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          verticalAlignment = Alignment.Top,
        ) {
          Icon(
            imageVector = Icons.Rounded.CloudOff,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp),
          )
          Text(
            text = localizedString(R.string.dashboard_sales_empty_subtitle, currencyLabel),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }
    }
  }
}

@Composable
private fun RoadmapGrid(modules: List<ModuleDescriptor>) {
  BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
    val columns = if (maxWidth >= 600.dp) 3 else 2
    val rows = modules.chunked(columns)
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
      rows.forEach { rowItems ->
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          rowItems.forEach { module ->
            DisabledRoadmapModuleCard(
              module = module,
              modifier = Modifier.weight(1f),
            )
          }
          repeat(columns - rowItems.size) {
            Spacer(modifier = Modifier.weight(1f))
          }
        }
      }
    }
  }
}

@Composable
private fun DisabledRoadmapModuleCard(
  module: ModuleDescriptor,
  modifier: Modifier = Modifier,
) {
  val icon: ImageVector = when (module.id) {
    "pos_billing" -> Icons.Outlined.PointOfSale
    "menu_catalog" -> Icons.Outlined.RestaurantMenu
    "table_floor" -> Icons.Outlined.TableRestaurant
    "kds_kitchen" -> Icons.Outlined.SoupKitchen
    "inventory_stock" -> Icons.Outlined.Inventory2
    else -> Icons.Outlined.Assessment
  }

  // Non-interactive disabled placeholder card so there are zero nonfunctional buttons.
  Surface(
    modifier = modifier.testTag("module_placeholder_${module.id}"),
    shape = RoundedCornerShape(16.dp),
    color = MaterialTheme.colorScheme.surfaceContainerLow,
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)),
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
        .alpha(0.84f),
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Surface(
          shape = MaterialTheme.shapes.small,
          color = MaterialTheme.colorScheme.surfaceVariant,
          modifier = Modifier.size(38.dp),
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = icon,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(20.dp),
            )
          }
        }

        StatusPill(
          text = module.targetPartLabel,
          containerColor = MaterialTheme.colorScheme.surfaceVariant,
          contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }

      Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(
          text = module.title,
          style = MaterialTheme.typography.titleMedium,
          color = MaterialTheme.colorScheme.onSurface,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
        Text(
          text = module.subtitle,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
        )
      }

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
      ) {
        Icon(
          imageVector = Icons.Outlined.LockClock,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.tertiary,
          modifier = Modifier.size(14.dp),
        )
        Text(
          text = localizedString(R.string.badge_coming_soon),
          style = MonospaceMetaTextStyle,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.tertiary,
        )
      }
    }
  }
}
