package com.example.ui.screens.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.BuildConfig
import com.example.R
import com.example.core.locale.localizedString
import com.example.ui.components.ErrorStateCard
import com.example.ui.components.StatusPill
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.DeepNavyDark
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.EmeraldGreenLight

@Composable
fun SplashScreen(
  onReadyToNavigate: () -> Unit,
  viewModel: SplashViewModel = hiltViewModel(),
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect(uiState) {
    if (uiState is SplashUiState.Ready) {
      onReadyToNavigate()
    }
  }

  SplashContent(
    uiState = uiState,
    onRetry = { viewModel.bootstrap(minDisplayMs = 250L) },
  )
}

@Composable
fun SplashContent(
  uiState: SplashUiState,
  onRetry: () -> Unit,
) {
  var visible by remember { mutableStateOf(false) }
  LaunchedEffect(Unit) { visible = true }

  val progress by animateFloatAsState(
    targetValue = if (uiState is SplashUiState.Ready) 1f else 0.72f,
    animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing),
    label = "splash_progress",
  )

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        brush = Brush.verticalGradient(
          colors = listOf(DeepNavyDark, DeepNavy, Color(0xFF14355B)),
        )
      )
      .safeDrawingPadding()
      .padding(24.dp)
      .testTag("splash_screen"),
  ) {
    Column(
      modifier = Modifier
        .align(Alignment.Center)
        .widthIn(max = 420.dp)
        .fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
    ) {
      AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(420)) + scaleIn(initialScale = 0.88f, animationSpec = tween(420)),
      ) {
        Surface(
          shape = RoundedCornerShape(28.dp),
          color = Color(0xFF112943),
          shadowElevation = 12.dp,
          modifier = Modifier.size(116.dp),
        ) {
          Image(
            painter = painterResource(id = R.drawable.ic_meethopasal_logo),
            contentDescription = localizedString(R.string.app_name),
            contentScale = ContentScale.Crop,
            modifier = Modifier
              .fillMaxSize()
              .clip(RoundedCornerShape(28.dp)),
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      Text(
        text = localizedString(R.string.app_name),
        style = MaterialTheme.typography.displaySmall,
        color = Color.White,
        textAlign = TextAlign.Center,
        modifier = Modifier.testTag("splash_brand_title"),
      )

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = localizedString(R.string.brand_tagline),
        style = MaterialTheme.typography.titleMedium,
        color = Color(0xFFCBD5E1),
        textAlign = TextAlign.Center,
      )

      Spacer(modifier = Modifier.height(16.dp))

      StatusPill(
        text = localizedString(R.string.splash_offline_badge),
        containerColor = EmeraldGreen.copy(alpha = 0.22f),
        contentColor = EmeraldGreenLight,
        icon = Icons.Rounded.CloudOff,
      )

      Spacer(modifier = Modifier.height(32.dp))

      when (uiState) {
        is SplashUiState.Initializing, SplashUiState.Ready -> {
          LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
              .widthIn(max = 220.dp)
              .fillMaxWidth()
              .height(6.dp)
              .clip(RoundedCornerShape(50)),
            color = EmeraldGreenLight,
            trackColor = Color.White.copy(alpha = 0.16f),
          )
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = if (uiState is SplashUiState.Ready) {
              localizedString(R.string.splash_ready)
            } else {
              localizedString(R.string.splash_initializing)
            },
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFFE2E8F0),
            fontWeight = FontWeight.Medium,
          )
        }
        is SplashUiState.Error -> {
          ErrorStateCard(
            message = uiState.message,
            onRetry = onRetry,
          )
        }
      }
    }

    Text(
      text = "v${BuildConfig.VERSION_NAME} • Part 01 Foundation",
      style = MaterialTheme.typography.labelMedium,
      color = Color(0xFF94A3B8),
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(bottom = 8.dp),
    )
  }
}
