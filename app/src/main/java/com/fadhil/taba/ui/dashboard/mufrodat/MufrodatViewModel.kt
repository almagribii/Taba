package com.fadhil.taba.ui.dashboard.mufrodat

import android.app.Application
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fadhil.taba.BuildConfig
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import java.util.Locale

@Serializable
data class AIFeedbackData(
    val score: Int,
    val feedback: String,
    val tips: String
)

// Data Class Request & Response Groq API
@Serializable
data class GroqMufrodatRequest(
    val model: String,
    val messages: List<GroqMessage>,
    val response_format: ResponseFormat? = ResponseFormat("json_object")
)

@Serializable
data class GroqMessage(
    val role: String,
    val content: String
)

@Serializable
data class ResponseFormat(
    val type: String
)

@Serializable
data class GroqMufrodatResponse(
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

class MufrodatViewModel(application: Application) : AndroidViewModel(application), TextToSpeech.OnInitListener {
    // Gunakan Groq API Key dari BuildConfig kamu
    private val apiKey = BuildConfig.GROQ_API_KEY

    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 30000
            connectTimeoutMillis = 15000
            socketTimeoutMillis = 15000
        }
    }

    private val _aiFeedback = MutableStateFlow<AIFeedbackData?>(null)
    val aiFeedback: StateFlow<AIFeedbackData?> = _aiFeedback

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private var tts: TextToSpeech? = null
    private val _isTtsReady = MutableStateFlow(false)
    val isTtsReady: StateFlow<Boolean> = _isTtsReady

    private val _currentlyPlayingText = MutableStateFlow<String?>(null)
    val currentlyPlayingText: StateFlow<String?> = _currentlyPlayingText

    private var _audioSpeed = 1.0f
    private var _voiceGender = "female"

    init {
        tts = TextToSpeech(application, this)
    }

    fun updateTtsSettings(speed: Float, gender: String) {
        _audioSpeed = speed
        _voiceGender = gender
        if (_isTtsReady.value) {
            tts?.setSpeechRate(speed)
            applyVoiceGender(gender)
        }
    }

    private fun applyVoiceGender(gender: String) {
        try {
            val voices = tts?.voices
            val locale = Locale("ar")
            if (voices != null) {
                val arabicVoices = voices.filter { it.locale.language == locale.language }

                val targetVoice = arabicVoices.find { voice ->
                    val name = voice.name.lowercase()
                    (gender == "male" && (name.contains("male") || name.contains("man") || name.contains("boy"))) ||
                            (gender == "female" && (name.contains("female") || name.contains("woman") || name.contains("girl")))
                } ?: arabicVoices.find { voice ->
                    gender == "male" && arabicVoices.size > 1 && voice != arabicVoices.first()
                } ?: arabicVoices.firstOrNull()

                if (targetVoice != null) {
                    Log.d("TTS", "Setting voice to: ${targetVoice.name} for gender: $gender")
                    tts?.voice = targetVoice
                } else {
                    Log.e("TTS", "No Arabic voice found at all")
                }
            }
        } catch (e: Exception) {
            Log.e("TTS", "Error setting voice gender", e)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("ar"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Bahasa Arab tidak didukung di perangkat ini.")
            } else {
                _isTtsReady.value = true
                tts?.setSpeechRate(_audioSpeed)
                applyVoiceGender(_voiceGender)

                tts?.setOnUtteranceProgressListener(object : android.speech.tts.UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _currentlyPlayingText.value = utteranceId
                    }

                    override fun onDone(utteranceId: String?) {
                        if (_currentlyPlayingText.value == utteranceId) {
                            _currentlyPlayingText.value = null
                        }
                    }

                    override fun onError(utteranceId: String?) {
                        _currentlyPlayingText.value = null
                    }

                    override fun onStop(utteranceId: String?, interrupted: Boolean) {
                        _currentlyPlayingText.value = null
                    }
                })
            }
        }
    }

    fun playVoice(text: String) {
        if (_isTtsReady.value) {
            if (_currentlyPlayingText.value == text) {
                tts?.stop()
                _currentlyPlayingText.value = null
            } else {
                tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, text)
            }
        }
    }

    fun checkPronunciation(arabicWord: String, userSpeech: String) {
        if (userSpeech.isBlank()) {
            Log.e("MufrodatViewModel", "User speech is blank")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _aiFeedback.value = null // Reset feedback sebelumnya
            try {
                val systemPrompt = """
                    Berperanlah sebagai guru Bahasa Arab yang ahli dalam pengucapan dan makhraj.
                    Tugas utama kamu adalah menilai hasil ucapan user dari Speech-to-Text.
                    
                    FORMAT OUTPUT HARUS BERUPA JSON MURNI DENGAN KEYS:
                    {
                      "score": (integer 0-100),
                      "feedback": (string),
                      "tips": (string)
                    }
                """.trimIndent()

                val userPrompt = """
                    User mencoba mengucapkan Mufrodat: "$arabicWord".
                    Hasil suara user (STT): "$userSpeech".
                    
                    Tugas:
                    1. Skor (0-100): Berikan skor yang ADIL dan objektif dengan memperhatikan harakat dari mufrodatnya. Jika user mengucap harakat fathah tetapi seharusnya kasroh, kurangi poin dan jelaskan.
                    2. Feedback: Kalimat apresiasi atau koreksi ringan dalam Bahasa Indonesia.
                    3. Tips: 1 tips sangat singkat (maksimal 7 kata) untuk perbaikan makhraj.
                """.trimIndent()

                val url = "https://api.groq.com/openai/v1/chat/completions"

                val requestBody = GroqMufrodatRequest(
                    model = "llama-3.3-70b-versatile",
                    messages = listOf(
                        GroqMessage(role = "system", content = systemPrompt),
                        GroqMessage(role = "user", content = userPrompt)
                    )
                )

                val response = client.post(url) {
                    contentType(ContentType.Application.Json)
                    header(HttpHeaders.Authorization, "Bearer $apiKey")
                    setBody(requestBody)
                }

                val responseStatus = response.status

                if (responseStatus == HttpStatusCode.TooManyRequests) {
                    _aiFeedback.value = AIFeedbackData(0, "Terlalu banyak permintaan (Limit tercapai).", "Mohon tunggu sebentar lalu coba lagi.")
                    return@launch
                }

                if (!responseStatus.isSuccess()) {
                    _aiFeedback.value = AIFeedbackData(0, "Error dari AI (${responseStatus.value}).", "Coba lagi nanti.")
                    return@launch
                }

                val groqResponse: GroqMufrodatResponse = response.body()

                if (groqResponse.error != null) {
                    throw Exception(groqResponse.error.message)
                }

                val rawContent = groqResponse.choices?.firstOrNull()?.message?.content

                if (!rawContent.isNullOrBlank()) {
                    val jsonParser = Json { ignoreUnknownKeys = true }
                    val feedback = jsonParser.decodeFromString<AIFeedbackData>(rawContent)
                    _aiFeedback.value = feedback
                    Log.d("MufrodatViewModel", "AI Feedback success: ${feedback.score}")
                } else {
                    Log.e("MufrodatViewModel", "Gagal memproses response kosong dari Groq")
                    _aiFeedback.value = AIFeedbackData(0, "Gagal memproses jawaban AI.", "Coba ulangi lagi.")
                }
            } catch (e: Exception) {
                Log.e("MufrodatViewModel", "Error AI Correction", e)
                _aiFeedback.value = AIFeedbackData(0, "Terjadi kesalahan koneksi ke AI: ${e.message}", "Coba lagi nanti.")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetFeedback() {
        _aiFeedback.value = null
    }

    override fun onCleared() {
        super.onCleared()
        tts?.stop()
        tts?.shutdown()
        client.close()
    }
}