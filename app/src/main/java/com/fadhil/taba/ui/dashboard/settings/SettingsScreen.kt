package com.fadhil.taba.ui.dashboard.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fadhil.taba.R
import com.fadhil.taba.ui.theme.GreenPrimary
import com.fadhil.taba.data.settings.AppSettingsStore
import com.fadhil.taba.data.settings.Localization
import java.io.File
import java.io.FileOutputStream

@Composable
fun SettingsScreen(
    username: String,
    avatarPath: String?,
    onAvatarChange: (String) -> Unit,
    onHelpClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    onSignOut: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val settings by AppSettingsStore.settings.collectAsState()
    val lang = settings.language
    val isFullHarakat = settings.isFullHarakat

    var isNotifEnabled by rememberSaveable { mutableStateOf(true) }
    var isMicEnabled by rememberSaveable { mutableStateOf(true) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showAudioSpeedDialog by remember { mutableStateOf(false) }
    var showGenderDialog by remember { mutableStateOf(false) }

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

    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text(Localization.getString("select_language", lang)) },
            text = {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                AppSettingsStore.setLanguage(context, "in")
                                showLanguageDialog = false
                            }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = lang == "in", onClick = null)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Bahasa Indonesia")
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                AppSettingsStore.setLanguage(context, "en")
                                showLanguageDialog = false
                            }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = lang == "en", onClick = null)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("English")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text(Localization.getString("cancel", lang))
                }
            }
        )
    }

    if (showAudioSpeedDialog) {
        AlertDialog(
            onDismissRequest = { showAudioSpeedDialog = false },
            title = { Text(Localization.getString("audio_speed", lang)) },
            text = {
                Column {
                    listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f).forEach { speed ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    AppSettingsStore.setAudioSpeed(context, speed)
                                    showAudioSpeedDialog = false
                                }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = settings.audioSpeed == speed, onClick = null)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text("${speed}x")
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAudioSpeedDialog = false }) {
                    Text(Localization.getString("cancel", lang))
                }
            }
        )
    }

    if (showGenderDialog) {
        AlertDialog(
            onDismissRequest = { showGenderDialog = false },
            title = { Text(Localization.getString("voice_gender", lang)) },
            text = {
                Column {
                    listOf("male", "female").forEach { gender ->
                        val label = if (gender == "male") Localization.getString("male", lang) 
                                   else Localization.getString("female", lang)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    AppSettingsStore.setVoiceGender(context, gender)
                                    showGenderDialog = false
                                }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = settings.voiceGender == gender, onClick = null)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(label)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showGenderDialog = false }) {
                    Text(Localization.getString("cancel", lang))
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Judul (Lebih Kecil)
        Text(text = Localization.getString("settings_title", lang), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = GreenPrimary)
        Text(text = "الإعدادات", fontSize = 16.sp, color = GreenPrimary.copy(alpha = 0.7f), modifier = Modifier.padding(bottom = 16.dp))

        // Profile Card (Lebih Kecil)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .clickable { launcher.launch("image/*") },
            color = Color.White,
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, GreenPrimary.copy(alpha = 0.05f))
        ) {
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(60.dp).clip(CircleShape)) {
                    if (avatarPath != null) {
                        val bitmap = remember(avatarPath) { android.graphics.BitmapFactory.decodeFile(avatarPath) }
                        if (bitmap != null) {
                            Image(bitmap = bitmap.asImageBitmap(), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                        }
                    } else {
                        Image(painter = painterResource(id = R.drawable.profile), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = username, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = GreenPrimary)
                    Text(text = Localization.getString("active_learner", lang), color = Color.Gray, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Surface(color = Color(0xFFF0FDF4), shape = RoundedCornerShape(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFF166534), modifier = Modifier.size(10.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                        }
                    }
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Sections
        SettingSectionTitle(Localization.getString("general_settings", lang))
        Surface(modifier = Modifier.fillMaxWidth(), color = Color.White, shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, Color(0xFFF3F4F6))) {
            Column {
                SettingItem(Icons.Default.Person, Localization.getString("user_profile", lang))
                SettingDivider()
                SettingItem(
                    Icons.Default.Language, 
                    Localization.getString("app_language", lang), 
                    if (lang == "en") "English" else "Bahasa Indonesia",
                    onClick = { showLanguageDialog = true }
                )
                SettingDivider()
                SettingItem(Icons.Default.VolumeUp, Localization.getString("sound_volume", lang))
                SettingDivider()
                SettingSwitchItem(Icons.Default.Notifications, Localization.getString("learning_notifications", lang), isNotifEnabled) { isNotifEnabled = it }
                SettingDivider()
                SettingSwitchItem(Icons.Default.Mic, Localization.getString("microphone_permission", lang), isMicEnabled) { isMicEnabled = it }
                SettingDivider()
                SettingSwitchItem(
                    Icons.Default.TextFormat, 
                    Localization.getString("full_harakat_mode", lang), 
                    isFullHarakat, 
                    subtitle = Localization.getString("full_harakat_subtitle", lang)
                ) { AppSettingsStore.setFullHarakat(context, it) }
                SettingDivider()
                SettingItem(Icons.Default.BarChart, Localization.getString("progress_history", lang))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        SettingSectionTitle(Localization.getString("learning_preferences", lang))
        Surface(modifier = Modifier.fillMaxWidth(), color = Color.White, shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, Color(0xFFF3F4F6))) {
            Column {
                SettingItem(
                    Icons.Default.Speed, 
                    Localization.getString("audio_speed", lang), 
                    "${settings.audioSpeed}x",
                    onClick = { showAudioSpeedDialog = true }
                )
                SettingDivider()
                SettingItem(
                    Icons.Default.Wc, 
                    Localization.getString("voice_gender", lang), 
                    if (settings.voiceGender == "male") Localization.getString("male", lang) else Localization.getString("female", lang),
                    onClick = { showGenderDialog = true }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Surface(modifier = Modifier.fillMaxWidth(), color = Color.White, shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, Color(0xFFF3F4F6))) {
            Column {
                SettingItem(Icons.Default.Help, Localization.getString("help", lang), onClick = onHelpClick)
                SettingDivider()
                SettingItem(Icons.Default.PrivacyTip, Localization.getString("privacy_policy", lang), onClick = onPrivacyPolicyClick)
                SettingDivider()
                SettingItem(icon = Icons.AutoMirrored.Filled.Logout, title = Localization.getString("sign_out", lang), textColor = Color(0xFFEF4444), onClick = onSignOut)
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun SettingSectionTitle(title: String) {
    Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF166534), modifier = Modifier.padding(start = 4.dp, bottom = 8.dp))
}

@Composable
fun SettingItem(icon: ImageVector, title: String, value: String? = null, textColor: Color = GreenPrimary, onClick: () -> Unit = {}) {
    Row(modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
        Surface(color = Color(0xFFF0FDF4), shape = CircleShape, modifier = Modifier.size(28.dp)) {
            Box(contentAlignment = Alignment.Center) { Icon(icon, contentDescription = null, tint = Color(0xFF166534), modifier = Modifier.size(16.dp)) }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = title, modifier = Modifier.weight(1f), fontSize = 14.sp, color = textColor, fontWeight = FontWeight.Medium)
        if (value != null) { Text(text = value, fontSize = 12.sp, color = Color(0xFF166534), modifier = Modifier.padding(horizontal = 4.dp)) }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFFD1D5DB), modifier = Modifier.size(16.dp))
    }
}

@Composable
fun SettingSwitchItem(icon: ImageVector, title: String, checked: Boolean, subtitle: String? = null, onCheckedChange: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
        Surface(color = Color(0xFFF0FDF4), shape = CircleShape, modifier = Modifier.size(28.dp)) {
            Box(contentAlignment = Alignment.Center) { Icon(icon, contentDescription = null, tint = Color(0xFF166534), modifier = Modifier.size(16.dp)) }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 14.sp, color = GreenPrimary, fontWeight = FontWeight.Medium)
            if (subtitle != null) { Text(text = subtitle, fontSize = 10.sp, color = Color.Gray) }
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange, colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF166534)))
    }
}

@Composable
fun SettingDivider() {
    HorizontalDivider(modifier = Modifier.padding(horizontal = 12.dp), thickness = 1.dp, color = Color(0xFFF3F4F6))
}
