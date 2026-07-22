package com.fadhil.taba.ui.dashboard.materi

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.ChevronRight
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fadhil.taba.data.model.Module
import com.fadhil.taba.ui.theme.GreenPrimary
import com.fadhil.taba.data.settings.AppSettingsStore
import com.fadhil.taba.data.settings.Localization
import com.fadhil.taba.ui.dashboard.TabaHeader
import com.fadhil.taba.ui.dashboard.mufrodat.MufrodatViewModel
import com.fadhil.taba.ui.dashboard.mufrodat.VocabMiniCard

fun String.removeHarakat(): String {
    val regex = Regex("[\u064B-\u065F]")
    return this.replace(regex, "")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailMateriScreen(
    module: Module, 
    onBack: () -> Unit,
    onPracticeClick: (Module) -> Unit,
    onHiwarClick: (Module) -> Unit,
    mufrodatViewModel: MufrodatViewModel = viewModel()
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val settings by AppSettingsStore.settings.collectAsState()
    val lang = settings.language
    
    var textSizeMultiplier by rememberSaveable { mutableStateOf(1.0f) }
    var isLandscape by rememberSaveable { mutableStateOf(false) }
    
    var localHarakatOverride by rememberSaveable { mutableStateOf<Boolean?>(null) }
    val showHarakat = localHarakatOverride ?: settings.isFullHarakat
    
    var showSettings by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    val vocabList = remember(module, settings.starredVocabKeys) {
        module.vocabularies.map { vocab ->
            vocab.copy(isStarred = settings.starredVocabKeys.contains("${module.id}_${vocab.arabic}"))
        }.sortedByDescending { it.isStarred }
    }

    fun formatArabic(text: String): String {
        return if (showHarakat) text else text.removeHarakat()
    }

    LaunchedEffect(isLandscape) {
        activity?.requestedOrientation = if (isLandscape) {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GreenPrimary)
    ) {
        TabaHeader(
            title = if (lang == "en") module.titleEn else module.title,
            onBack = onBack,
            trailingAction = {
                IconButton(onClick = { showSettings = true }) {
                    Icon(Icons.Default.Settings, contentDescription = Localization.getString("text_settings", lang), tint = Color.White)
                }
            }
        )

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = (-8).dp),
            color = Color(0xFFF9F7F2),
            shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                if (!isLandscape) {
                    Image(
                        painter = painterResource(id = module.imageResId),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Fit
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                Text(
                    text = formatArabic(module.arabicTitle),
                    fontSize = (28 * textSizeMultiplier).sp,
                    fontWeight = FontWeight.Bold,
                    color = GreenPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                
                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
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
                
                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    color = Color(0xFFF0FDF4),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFFDCFCE7)),
                    onClick = { onHiwarClick(module) }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, tint = Color(0xFF166534))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            val hiwarTitle = Localization.getString("hiwar_title", lang)
                            val hiwarLabel = if (lang == "en") "(Conversation Practice)" else "(Latihan Percakapan)"
                            Text("$hiwarTitle $hiwarLabel", fontWeight = FontWeight.Bold, color = Color(0xFF166534), fontSize = 14.sp)
                            Text(Localization.getString("hiwar_subtitle", lang), color = Color(0xFF166534).copy(alpha = 0.7f), fontSize = 11.sp)
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF166534))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SectionHeader(Localization.getString("vocabulary", lang), modifier = Modifier.weight(1f))
                    TextButton(
                        onClick = { onPracticeClick(module) },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(Localization.getString("practice_now", lang), color = Color(0xFF166534), fontWeight = FontWeight.Bold)
                    }
                }
                
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(vocabList) { vocab ->
                        VocabMiniCard(
                            vocab = vocab,
                            lang = lang,
                            formatArabic = ::formatArabic,
                            isSelected = false,
                            onClick = { onPracticeClick(module) },
                            onStarClick = {
                                AppSettingsStore.toggleStar(context, module.id, vocab.arabic)
                            },
                            onVoiceClick = {
                                mufrodatViewModel.playVoice(vocab.arabic)
                            }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                SectionHeader(
                    title = Localization.getString("questions", lang), 
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    shadowElevation = 1.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        module.questions.forEachIndexed { index, question ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            ) {
                                Text(
                                    text = formatArabic(question.arabic),
                                    fontSize = (18 * textSizeMultiplier).sp,
                                    lineHeight = (30 * textSizeMultiplier).sp,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Right
                                )
                                Text(
                                    text = if (lang == "en") question.english else question.indonesian,
                                    fontSize = (14 * textSizeMultiplier).sp,
                                    color = Color.Gray,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Left
                                )
                            }
                            if (index < module.questions.size - 1) {
                                HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)
                            }
                        }
                    }
                }
            }
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
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(Localization.getString("show_harakat", lang), modifier = Modifier.weight(1f))
                    Switch(
                        checked = showHarakat,
                        onCheckedChange = { localHarakatOverride = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = GreenPrimary)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
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
fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = GreenPrimary,
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    )
}
