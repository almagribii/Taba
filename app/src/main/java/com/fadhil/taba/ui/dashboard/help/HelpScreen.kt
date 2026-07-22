package com.fadhil.taba.ui.dashboard.help

import android.widget.TextView
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import android.app.Activity
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.core.graphics.toColorInt
import androidx.core.text.HtmlCompat
import com.fadhil.taba.ui.theme.GoldAccent
import com.fadhil.taba.ui.theme.GreenPrimary

data class HelpTopic(
    val title: String,
    val htmlContent: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(
    username: String,
    avatarPath: String?,
    lang: String,
    onBack: () -> Unit
) {
    val helpTopics = remember {
        listOf(
            HelpTopic(
                title = "Memulai",
                htmlContent = "1. Masuk dengan akun <b>Google</b> pada layar Welcome.<br>2. Setelah berhasil masuk, Anda akan diarahkan ke <b>Beranda</b>.",
                icon = Icons.AutoMirrored.Filled.Login
            ),
            HelpTopic(
                title = "Navigasi Utama",
                htmlContent = "• <b>Beranda</b>: Ikhtisar materi dan banner.<br>• <b>Materi</b>: Daftar modul pelajaran.<br>• <b>Tanya AI</b>: Bertanya atau berlatih percakapan interaktif.<br>• <b>Pengaturan</b>: Mengubah preferensi dan bantuan.",
                icon = Icons.Default.Navigation
            ),
            HelpTopic(
                title = "Fitur Penting",
                htmlContent = "• <b>Latihan Mufrodat</b>: Pelajaran kosakata interaktif.<br>• <b>Al-Hiwar</b>: Latihan percakapan berbasis AI.<br>• <b>Pengaturan Suara</b>: Atur kecepatan audio di halaman Pengaturan.",
                icon = Icons.Default.Star
            ),
            HelpTopic(
                title = "Masalah Umum",
                htmlContent = "• <b>Tidak bisa masuk</b>: Periksa koneksi internet.<br>• <b>Audio tidak berbunyi</b>: Periksa volume perangkat.<br>• <b>AI tidak merespons</b>: Pastikan internet stabil.",
                icon = Icons.Default.ErrorOutline
            ),
            HelpTopic(
                title = "Beri Masukan",
                htmlContent = "Kirimkan saran atau laporan bug melalui email developer yang tertera di halaman <b>Tentang Aplikasi</b>.",
                icon = Icons.Default.Feedback
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GreenPrimary)
    ) {
        // set status bar to match header
        val view = LocalView.current
        if (!view.isInEditMode) {
            SideEffect {
                val window = (view.context as Activity).window
                window.statusBarColor = GreenPrimary.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            }
        }

        // Hero Header - three-tone angled stripes
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {
            // base band
            Box(modifier = Modifier
                .matchParentSize()
                .background(GreenPrimary))

            // three angled stripes drawn on Canvas
            androidx.compose.foundation.Canvas(modifier = Modifier
                .matchParentSize()) {
                val w = size.width
                val h = size.height

                // left dominant stripe
                val p1 = androidx.compose.ui.graphics.Path().apply {
                    moveTo(0f, 0f)
                    lineTo(w * 0.65f, 0f)
                    lineTo(w * 0.45f, h)
                    lineTo(0f, h)
                    close()
                }
                drawPath(path = p1, color = GreenPrimary.copy(alpha = 0.95f))

                // center thinner stripe
                val p2 = androidx.compose.ui.graphics.Path().apply {
                    moveTo(w * 0.5f, 0f)
                    lineTo(w * 0.9f, 0f)
                    lineTo(w * 0.7f, h)
                    lineTo(w * 0.3f, h)
                    close()
                }
                drawPath(path = p2, color = GreenPrimary.copy(alpha = 0.8f))

                // right accent chevron
                val p3 = androidx.compose.ui.graphics.Path().apply {
                    moveTo(w * 0.75f, 0f)
                    lineTo(w, 0f)
                    lineTo(w, h)
                    lineTo(w * 0.6f, h)
                    close()
                }
                drawPath(path = p3, color = GreenPrimary.copy(alpha = 0.6f))
            }

            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(text = "Bantuan", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                    Text(text = "Panduan", color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
                }
            }
        }

        // White content Surface placed below header with small overlap
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
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 18.dp)
            ) {
                helpTopics.forEach { topic ->
                    HelpTopicCard(topic)
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = onBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                ) {
                    Text(text = "Kembali ke Pengaturan", fontWeight = FontWeight.Bold)
                }
                
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun HelpTopicCard(topic: HelpTopic) {
    var expanded by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = GreenPrimary.copy(alpha = 0.05f),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            topic.icon,
                            contentDescription = null,
                            tint = GreenPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = topic.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937),
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color(0xFFF3F4F6))
                Spacer(modifier = Modifier.height(12.dp))
                HtmlText(html = topic.htmlContent)
            }
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
