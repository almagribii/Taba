package com.fadhil.taba.data.settings

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

data class AppSettings(
    val language: String = "in", // "in" for Indonesia, "en" for English
    val isFullHarakat: Boolean = true,
    val displayName: String = "",
    val avatarPath: String? = null,
    val darkMode: Boolean = false,
    val homeHeroTitle: String = "Ayo Mulai Berbicara!",
    val homeHeroSubtitle: String = "Latih percakapan, kosakata, dan pelafalan Bahasa Arab dengan bantuan AI.",
    val homeActionText: String = "Mulai Belajar",
    val homeSectionTitle: String = "Daftar Materi",
    val homeSectionActionText: String = "Lihat Semua >",
    val materiBannerTitle: String = "6 Materi Interaktif",
    val materiBannerSubtitle: String = "Latihan kosakata + hiwar + pelafalan",
    val searchPlaceholder: String = "Cari materi...",
    val mufrodatTitle: String = "Al-Mufradat",
    val mufrodatSubtitle: String = "الْمُفْرَدَاتُ",
    val mufrodatMaterialsLabel: String = "Materi",
    val mufrodatPracticeTitle: String = "Ucapkan kata ini",
    val mufrodatPracticeSubtitle: String = "Tekan tombol mic dan ucapkan kata di atas",
    val otherVocabHeadingTemplate: String = "Kosakata Lainnya di %s",
    val audioSpeed: Float = 1.0f,
    val voiceGender: String = "female", // "male" or "female"
    val mufrodatFullHarakat: Boolean = true,
    val mufrodatHorizontalLayout: Boolean = false,
    val starredVocabKeys: Set<String> = emptySet()
)

object AppSettingsStore {
    private const val PREFS_NAME = "app_settings"
    private const val KEY_LANGUAGE = "language"
    private const val KEY_FULL_HARAKAT = "is_full_harakat"
    private const val KEY_DISPLAY_NAME = "display_name"
    private const val KEY_AVATAR_PATH = "avatar_path"
    private const val KEY_DARK_MODE = "dark_mode"
    private const val KEY_HOME_HERO_TITLE = "home_hero_title"
    private const val KEY_HOME_HERO_SUBTITLE = "home_hero_subtitle"
    private const val KEY_HOME_ACTION_TEXT = "home_action_text"
    private const val KEY_HOME_SECTION_TITLE = "home_section_title"
    private const val KEY_HOME_SECTION_ACTION_TEXT = "home_section_action_text"
    private const val KEY_MATERI_BANNER_TITLE = "materi_banner_title"
    private const val KEY_MATERI_BANNER_SUBTITLE = "materi_banner_subtitle"
    private const val KEY_SEARCH_PLACEHOLDER = "search_placeholder"
    private const val KEY_MUFRODAT_TITLE = "mufrodat_title"
    private const val KEY_MUFRODAT_SUBTITLE = "mufrodat_subtitle"
    private const val KEY_MUFRODAT_MATERIALS_LABEL = "mufrodat_materials_label"
    private const val KEY_MUFRODAT_PRACTICE_TITLE = "mufrodat_practice_title"
    private const val KEY_MUFRODAT_PRACTICE_SUBTITLE = "mufrodat_practice_subtitle"
    private const val KEY_OTHER_VOCAB_TEMPLATE = "other_vocab_template"

    private val lock = Any()
    private var initialized = false
    private val fallback = AppSettings()
    private val _settings = MutableStateFlow(fallback)
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    private const val KEY_AUDIO_SPEED = "audio_speed"
    private const val KEY_VOICE_GENDER = "voice_gender"
    private const val KEY_MUFRODAT_FULL_HARAKAT = "mufrodat_full_harakat"
    private const val KEY_MUFRODAT_HORIZONTAL_LAYOUT = "mufrodat_horizontal_layout"
    private const val KEY_STARRED_VOCAB = "starred_vocab"

    fun initialize(context: Context) {
        synchronized(lock) {
            if (initialized) return
            _settings.value = load(context.applicationContext)
            initialized = true
        }
    }

