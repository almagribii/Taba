package com.fadhil.taba.ui.dashboard.chat_ai

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fadhil.taba.BuildConfig
import com.fadhil.taba.data.settings.Localization
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

// 1. Data Class UI Chat
@Serializable
data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val time: String = "",
    val shouldAnimate: Boolean = false
)

// 2. Data Class Request Groq (Format OpenAI)
@Serializable
data class GroqRequest(
    val model: String,
    val messages: List<GroqMessage>
)

@Serializable
data class GroqMessage(
    val role: String, // "system", "user", atau "assistant"
    val content: String
)

// 3. Data Class Response Groq
@Serializable
data class GroqResponse(
    val choices: List<GroqChoice>? = null,
    val error: GroqErrorDetail? = null
)

@Serializable
data class GroqChoice(
    val message: GroqMessage
)

@Serializable
data class GroqTranscriptionResponse(
    val text: String
)

@Serializable
data class GroqErrorDetail(
    val message: String
)

class ChatAiViewModel : ViewModel() {
    // Pastikan BuildConfig ini berisi API Key dari Groq (diawali dengan `gsk_...`)
    private val apiKey = BuildConfig.GROQ_API_KEY

    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
    }

    private val _messages = MutableStateFlow<List<ChatMessage>>(listOf(
        ChatMessage("Ahlan! Saya adalah asisten TABA. Ada yang bisa saya bantu dalam belajar Bahasa Arab hari ini?", false, "Now", shouldAnimate = true)
    ))
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isTranscribing = MutableStateFlow(false)
    val isTranscribing: StateFlow<Boolean> = _isTranscribing.asStateFlow()

    private fun systemInstructionTextFor(userText: String): String {
        return if (Localization.prefersEnglishResponse(userText)) {
            "Act as TABA's intelligent Arabic learning assistant. Reply in English, keep the answer friendly, educational, and motivating. If the user asks something outside language learning, politely redirect them back to learning Arabic. Keep the answer concise and easy to understand."
        } else {
            "Berperanlah sebagai asisten cerdas TABA yang ahli dalam Bahasa Arab. Berikan jawaban yang ramah, edukatif, dan memotivasi user untuk belajar. Jika user bertanya hal di luar belajar bahasa, arahkan kembali dengan sopan. Jawaban harus ringkas dan mudah dipahami."
        }
    }

    private fun getCurrentTime(): String = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date())

    fun sendMessage(userText: String) {
        if (userText.isBlank()) return

        val englishMode = Localization.prefersEnglishResponse(userText)
        val currentMessages = _messages.value.toMutableList()
        currentMessages.add(ChatMessage(userText, true, getCurrentTime(), shouldAnimate = false))
        _messages.value = currentMessages

        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Endpoint Groq API
                val url = "https://api.groq.com/openai/v1/chat/completions"

                // Susun pesan: instruksi sistem diawali dengan role "system"
                val requestBody = GroqRequest(
                    model = "llama-3.3-70b-versatile", // Model cepat, cerdas, & gratis
                    messages = listOf(
                        GroqMessage(role = "system", content = systemInstructionTextFor(userText)),
                        GroqMessage(role = "user", content = userText)
                    )
                )

                val response: GroqResponse = client.post(url) {
                    contentType(ContentType.Application.Json)
                    // API Key dikirim melalui Header Authorization Bearer Token
                    header(HttpHeaders.Authorization, "Bearer $apiKey")
                    setBody(requestBody)
                }.body()

                if (response.error != null) {
                    throw Exception(response.error.message)
                }

                val aiResponse = response.choices?.firstOrNull()?.message?.content
                    ?: if (englishMode) "Sorry, the AI did not return a response." else "Maaf, sistem AI tidak memberikan jawaban."

                val updatedMessages = _messages.value.toMutableList()
                updatedMessages.add(ChatMessage(aiResponse, false, getCurrentTime(), shouldAnimate = true))
                _messages.value = updatedMessages
            } catch (e: Exception) {
                Log.e("ChatAiViewModel", "Error sending message", e)
                val updatedMessages = _messages.value.toMutableList()
                val errorText = if (englishMode) {
                    "An error occurred: ${e.localizedMessage}"
                } else {
                    "Terjadi kesalahan: ${e.localizedMessage}"
                }
                updatedMessages.add(ChatMessage(errorText, false, shouldAnimate = true))
                _messages.value = updatedMessages
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun transcribeAudio(filePath: String, onResult: (String) -> Unit) {
        val file = File(filePath)
        if (!file.exists()) return

        viewModelScope.launch {
            _isTranscribing.value = true
            try {
                val url = "https://api.groq.com/openai/v1/audio/transcriptions"
                val response = client.post(url) {
                    header(HttpHeaders.Authorization, "Bearer $apiKey")
                    setBody(MultiPartFormDataContent(
                        formData {
                            append("file", file.readBytes(), Headers.build {
                                append(HttpHeaders.ContentDisposition, "filename=\"${file.name}\"")
                                append(HttpHeaders.ContentType, "audio/mpeg")
                            })
                            append("model", "whisper-large-v3")
                        }
                    ))
                }

                if (response.status.isSuccess()) {
                    val result: GroqTranscriptionResponse = response.body()
                    onResult(result.text)
                } else {
                    Log.e("ChatAiViewModel", "Transcription failed: ${response.status}")
                }
            } catch (e: Exception) {
                Log.e("ChatAiViewModel", "Transcription error", e)
            } finally {
                _isTranscribing.value = false
            }
        }
    }

    fun markMessageAsAnimated(index: Int) {
        val currentMessages = _messages.value.toMutableList()
        if (index in currentMessages.indices) {
            if (currentMessages[index].shouldAnimate) {
                currentMessages[index] = currentMessages[index].copy(shouldAnimate = false)
                _messages.value = currentMessages
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        client.close()
    }
}
