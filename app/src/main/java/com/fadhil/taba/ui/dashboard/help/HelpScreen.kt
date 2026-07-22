package com.fadhil.taba.ui.dashboard.help

import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.toColorInt
import androidx.core.text.HtmlCompat
import com.fadhil.taba.data.settings.Localization
import com.fadhil.taba.ui.dashboard.TabaHeader
import com.fadhil.taba.ui.theme.GreenPrimary

@Composable
fun HelpScreen(
    username: String,
    avatarPath: String?,
    lang: String,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GreenPrimary)
    ) {
        TabaHeader(
            title = if (lang == "en") "Help Center" else "Pusat Bantuan",
            subtitle = if (lang == "en") "User Guide" else "Panduan Pengguna",
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
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                // Intro Section
                HelpSection(
                    title = if (lang == "en") "Welcome to Taba" else "Selamat Datang di Taba",
                    icon = Icons.Default.WavingHand,
                    htmlContent = if (lang == "en") {
                        "Hello <b>$username</b>! Taba is designed to help you learn Arabic interactively. Follow this guide to get the most out of your learning journey."
                    } else {
                        "Halo <b>$username</b>! Taba dirancang untuk membantu Anda belajar bahasa Arab secara interaktif. Ikuti panduan ini untuk memaksimalkan perjalanan belajar Anda."
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Getting Started
                HelpSection(
                    title = if (lang == "en") "Getting Started" else "Memulai",
                    icon = Icons.Default.RocketLaunch,
                    htmlContent = if (lang == "en") {
                        "1. Sign in with your <b>Google Account</b> on the Welcome screen.<br>2. Once logged in, you will be taken to the <b>Home</b> dashboard where you can see your learning overview."
                    } else {
                        "1. Masuk dengan <b>Akun Google</b> Anda pada layar Selamat Datang.<br>2. Setelah masuk, Anda akan diarahkan ke <b>Beranda</b> untuk melihat ringkasan pembelajaran Anda."
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Core Navigation
                HelpSection(
                    title = if (lang == "en") "Core Navigation" else "Navigasi Utama",
                    icon = Icons.Default.Explore,
                    htmlContent = if (lang == "en") {
                        "• <b>Home</b>: Overview of materials and featured banners.<br>" +
                        "• <b>Lessons</b>: List of all interactive modules. Tap a module to start practicing.<br>" +
                        "• <b>Ask AI</b>: Chat freely or practice interactive conversations with AI.<br>" +
                        "• <b>Settings</b>: Change app language, audio speed, and profile info."
                    } else {
                        "• <b>Beranda</b>: Ikhtisar materi dan banner pilihan.<br>" +
                        "• <b>Materi</b>: Daftar semua modul interaktif. Ketuk modul untuk mulai berlatih.<br>" +
                        "• <b>Tanya AI</b>: Mengobrol bebas atau berlatih percakapan interaktif dengan AI.<br>" +
                        "• <b>Pengaturan</b>: Ubah bahasa aplikasi, kecepatan audio, dan info profil."
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Key Features
                HelpSection(
                    title = if (lang == "en") "Key Features" else "Fitur Unggulan",
                    icon = Icons.AutoMirrored.Filled.MenuBook,
                    htmlContent = if (lang == "en") {
                        "• <b>Vocabulary (Mufrodat)</b>: Interactive cards with audio and pronunciation checking.<br>" +
                        "• <b>Conversation (Al-Hiwar)</b>: Practice real-life scenarios with AI assistance.<br>" +
                        "• <b>Audio Settings</b>: Adjust voice gender and playback speed to suit your pace."
                    } else {
                        "• <b>Kosakata (Mufrodat)</b>: Kartu interaktif dengan audio dan pengecekan pelafalan.<br>" +
                        "• <b>Percakapan (Al-Hiwar)</b>: Berlatih skenario dunia nyata dengan bantuan AI.<br>" +
                        "• <b>Pengaturan Audio</b>: Atur jenis suara dan kecepatan putar sesuai kenyamanan Anda."
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Troubleshooting
                HelpSection(
                    title = if (lang == "en") "Troubleshooting" else "Masalah & Solusi",
                    icon = Icons.Default.Info,
                    htmlContent = if (lang == "en") {
                        "• <b>Login Issue</b>: Check your internet connection and try again.<br>" +
                        "• <b>No Audio</b>: Ensure device volume is up and audio permissions are granted.<br>" +
                        "• <b>AI Unresponsive</b>: AI features require a stable internet connection."
                    } else {
                        "• <b>Gagal Masuk</b>: Periksa koneksi internet Anda dan coba lagi.<br>" +
                        "• <b>Audio Mati</b>: Pastikan volume perangkat aktif dan izin audio diberikan.<br>" +
                        "• <b>AI Tidak Merespons</b>: Fitur AI memerlukan koneksi internet yang stabil."
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Feedback
                HelpSection(
                    title = if (lang == "en") "Give Feedback" else "Beri Masukan",
                    icon = Icons.Default.Feedback,
                    htmlContent = if (lang == "en") {
                        "Have a suggestion or found a bug? Send us a message through the contact form or email the developer at the <b>About</b> page."
                    } else {
                        "Punya saran atau menemukan bug? Kirimkan pesan melalui formulir kontak atau email pengembang di halaman <b>Tentang</b>."
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = onBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                ) {
                    Text(
                        text = Localization.getString("back", lang),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}

@Composable
fun HelpSection(
    title: String,
    icon: ImageVector,
    htmlContent: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                color = GreenPrimary.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = GreenPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = GreenPrimary
            )
        }
        
        Spacer(modifier = Modifier.height(10.dp))
        
        HtmlText(
            html = htmlContent,
            modifier = Modifier.padding(start = 48.dp) // Align text with title
        )
    }
}

@Composable
fun HtmlText(
    html: String,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            TextView(context).apply {
                textSize = 14f
                setTextColor("#4B5563".toColorInt())
                setLineSpacing(10f, 1.2f)
            }
        },
        update = { textView ->
            textView.text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY)
        }
    )
}
