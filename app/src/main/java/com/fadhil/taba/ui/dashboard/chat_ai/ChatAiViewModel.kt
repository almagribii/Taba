package com.fadhil.taba.ui.dashboard.chat_ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

data class ChatMessage(
    val text: String,
    val isUser: Boolean
)

@Serializable
data class GeminiRequest(
    val contents: List<Content>
)

@Serializable
data class Content(
    val parts: List<Part>
)

@Serializable
data class Part(
    val text: String
)

@Serializable
data class GeminiResponse(
    val candidates: List<Candidate>? = null
)

@Serializable
data class Candidate(
    val content: Content? = null
)

class ChatAiViewModel : ViewModel() {
    // API Key Gemini. Sebaiknya diletakkan di BuildConfig/local.properties
    private val apiKey = "YOUR_GEMINI_API_KEY" 
    
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

    fun sendMessage(userText: String) {
        if (userText.isBlank()) return

        val currentMessages = _messages.value.toMutableList()
        currentMessages.add(ChatMessage(userText, true))
        _messages.value = currentMessages

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey"
                
                val response: GeminiResponse = client.post(url) {
                    contentType(ContentType.Application.Json)
                    setBody(GeminiRequest(
                        contents = listOf(Content(parts = listOf(Part(text = userText))))
                    ))
                }.body()

                val aiResponse = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text 
                    ?: "Maaf, saya tidak mengerti."
                
                val updatedMessages = _messages.value.toMutableList()
                updatedMessages.add(ChatMessage(aiResponse, false))
                _messages.value = updatedMessages
            } catch (e: Exception) {
                val updatedMessages = _messages.value.toMutableList()
                updatedMessages.add(ChatMessage("Error: ${e.message}. Pastikan koneksi internet aktif.", false))
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
