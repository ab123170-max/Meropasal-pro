package com.example.ui.screens.info

import android.os.Build
import androidx.lifecycle.ViewModel
import com.example.BuildConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class TechStackItem(
  val name: String,
  val role: String,
)

data class SystemInfoModel(
  val applicationId: String,
  val versionName: String,
  val versionCode: Int,
  val targetSdk: Int,
  val runtimeSdk: Int,
  val techStack: List<TechStackItem>,
)

@HiltViewModel
class InfoViewModel @Inject constructor() : ViewModel() {

  private val _uiState = MutableStateFlow(
    SystemInfoModel(
      applicationId = BuildConfig.APPLICATION_ID,
      versionName = BuildConfig.VERSION_NAME,
      versionCode = BuildConfig.VERSION_CODE,
      targetSdk = 36,
      runtimeSdk = Build.VERSION.SDK_INT,
      techStack = listOf(
        TechStackItem("Kotlin & Coroutines", "Asynchronous StateFlow pipelines"),
        TechStackItem("Jetpack Compose M3", "Edge-to-edge adaptive Material 3 UI"),
        TechStackItem("Room Database (SQLite)", "Offline-first local persistence & migrations"),
        TechStackItem("Dagger Hilt", "Compile-time dependency injection"),
        TechStackItem("Navigation Compose", "Type-safe @Serializable destination graph"),
        TechStackItem("WorkManager", "Reliable offline background verification"),
      ),
    )
  )
  val uiState: StateFlow<SystemInfoModel> = _uiState.asStateFlow()
}
