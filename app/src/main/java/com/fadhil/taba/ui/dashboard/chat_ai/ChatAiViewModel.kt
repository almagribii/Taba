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
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// 1. Data Class UI Chat
@Serializable
data class ChatMessage(
    val text: String,
    val isUser: Boolean
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
                // Endpoint Groq API
                val url = "https://api.groq.com/openai/v1/chat/completions"

                // Susun pesan: instruksi sistem diawali dengan role "system"
                val requestBody = GroqRequest(
                    model = "llama-3.3-70b-versatile", // Model cepat, cerdas, & gratis
                    messages = listOf(
                        GroqMessage(role = "system", content = systemInstructionText),
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