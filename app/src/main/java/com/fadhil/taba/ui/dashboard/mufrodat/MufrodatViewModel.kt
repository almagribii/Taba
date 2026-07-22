package com.fadhil.taba.ui.dashboard.mufrodat

import android.app.Application
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fadhil.taba.BuildConfig
import com.fadhil.taba.data.settings.Localization
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import java.util.Locale
import kotlin.random.Random

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
    private val apiKey = BuildConfig.GROQ_API_KEY_MUFRODAT

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

    // Playback and Waveform states
    private val _playbackPosition = MutableStateFlow(0f)
    val playbackPosition: StateFlow<Float> = _playbackPosition.asStateFlow()

    private val _playbackDuration = MutableStateFlow(0f)
    val playbackDuration: StateFlow<Float> = _playbackDuration.asStateFlow()

    private val _waveformAmplitudes = MutableStateFlow<List<Float>>(emptyList())
    val waveformAmplitudes: StateFlow<List<Float>> = _waveformAmplitudes.asStateFlow()

    private var playbackJob: Job? = null
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
                            stopPlaybackSimulation()
                        }
                    }

                    override fun onError(utteranceId: String?) {
                        stopPlaybackSimulation()
                    }

                    override fun onStop(utteranceId: String?, interrupted: Boolean) {
                        stopPlaybackSimulation()
                    }
                })
            }
        }
    }

    private fun stopPlaybackSimulation() {
        _currentlyPlayingText.value = null
        playbackJob?.cancel()
        _playbackPosition.value = _playbackDuration.value // Mark as finished
    }

    fun playVoice(text: String) {
        if (_isTtsReady.value) {
            if (_currentlyPlayingText.value == text) {
                tts?.stop()
                stopPlaybackSimulation()
            } else {
                // Estimate duration: approx 6 characters per second, adjusted by speed
                val estimatedSeconds = (text.length / 6f) / _audioSpeed
                _playbackDuration.value = estimatedSeconds.coerceAtLeast(1.0f)
                _playbackPosition.value = 0f
                
                // Initialize waveform with some baseline random values
                _waveformAmplitudes.value = List(30) { Random.nextFloat() * 0.5f + 0.1f }

                tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, text)
                startPlaybackSimulation()
            }
        }
    }

    private fun startPlaybackSimulation() {
        playbackJob?.cancel()
        playbackJob = viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            val totalDurationMs = (_playbackDuration.value * 1000).toLong()

            while (System.currentTimeMillis() - startTime < totalDurationMs) {
                val elapsed = (System.currentTimeMillis() - startTime) / 1000f
                _playbackPosition.value = elapsed
                
                // Animate waveform: subtly shift existing amplitudes
                val currentAmps = _waveformAmplitudes.value.toMutableList()
                if (currentAmps.isNotEmpty()) {
                    currentAmps.removeAt(0)
                    currentAmps.add(Random.nextFloat() * 0.8f + 0.2f)
                    _waveformAmplitudes.value = currentAmps
                }

                delay(100) // Update every 100ms
            }
            _playbackPosition.value = _playbackDuration.value
        }
    }

    fun checkPronunciation(arabicWord: String, userSpeech: String) {
        if (userSpeech.isBlank()) {
            Log.e("MufrodatViewModel", "User speech is blank")
            return
        }

        val responseLanguage = Localization.responseLanguageFor(userSpeech)

        viewModelScope.launch {
            _isLoading.value = true
            _aiFeedback.value = null // Reset feedback sebelumnya
            try {
                val systemPrompt = if (responseLanguage == "en") {
                    """
                        Act as an expert Arabic pronunciation teacher.
                        Evaluate the user's Speech-to-Text answer.
                        Return JSON only with these keys:
                        {
                          "score": (integer 0-100),
                          "feedback": (string),
                          "tips": (string)
                        }
                        Write feedback and tips in English.
                    """.trimIndent()
                } else {
                    """
                        Berperanlah sebagai guru Bahasa Arab yang ahli dalam pengucapan dan makhraj.
                        Tugas utama kamu adalah menilai hasil ucapan user dari Speech-to-Text.
                        
                        FORMAT OUTPUT HARUS BERUPA JSON MURNI DENGAN KEYS:
                        {
                          "score": (integer 0-100),
                          "feedback": (string),
                          "tips": (string)
                        }
                        Tulis feedback dan tips dalam Bahasa Indonesia.
                    """.trimIndent()
                }

                val userPrompt = if (responseLanguage == "en") {
                    """
                        User tried to pronounce this vocabulary: "$arabicWord".
                        User speech-to-text result: "$userSpeech".
                        
                        1. Score (0-100): Be fair and objective by considering pronunciation and word accuracy.
                        2. Feedback: One short encouraging or corrective sentence in English.
                        3. Tips: One very short tip (max 7 words) for makhraj improvement.
                    """.trimIndent()
                } else {
                    """
                    User mencoba mengucapkan Mufrodat: "$arabicWord".
                    Hasil suara user (STT): "$userSpeech".
                    
                    Tugas:
                    1. Skor (0-100): Berikan skor yang ADIL dan objektif dengan memperhatikan harakat dari mufrodatnya. Jika user mengucap harakat fathah tetapi seharusnya kasroh, kurangi poin dan jelaskan.
                    2. Feedback: Kalimat apresiasi atau koreksi ringan dalam Bahasa Indonesia.
                    3. Tips: 1 tips sangat singkat (maksimal 7 kata) untuk perbaikan makhraj.
                    """.trimIndent()
                }

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