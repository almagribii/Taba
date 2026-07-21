package com.fadhil.taba.ui.dashboard.hiwar

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.speech.RecognizerIntent
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.fadhil.taba.ui.dashboard.materi.removeHarakat
import com.fadhil.taba.ui.dashboard.mufrodat.AIFeedbackSection
import com.fadhil.taba.ui.dashboard.mufrodat.MufrodatHeader
import com.fadhil.taba.ui.dashboard.mufrodat.MufrodatSettingsBottomSheet
import com.fadhil.taba.ui.theme.GreenPrimary
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.*

@Serializable
data class HiwarMessage(
    val text: String,
    val translation: String? = null,
    val isUser: Boolean,
    val time: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HiwarScreen(
    module: Module,
    username: String,
    avatarPath: String?,
    onBack: () -> Unit,
    viewModel: HiwarViewModel = viewModel()
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
    
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    val questions = module.questions
    val currentQuestionObj = questions.getOrNull(currentQuestionIndex)
    
    val conversation = remember { mutableStateListOf<HiwarMessage>() }
    
    var showSettingsSheet by remember { mutableStateOf(false) }

    fun formatArabic(text: String): String = if (showHarakat) text else text.removeHarakat()
    
    fun getCurrentTime(): String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    
    fun cleanQuestion(text: String): String {
        return text.replace(Regex("^[0-9١-٩]+\\.\\s*"), "")
    }

    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0) ?: ""
            conversation.add(HiwarMessage(spokenText, null, true, getCurrentTime()))
            currentQuestionObj?.let {
                viewModel.checkHiwarResponse(it.arabic, spokenText, module.title, module.content)
            }
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
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    Scaffold(
        topBar = {
            MufrodatHeader(
                title = Localization.getString("hiwar_title", lang),
                onBack = onBack,
                lang = lang,
                onSettingsClick = { showSettingsSheet = true }
            )
        },
        containerColor = Color(0xFFF9F7F2)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
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
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                questions.forEachIndexed { index, _ ->
                    val isSelected = index == currentQuestionIndex
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) GreenPrimary else Color.White)
                            .border(1.dp, if (isSelected) GreenPrimary else Color.LightGray, CircleShape)
                            .clickable {
                                currentQuestionIndex = index
                                conversation.clear()
                                viewModel.resetFeedback()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (index + 1).toString(),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else Color.Gray
                        )
                        
                        if (currentlyPlaying == cleanQuestion(questions[index].arabic)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(GreenPrimary.copy(alpha = 0.7f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.GraphicEq,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Practice Area
            currentQuestionObj?.let { qObj ->
                val cleanQ = cleanQuestion(qObj.arabic)
                PracticeSection(
                    currentQuestion = cleanQ,
                    translation = if (lang == "en") qObj.english else qObj.indonesian,
                    formatArabic = ::formatArabic,
                    onListen = { viewModel.playVoice(cleanQ) },
                    onMicClick = { permissionLauncher.launch(Manifest.permission.RECORD_AUDIO) },
                    isPlaying = currentlyPlaying == cleanQ,
                    lang = lang
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            // Conversation History
            Text(
                text = Localization.getString("conversation", lang),
                modifier = Modifier.padding(horizontal = 16.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = GreenPrimary
            )
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                currentQuestionObj?.let { qObj ->
                    ChatBubble(
                        message = HiwarMessage(
                            text = cleanQuestion(qObj.arabic), 
                            translation = if (lang == "en") qObj.english else qObj.indonesian, 
                            isUser = false, 
                            time = "09:32"
                        ),
                        username = username,
                        avatarPath = avatarPath,
                        formatArabic = ::formatArabic,
                        onPlayVoice = { viewModel.playVoice(it) },
                        isPlaying = currentlyPlaying == cleanQuestion(qObj.arabic)
                    )
                }
                
                conversation.forEach { msg ->
                    ChatBubble(
                        message = msg,
                        username = username,
                        avatarPath = avatarPath,
                        formatArabic = ::formatArabic,
                        onPlayVoice = { viewModel.playVoice(it) },
                        isPlaying = currentlyPlaying == msg.text
                    )
                }
            }

            if (isAiLoading) {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = GreenPrimary)
                }
            }

            aiFeedback?.let { feedback ->
                AIFeedbackSection(lang, feedback)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        if (currentQuestionIndex > 0) {
                            currentQuestionIndex--
                            conversation.clear()
                            viewModel.resetFeedback()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = currentQuestionIndex > 0,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(Localization.getString("previous", lang), fontSize = 12.sp)
                }

                // Next Question Button
                Button(
                    onClick = {
                        if (currentQuestionIndex < questions.size - 1) {
                            currentQuestionIndex++
                            conversation.clear()
                            viewModel.resetFeedback()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = currentQuestionIndex < questions.size - 1,
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(Localization.getString("next", lang), color = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.White)
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
    lang: String
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
                            Image(painter = painterResource(id = R.drawable.kuda), contentDescription = null, modifier = Modifier.size(28.dp))
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
            
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onMicClick() },
                color = Color(0xFFFEFCE8),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFFFEF08A))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(GreenPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Mic, contentDescription = null, tint = Color.White)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(Localization.getString("answer_voice", lang), fontSize = 12.sp, color = GreenPrimary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ChatBubble(
    message: HiwarMessage,
    username: String,
    avatarPath: String?,
    formatArabic: (String) -> String,
    onPlayVoice: (String) -> Unit,
    isPlaying: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!message.isUser) {
            Image(
                painter = painterResource(id = R.drawable.sholat),
                contentDescription = null,
                modifier = Modifier.size(32.dp).clip(CircleShape).background(Color.White)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        
        Surface(
            modifier = Modifier.widthIn(max = 280.dp),
            color = if (message.isUser) Color(0xFFFEFCE8) else Color.White,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (message.isUser) 16.dp else 0.dp,
                bottomEnd = if (message.isUser) 0.dp else 16.dp
            ),
            border = if (message.isUser) BorderStroke(1.dp, Color(0xFFFEF08A)) else BorderStroke(1.dp, Color(0xFFF3F4F6))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = if (message.isUser) message.text else formatArabic(message.text),
                    fontSize = 14.sp,
                    color = if (message.isUser) Color.Black else GreenPrimary,
                    fontWeight = FontWeight.Medium
                )
                // Point 4: Translation in chat bubble
                message.translation?.let {
                    Text(
                        text = it,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (!message.isUser) {
                        IconButton(
                            onClick = { onPlayVoice(message.text) },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(14.dp))
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = message.time,
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                        if (message.isUser) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.DoneAll,
                                contentDescription = null,
                                tint = Color(0xFF166534),
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
            }
        }

        if (message.isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(modifier = Modifier.size(32.dp).clip(CircleShape)) {
                if (avatarPath != null) {
                    val bitmap = remember(avatarPath) { android.graphics.BitmapFactory.decodeFile(avatarPath) }
                    if (bitmap != null) {
                        Image(bitmap = bitmap.asImageBitmap(), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    }
                } else {
                    Image(painter = painterResource(id = R.drawable.profile), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                }
            }
        }
    }
}
