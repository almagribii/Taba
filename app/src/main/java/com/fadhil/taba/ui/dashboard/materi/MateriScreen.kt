package com.fadhil.taba.ui.dashboard.materi

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FilterList
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
import com.fadhil.taba.data.settings.AppSettingsStore
import com.fadhil.taba.data.settings.Localization
import com.fadhil.taba.ui.dashboard.TabaHeader

@Composable
fun MateriScreen(
    onBack: () -> Unit,
    onModuleClick: (Module) -> Unit,
    bannerTitle: String = "6 Materi Interaktif",
    bannerSubtitle: String = "Latihan kosakata + hiwar + pelafalan",
    searchPlaceholder: String = "Cari materi..."
) {
    val settings by AppSettingsStore.settings.collectAsState()
    val lang = settings.language

    var searchQuery by remember { mutableStateOf("") }
    val filteredModules = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            ModuleData.modules
        } else {
            val query = searchQuery.trim().lowercase()
            ModuleData.modules.filter { module ->
                module.title.lowercase().contains(query) ||
                    module.titleEn.lowercase().contains(query) ||
                    module.arabicTitle.lowercase().contains(query)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GreenPrimary)
    ) {
        TabaHeader(
            title = Localization.getString("materi", lang),
            subtitle = "المواد الدراسية"
        )

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = (-8).dp),
            color = Color(0xFFF9F7F2),
            shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 80.dp), // Added bottom padding for bottom bar
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item(span = { GridItemSpan(2) }) {
                    MateriBanner(bannerTitle = settings.materiBannerTitle, bannerSubtitle = settings.materiBannerSubtitle)
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
                            placeholder = { Text(settings.searchPlaceholder, color = Color.Gray) },
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
                if (filteredModules.isEmpty()) {
                    item(span = { GridItemSpan(2) }) {
                        Text(
                            text = Localization.getString("no_materi_found", lang),
                            color = Color.Gray,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)
                        )
                    }
                } else {
                    items(filteredModules) { module ->
                        ModuleCardNew(module = module, lang = lang, onClick = { onModuleClick(module) })
                    }
                }
                
                item(span = { GridItemSpan(2) }) {
                }
            }
        }
    }
}

@Composable
fun MateriBanner(
    bannerTitle: String,
    bannerSubtitle: String
) {
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
            Image(
                painter = painterResource(id = R.drawable.banner_materi),
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
                        text = bannerTitle,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = GreenPrimary
                    )
                    Text(
                        text = bannerSubtitle,
                        fontSize = 13.sp,
                        color = GreenPrimary.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun ModuleCardNew(module: Module, lang: String, onClick: () -> Unit) {
    val exerciseCount = remember { (1..4).random() }
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
                Box(
                    modifier = Modifier.size(85.dp)
                ) {
                    Image(
                        painter = painterResource(id = module.imageResId),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp),
                        contentScale = ContentScale.Fit
                    )
                    
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
                    val moduleTitle = if (lang == "en") module.titleEn else module.title
                    Text(
                        text = "${if (lang == "en") "In" else "Di"} $moduleTitle",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        maxLines = 1,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
//            Text(
//                text = Localization.getString("exercises_completed", lang).format(exerciseCount),
//                fontSize = 10.sp,
//                color = Color.Gray,
//                modifier = Modifier.padding(bottom = 4.dp)
//            )
//            LinearProgressIndicator(
//                progress = { exerciseCount / 5f },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(6.dp)
//                    .clip(CircleShape),
//                color = Color(0xFF166534),
//                trackColor = Color(0xFFF3F4F6)
//            )
            
        }
    }
}
