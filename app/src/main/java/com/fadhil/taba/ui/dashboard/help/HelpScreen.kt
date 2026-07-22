package com.fadhil.taba.ui.dashboard.help

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.ContactSupport
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fadhil.taba.R
import com.fadhil.taba.ui.theme.GoldAccent
import com.fadhil.taba.ui.theme.GreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(
    username: String,
    avatarPath: String?,
    lang: String,
    onBack: () -> Unit
) {
    // Header area with accent gradient, back button sits at top-left (higher than previous)
    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .background(
                brush = Brush.verticalGradient(listOf(GreenPrimary.copy(alpha = 0.95f), GoldAccent.copy(alpha = 0.15f)))
            )) {
            IconButton(onClick = onBack, modifier = Modifier.padding(12.dp)) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }

            Column(modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 20.dp)) {
                Text(text = "Bantuan & Panduan", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(text = "Butuh bantuan? Temukan solusi cepat di sini.", color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp)
            }
        }

        // Content scrollable, overlapping the header for a modern card look
        Column(modifier = Modifier
            .fillMaxSize()
            .offset(y = (-28).dp)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)) {

            // Intro card overlapping header
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    val resId = try { R.drawable.help_illustration } catch (_: Exception) { 0 }
                    if (resId != 0) {
                        androidx.compose.foundation.Image(painter = painterResource(id = resId), contentDescription = null, modifier = Modifier.size(80.dp))
                    }
                    Column(modifier = Modifier.padding(start = 12.dp)) {
                        Text(text = "Mulai Cepat", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = GreenPrimary)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "Masuk → Beranda → Materi → Latihan. Gunakan Tanya AI untuk latihan bicara.", color = Color(0xFF4B5563))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Feature quick actions as small cards
            QuickFeatureCard(icon = Icons.Default.HelpOutline, title = "Panduan Navigasi", subtitle = "Cara menggunakan menu dan modul")
            QuickFeatureCard(icon = Icons.Default.ContactSupport, title = "Hubungi Dukungan", subtitle = "Laporkan masalah atau minta bantuan")
            QuickFeatureCard(icon = Icons.Default.BugReport, title = "Laporkan Bug", subtitle = "Laporkan gangguan atau crash")
            QuickFeatureCard(icon = Icons.Default.Feedback, title = "Kirim Masukan", subtitle = "Usulan fitur atau perbaikan")

            Spacer(modifier = Modifier.height(8.dp))

            // Sections with subtle cards
            SectionCard(title = "Memulai") {
                Text(text = "1. Masuk dengan Google.\n2. Akses Beranda untuk melihat materi.", color = Color(0xFF374151))
            }

            SectionCard(title = "Navigasi Utama") {
                Text(text = "Beranda, Materi, Tanya AI, Pengaturan — ketuk ikon di bawah untuk berpindah.", color = Color(0xFF374151))
            }

            SectionCard(title = "Fitur Penting") {
                Text(text = "Latihan Mufrodat, Al-Hiwar (percakapan AI), Pengaturan audio dan kecepatan.", color = Color(0xFF374151))
            }

            SectionCard(title = "Solusi Cepat") {
                Text(text = "Tidak bisa masuk: Periksa internet. Audio mati: cek volume/perijinan. AI tidak merespons: periksa koneksi.", color = Color(0xFF374151))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onBack, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)) {
                Text(text = "Kembali", color = Color.White)
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun QuickFeatureCard(icon: ImageVector, title: String, subtitle: String) {
    Card(shape = RoundedCornerShape(12.dp), modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 6.dp), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = RoundedCornerShape(8.dp), color = GreenPrimary.copy(alpha = 0.1f), modifier = Modifier.size(44.dp)) {
                Box(contentAlignment = Alignment.Center) { Icon(icon, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(22.dp)) }
            }
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(text = title, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
                Text(text = subtitle, color = Color(0xFF6B7280), fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(shape = RoundedCornerShape(12.dp), modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 6.dp), elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = title, fontWeight = FontWeight.Bold, color = GreenPrimary)
            Spacer(modifier = Modifier.height(6.dp))
            content()
        }
    }
}

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fadhil.taba.R
import com.fadhil.taba.ui.theme.GreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(
    username: String,
    avatarPath: String?,
    lang: String,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Bantuan", color = GreenPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Hero
            Surface(
                shape = MaterialTheme.shapes.medium,
                tonalElevation = 2.dp,
                color = Color(0xFFF9F7F2),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
                    Column {
                        Text(text = "Butuh bantuan?", fontSize = 18.sp, color = GreenPrimary)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "Panduan singkat menggunakan Taba.", fontSize = 14.sp, color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            SectionTitle("Memulai")
            SectionText("1. Masuk dengan akun Google pada layar Welcome.\n2. Setelah berhasil masuk, Anda akan diarahkan ke Beranda.")

            Spacer(modifier = Modifier.height(8.dp))

            SectionTitle("Navigasi Utama")
            SectionText("- Beranda: Ikhtisar materi dan banner.\n- Materi: Daftar modul pelajaran, ketuk modul untuk detail dan latihan.\n- Tanya AI: Bertanya atau berlatih percakapan interaktif.\n- Pengaturan: Mengubah preferensi, bahasa, dan bantuan.")

            Spacer(modifier = Modifier.height(8.dp))

            SectionTitle("Fitur Penting")
            SectionText("- Latihan Mufrodat: Pelajaran kosakata dengan latihan interaktif.\n- Al-Hiwar: Latihan percakapan berbasis AI.\n- Pengaturan suara dan kecepatan audio untuk latihan mendengarkan.")

            Spacer(modifier = Modifier.height(8.dp))

            SectionTitle("Pengaturan & Profil")
            SectionText("Ubah nama tampil, avatar, bahasa aplikasi, dan preferensi belajar di halaman Pengaturan.")

            Spacer(modifier = Modifier.height(8.dp))

            SectionTitle("Masalah Umum & Solusi")
            SectionText("- Tidak bisa masuk: Periksa koneksi internet dan coba lagi.\n- Audio tidak berbunyi: Periksa volume perangkat dan pengaturan audio dalam aplikasi.\n- Fitur AI tidak merespons: Pastikan koneksi internet stabil.")

            Spacer(modifier = Modifier.height(8.dp))

            SectionTitle("Beri Masukan")
            SectionText("Untuk saran atau laporan bug, kirimkan pesan melalui formulir kontak pada halaman profil atau email developer (lihat halaman About).")

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onBack, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)) {
                Text(text = "Kembali ke Pengaturan", color = Color.White)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(text = title, fontSize = 14.sp, color = GreenPrimary, modifier = Modifier.padding(vertical = 6.dp))
}

@Composable
private fun SectionText(text: String) {
    Text(text = text, fontSize = 13.sp, color = Color(0xFF374151), modifier = Modifier.padding(bottom = 6.dp))
}