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
    val otherVocabHeadingTemplate: String = "Kosakata Lainnya di %s"
)

object AppSettingsStore {
    private const val PREFS_NAME = "app_settings"
    private const val KEY_LANGUAGE = "language"
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
            otherVocabHeadingTemplate = prefs.getString(KEY_OTHER_VOCAB_TEMPLATE, fallback.otherVocabHeadingTemplate).orEmpty()
        )
    }

    private fun save(context: Context, settings: AppSettings) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_LANGUAGE, settings.language)
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
