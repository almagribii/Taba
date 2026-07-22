package com.fadhil.taba.ui.dashboard.chat_ai

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import java.text.BreakIterator
import com.fadhil.taba.R
import com.fadhil.taba.data.settings.AppSettingsStore
import com.fadhil.taba.data.settings.Localization
import com.fadhil.taba.ui.dashboard.TabaHeader
import com.fadhil.taba.ui.theme.GreenPrimary

@Composable
fun ChatAiScreen(
    username: String = "Pengguna",
    avatarPath: String? = null,
    viewModel: ChatAiViewModel = viewModel()
) {
    val messages by viewModel.messages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val settings by AppSettingsStore.settings.collectAsState()
    val lang = settings.language

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0) ?: ""
            inputText = spokenText
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, if (lang == "en") "en-US" else "id-ID")
            }
            speechLauncher.launch(intent)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GreenPrimary)
    ) {
        TabaHeader(
            title = "Taba AI",
            subtitle = if (isLoading) "Sedang mengetik..." else "Online"
        )

        // Main Surface overlapping header slightly
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = (-8).dp),
            color = Color(0xFFF9F7F2),
            shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    itemsIndexed(messages) { index, message ->
                        ModernChatBubble(
                            message = message,
                            avatarPath = avatarPath,
                            onAnimationComplete = {
                                viewModel.markMessageAsAnimated(index)
                            }
                        )
                    }
                }

                if (messages.size <= 1) {
                    ChatEmptyStateCard(lang, modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
                }

                // Input Bar
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .navigationBarsPadding()
                            .imePadding(),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            placeholder = { 
                                Text(
                                    Localization.getString("chat_ai_hint", lang).ifEmpty { "Tanya Taba AI..." },
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                ) 
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(24.dp),
                            trailingIcon = {
                                IconButton(onClick = {
                                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                }) {
                                    Icon(Icons.Default.Mic, contentDescription = null, tint = GreenPrimary)
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF9FAFB),
                                unfocusedContainerColor = Color(0xFFF9FAFB),
                                focusedBorderColor = GreenPrimary.copy(alpha = 0.5f),
                                unfocusedBorderColor = Color.Transparent
                            ),
                            maxLines = 5
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        IconButton(
                            onClick = {
                                if (inputText.isNotBlank()) {
                                    viewModel.sendMessage(inputText)
                                    inputText = ""
                                }
                            },
                            modifier = Modifier
                                .padding(bottom = 4.dp)
                                .size(48.dp)
                                .background(
                                    if (inputText.isNotBlank()) GreenPrimary else Color(0xFFE5E7EB),
                                    CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatEmptyStateCard(lang: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Mic, contentDescription = null, tint = GreenPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = Localization.getString("chat_empty_title", lang),
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = GreenPrimary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = Localization.getString("chat_empty_body", lang),
                fontSize = 13.sp,
                color = Color.DarkGray,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun ModernChatBubble(
    message: ChatMessage,
    avatarPath: String?,
    onAnimationComplete: () -> Unit = {}
) {
    val isUser = message.isUser
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(0.5.dp, Color.LightGray.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.taba),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start,
            modifier = Modifier.weight(1f, fill = false)
        ) {
            Surface(
                color = if (isUser) GreenPrimary else Color.White,
                shape = RoundedCornerShape(
                    topStart = 18.dp,
                    topEnd = 18.dp,
                    bottomStart = if (isUser) 18.dp else 2.dp,
                    bottomEnd = if (isUser) 2.dp else 18.dp
                ),
                shadowElevation = 0.5.dp,
                border = if (!isUser) borderStroke() else null
            ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                    if (!isUser && message.shouldAnimate) {
                        TypewriterText(
                            text = message.text,
                            color = Color.Black,
                            fontSize = 15.sp,
                            lineHeight = 21.sp,
                            onAnimationComplete = onAnimationComplete
                        )
                    } else {
                        Text(
                            text = message.text,
                            color = if (isUser) Color.White else Color.Black,
                            fontSize = 15.sp,
                            lineHeight = 21.sp
                        )
                    }
                    if (message.time.isNotEmpty()) {
                        Text(
                            text = message.time,
                            fontSize = 10.sp,
                            color = if (isUser) Color.White.copy(alpha = 0.7f) else Color.Gray,
                            modifier = Modifier
                                .align(Alignment.End)
                                .padding(top = 4.dp)
                        )
                    }
                }
            }
        }

        if (isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(0.5.dp, Color.LightGray.copy(alpha = 0.5f), CircleShape)
            ) {
                if (avatarPath != null) {
                    val bitmap = remember(avatarPath) { android.graphics.BitmapFactory.decodeFile(avatarPath) }
                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
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

@Composable
fun borderStroke() = androidx.compose.foundation.BorderStroke(0.5.dp, Color(0xFFF3F4F6))

@Composable
fun TypewriterText(
    text: String,
    delayMillis: Long = 20,
    color: Color = Color.Unspecified,
    fontSize: androidx.compose.ui.unit.TextUnit = androidx.compose.ui.unit.TextUnit.Unspecified,
    lineHeight: androidx.compose.ui.unit.TextUnit = androidx.compose.ui.unit.TextUnit.Unspecified,
    onAnimationComplete: () -> Unit = {}
) {
    var displayedText by remember { mutableStateOf("") }

    LaunchedEffect(text) {
        displayedText = ""
        val iterator = BreakIterator.getCharacterInstance()
        iterator.setText(text)
        var start = iterator.first()
        var end = iterator.next()

        while (end != BreakIterator.DONE) {
            displayedText += text.substring(start, end)
            delay(delayMillis)
            start = end
            end = iterator.next()
        }
        onAnimationComplete()
    }

    Text(
        text = displayedText,
        color = color,
        fontSize = fontSize,
        lineHeight = lineHeight
    )
}
