package com.fadhil.taba.ui.dashboard

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fadhil.taba.R
import com.fadhil.taba.ui.auth.AuthViewModel
import com.fadhil.taba.ui.theme.GreenPrimary
import java.io.File
import java.io.FileOutputStream

@Composable
fun DashboardScreen(
    authViewModel: AuthViewModel = viewModel(),
    onSignOut: () -> Unit
) {
    val context = LocalContext.current
    val user by authViewModel.currentUser.collectAsState()
    var currentRoute by remember { mutableStateOf("home") }
    
    // Avatar state
    var avatarPath by remember { mutableStateOf<String?>(null) }
    
    // Muat path avatar saat pertama kali buka
    LaunchedEffect(Unit) {
        val file = File(context.filesDir, "user_avatar.jpg")
        if (file.exists()) {
            avatarPath = file.absolutePath
        }
    }
    
    // Background krem
    val backgroundColor = Color(0xFFF9F7F2)

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TabaTopBar(
                username = user?.userMetadata?.get("username")?.toString() ?: "Pengguna",
                avatarPath = avatarPath,
                onProfileClick = { currentRoute = "settings" }
            )
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
                "materi" -> MateriScreen()
                "mufrodat" -> MufrodatScreen()
                "settings" -> SettingsScreen(
                    avatarPath = avatarPath,
                    onAvatarChange = { newPath -> avatarPath = newPath },
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

@Composable
fun HomeScreen(username: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Ahlan wa Sahlan, $username!", style = MaterialTheme.typography.headlineSmall)
    }
}

@Composable
fun HiwarScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Halaman Al-Hiwar (Percakapan)")
    }
}

@Composable
fun MateriScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Halaman Materi Pelajaran")
    }
}

@Composable
fun MufrodatScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Halaman Al-Mufradat (Kosakata)")
    }
}

@Composable
fun SettingsScreen(
    avatarPath: String?,
    onAvatarChange: (String) -> Unit,
    onSignOut: () -> Unit
) {
    val context = LocalContext.current
    
    // Launcher untuk memilih gambar dari galeri
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                // Simpan gambar ke local storage internal aplikasi
                val inputStream = context.contentResolver.openInputStream(it)
                val file = File(context.filesDir, "user_avatar.jpg")
                val outputStream = FileOutputStream(file)
                inputStream?.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }
                // Beritahu dashboard bahwa avatar telah berubah
                onAvatarChange(file.absolutePath)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Pengaturan Profil",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = GreenPrimary
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Tampilan Avatar yang bisa diklik untuk ganti
        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier.size(120.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(2.dp, GreenPrimary, CircleShape)
            ) {
                if (avatarPath != null) {
                    val bitmap = remember(avatarPath) {
                        android.graphics.BitmapFactory.decodeFile(avatarPath)
                    }
                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Avatar",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.profile),
                        contentDescription = "Avatar",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // Tombol Ikon Kamera untuk Ganti Foto
            IconButton(
                onClick = { launcher.launch("image/*") },
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(GreenPrimary)
                    .padding(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Ganti Foto",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Ketuk ikon kamera untuk mengganti foto profil", fontSize = 12.sp, color = Color.Gray)

        Spacer(modifier = Modifier.weight(1f))

        // Tombol Logout
        Button(
            onClick = onSignOut,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Keluar (Logout)", fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}
