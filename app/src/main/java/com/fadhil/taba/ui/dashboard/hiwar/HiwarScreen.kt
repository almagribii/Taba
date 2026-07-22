package com.fadhil.taba.ui.dashboard.hiwar

import android.Manifest
import android.app.Activity
import android.content.pm.ActivityInfo
import android.media.MediaRecorder
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fadhil.taba.R
import com.fadhil.taba.data.model.Module
import com.fadhil.taba.data.settings.AppSettingsStore
import com.fadhil.taba.data.settings.Localization
import com.fadhil.taba.ui.dashboard.TabaHeader
import com.fadhil.taba.ui.dashboard.materi.removeHarakat
import com.fadhil.taba.ui.dashboard.mufrodat.AIFeedbackSection
import com.fadhil.taba.ui.dashboard.mufrodat.AIFeedbackPlaceholderSection
import com.fadhil.taba.ui.dashboard.mufrodat.MufrodatSettingsBottomSheet
import com.fadhil.taba.ui.dashboard.mufrodat.WaveformVisualizer
import com.fadhil.taba.ui.theme.GreenPrimary
import kotlinx.coroutines.delay
import java.io.File
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HiwarScreen(
    module: Module,
    username: String,
    onBack: () -> Unit,
    viewModel: HiwarViewModel = viewModel()
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val settings by AppSettingsStore.settings.collectAsState()
    val lang = settings.language
    val avatarPath = settings.avatarPath
    val showHarakat = settings.mufrodatFullHarakat
    val isLandscape = settings.mufrodatHorizontalLayout
    
    val aiFeedback by viewModel.aiFeedback.collectAsState()
    val isAiLoading by viewModel.isLoading.collectAsState()
    val isTranscribing by viewModel.isTranscribing.collectAsState()
    val currentlyPlaying by viewModel.currentlyPlayingText.collectAsState()

    val userVoicePath by viewModel.userVoicePath.collectAsState()
    val isPlayingUserVoice by viewModel.isPlayingUserVoice.collectAsState()
    val userVoicePosition by viewModel.userVoicePosition.collectAsState()
    val userVoiceDuration by viewModel.userVoiceDuration.collectAsState()
    val userWaveform by viewModel.userWaveform.collectAsState()
    
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    val questions = module.questions
    val currentQuestionObj = questions.getOrNull(currentQuestionIndex)
    
    var showSettingsSheet by remember { mutableStateOf(false) }

    // Recording Logic
    var recorder by remember { mutableStateOf<MediaRecorder?>(null) }
    var tempAudioFile by remember { mutableStateOf<File?>(null) }
    var isRecording by remember { mutableStateOf(false) }
    var recordingQuestionIndex by remember { mutableIntStateOf(-1) }
    var recordingTime by remember { mutableIntStateOf(0) }

    LaunchedEffect(isRecording) {
        if (isRecording) {
            recordingTime = 0
            while (isRecording) {
                delay(1000L)
                recordingTime++
            }
        }
    }

    fun formatArabic(text: String): String = if (showHarakat) text else text.removeHarakat()

    fun stopRecording(discardFile: Boolean = false) {
        if (isRecording) {
            try {
                recorder?.stop()
            } catch (e: Exception) {
                Log.w("HiwarScreen", "Recorder stop skipped", e)
            }
        }
        try {
            recorder?.release()
        } catch (e: Exception) {
            Log.w("HiwarScreen", "Recorder release skipped", e)
        }
        recorder = null
        isRecording = false
        if (discardFile) {
            tempAudioFile?.takeIf { it.exists() }?.delete()
            tempAudioFile = null
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val file = File(context.cacheDir, "hiwar_rec_${System.currentTimeMillis()}.m4a")
            tempAudioFile = file
            try {
                recorder = MediaRecorder().apply {
                    setAudioSource(MediaRecorder.AudioSource.MIC)
                    setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                    setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                    setOutputFile(file.absolutePath)
                    prepare()
                    start()
                }
                isRecording = true
                recordingQuestionIndex = currentQuestionIndex
            } catch (e: Exception) {
                stopRecording(discardFile = true)
                Log.e("HiwarScreen", "Failed to start recording", e)
            }
        }
    }

    LaunchedEffect(settings.audioSpeed, settings.voiceGender) {
        viewModel.updateTtsSettings(settings.audioSpeed, settings.voiceGender)
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
            stopRecording(discardFile = true)
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    LaunchedEffect(currentQuestionIndex) {
        if (recordingQuestionIndex != -1 && recordingQuestionIndex != currentQuestionIndex) {
            stopRecording(discardFile = true)
            viewModel.clearRecordedResponse()
        }
        recordingQuestionIndex = currentQuestionIndex
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GreenPrimary)
    ) {
        TabaHeader(
            title = Localization.getString("hiwar_title", lang),
            onBack = onBack,
            trailingAction = {
                IconButton(onClick = { showSettingsSheet = true }) {
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
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 100.dp)
                ) {
                    TopicBanner(module, lang, ::formatArabic)

                    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = Localization.getString("hiwar_practice", lang), 
                                fontSize = 16.sp, 
                                fontWeight = FontWeight.Bold, 
                                color = GreenPrimary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "✦", color = Color(0xFFEAB308), fontSize = 14.sp)
                        }
                        Text(
                            text = Localization.getString("hiwar_headline", lang), 
                            fontSize = 12.sp, 
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    PracticeSection(
                        currentQuestion = currentQuestionObj?.arabic ?: "",
                        translation = if (lang == "en") currentQuestionObj?.english ?: "" else currentQuestionObj?.indonesian ?: "",
                        formatArabic = ::formatArabic,
                        onListen = {
                            currentQuestionObj?.let { viewModel.playVoice(it.arabic) }
                        },
                        onMicClick = {
                            if (isRecording) {
                                stopRecording()
                                tempAudioFile?.let { file ->
                                    viewModel.setRecordedVoice(file.absolutePath)
                                    currentQuestionObj?.let { q ->
                                        viewModel.transcribeAudio(file.absolutePath, q.arabic, module.title, module.content)
                                    }
                                }
                            } else {
                                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        },
                        isPlaying = currentlyPlaying == currentQuestionObj?.arabic,
                        lang = lang,
                        userVoicePath = userVoicePath,
                        avatarPath = avatarPath,
                        isPlayingUserVoice = isPlayingUserVoice,
                        userVoicePosition = userVoicePosition,
                        userVoiceDuration = userVoiceDuration,
                        userWaveform = userWaveform,
                        onPlayUserVoice = { viewModel.playUserVoice() },
                        onSeekUserVoice = { viewModel.seekUserVoice(it) },
                        onRefreshRecord = { viewModel.clearRecordedResponse() },
                        isRecording = isRecording,
                        isTranscribing = isTranscribing,
                        recordingTime = recordingTime
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    if (isAiLoading || isTranscribing) {
                        Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = GreenPrimary)
                        }
                    } else if (aiFeedback != null) {
                        AIFeedbackSection(lang, aiFeedback!!)
                    } else {
                        AIFeedbackPlaceholderSection(lang)
                    }
                }

                // Bottom Nav (Previous / Next)
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth(),
                    color = Color.White,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 20.dp, vertical = 16.dp)
                            .navigationBarsPadding(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = { if (currentQuestionIndex > 0) currentQuestionIndex-- },
                            enabled = currentQuestionIndex > 0
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(Localization.getString("previous", lang))
                        }

                        Surface(
                            color = GreenPrimary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "${currentQuestionIndex + 1}/${questions.size}",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                color = GreenPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Button(
                            onClick = { if (currentQuestionIndex < questions.size - 1) currentQuestionIndex++ },
                            enabled = currentQuestionIndex < questions.size - 1,
                            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(Localization.getString("next", lang), color = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                        }
                    }
                }
            }
        }
    }

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
}

