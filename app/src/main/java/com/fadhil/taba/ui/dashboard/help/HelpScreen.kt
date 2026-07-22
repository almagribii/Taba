package com.fadhil.taba.ui.dashboard.help

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

@Composable
fun HelpScreen(
    username: String,
    avatarPath: String?,
    lang: String,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text(text = "Bantuan", color = GreenPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.Transparent)
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
                    // Fallback ilustrasi kalau resource ada
                    val resId = try { R.drawable.help_illustration } catch (e: Exception) { 0 }
                    if (resId != 0) Image(painter = painterResource(id = resId), contentDescription = null, modifier = Modifier.size(96.dp))
                    Spacer(modifier = Modifier.width(12.dp))
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