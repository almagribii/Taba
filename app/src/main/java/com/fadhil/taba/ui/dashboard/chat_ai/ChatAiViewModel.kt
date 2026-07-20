package com.fadhil.taba.ui.dashboard.chat_ai

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fadhil.taba.BuildConfig
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class ChatMessage(
    val text: String,
    val isUser: Boolean
)

@Serializable
data class GeminiRequest(
    val contents: List<Content>,
    val systemInstruction: Content? = null
)

@Serializable
data class Content(
    val role: String? = null,
    val parts: List<Part>
)

@Serializable
data class Part(
    val text: String
)

@Serializable
data class GeminiResponse(
    val candidates: List<Candidate>? = null,
    val error: GeminiError? = null
)

@Serializable
data class Candidate(
    val content: Content
)

@Serializable
data class GeminiError(
    val message: String
)

class ChatAiViewModel : ViewModel() {
    private val apiKey = BuildConfig.GEMINI_API_KEY_CHATBOT
    
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
        ChatMessage("Ahlan! Saya adalah asisten AI TABA. Ada yang bisa saya bantu dalam belajar Bahasa Arab hari ini?", false)
    ))
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val systemInstructionText = "Berperanlah sebagai asisten cerdas TABA yang ahli dalam Bahasa Arab. Berikan jawaban yang ramah, edukatif, dan memotivasi user untuk belajar. Jika user bertanya hal di luar belajar bahasa, arahkan kembali dengan sopan. Jawaban harus ringkas dan mudah dipahami."

    fun sendMessage(userText: String) {
        if (userText.isBlank()) return

        val currentMessages = _messages.value.toMutableList()
        currentMessages.add(ChatMessage(userText, true))
        _messages.value = currentMessages

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"
                
                val requestBody = GeminiRequest(
                    contents = listOf(
                        Content(role = "user", parts = listOf(Part(text = userText)))
                    ),
                    systemInstruction = Content(parts = listOf(Part(text = systemInstructionText)))
                )

                val response: GeminiResponse = client.post(url) {
                    contentType(ContentType.Application.Json)
                    setBody(requestBody)
                }.body()

                if (response.error != null) {
                    throw Exception(response.error.message)
                }

                val aiResponse = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: "Maaf, sistem AI tidak memberikan jawaban."
                
                val updatedMessages = _messages.value.toMutableList()
                updatedMessages.add(ChatMessage(aiResponse, false))
                _messages.value = updatedMessages
            } catch (e: Exception) {
                Log.e("ChatAiViewModel", "Error sending message", e)
                val updatedMessages = _messages.value.toMutableList()
                updatedMessages.add(ChatMessage("Terjadi kesalahan: ${e.localizedMessage}", false))
                _messages.value = updatedMessages
            } finally {
                _isLoading.value = false
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        client.close()
    }
}
