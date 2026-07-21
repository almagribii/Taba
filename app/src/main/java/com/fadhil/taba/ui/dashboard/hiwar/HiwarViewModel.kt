package com.fadhil.taba.ui.dashboard.hiwar

import android.app.Application
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fadhil.taba.BuildConfig
import com.fadhil.taba.ui.dashboard.mufrodat.AIFeedbackData
import io.ktor.client.*
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
import kotlinx.serialization.json.*
import java.util.Locale

class HiwarViewModel(application: Application) : AndroidViewModel(application), TextToSpeech.OnInitListener {
    private val apiKey = BuildConfig.GEMINI_API_KEY_HIWAR
    
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
                    tts?.voice = targetVoice
                }
            }
        } catch (e: Exception) {
            Log.e("TTS", "Error setting voice gender", e)
        }
    }

    fun checkHiwarResponse(question: String, userSpeech: String, moduleTitle: String, moduleContent: String) {
        if (userSpeech.isBlank()) return
        
        viewModelScope.launch {
            _isLoading.value = true
            _aiFeedback.value = null
            try {
                val prompt = """
                    Berperanlah sebagai guru Bahasa Arab yang interaktif. 
                    Topik Materi: "$moduleTitle"
                    Isi Materi: "$moduleContent"
                    
                    User sedang berlatih percakapan (Al-Hiwar) dan merespons pertanyaan: "$question".
                    Jawaban user (dari Speech-to-Text): "$userSpeech".
                    
                    Tugas:
                    1. Skor (0-100): Berikan skor kemiripan bunyi dan ketepatan konteks jawaban sesuai dengan topik "$moduleTitle". Berikan nilai yang cenderung toleran (80-100 jika benar secara konteks).
                    2. Feedback: Berikan 1 kalimat apresiasi atau koreksi ringan dalam Bahasa Indonesia yang relevan dengan topik.
                    3. Tips: Berikan 1 tips sangat singkat (maksimal 10 kata) untuk memperbaiki makhraj atau pilihan kata agar lebih sesuai dengan konteks "$moduleTitle".
                    
                    FORMAT JSON (MURNI):
                    {
                      "score": (integer),
                      "feedback": (string),
                      "tips": (string)
                    }
                """.trimIndent()

                val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=$apiKey"
                val response = client.post(url) {
                    contentType(ContentType.Application.Json)
                    setBody(mapOf(
                        "contents" to listOf(mapOf(
                            "parts" to listOf(mapOf("text" to prompt))
                        ))
                    ))
                }
                
                val responseStatus = response.status
                val responseText = response.bodyAsText()

                if (responseStatus == HttpStatusCode.TooManyRequests) {
                    _aiFeedback.value = AIFeedbackData(0, "Terlalu banyak permintaan (Limit tercapai).", "Mohon tunggu sebentar lalu coba lagi.")
                    return@launch
                }

                if (!responseStatus.isSuccess()) {
                    _aiFeedback.value = AIFeedbackData(0, "Error dari AI (${responseStatus.value}).", "Coba lagi nanti.")
                    return@launch
                }

                val responseJson = Json { ignoreUnknownKeys = true }
                val geminiRawElement = responseJson.parseToJsonElement(responseText)
                val aiTextResponse = geminiRawElement.jsonObject["candidates"]
                    ?.jsonArray?.getOrNull(0)
                    ?.jsonObject?.get("content")
                    ?.jsonObject?.get("parts")
                    ?.jsonArray?.getOrNull(0)
                    ?.jsonObject?.get("text")
                    ?.jsonPrimitive?.content ?: ""

                val jsonRegex = Regex("""\{.*\}""", RegexOption.DOT_MATCHES_ALL)
                val jsonMatch = jsonRegex.find(aiTextResponse)?.value
                
                if (jsonMatch != null) {
                    _aiFeedback.value = Json.decodeFromString<AIFeedbackData>(jsonMatch)
                } else {
                    _aiFeedback.value = AIFeedbackData(0, "Gagal memproses jawaban AI.", "Coba ulangi lagi.")
                }
            } catch (e: Exception) {
                Log.e("HiwarViewModel", "Error AI Correction", e)
                _aiFeedback.value = AIFeedbackData(0, "Terjadi kesalahan koneksi ke AI.", "Periksa internet dan coba lagi.")
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
