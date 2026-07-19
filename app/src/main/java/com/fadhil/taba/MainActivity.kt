package com.fadhil.taba

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fadhil.taba.data.settings.AppSettingsStore
import com.fadhil.taba.ui.auth.AuthViewModel
import com.fadhil.taba.ui.auth.WelcomeScreen
import com.fadhil.taba.ui.dashboard.DashboardScreen
import com.fadhil.taba.ui.theme.TabaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            LaunchedEffect(context) {
                AppSettingsStore.initialize(context)
            }
            val settings by AppSettingsStore.settings.collectAsState()
            TabaTheme(darkTheme = settings.darkMode) {
                TabaApp()
            }
        }
    }
}

@Composable
fun TabaApp() {
    val authViewModel: AuthViewModel = viewModel()
    val currentUser by authViewModel.currentUser.collectAsState()
    
    // State Navigasi Sederhana
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Welcome) }

    // Jika user sudah login, arahkan ke Dashboard
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            currentScreen = Screen.Dashboard
        } else if (currentScreen == Screen.Dashboard) {
            currentScreen = Screen.Welcome
        }
    }

    when (val screen = currentScreen) {
        Screen.Welcome -> {
            WelcomeScreen(
                viewModel = authViewModel,
                onLoginSuccess = { currentScreen = Screen.Dashboard }
            )
        }
        is Screen.Login -> {
            // Screen Login email sudah tidak digunakan, arahkan balik ke Welcome jika terpanggil
            currentScreen = Screen.Welcome
        }
        Screen.Dashboard -> {
            DashboardScreen(
                authViewModel = authViewModel,
                onSignOut = { currentScreen = Screen.Welcome }
            )
        }
    }
}

sealed class Screen {
    object Welcome : Screen()
    data class Login(val isSignUp: Boolean) : Screen()
    object Dashboard : Screen()
}
