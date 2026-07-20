package com.fadhil.taba.ui.dashboard.materi

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fadhil.taba.data.model.Module
import com.fadhil.taba.ui.theme.GreenPrimary
import com.fadhil.taba.data.settings.AppSettingsStore
import com.fadhil.taba.data.settings.Localization

fun String.removeHarakat(): String {
    val regex = Regex("[\u064B-\u065F]")
    return this.replace(regex, "")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailMateriScreen(
    module: Module, 
    onBack: () -> Unit,
    onPracticeClick: (Module) -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val settings by AppSettingsStore.settings.collectAsState()
    val lang = settings.language
    
    // States for UI customization
    var textSizeMultiplier by rememberSaveable { mutableStateOf(1.0f) }
    var isLandscape by rememberSaveable { mutableStateOf(false) }
    
    // Harakat logic: follows global unless overridden locally
    var localHarakatOverride by rememberSaveable { mutableStateOf<Boolean?>(null) }
    val showHarakat = localHarakatOverride ?: settings.isFullHarakat
    
    var showSettings by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    fun formatArabic(text: String): String {
        return if (showHarakat) text else text.removeHarakat()
    }

    // Mengunci orientasi berdasarkan state isLandscape
    LaunchedEffect(isLandscape) {
        activity?.requestedOrientation = if (isLandscape) {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    // Kembalikan ke Portrait saat keluar dari layar ini
    DisposableEffect(Unit) {
        onDispose {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    Scaffold(
        containerColor = Color(0xFFF9F7F2),
        topBar = {
            TopAppBar(
                title = { 
                    val moduleTitle = if (lang == "en") module.titleEn else module.title
                    Text(moduleTitle, fontWeight = FontWeight.Bold) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = Localization.getString("back", lang))
                    }
                },
                actions = {
                    IconButton(onClick = { showSettings = true }) {
                        Icon(Icons.Default.Settings, contentDescription = Localization.getString("text_settings", lang))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Sembunyikan gambar saat landscape agar teks lebih luas
            if (!isLandscape) {
                Image(
                    painter = painterResource(id = module.imageResId),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Fit
                )
                
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            Text(
                text = formatArabic(module.arabicTitle),
                fontSize = (28 * textSizeMultiplier).sp,
                fontWeight = FontWeight.Bold,
                color = GreenPrimary,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 1.dp
            ) {
                Text(
                    text = formatArabic(module.content),
                    fontSize = (20 * textSizeMultiplier).sp,
                    lineHeight = (36 * textSizeMultiplier).sp,
                    color = Color.Black,
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Right
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionHeader(Localization.getString("vocabulary", lang))
                TextButton(onClick = { onPracticeClick(module) }) {
                    Text(Localization.getString("practice_now", lang), color = Color(0xFF166534), fontWeight = FontWeight.Bold)
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                module.vocabularies.take(3).forEach { vocab ->
                    val meaning = if (lang == "en") vocab.english else vocab.indonesian
                    VocabularyItem(formatArabic(vocab.arabic), meaning, textSizeMultiplier)
                }
                if (module.vocabularies.size > 3) {
                    OutlinedButton(
                        onClick = { onPracticeClick(module) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFF166534))
                    ) {
                        Text(Localization.getString("see_all_vocab", lang).format(module.vocabularies.size), color = Color(0xFF166534))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            SectionHeader(Localization.getString("questions", lang))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    module.questions.forEachIndexed { index, question ->
                        Text(
                            text = formatArabic(question),
                            fontSize = (18 * textSizeMultiplier).sp,
                            lineHeight = (30 * textSizeMultiplier).sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            textAlign = TextAlign.Right
                        )
                        if (index < module.questions.size - 1) {
                            HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showSettings) {
        ModalBottomSheet(
            onDismissRequest = { showSettings = false },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(bottom = 32.dp)
            ) {
                Text(Localization.getString("text_settings", lang), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = GreenPrimary)
                Spacer(modifier = Modifier.height(24.dp))
                
                // Ukuran Teks
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.FormatSize, contentDescription = null, tint = GreenPrimary)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(Localization.getString("text_size", lang), modifier = Modifier.weight(1f))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { if (textSizeMultiplier > 0.8f) textSizeMultiplier -= 0.1f }) {
                            Text("-", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                        Text("${(textSizeMultiplier * 100).toInt()}%", fontWeight = FontWeight.Bold)
                        IconButton(onClick = { if (textSizeMultiplier < 2.0f) textSizeMultiplier += 0.1f }) {
                            Text("+", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Harakat
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(Localization.getString("show_harakat", lang), modifier = Modifier.weight(1f))
                    Switch(
                        checked = showHarakat,
                        onCheckedChange = { localHarakatOverride = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = GreenPrimary)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Layout / Orientation
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ScreenRotation, contentDescription = null, tint = GreenPrimary)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(Localization.getString("vertical_layout", lang), modifier = Modifier.weight(1f))
                    Switch(
                        checked = isLandscape,
                        onCheckedChange = { isLandscape = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = GreenPrimary)
                    )
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = GreenPrimary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    )
}

@Composable
fun VocabularyItem(arabic: String, meaning: String, textSizeMultiplier: Float = 1.0f) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = meaning, fontSize = (14 * textSizeMultiplier).sp, color = Color.Gray, modifier = Modifier.weight(1f))
            Text(text = arabic, fontSize = (18 * textSizeMultiplier).sp, fontWeight = FontWeight.Bold, color = GreenPrimary, textAlign = TextAlign.Right)
        }
    }
}