    fun update(context: Context, transform: (AppSettings) -> AppSettings) {
        initialize(context)
        val updated = transform(_settings.value)
        save(context.applicationContext, updated)
        _settings.value = updated
    }

    fun setLanguage(context: Context, language: String) {
        update(context) { settings ->
            val updated = settings.copy(language = language)
            if (language == "en") {
                updated.copy(
                    homeHeroTitle = "Let's Start Speaking!",
                    homeHeroSubtitle = "Practice Arabic conversation, vocabulary, and pronunciation with AI help.",
                    homeActionText = "Start Learning",
                    homeSectionTitle = "Material List",
                    homeSectionActionText = "See All >",
                    materiBannerTitle = "6 Interactive Materials",
                    materiBannerSubtitle = "Vocabulary + hiwar + pronunciation practice",
                    searchPlaceholder = "Search material...",
                    mufrodatTitle = "Vocabulary",
                    mufrodatMaterialsLabel = "Material",
                    mufrodatPracticeTitle = "Speak this word",
                    mufrodatPracticeSubtitle = "Press the mic button and speak the word above",
                    otherVocabHeadingTemplate = "Other Vocabulary in %s"
                )
            } else {
                updated.copy(
                    homeHeroTitle = "Ayo Mulai Berbicara!",
                    homeHeroSubtitle = "Latih percakapan, kosakata, dan pelafalan Bahasa Arab dengan bantuan AI.",
                    homeActionText = "Mulai Belajar",
                    homeSectionTitle = "Daftar Materi",
                    homeSectionActionText = "Lihat Semua >",
                    materiBannerTitle = "6 Materi Interaktif",
                    materiBannerSubtitle = "Latihan kosakata + hiwar + pelafalan",
                    searchPlaceholder = "Cari materi...",
                    mufrodatTitle = "Al-Mufradat",
                    mufrodatMaterialsLabel = "Materi",
                    mufrodatPracticeTitle = "Ucapkan kata ini",
                    mufrodatPracticeSubtitle = "Tekan tombol mic dan ucapkan kata di atas",
                    otherVocabHeadingTemplate = "Kosakata Lainnya di %s"
                )
            }
        }
    }

    fun setFullHarakat(context: Context, enabled: Boolean) {
        update(context) { it.copy(isFullHarakat = enabled) }
    }

    fun setDisplayName(context: Context, value: String) {
        update(context) { it.copy(displayName = value) }
    }

    fun setDarkMode(context: Context, enabled: Boolean) {
        update(context) { it.copy(darkMode = enabled) }
    }

    fun setHomeHeroTitle(context: Context, value: String) {
        update(context) { it.copy(homeHeroTitle = value) }
    }

    fun setHomeHeroSubtitle(context: Context, value: String) {
        update(context) { it.copy(homeHeroSubtitle = value) }
    }

    fun setHomeActionText(context: Context, value: String) {
        update(context) { it.copy(homeActionText = value) }
    }

    fun setHomeSectionTitle(context: Context, value: String) {
        update(context) { it.copy(homeSectionTitle = value) }
    }

    fun setHomeSectionActionText(context: Context, value: String) {
        update(context) { it.copy(homeSectionActionText = value) }
    }

    fun setMateriBannerTitle(context: Context, value: String) {
        update(context) { it.copy(materiBannerTitle = value) }
    }

    fun setMateriBannerSubtitle(context: Context, value: String) {
        update(context) { it.copy(materiBannerSubtitle = value) }
    }

    fun setSearchPlaceholder(context: Context, value: String) {
        update(context) { it.copy(searchPlaceholder = value) }
    }

    fun setMufrodatTitle(context: Context, value: String) {
        update(context) { it.copy(mufrodatTitle = value) }
    }

    fun setMufrodatSubtitle(context: Context, value: String) {
        update(context) { it.copy(mufrodatSubtitle = value) }
    }

    fun setMufrodatMaterialsLabel(context: Context, value: String) {
        update(context) { it.copy(mufrodatMaterialsLabel = value) }
    }

    fun setMufrodatPracticeTitle(context: Context, value: String) {
        update(context) { it.copy(mufrodatPracticeTitle = value) }
    }

