package com.fadhil.taba.ui.dashboard.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
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
import com.fadhil.taba.R
import com.fadhil.taba.data.settings.AppSettingsStore
import com.fadhil.taba.data.settings.Localization
import com.fadhil.taba.ui.dashboard.TabaHeader
import com.fadhil.taba.ui.theme.GreenPrimary
import io.github.jan.supabase.auth.user.UserInfo
import java.io.File
import java.io.FileOutputStream

@Composable
fun UserDetailScreen(
    userInfo: UserInfo?,
    username: String,
    avatarPath: String?,
    onAvatarChange: (String) -> Unit,
    onSignOut: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val settings by AppSettingsStore.settings.collectAsState()
    val lang = settings.language
    val scrollState = rememberScrollState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(it)
                val file = File(context.filesDir, "user_avatar.jpg")
                val outputStream = FileOutputStream(file)
                inputStream?.use { input -> outputStream.use { output -> input.copyTo(output) } }
                onAvatarChange(file.absolutePath)
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GreenPrimary)
    ) {
        TabaHeader(
            title = if (lang == "en") "User Profile" else "Detail Pengguna",
            onBack = onBack
        )

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = (-8).dp),
            color = Color(0xFFF8F9FA),
            shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Large Avatar with Edit Button
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(2.dp, GreenPrimary.copy(alpha = 0.1f), CircleShape)
                        .clickable { launcher.launch("image/*") }
                ) {
                    if (avatarPath != null) {
                        val bitmap = remember(avatarPath) { android.graphics.BitmapFactory.decodeFile(avatarPath) }
                        if (bitmap != null) {
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.profile),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    
                    // Edit Overlay Icon
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.PhotoCamera,
                            contentDescription = "Change Avatar",
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = username,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = GreenPrimary
                )
                Text(
                    text = Localization.getString("active_learner", lang),
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(32.dp))

                // User Info Details Card
                InfoCard(
                    title = if (lang == "en") "Account Information" else "Informasi Akun",
                    items = listOf(
                        InfoItem(Icons.Default.Person, if (lang == "en") "Name" else "Nama", username),
                        InfoItem(Icons.Default.Email, "Email", userInfo?.email ?: "-"),
                        InfoItem(Icons.Default.Fingerprint, "User ID", if (userInfo?.id != null) userInfo.id.take(13) + "..." else "-")
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Stats Card (Placeholder)
                InfoCard(
                    title = if (lang == "en") "Learning Stats" else "Statistik Belajar",
                    items = listOf(
                        InfoItem(Icons.Default.Star, if (lang == "en") "Saved Vocabulary" else "Kosakata Tersimpan", settings.starredVocabKeys.size.toString()),
                        InfoItem(Icons.Default.AutoGraph, if (lang == "en") "Current Level" else "Level Saat Ini", "Level 1")
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))
                
                // Sign Out Action
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSignOut() },
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFFFEE2E2))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = Color(0xFFEF4444))
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = Localization.getString("sign_out", lang),
                            color = Color(0xFFEF4444),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = onBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                ) {
                    Text(text = Localization.getString("back", lang), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

data class InfoItem(val icon: androidx.compose.ui.graphics.vector.ImageVector, val label: String, val value: String)

@Composable
fun InfoCard(title: String, items: List<InfoItem>) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            items.forEachIndexed { index, item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = GreenPrimary.copy(alpha = 0.05f),
                        shape = CircleShape,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(item.icon, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(18.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = item.label, fontSize = 12.sp, color = Color.Gray)
                        Text(text = item.value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                    }
                }
                if (index < items.size - 1) {
                    HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)
                }
            }
        }
    }
}
