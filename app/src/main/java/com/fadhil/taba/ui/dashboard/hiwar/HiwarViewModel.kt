package com.fadhil.taba.ui.dashboard.hiwar

import android.app.Application
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fadhil.taba.BuildConfig
import com.fadhil.taba.data.settings.Localization
import com.fadhil.taba.ui.dashboard.mufrodat.AIFeedbackData
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import java.io.File
import java.util.Locale
import kotlin.random.Random

@Serializable
data class GroqHiwarRequest(
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
data class GroqHiwarResponse(
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

@Serializable
data class GroqTranscriptionResponse(
    val text: String
)

class HiwarViewModel(application: Application) : AndroidViewModel(application), TextToSpeech.OnInitListener {
    private val apiKey = BuildConfig.GROQ_API_KEY_HIWAR

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

    private val _isTranscribing = MutableStateFlow(false)
    val isTranscribing: StateFlow<Boolean> = _isTranscribing.asStateFlow()

    private var tts: TextToSpeech? = null
    private val _isTtsReady = MutableStateFlow(false)
    val isTtsReady: StateFlow<Boolean> = _isTtsReady

    private val _currentlyPlayingText = MutableStateFlow<String?>(null)
    val currentlyPlayingText: StateFlow<String?> = _currentlyPlayingText

    // User Voice Playback States
    private var mediaPlayer: MediaPlayer? = null
    private val _userVoicePath = MutableStateFlow<String?>(null)
    val userVoicePath: StateFlow<String?> = _userVoicePath.asStateFlow()

    private val _isPlayingUserVoice = MutableStateFlow(false)
    val isPlayingUserVoice: StateFlow<Boolean> = _isPlayingUserVoice.asStateFlow()

    private val _userVoicePosition = MutableStateFlow(0f)
    val userVoicePosition: StateFlow<Float> = _userVoicePosition.asStateFlow()

    private val _userVoiceDuration = MutableStateFlow(0f)
    val userVoiceDuration: StateFlow<Float> = _userVoiceDuration.asStateFlow()

    private val _userWaveform = MutableStateFlow<List<Float>>(emptyList())
    val userWaveform: StateFlow<List<Float>> = _userWaveform.asStateFlow()

    private var playbackTrackerJob: Job? = null
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
                // Stop user voice if playing
                if (_isPlayingUserVoice.value) playUserVoice()
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

    fun playUserVoice() {
        val path = _userVoicePath.value ?: return
        if (_isPlayingUserVoice.value) {
            mediaPlayer?.pause()
            _isPlayingUserVoice.value = false
            playbackTrackerJob?.cancel()
        } else {
            // Stop TTS if playing
            tts?.stop()
            _currentlyPlayingText.value = null

            if (mediaPlayer == null) {
                try {
                    mediaPlayer = MediaPlayer().apply {
                        setDataSource(path)
                        prepare()
                        setOnCompletionListener {
                            _isPlayingUserVoice.value = false
                            _userVoicePosition.value = _userVoiceDuration.value
                            playbackTrackerJob?.cancel()
                        }
                    }
                    _userVoiceDuration.value = mediaPlayer!!.duration / 1000f
                } catch (e: Exception) {
                    Log.e("HiwarViewModel", "MediaPlayer Error", e)
                    return
                }
            } else if (_userVoiceDuration.value > 0f && _userVoicePosition.value >= (_userVoiceDuration.value - 0.05f)) {
                mediaPlayer?.seekTo(0)
                _userVoicePosition.value = 0f
            }
            mediaPlayer?.start()
            _isPlayingUserVoice.value = true
            startUserVoiceTracking()
        }
    }

    private fun startUserVoiceTracking() {
        playbackTrackerJob?.cancel()
        playbackTrackerJob = viewModelScope.launch {
            while (_isPlayingUserVoice.value) {
                mediaPlayer?.let {
                    if (it.isPlaying) {
                        _userVoicePosition.value = it.currentPosition / 1000f
                    }
                }
                delay(50)
            }
        }
    }

    fun seekUserVoice(positionSec: Float) {
        mediaPlayer?.let {
            try {
                it.seekTo((positionSec * 1000).toInt())
                _userVoicePosition.value = positionSec
            } catch (e: Exception) {
                Log.e("HiwarViewModel", "Seek Error", e)
            }
        }
    }

    fun setRecordedVoice(path: String) {
        _userVoicePath.value = path
        _userWaveform.value = List(40) { Random.nextFloat() * 0.7f + 0.1f }

        _userVoiceDuration.value = readAudioDurationSeconds(path)
        _userVoicePosition.value = 0f

        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun readAudioDurationSeconds(path: String): Float {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(path)
            val durationMs = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L
            durationMs / 1000f
        } catch (e: Exception) {
            Log.e("HiwarViewModel", "Duration read error", e)
            0f
        } finally {
            retriever.release()
        }
    }

    fun clearRecordedResponse() {
        tts?.stop()
        _currentlyPlayingText.value = null
        _userVoicePath.value = null
        _isPlayingUserVoice.value = false
        _userVoicePosition.value = 0f
        _userVoiceDuration.value = 0f
        _userWaveform.value = emptyList()
        _aiFeedback.value = null
        mediaPlayer?.release()
        mediaPlayer = null
        playbackTrackerJob?.cancel()
    }

    fun showSpeechCaptureHint(language: String) {
        _aiFeedback.value = if (language == "en") {
            AIFeedbackData(0, "I couldn't catch your speech. Please try again.", "Speak a little closer to the mic.")
        } else {
            AIFeedbackData(0, "Ucapan belum tertangkap. Coba ulangi lagi.", "Dekatkan mic dan bicara lebih jelas.")
        }
    }

    fun transcribeAudio(filePath: String, question: String, moduleTitle: String, moduleContent: String) {
        val file = File(filePath)
        if (!file.exists()) return

        viewModelScope.launch {
            _isTranscribing.value = true
            _aiFeedback.value = null
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
                    checkHiwarResponse(question, result.text, moduleTitle, moduleContent)
                } else {
                    Log.e("HiwarViewModel", "Transcription failed: ${response.status}")
                    _aiFeedback.value = AIFeedbackData(0, "Gagal mengenali suara.", "Pastikan suara terdengar jelas.")
                }
            } catch (e: Exception) {
                Log.e("HiwarViewModel", "Transcription error", e)
                _aiFeedback.value = AIFeedbackData(0, "Error Transkripsi.", "Cek koneksi internet.")
            } finally {
                _isTranscribing.value = false
            }
        }
    }

    fun checkHiwarResponse(question: String, userSpeech: String, moduleTitle: String, moduleContent: String) {
        if (userSpeech.isBlank()) return

        val responseLanguage = Localization.responseLanguageFor(userSpeech)

        viewModelScope.launch {
            _isLoading.value = true
            _aiFeedback.value = null
            try {
                val systemPrompt = if (responseLanguage == "en") {
                    """
                        Act as an interactive Arabic teacher.
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
                        Berperanlah sebagai guru Bahasa Arab yang interaktif.
                        Tugas utama kamu adalah menilai jawaban user yang berupa Speech-to-Text.
                        
                        FORMAT OUTPUT HARUS JSON MURNI DENGAN KEYS:
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
                        Topic: "$moduleTitle"
                        Material: "$moduleContent"
                        
                        User answered the question: "$question".
                        User speech: "$userSpeech".
                        
                        1. Score (0-100): Evaluate pronunciation and topic accuracy generously.
                        2. Feedback: One short encouraging or corrective sentence in English.
                        3. Tips: One very short tip (max 10 words) for makhraj or word choice.
                    """.trimIndent()
                } else {
                    """
                    Topik Materi: "$moduleTitle"
                    Isi Materi: "$moduleContent"
                    
                    User merespons pertanyaan: "$question".
                    Jawaban user: "$userSpeech".
                    
                    1. Skor (0-100): Nilai kemiripan bunyi & konteks topik. Berikan nilai toleran (80-100 jika benar secara konteks).
                    2. Feedback: 1 kalimat apresiasi/koreksi ringan dalam Bahasa Indonesia.
                    3. Tips: 1 tips sangat singkat (maksimal 10 kata) untuk makhraj/pilihan kata.
                    """.trimIndent()
                }

                val url = "https://api.groq.com/openai/v1/chat/completions"

                val requestBody = GroqHiwarRequest(
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

                if (response.status == HttpStatusCode.TooManyRequests) {
                    _aiFeedback.value = AIFeedbackData(0, "Limit AI tercapai.", "Tunggu sebentar.")
                    return@launch
                }

                if (!response.status.isSuccess()) {
                    _aiFeedback.value = AIFeedbackData(0, "Error AI.", "Coba lagi.")
                    return@launch
                }

                val groqResponse: GroqHiwarResponse = response.body()
                val rawContent = groqResponse.choices?.firstOrNull()?.message?.content

                if (!rawContent.isNullOrBlank()) {
                    val jsonParser = Json { ignoreUnknownKeys = true }
                    _aiFeedback.value = jsonParser.decodeFromString<AIFeedbackData>(rawContent)
                }
            } catch (e: Exception) {
                Log.e("HiwarViewModel", "Error AI", e)
                _aiFeedback.value = AIFeedbackData(0, "Gagal terhubung AI.", "Cek internet.")
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
        mediaPlayer?.release()
        client.close()
    }
}
