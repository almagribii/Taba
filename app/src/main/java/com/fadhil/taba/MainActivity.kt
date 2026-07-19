package com.fadhil.taba

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.Saver
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
            LaunchedEffect(Unit) {
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
    
    // Gunakan rememberSaveable agar state navigasi bertahan saat rotasi layar
    var currentScreen by rememberSaveable(saver = ScreenSaver) { mutableStateOf<Screen>(Screen.Welcome) }

    // Jika user sudah login, arahkan ke Dashboard (Hanya jika belum di Dashboard)
    LaunchedEffect(currentUser) {
        if (currentUser != null && currentScreen == Screen.Welcome) {
            currentScreen = Screen.Dashboard
        } else if (currentUser == null && currentScreen == Screen.Dashboard) {
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
        Screen.Dashboard -> {
            DashboardScreen(
                authViewModel = authViewModel,
                onSignOut = { currentScreen = Screen.Welcome }
            )
        }
    }
}

// Saver untuk menyimpan state navigasi saat Activity dibuat ulang (misalnya saat rotasi)
val ScreenSaver = Saver<MutableState<Screen>, String>(
    save = {
        when (it.value) {
            is Screen.Welcome -> "welcome"
            is Screen.Dashboard -> "dashboard"
        }
    },
    restore = {
        mutableStateOf(
            when (it) {
                "dashboard" -> Screen.Dashboard
                else -> Screen.Welcome
            }
        )
    }
)

sealed class Screen {
    object Welcome : Screen()
    object Dashboard : Screen()
}
