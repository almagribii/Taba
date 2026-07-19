package com.fadhil.taba.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
fun MateriScreen(
    onBack: () -> Unit,
    onModuleClick: (Module) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F7F2))
    ) {

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item(span = { GridItemSpan(2) }) {
                MateriBanner()
            }

            item(span = { GridItemSpan(2) }) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Cari materi...", color = Color.Gray) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = Color(0xFFF3F4F6),
                            unfocusedBorderColor = Color(0xFFF3F4F6)
                        )
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Surface(
                        modifier = Modifier
                            .size(52.dp)
                            .clickable { },
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFF3F4F6))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.FilterList, contentDescription = "Filter", tint = GreenPrimary)
                        }
                    }
                }
            }

            // Grid Module Cards
            items(ModuleData.modules) { module ->
                ModuleCardNew(module = module, onClick = { onModuleClick(module) })
            }
        }
    }
}

@Composable
fun MateriBanner() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFE6EFE9), Color(0xFFD4E2D9))
                    )
                )
        ) {
            // Background Mosque Illustration
            Image(
                painter = painterResource(id = R.drawable.splash),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(end = 16.dp),
                contentScale = ContentScale.Crop,
                alpha = 0.15f,
                alignment = Alignment.CenterEnd
            )
            
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circle Star Icon
                Surface(
                    modifier = Modifier.size(60.dp),
                    shape = CircleShape,
                    color = GreenPrimary
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFDE68A), modifier = Modifier.size(30.dp))
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = "6 Materi Interaktif",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = GreenPrimary
                    )
                    Text(
                        text = "Latihan kosakata + hiwar + pelafalan",
                        fontSize = 13.sp,
                        color = GreenPrimary.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun ModuleCardNew(module: Module, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 1.dp,
        border = BorderStroke(1.dp, Color(0xFFF3F4F6))
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Image with Badge on Top Right
                Box(
                    modifier = Modifier.size(85.dp)
                ) {
                    // Illustration
                    Image(
                        painter = painterResource(id = module.imageResId),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp),
                        contentScale = ContentScale.Fit
                    )
                    
                    // Number Circle Badge (Top Left of the Image)
                    Surface(
                        color = Color(0xFFFDE68A),
                        shape = CircleShape,
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.TopStart),
                        border = BorderStroke(2.dp, Color.White)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = module.id.toString(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GreenPrimary
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.width(12.dp))

                // Titles (Right Side - Horizontally centered in their section)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = module.arabicTitle,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = GreenPrimary,
                        maxLines = 1,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Text(
                        text = "Di ${module.title}",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        maxLines = 1,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Progress Section
            Text(
                text = "${(1..4).random()}/5 latihan selesai",
                fontSize = 10.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                LinearProgressIndicator(
//                    progress = { 0.6f },
//                    modifier = Modifier
//                        .weight(1f)
//                        .height(6.dp)
//                        .clip(CircleShape),
//                    color = Color(0xFF166534),
//                    trackColor = Color(0xFFF3F4F6)
//                )
//                
//                Spacer(modifier = Modifier.width(8.dp))
//                
//                Surface(
//                    modifier = Modifier.size(24.dp),
//                    shape = CircleShape,
//                    color = Color(0xFFF3F4F6)
//                ) {
//                    Box(contentAlignment = Alignment.Center) {
//                        Icon(Icons.Default.ChevronRight, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
//                    }
//                }
//            }
        }
    }
}