@Composable
fun TopicBanner(module: Module, lang: String, formatArabic: (String) -> String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        color = GreenPrimary,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = module.imageResId),
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(Localization.getString("current_topic", lang), color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                val title = if (lang == "en") module.titleEn else module.title
                Text(
                    text = "${formatArabic(module.arabicTitle)} / $title",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.White)
        }
    }
}

@Composable
fun PracticeSection(
    currentQuestion: String,
    translation: String,
    formatArabic: (String) -> String,
    onListen: () -> Unit,
    onMicClick: () -> Unit,
    isPlaying: Boolean = false,
    lang: String,
    userVoicePath: String?,
    avatarPath: String?,
    isPlayingUserVoice: Boolean,
    userVoicePosition: Float,
    userVoiceDuration: Float,
    userWaveform: List<Float>,
    onPlayUserVoice: () -> Unit,
    onSeekUserVoice: (Float) -> Unit,
    onRefreshRecord: () -> Unit,
    isRecording: Boolean,
    isTranscribing: Boolean,
    recordingTime: Int
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        color = Color.White,
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color(0xFFF3F4F6))
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {
                    Surface(color = Color(0xFFF0FDF4), shape = CircleShape, modifier = Modifier.size(40.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Image(painter = painterResource(id = R.drawable.taba), contentDescription = null, modifier = Modifier.fillMaxSize())
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Taba AI", fontWeight = FontWeight.Bold, color = GreenPrimary, fontSize = 10.sp)
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFFF0FDF4), RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomEnd = 16.dp))
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = formatArabic(currentQuestion),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = GreenPrimary
                            )
                            Text(
                                text = translation,
                                fontSize = 13.sp,
                                color = Color.Gray
                            )
                        }
                        IconButton(
                            onClick = onListen,
                            modifier = Modifier.size(48.dp).background(if (isPlaying) GreenPrimary.copy(alpha = 0.1f) else Color.White, CircleShape).border(1.dp, Color(0xFFF3F4F6), CircleShape)
                        ) {
                            Icon(if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow, contentDescription = null, tint = GreenPrimary)
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            if (userVoicePath == null) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = !isTranscribing) { onMicClick() },
                    color = if (isRecording) Color(0xFFFEF2F2) else Color(0xFFFEFCE8),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, if (isRecording) Color(0xFFFECACA) else Color(0xFFFEF08A))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(if (isRecording) Color.Red else GreenPrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                if (isRecording) Icons.Default.Stop else Icons.Default.Mic, 
                                contentDescription = null, 
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            if (isRecording) {
                                String.format(Locale.getDefault(), "%02d:%02d - Stop Recording", recordingTime / 60, recordingTime % 60)
                            } else if (isTranscribing) {
                                "Mengenali suara..."
                            } else {
                                Localization.getString("answer_voice", lang)
                            },
                            fontSize = 12.sp, 
                            color = if (isRecording) Color.Red else GreenPrimary, 
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.weight(1f),
                        color = Color(0xFFF9FAFB),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = onPlayUserVoice,
                                    modifier = Modifier.size(44.dp).background(GreenPrimary, CircleShape)
                                ) {
                                    Icon(
                                        imageVector = if (isPlayingUserVoice) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Box(modifier = Modifier.fillMaxWidth().height(40.dp)) {
                                        WaveformVisualizer(
                                            amplitudes = userWaveform,
                                            isActive = isPlayingUserVoice,
                                            progress = if (userVoiceDuration > 0f) userVoicePosition / userVoiceDuration else null
                                        )
                                        Slider(
                                            value = userVoicePosition,
                                            onValueChange = onSeekUserVoice,
                                            valueRange = 0f..userVoiceDuration.coerceAtLeast(0.1f),
                                            modifier = Modifier.fillMaxSize(),
                                            colors = SliderDefaults.colors(
                                                thumbColor = Color.Transparent,
                                                activeTrackColor = Color.Transparent,
                                                inactiveTrackColor = Color.Transparent,
                                                activeTickColor = Color.Transparent,
                                                inactiveTickColor = Color.Transparent
                                            )
                                        )
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        val elapsed = String.format(Locale.getDefault(), "%02d:%02d", (userVoicePosition / 60).toInt(), (userVoicePosition % 60).toInt())
                                        val total = String.format(Locale.getDefault(), "%02d:%02d", (userVoiceDuration / 60).toInt(), (userVoiceDuration % 60).toInt())
                                        Text(text = elapsed, fontSize = 10.sp, color = GreenPrimary, fontWeight = FontWeight.Bold)
                                        Text(text = total, fontSize = 10.sp, color = Color.Gray)
                                    }
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                IconButton(onClick = onRefreshRecord) {
                                    Icon(Icons.Default.Refresh, contentDescription = "Retry", tint = Color.Gray)
                                }
                            }
                        }
                    }

                    if (avatarPath != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .border(1.dp, Color(0xFFE5E7EB), CircleShape)
                        ) {
                            val bitmap = remember(avatarPath) { android.graphics.BitmapFactory.decodeFile(avatarPath) }
                            if (bitmap != null) {
                                Image(
                                    bitmap = bitmap.asImageBitmap(),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = R.drawable.profile),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
