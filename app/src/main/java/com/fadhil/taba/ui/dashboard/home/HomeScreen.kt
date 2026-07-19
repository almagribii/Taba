package com.fadhil.taba.ui.dashboard.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fadhil.taba.R
import com.fadhil.taba.data.local.ModuleData
import com.fadhil.taba.data.model.Module
import com.fadhil.taba.ui.theme.GreenPrimary

@Composable
fun HomeScreen(
    username: String,
    onStartLearningClick: () -> Unit,
    onModuleClick: (Module) -> Unit,
    heroTitle: String = "Ayo Mulai Berbicara!",
    heroSubtitle: String = "Latih percakapan, kosakata, dan pelafalan Bahasa Arab dengan bantuan AI.",
    startButtonText: String = "Mulai Belajar",
    sectionTitle: String = "Daftar Materi",
    sectionActionText: String = "Lihat Semua >"
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        
        // Banner Hero
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .clip(RoundedCornerShape(28.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.home),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                Text(
                    text = heroTitle,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = heroSubtitle,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .width(200.dp)
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                Button(
                    onClick = onStartLearningClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEAD8B1)),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = startButtonText, color = GreenPrimary, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = GreenPrimary)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Title Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = sectionTitle,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = GreenPrimary
            )
            TextButton(onClick = onStartLearningClick) {
                Text(text = sectionActionText, color = Color.Gray, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Preview Module List
        ModuleData.modules.take(6).forEach { module ->
            HomeModuleCard(module = module, onClick = { onModuleClick(module) })
            Spacer(modifier = Modifier.height(12.dp))
        }
        
        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
fun HomeModuleCard(module: Module, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFF3F4F6))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(70.dp)) {
                Image(
                    painter = painterResource(id = module.imageResId),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
                Surface(
                    color = Color(0xFFFDE68A),
                    shape = CircleShape,
                    modifier = Modifier.size(20.dp).align(Alignment.TopStart),
                    border = BorderStroke(1.5.dp, Color.White)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = module.id.toString(), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = GreenPrimary)
                    }
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = module.arabicTitle,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = GreenPrimary
                )
                Text(
                    text = "Di ${module.title}",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
            
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
        }
    }
}
