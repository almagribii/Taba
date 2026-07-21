package com.fadhil.taba.ui.dashboard.mufrodat

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
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
    viewModel: MufrodatViewModel = viewModel()
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val settings by AppSettingsStore.settings.collectAsState()
    val lang = settings.language
    val showHarakat = settings.mufrodatFullHarakat
    val isLandscape = settings.mufrodatHorizontalLayout
    
    val aiFeedback by viewModel.aiFeedback.collectAsState()
    val isAiLoading by viewModel.isLoading.collectAsState()
    val currentlyPlaying by viewModel.currentlyPlayingText.collectAsState()
    
    val module = initialModule ?: ModuleData.modules[0]
    
    val vocabList = remember(module, settings.starredVocabKeys) {
        module.vocabularies.map { vocab ->
            vocab.copy(isStarred = settings.starredVocabKeys.contains("${module.id}_${vocab.arabic}"))
        }.sortedByDescending { it.isStarred }
    }
    
    var currentVocabIndex by remember { mutableStateOf(0) }
    val currentVocab = vocabList.getOrElse(currentVocabIndex) { vocabList[0] }
    
    val backgroundColor = Color(0xFFF9F7F2)
    
    var showSettingsSheet by remember { mutableStateOf(false) }

    // Sync TTS settings
    LaunchedEffect(settings.audioSpeed, settings.voiceGender) {
        viewModel.updateTtsSettings(settings.audioSpeed, settings.voiceGender)
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

    // Launcher untuk Speech Recognition
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0) ?: ""
            viewModel.checkPronunciation(currentVocab.arabic, spokenText)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar-SA")
            }
            speechLauncher.launch(intent)
        }
    }

    fun formatArabic(text: String): String = if (showHarakat) text else text.removeHarakat()

    Scaffold(
        topBar = {
            MufrodatHeader(
                title = if (lang == "en") "Vocabulary" else "Kosa Kata",
                onBack = onBack,
                lang = lang,
                onSettingsClick = { showSettingsSheet = true }
            )
        },
        containerColor = backgroundColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(backgroundColor)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Materials Info Tag (Keeping it below header as it's useful context)
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.padding(horizontal = 16.dp).wrapContentWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.MenuBook, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    val moduleTitle = if (lang == "en") module.titleEn else module.title
                    Text(
                        text = "${settings.mufrodatMaterialsLabel}: ${formatArabic(module.arabicTitle)} / ${if (lang == "en") "In" else "Di"} $moduleTitle",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            MufrodatPracticeCard(
                vocab = currentVocab,
                title = settings.mufrodatPracticeTitle,
                subtitle = settings.mufrodatPracticeSubtitle,
                lang = lang,
                formatArabic = ::formatArabic,
                onListenClick = {
                    viewModel.playVoice(currentVocab.arabic)
                },
                onSpeakClick = {
                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                },
                onStarClick = {
                    AppSettingsStore.toggleStar(context, module.id, currentVocab.arabic)
                },
                onNext = {
                    if (currentVocabIndex < vocabList.size - 1) {
                        currentVocabIndex++
                        viewModel.resetFeedback()
                    } else {
                        currentVocabIndex = 0
                        viewModel.resetFeedback()
                    }
                },
                isPlaying = currentlyPlaying == currentVocab.arabic
            )
            Spacer(modifier = Modifier.height(24.dp))
            
            if (isAiLoading) {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = GreenPrimary)
                }
            }
            
            aiFeedback?.let { feedback ->
                AIFeedbackSection(lang, feedback)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            OtherVocabSection(
                vocabularies = vocabList,
                arabicTitle = formatArabic(module.arabicTitle),
                currentVocabIndex = currentVocabIndex,
                headingTemplate = settings.otherVocabHeadingTemplate,
                lang = lang,
                formatArabic = ::formatArabic,
                onVocabClick = { index -> 
                    currentVocabIndex = index 
                    viewModel.resetFeedback()
                },
                currentlyPlayingText = currentlyPlaying,
                onVoiceClick = { viewModel.playVoice(it) },
                onStarClick = { AppSettingsStore.toggleStar(context, module.id, it) }
            )
            
            if (showSettingsSheet) {
                MufrodatSettingsBottomSheet(
                    settings = settings,
                    onDismiss = { showSettingsSheet = false },
                    onSpeedChange = { AppSettingsStore.setAudioSpeed(context, it) },
                    onGenderChange = { AppSettingsStore.setVoiceGender(context, it) },
                    onHarakatChange = { AppSettingsStore.setMufrodatFullHarakat(context, it) },
                    onLayoutChange = { AppSettingsStore.setMufrodatHorizontalLayout(context, it) },
                    lang = lang
                )
            }
            
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MufrodatHeader(
    title: String,
    onBack: () -> Unit,
    lang: String,
    onSettingsClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(title, fontWeight = FontWeight.Bold)
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = Localization.getString("back", lang))
            }
        },
        actions = {
            IconButton(onClick = onSettingsClick) {
                Icon(Icons.Default.Settings, contentDescription = Localization.getString("text_settings", lang))
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White
        )
    )
}


