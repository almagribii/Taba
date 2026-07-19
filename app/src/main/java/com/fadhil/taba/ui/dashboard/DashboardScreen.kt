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

@Composable
fun DashboardScreen(
    authViewModel: AuthViewModel = viewModel(),
    onSignOut: () -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(context) {
        AppSettingsStore.initialize(context)
    }
    val settings by AppSettingsStore.settings.collectAsState()
    val user by authViewModel.currentUser.collectAsState()
    var currentRoute by remember { mutableStateOf("home") }
    
    // States untuk sub-navigasi
    var selectedModuleForDetail by remember { mutableStateOf<Module?>(null) }
    var selectedModuleForPractice by remember { mutableStateOf<Module?>(null) }
    
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
            module = selectedModuleForDetail!!,
            onBack = { selectedModuleForDetail = null },
            onPracticeClick = { module ->
                selectedModuleForPractice = module
                selectedModuleForDetail = null
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
                        onModuleClick = { selectedModuleForDetail = it },
                        heroTitle = settings.homeHeroTitle,
                        heroSubtitle = settings.homeHeroSubtitle,
                        startButtonText = settings.homeActionText,
                        sectionTitle = settings.homeSectionTitle,
                        sectionActionText = settings.homeSectionActionText
                    )
                    "materi" -> MateriScreen(
                        onBack = { currentRoute = "home" },
                        onModuleClick = { selectedModuleForDetail = it },
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
