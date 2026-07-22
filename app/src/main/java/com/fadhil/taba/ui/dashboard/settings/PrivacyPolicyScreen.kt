package com.fadhil.taba.ui.dashboard.settings

import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.toColorInt
import androidx.core.text.HtmlCompat
import com.fadhil.taba.data.settings.AppSettingsStore
import com.fadhil.taba.data.settings.Localization
import com.fadhil.taba.ui.dashboard.TabaHeader
import com.fadhil.taba.ui.theme.GreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(
    onBack: () -> Unit
) {
    val settings by AppSettingsStore.settings.collectAsState()
    val lang = settings.language
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GreenPrimary)
    ) {
        TabaHeader(
            title = Localization.getString("privacy_policy", lang),
            onBack = onBack
        )

        // Content
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
                    .padding(horizontal = 16.dp, vertical = 18.dp)
            ) {
                PrivacySection(
                    title = if (lang == "en") "1. Introduction" else "1. Pendahuluan",
                    htmlContent = if (lang == "en") {
                        "Welcome to <b>Taba</b>. We are committed to protecting your personal information and your right to privacy. This policy explains how we handle your data when using our app."
                    } else {
                        "Selamat datang di <b>Taba</b>. Kami berkomitmen untuk melindungi informasi pribadi Anda dan hak privasi Anda. Kebijakan ini menjelaskan bagaimana kami mengelola data Anda."
                    }
                )

                PrivacySection(
                    title = if (lang == "en") "2. Information We Collect" else "2. Informasi yang Kami Kumpulkan",
                    htmlContent = if (lang == "en") {
                        "We collect information via <b>Supabase Auth</b>, including:<br>• Name and email address<br>• Profile picture<br>• Learning progress (XP, level, history)."
                    } else {
                        "Kami mengumpulkan informasi melalui <b>Supabase Auth</b>, termasuk:<br>• Nama dan alamat email<br>• Foto profil<br>• Progres belajar (XP, level, riwayat)."
                    }
                )

                PrivacySection(
                    title = if (lang == "en") "3. Device Permissions" else "3. Izin Perangkat",
                    htmlContent = if (lang == "en") {
                        "• <b>Microphone</b>: Used for speech recognition during <i>Al-Hiwar</i> practice.<br>• <b>Internet</b>: Necessary for data sync and AI feedback."
                    } else {
                        "• <b>Mikrofon</b>: Digunakan untuk pengenalan suara selama latihan <i>Al-Hiwar</i>.<br>• <b>Internet</b>: Diperlukan untuk sinkronisasi data dan umpan balik AI."
                    }
                )

                PrivacySection(
                    title = if (lang == "en") "4. Data Storage" else "4. Penyimpanan Data",
                    htmlContent = if (lang == "en") {
                        "Your data is securely stored using <b>Supabase</b> cloud infrastructure. We implement technical measures to protect your information."
                    } else {
                        "Data Anda disimpan secara aman menggunakan infrastruktur cloud <b>Supabase</b>. Kami menerapkan langkah teknis untuk melindungi informasi Anda."
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = if (lang == "en") "Last updated: July 2024" else "Terakhir diperbarui: Juli 2024",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 16.dp, start = 4.dp)
                )

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
                
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun PrivacySection(title: String, htmlContent: String) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = GreenPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))
            HtmlText(html = htmlContent)
        }
    }
}

@Composable
fun HtmlText(html: String) {
    AndroidView(
        factory = { context ->
            TextView(context).apply {
                textSize = 14f
                setTextColor("#4B5563".toColorInt())
                setLineSpacing(8f, 1f)
            }
        },
        update = { textView ->
            textView.text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY)
        }
    )
}
