package com.fadhil.taba.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fadhil.taba.data.model.Module
import com.fadhil.taba.data.settings.AppSettingsStore
import com.fadhil.taba.ui.auth.AuthViewModel
import com.fadhil.taba.ui.dashboard.chat_ai.ChatAiScreen
import com.fadhil.taba.ui.dashboard.home.HomeScreen
import com.fadhil.taba.ui.dashboard.materi.DetailMateriScreen
import com.fadhil.taba.ui.dashboard.materi.MateriScreen
import com.fadhil.taba.ui.dashboard.mufrodat.MufrodatScreen
import com.fadhil.taba.ui.dashboard.settings.SettingsScreen
import java.io.File

import androidx.compose.runtime.saveable.rememberSaveable
import com.fadhil.taba.data.local.ModuleData

@Composable
fun DashboardScreen(
    authViewModel: AuthViewModel = viewModel(),
    onSignOut: () -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        AppSettingsStore.initialize(context)
    }
    val settings by AppSettingsStore.settings.collectAsState()
    val user by authViewModel.currentUser.collectAsState()
    var currentRoute by rememberSaveable { mutableStateOf("home") }
    
    // States untuk sub-navigasi, gunakan ID atau ID modul agar bisa disimpan saat rotasi
    var selectedModuleId by rememberSaveable { mutableStateOf<Int?>(null) }
    var practiceModuleId by rememberSaveable { mutableStateOf<Int?>(null) }
    
    val selectedModuleForDetail = selectedModuleId?.let { id -> ModuleData.modules.find { it.id == id } }
    val selectedModuleForPractice = practiceModuleId?.let { id -> ModuleData.modules.find { it.id == id } }
    
    // Avatar state
    val avatarPath = settings.avatarPath ?: run {
        val file = File(context.filesDir, "user_avatar.jpg")
        if (file.exists()) file.absolutePath else null
    }
    val profileName = settings.displayName.takeIf { it.isNotBlank() }
        ?: user?.userMetadata?.get("username")?.toString()
        ?: "Pengguna"
    
    val backgroundColor = Color(0xFFF9F7F2)

    if (selectedModuleForDetail != null) {
        DetailMateriScreen(
            module = selectedModuleForDetail,
            onBack = { selectedModuleId = null },
            onPracticeClick = { module ->
                practiceModuleId = module.id
                selectedModuleId = null
                currentRoute = "mufrodat_internal"
            }
        )
    } else if (currentRoute == "mufrodat_internal") {
        MufrodatScreen(
            initialModule = selectedModuleForPractice,
            onBack = { currentRoute = "materi" }
        )
    } else {
        Scaffold(
            containerColor = backgroundColor,
            topBar = {
                if (currentRoute != "settings" && currentRoute != "chat_ai") {
                    TabaTopBar(
                        username = profileName,
                        avatarPath = avatarPath,
                        onProfileClick = { currentRoute = "settings" }
                    )
                }
            },
            bottomBar = {
                TabaBottomBar(
                    currentRoute = currentRoute,
                    lang = settings.language,
                    onNavigate = { currentRoute = it }
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (currentRoute) {
                    "home" -> HomeScreen(
                        username = profileName,
                        onStartLearningClick = { currentRoute = "materi" },
                        onModuleClick = { selectedModuleId = it.id },
                        sectionTitle = settings.homeSectionTitle,
                        sectionActionText = settings.homeSectionActionText
                    )
                    "materi" -> MateriScreen(
                        onBack = { currentRoute = "home" },
                        onModuleClick = { selectedModuleId = it.id },
                        bannerTitle = settings.materiBannerTitle,
                        bannerSubtitle = settings.materiBannerSubtitle,
                        searchPlaceholder = settings.searchPlaceholder
                    )
                    "chat_ai" -> ChatAiScreen()
                    "settings" -> SettingsScreen(
                        username = profileName,
                        avatarPath = avatarPath,
                        onAvatarChange = { newPath ->
                            AppSettingsStore.update(context) { it.copy(avatarPath = newPath) }
                        },
                        onSignOut = {
                            authViewModel.signOut(context) {
                                onSignOut()
                            }
                        }
                    )
                }
            }
        }
    }
}
