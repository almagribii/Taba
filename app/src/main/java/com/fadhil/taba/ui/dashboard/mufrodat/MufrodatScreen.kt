package com.fadhil.taba.ui.dashboard.mufrodat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.fadhil.taba.data.model.ModuleVocabulary
import com.fadhil.taba.ui.theme.GreenPrimary
import com.fadhil.taba.data.settings.AppSettingsStore
import com.fadhil.taba.data.settings.Localization
import com.fadhil.taba.ui.dashboard.materi.removeHarakat

@Composable
fun MufrodatScreen(
    initialModule: Module? = null,
    onBack: () -> Unit = {},
    headerTitle: String = "Al-Mufradat",
    headerSubtitle: String = "الْمُفْرَدَاتُ",
    materialsLabel: String = "Materi",
    practiceTitle: String = "Ucapkan kata ini",
    practiceSubtitle: String = "Tekan tombol mic dan ucapkan kata di atas",
    otherVocabHeadingTemplate: String = "Kosakata Lainnya di %s"
) {
    val settings by AppSettingsStore.settings.collectAsState()
    val lang = settings.language
    val showHarakat = settings.isFullHarakat
    
    val module = initialModule ?: ModuleData.modules[0]
    var currentVocabIndex by remember { mutableStateOf(0) }
    val currentVocab = module.vocabularies[currentVocabIndex]
    
    val backgroundColor = Color(0xFFF9F7F2)

    fun formatArabic(text: String): String = if (showHarakat) text else text.removeHarakat()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .verticalScroll(rememberScrollState())
    ) {
        MufrodatHeader(
            module = module,
            onBack = onBack,
            headerTitle = settings.mufrodatTitle,
            headerSubtitle = formatArabic(settings.mufrodatSubtitle),
            materialsLabel = settings.mufrodatMaterialsLabel,
            lang = lang,
            formatArabic = ::formatArabic
        )
        Spacer(modifier = Modifier.height(16.dp))
        MufrodatPracticeCard(
            vocab = currentVocab,
            title = settings.mufrodatPracticeTitle,
            subtitle = settings.mufrodatPracticeSubtitle,
            lang = lang,
            formatArabic = ::formatArabic,
            onNext = {
                if (currentVocabIndex < module.vocabularies.size - 1) {
                    currentVocabIndex++
                } else {
                    currentVocabIndex = 0
                }
            }
        )
        Spacer(modifier = Modifier.height(24.dp))
        AIFeedbackSection(lang)
        Spacer(modifier = Modifier.height(24.dp))
        OtherVocabSection(
            module = module,
            currentVocabIndex = currentVocabIndex,
            headingTemplate = settings.otherVocabHeadingTemplate,
            lang = lang,
            formatArabic = ::formatArabic,
            onVocabClick = { index -> currentVocabIndex = index }
        )
        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
fun MufrodatHeader(
    module: Module,
    onBack: () -> Unit,
    headerTitle: String,
    headerSubtitle: String,
    materialsLabel: String,
    lang: String,
    formatArabic: (String) -> String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(40.dp).background(Color(0xFFF0F2EE), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = Localization.getString("back", lang), tint = GreenPrimary)
            }
            
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = headerTitle, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = GreenPrimary)
                Text(text = headerSubtitle, fontSize = 18.sp, color = GreenPrimary.copy(alpha = 0.7f))
            }

            Box {
                IconButton(
                    onClick = { },
                    modifier = Modifier.size(44.dp).background(GreenPrimary, CircleShape)
                ) {
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.White)
                }
                Box(modifier = Modifier.size(10.dp).background(Color(0xFFEAB308), CircleShape).border(2.dp, Color.White, CircleShape).align(Alignment.TopEnd))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            color = Color.White,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.wrapContentWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.MenuBook, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                val moduleTitle = if (lang == "en") module.titleEn else module.title
                Text(
                    text = "$materialsLabel: ${formatArabic(module.arabicTitle)} / ${if (lang == "en") "In" else "Di"} $moduleTitle",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun MufrodatPracticeCard(
    vocab: ModuleVocabulary,
    title: String,
    subtitle: String,
    lang: String,
    formatArabic: (String) -> String,
    onNext: () -> Unit
) {
    Surface(
        modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = painterResource(id = vocab.imageResId ?: R.drawable.materi),
                    contentDescription = null,
                    modifier = Modifier.size(140.dp).clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
                
                Spacer(modifier = Modifier.width(20.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconButton(onClick = { }, modifier = Modifier.align(Alignment.End)) {
                        Icon(Icons.Default.StarBorder, contentDescription = null, tint = Color.Gray)
                    }
                    Text(text = formatArabic(vocab.arabic), fontSize = 36.sp, fontWeight = FontWeight.Bold, color = GreenPrimary)
                    Text(text = if (lang == "en") vocab.english else vocab.indonesian, fontSize = 18.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { },
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF0F2EE)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.VolumeUp, contentDescription = null, tint = GreenPrimary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(Localization.getString("listen", lang), color = GreenPrimary, fontSize = 12.sp)
                }

                Button(
                    onClick = { },
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF0F2EE)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Mic, contentDescription = null, tint = GreenPrimary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(Localization.getString("speak", lang), color = GreenPrimary, fontSize = 12.sp)
                }

                Button(
                    onClick = onNext,
                    modifier = Modifier.weight(1.2f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(Localization.getString("next", lang), color = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier.fillMaxWidth().background(Color(0xFFF9FAFB), RoundedCornerShape(16.dp)).padding(16.dp)
            ) {
                Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GreenPrimary)
                Text(text = subtitle, fontSize = 10.sp, color = Color.Gray)
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { },
                        modifier = Modifier.size(44.dp).background(GreenPrimary, CircleShape)
                    ) {
                        Icon(Icons.Default.Mic, contentDescription = null, tint = Color.White)
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Box(modifier = Modifier.weight(1f).height(30.dp), contentAlignment = Alignment.Center) {
                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            repeat(15) {
                                Box(modifier = Modifier.width(3.dp).height((10..30).random().dp).background(GreenPrimary.copy(alpha = 0.3f), CircleShape))
                            }
                        }
                    }
                    
                    Text(text = "00:03 / 00:05", fontSize = 10.sp, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun AIFeedbackSection(lang: String) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFFF0FDF4),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDCFCE7))
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.SmartToy, contentDescription = null, tint = Color(0xFF166534), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = Localization.getString("ai_feedback", lang), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF166534))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "${Localization.getString("pronunciation", lang)} 88% - ${if (lang == "en") "Good" else "Baik"}", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = GreenPrimary)
                    Text(text = if (lang == "en") "Clarify the sound of letter رَ" else "Perjelas bunyi huruf رَ", fontSize = 12.sp, color = Color.Gray)
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = Localization.getString("ai_note", lang), fontSize = 9.sp, color = Color.Gray)
                    }
                }
                
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(60.dp)) {
                    CircularProgressIndicator(
                        progress = { 0.88f },
                        modifier = Modifier.size(60.dp),
                        color = Color(0xFF166534),
                        trackColor = Color(0xFFDCFCE7),
                        strokeWidth = 6.dp,
                        strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                    )
                    Text(text = "88%", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF166534))
                }
            }
        }
    }
}