    fun setMufrodatPracticeSubtitle(context: Context, value: String) {
        update(context) { it.copy(mufrodatPracticeSubtitle = value) }
    }

    fun setOtherVocabHeadingTemplate(context: Context, value: String) {
        update(context) { it.copy(otherVocabHeadingTemplate = value) }
    }

    fun setAudioSpeed(context: Context, speed: Float) {
        update(context) { it.copy(audioSpeed = speed) }
    }

    fun setVoiceGender(context: Context, gender: String) {
        update(context) { it.copy(voiceGender = gender) }
    }

    fun setMufrodatFullHarakat(context: Context, enabled: Boolean) {
        update(context) { it.copy(mufrodatFullHarakat = enabled) }
    }

    fun setMufrodatHorizontalLayout(context: Context, enabled: Boolean) {
        update(context) { it.copy(mufrodatHorizontalLayout = enabled) }
    }

    fun toggleStar(context: Context, moduleId: Int, arabic: String) {
        update(context) { settings ->
            val key = "${moduleId}_$arabic"
            val current = settings.starredVocabKeys.toMutableSet()
            if (current.contains(key)) current.remove(key) else current.add(key)
            settings.copy(starredVocabKeys = current)
        }
    }

    fun setAvatarFromUri(context: Context, uri: Uri?) {
        initialize(context)
        val updatedPath = if (uri == null) {
            deleteAvatarFile(context.applicationContext)
            null
        } else {
            copyAvatarToInternalStorage(context.applicationContext, uri)
        }
        update(context) { it.copy(avatarPath = updatedPath) }
    }

    fun reset(context: Context) {
        deleteAvatarFile(context.applicationContext)
        val defaults = fallback
        save(context.applicationContext, defaults)
        _settings.value = defaults
    }

    private fun load(context: Context): AppSettings {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return AppSettings(
            language = prefs.getString(KEY_LANGUAGE, fallback.language) ?: fallback.language,
            isFullHarakat = prefs.getBoolean(KEY_FULL_HARAKAT, fallback.isFullHarakat),
            displayName = prefs.getString(KEY_DISPLAY_NAME, fallback.displayName).orEmpty(),
            avatarPath = prefs.getString(KEY_AVATAR_PATH, fallback.avatarPath),
            darkMode = prefs.getBoolean(KEY_DARK_MODE, fallback.darkMode),
            homeHeroTitle = prefs.getString(KEY_HOME_HERO_TITLE, fallback.homeHeroTitle).orEmpty(),
            homeHeroSubtitle = prefs.getString(KEY_HOME_HERO_SUBTITLE, fallback.homeHeroSubtitle).orEmpty(),
            homeActionText = prefs.getString(KEY_HOME_ACTION_TEXT, fallback.homeActionText).orEmpty(),
            homeSectionTitle = prefs.getString(KEY_HOME_SECTION_TITLE, fallback.homeSectionTitle).orEmpty(),
            homeSectionActionText = prefs.getString(KEY_HOME_SECTION_ACTION_TEXT, fallback.homeSectionActionText).orEmpty(),
            materiBannerTitle = prefs.getString(KEY_MATERI_BANNER_TITLE, fallback.materiBannerTitle).orEmpty(),
            materiBannerSubtitle = prefs.getString(KEY_MATERI_BANNER_SUBTITLE, fallback.materiBannerSubtitle).orEmpty(),
            searchPlaceholder = prefs.getString(KEY_SEARCH_PLACEHOLDER, fallback.searchPlaceholder).orEmpty(),
            mufrodatTitle = prefs.getString(KEY_MUFRODAT_TITLE, fallback.mufrodatTitle).orEmpty(),
            mufrodatSubtitle = prefs.getString(KEY_MUFRODAT_SUBTITLE, fallback.mufrodatSubtitle).orEmpty(),
            mufrodatMaterialsLabel = prefs.getString(KEY_MUFRODAT_MATERIALS_LABEL, fallback.mufrodatMaterialsLabel).orEmpty(),
            mufrodatPracticeTitle = prefs.getString(KEY_MUFRODAT_PRACTICE_TITLE, fallback.mufrodatPracticeTitle).orEmpty(),
            mufrodatPracticeSubtitle = prefs.getString(KEY_MUFRODAT_PRACTICE_SUBTITLE, fallback.mufrodatPracticeSubtitle).orEmpty(),
            otherVocabHeadingTemplate = prefs.getString(KEY_OTHER_VOCAB_TEMPLATE, fallback.otherVocabHeadingTemplate).orEmpty(),
            audioSpeed = prefs.getFloat(KEY_AUDIO_SPEED, fallback.audioSpeed),
            voiceGender = prefs.getString(KEY_VOICE_GENDER, fallback.voiceGender) ?: fallback.voiceGender,
            mufrodatFullHarakat = prefs.getBoolean(KEY_MUFRODAT_FULL_HARAKAT, fallback.mufrodatFullHarakat),
            mufrodatHorizontalLayout = prefs.getBoolean(KEY_MUFRODAT_HORIZONTAL_LAYOUT, fallback.mufrodatHorizontalLayout),
            starredVocabKeys = prefs.getStringSet(KEY_STARRED_VOCAB, emptySet()) ?: emptySet()
        )
    }