@Composable
fun MufrodatPracticeCard(
    vocab: ModuleVocabulary,
    title: String,
    subtitle: String,
    lang: String,
    formatArabic: (String) -> String,
    onListenClick: () -> Unit,
    onSpeakClick: () -> Unit,
    onStarClick: () -> Unit,
    onNext: () -> Unit,
    isPlaying: Boolean = false
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
                    painter = painterResource(id = R.drawable.kuda),
                    contentDescription = null,
                    modifier = Modifier.size(140.dp).clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
                
                Spacer(modifier = Modifier.width(20.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconButton(onClick = onStarClick, modifier = Modifier.align(Alignment.End)) {
                        Icon(
                            imageVector = if (vocab.isStarred) Icons.Default.Star else Icons.Default.StarBorder, 
                            contentDescription = "Favorite", 
                            tint = if (vocab.isStarred) Color(0xFFEAB308) else Color.Gray
                        )
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
                    onClick = onListenClick,
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if (isPlaying) GreenPrimary.copy(alpha = 0.1f) else Color(0xFFF0F2EE)),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    Icon(if (isPlaying) Icons.Default.Stop else Icons.Default.VolumeUp, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isPlaying) "Stop" else Localization.getString("listen", lang),
                        color = GreenPrimary,
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                }

                Button(
                    onClick = onSpeakClick,
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF0F2EE)),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    Icon(Icons.Default.Mic, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = Localization.getString("speak", lang),
                        color = GreenPrimary,
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                }

                Button(
                    onClick = onNext,
                    modifier = Modifier.weight(1.2f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Text(
                        text = Localization.getString("next", lang),
                        color = Color.White,
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
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
                        onClick = onSpeakClick,
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
fun AIFeedbackSection(lang: String, data: AIFeedbackData) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFFF0FDF4),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDCFCE7))
        ) {
            Row(
                modifier = Modifier.padding(16.dp), 
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.SmartToy, contentDescription = null, tint = Color(0xFF166534), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = Localization.getString("ai_feedback", lang), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF166534))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    val qualityText = when {
                        data.score >= 90 -> if (lang == "en") "Excellent!" else "Sangat Baik!"
                        data.score >= 70 -> if (lang == "en") "Good Job" else "Bagus"
                        else -> if (lang == "en") "Keep Practicing" else "Perlu Latihan"
                    }
                    
                    Text(text = qualityText, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = GreenPrimary)
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = data.feedback, fontSize = 13.sp, color = Color.DarkGray, lineHeight = 18.sp)
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = Color(0xFFDCFCE7),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Color(0xFF166534), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = data.tips, fontSize = 11.sp, color = Color(0xFF166534), fontWeight = FontWeight.Medium)
                        }
                    }
                }
                
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(64.dp)) {
                    CircularProgressIndicator(
                        progress = { data.score / 100f },
                        modifier = Modifier.size(64.dp),
                        color = if (data.score >= 70) Color(0xFF166534) else Color(0xFFB91C1C),
                        trackColor = Color(0xFFDCFCE7),
                        strokeWidth = 6.dp,
                        strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                    )
                    Text(text = "${data.score}%", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF166534))
                }
            }
        }
    }
}