@Composable
fun OtherVocabSection(
    module: Module,
    currentVocabIndex: Int,
    headingTemplate: String,
    lang: String,
    formatArabic: (String) -> String,
    onVocabClick: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = headingTemplate.format(formatArabic(module.arabicTitle)), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = GreenPrimary)
            Text(text = Localization.getString("see_all", lang), fontSize = 12.sp, color = Color.Gray, modifier = Modifier.clickable { })
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(module.vocabularies.size) { index ->
                val vocab = module.vocabularies[index]
                VocabMiniCard(
                    vocab = vocab,
                    lang = lang,
                    formatArabic = formatArabic,
                    isSelected = index == currentVocabIndex,
                    onClick = { onVocabClick(index) }
                )
            }
        }
    }
}

@Composable
fun VocabMiniCard(vocab: ModuleVocabulary, lang: String, formatArabic: (String) -> String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.size(width = 110.dp, height = 140.dp).clickable { onClick() },
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, GreenPrimary) else null,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = formatArabic(vocab.arabic), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = GreenPrimary)
            Image(
                painter = painterResource(id = vocab.imageResId ?: R.drawable.materi),
                contentDescription = null,
                modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Text(text = if (lang == "en") vocab.english else vocab.indonesian, fontSize = 11.sp, color = Color.Gray)
        }
    }
}
