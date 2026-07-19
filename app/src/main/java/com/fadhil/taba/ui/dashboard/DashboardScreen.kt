package com.fadhil.taba.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fadhil.taba.data.model.Module
import com.fadhil.taba.ui.auth.AuthViewModel
import java.io.File

@Composable
fun DashboardScreen(
    authViewModel: AuthViewModel = viewModel(),
    onSignOut: () -> Unit
) {
    val context = LocalContext.current
    val user by authViewModel.currentUser.collectAsState()
    var currentRoute by remember { mutableStateOf("home") }
    
    // Sub-navigation for Materi
    var selectedModule by remember { mutableStateOf<Module?>(null) }
    
    // Avatar state
    var avatarPath by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(Unit) {
        val file = File(context.filesDir, "user_avatar.jpg")
        if (file.exists()) {
            avatarPath = file.absolutePath
        }
    }
    
    val backgroundColor = Color(0xFFF9F7F2)

    // Jika ada modul yang dipilih, tampilkan Detail (menutup seluruh layar)
    if (selectedModule != null) {
        DetailMateriScreen(
            module = selectedModule!!,
            onBack = { selectedModule = null }
        )
    } else {
        Scaffold(
            containerColor = backgroundColor,
            topBar = {
                if (currentRoute != "settings") {
                    TabaTopBar(
                        username = user?.userMetadata?.get("username")?.toString() ?: "Pengguna",
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
                    "home" -> HomeScreen(user?.userMetadata?.get("username")?.toString() ?: "Pengguna")
                    "hiwar" -> HiwarScreen()
                "materi" -> MateriScreen(
                    onBack = { currentRoute = "home" },
                    onModuleClick = { selectedModule = it }
                )
                "mufrodat" -> MufrodatScreen()
                    "settings" -> SettingsScreen(
                        username = user?.userMetadata?.get("username")?.toString() ?: "Pengguna TABA",
                        avatarPath = avatarPath,
                        onAvatarChange = { avatarPath = it },
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