@Composable
fun OtherVocabSection(
    vocabularies: List<ModuleVocabulary>,
    arabicTitle: String,
    currentVocabIndex: Int,
    headingTemplate: String,
    lang: String,
    formatArabic: (String) -> String,
    onVocabClick: (Int) -> Unit,
    currentlyPlayingText: String? = null,
    onVoiceClick: ((String) -> Unit)? = null,
    onStarClick: ((String) -> Unit)? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = headingTemplate.format(arabicTitle), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = GreenPrimary)
            Text(text = Localization.getString("see_all", lang), fontSize = 12.sp, color = Color.Gray, modifier = Modifier.clickable { })
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(vocabularies.size) { index ->
                val vocab = vocabularies[index]
                VocabMiniCard(
                    vocab = vocab,
                    lang = lang,
                    formatArabic = formatArabic,
                    isSelected = index == currentVocabIndex,
                    onClick = { onVocabClick(index) },
                    isPlaying = currentlyPlayingText == vocab.arabic,
                    onVoiceClick = { onVoiceClick?.invoke(vocab.arabic) },
                    onStarClick = { onStarClick?.invoke(vocab.arabic) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MufrodatSettingsBottomSheet(
    settings: com.fadhil.taba.data.settings.AppSettings,
    onDismiss: () -> Unit,
    onSpeedChange: (Float) -> Unit,
    onGenderChange: (String) -> Unit,
    onHarakatChange: (Boolean) -> Unit,
    onLayoutChange: (Boolean) -> Unit,
    lang: String
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        sheetState = rememberModalBottomSheetState()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp, start = 24.dp, end = 24.dp)
        ) {
            Text(
                text = Localization.getString("learning_preferences", lang),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = GreenPrimary
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Audio Speed
            Text(
                text = Localization.getString("audio_speed", lang),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val speeds = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f)
                speeds.forEach { speed ->
                    FilterChip(
                        selected = settings.audioSpeed == speed,
                        onClick = { onSpeedChange(speed) },
                        label = { Text("${speed}x", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GreenPrimary.copy(alpha = 0.1f),
                            selectedLabelColor = GreenPrimary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = settings.audioSpeed == speed,
                            borderColor = Color.LightGray,
                            selectedBorderColor = GreenPrimary
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Voice Gender
            Text(
                text = Localization.getString("voice_gender", lang),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column {
                listOf("male", "female").forEach { gender ->
                    val label = if (gender == "male") Localization.getString("male", lang) 
                               else Localization.getString("female", lang)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onGenderChange(gender) }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = settings.voiceGender == gender,
                            onClick = null,
                            colors = RadioButtonDefaults.colors(selectedColor = GreenPrimary)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = label, fontSize = 14.sp, color = GreenPrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = Localization.getString("text_settings", lang),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = GreenPrimary
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Harakat (Specific to Mufrodat)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(Localization.getString("show_harakat", lang), modifier = Modifier.weight(1f), fontSize = 14.sp, color = Color.Gray)
                Switch(
                    checked = settings.mufrodatFullHarakat,
                    onCheckedChange = onHarakatChange,
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = GreenPrimary)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Layout / Orientation (Specific to Mufrodat)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(Localization.getString("vertical_layout", lang), modifier = Modifier.weight(1f), fontSize = 14.sp, color = Color.Gray)
                Switch(
                    checked = settings.mufrodatHorizontalLayout,
                    onCheckedChange = onLayoutChange,
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = GreenPrimary)
                )
            }
        }
    }
}

@Composable
fun VocabMiniCard(
    vocab: ModuleVocabulary, 
    lang: String, 
    formatArabic: (String) -> String, 
    isSelected: Boolean, 
    onClick: () -> Unit,
    onStarClick: (() -> Unit)? = null,
    onVoiceClick: (() -> Unit)? = null,
    isPlaying: Boolean = false
) {
    Surface(
        modifier = Modifier
            .width(165.dp)
            .height(95.dp)
            .clickable { onClick() },
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, GreenPrimary) else null,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Bagian Kiri: Gambar & Bintang "Terbang" di Kiri Atas
            Box(
                modifier = Modifier.size(60.dp),
                contentAlignment = Alignment.Center
            ) {
                // Gambar Kuda (Langsung pasang tanpa bulatan)
                Image(
                    painter = painterResource(id = R.drawable.kuda),
                    contentDescription = null,
                    modifier = Modifier.size(50.dp),
                    contentScale = ContentScale.Fit
                )
                
                // Ikon Bintang "Terbang" di KIRI ATAS gambar
                IconButton(
                    onClick = { onStarClick?.invoke() },
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.TopStart)
                        .offset(x = (-6).dp, y = (-6).dp),
                    enabled = onStarClick != null
                ) {
                    Icon(
                        imageVector = if (vocab.isStarred) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Favorite",
                        tint = if (vocab.isStarred) Color(0xFFEAB308) else Color.Gray.copy(alpha = 0.5f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Bagian Kanan: Teks Arab & Terjemahan + Ikon Suara Kecil
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = formatArabic(vocab.arabic),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = GreenPrimary,
                    maxLines = 1
                )
                Text(
                    text = if (lang == "en") vocab.english else vocab.indonesian,
                    fontSize = 11.sp,
                    color = Color.Gray,
                    maxLines = 1
                )
                
                Box(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 4.dp)
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(if (isPlaying) GreenPrimary.copy(alpha = 0.1f) else Color(0xFFF9F7F2))
                        .clickable(enabled = onVoiceClick != null) { onVoiceClick?.invoke() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.VolumeUp,
                        contentDescription = if (isPlaying) "Stop" else "Listen",
                        tint = GreenPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}