    private fun save(context: Context, settings: AppSettings) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_LANGUAGE, settings.language)
            .putBoolean(KEY_FULL_HARAKAT, settings.isFullHarakat)
            .putString(KEY_DISPLAY_NAME, settings.displayName)
            .putString(KEY_AVATAR_PATH, settings.avatarPath)
            .putBoolean(KEY_DARK_MODE, settings.darkMode)
            .putString(KEY_HOME_HERO_TITLE, settings.homeHeroTitle)
            .putString(KEY_HOME_HERO_SUBTITLE, settings.homeHeroSubtitle)
            .putString(KEY_HOME_ACTION_TEXT, settings.homeActionText)
            .putString(KEY_HOME_SECTION_TITLE, settings.homeSectionTitle)
            .putString(KEY_HOME_SECTION_ACTION_TEXT, settings.homeSectionActionText)
            .putString(KEY_MATERI_BANNER_TITLE, settings.materiBannerTitle)
            .putString(KEY_MATERI_BANNER_SUBTITLE, settings.materiBannerSubtitle)
            .putString(KEY_SEARCH_PLACEHOLDER, settings.searchPlaceholder)
            .putString(KEY_MUFRODAT_TITLE, settings.mufrodatTitle)
            .putString(KEY_MUFRODAT_SUBTITLE, settings.mufrodatSubtitle)
            .putString(KEY_MUFRODAT_MATERIALS_LABEL, settings.mufrodatMaterialsLabel)
            .putString(KEY_MUFRODAT_PRACTICE_TITLE, settings.mufrodatPracticeTitle)
            .putString(KEY_MUFRODAT_PRACTICE_SUBTITLE, settings.mufrodatPracticeSubtitle)
            .putString(KEY_OTHER_VOCAB_TEMPLATE, settings.otherVocabHeadingTemplate)
            .putFloat(KEY_AUDIO_SPEED, settings.audioSpeed)
            .putString(KEY_VOICE_GENDER, settings.voiceGender)
            .putBoolean(KEY_MUFRODAT_FULL_HARAKAT, settings.mufrodatFullHarakat)
            .putBoolean(KEY_MUFRODAT_HORIZONTAL_LAYOUT, settings.mufrodatHorizontalLayout)
            .putStringSet(KEY_STARRED_VOCAB, settings.starredVocabKeys)
            .apply()
    }

    private fun deleteAvatarFile(context: Context) {
        val avatarFile = File(context.filesDir, "user_avatar.jpg")
        if (avatarFile.exists() && !avatarFile.delete()) {
            throw IOException("Gagal menghapus avatar lama")
        }
    }

    private fun copyAvatarToInternalStorage(context: Context, uri: Uri): String {
        val targetFile = File(context.filesDir, "user_avatar.jpg")
        context.contentResolver.openInputStream(uri).use { input ->
            if (input == null) {
                throw IOException("Tidak dapat membaca gambar yang dipilih")
            }
            FileOutputStream(targetFile, false).use { output ->
                input.copyTo(output)
            }
        }
        return targetFile.absolutePath
    }
}
