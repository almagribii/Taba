package com.fadhil.taba.data.settings

object Localization {
    fun getString(key: String, language: String): String {
        return when (language) {
            "en" -> enStrings[key] ?: key
            else -> inStrings[key] ?: key
        }
    }

    fun prefersEnglishResponse(text: String): Boolean {
        val normalized = text.lowercase()
        val tokens = Regex("[a-z']+").findAll(normalized).map { it.value }.toList()
        if (tokens.isEmpty()) return false

        val englishMarkers = setOf(
            "the", "and", "you", "are", "what", "how", "why", "please", "thanks",
            "thank", "hello", "hi", "i", "to", "of", "for", "is", "it", "this", "that"
        )
        val indonesianMarkers = setOf(
            "yang", "dan", "tidak", "apa", "bagaimana", "saya", "kami", "mereka",
            "untuk", "dengan", "ini", "itu", "tolong", "terima", "kasih", "halo"
        )

        val englishScore = tokens.count { it in englishMarkers }
        val indonesianScore = tokens.count { it in indonesianMarkers }

        return englishScore > indonesianScore
    }

    fun responseLanguageFor(text: String, defaultLanguage: String = "in"): String {
        return if (defaultLanguage == "en" || prefersEnglishResponse(text)) "en" else "in"
    }

    private val inStrings = mapOf(
        "settings_title" to "Pengaturan",
        "active_learner" to "Pembelajar Aktif",
        "level" to "Level",
        "general_settings" to "Pengaturan Umum",
        "user_profile" to "Profil Pengguna",
        "app_language" to "Bahasa Aplikasi",
        "sound_volume" to "Suara & Volume",
        "learning_notifications" to "Notifikasi Belajar",
        "microphone_permission" to "Izin Mikrofon",
        "full_harakat_mode" to "Mode Harakat Penuh",
        "full_harakat_subtitle" to "Tampilkan semua harakat dalam teks Arab",
        "progress_history" to "Riwayat Progres",
        "learning_preferences" to "Preferensi Belajar",
        "audio_speed" to "Kecepatan Audio",
        "voice_gender" to "Suara Laki-laki / Perempuan",
        "help" to "Bantuan",
        "privacy_policy" to "Kebijakan Privasi",
        "sign_out" to "Keluar",
        "indonesian" to "Bahasa Indonesia",
        "english" to "Bahasa Inggris",
        "select_language" to "Pilih Bahasa",
        "cancel" to "Batal",
        "male" to "Laki-laki",
        "female" to "Perempuan",
        "normal" to "Normal",
        "text_settings" to "Pengaturan Teks",
        "text_size" to "Ukuran Teks",
        "show_harakat" to "Tampilkan Harakat",
        "vertical_layout" to "Layar Horizontal (Landscape)",
        "back" to "Kembali",
        "practice_now" to "Latihan Sekarang",
        "see_all_vocab" to "Lihat Semua Mufrodat (%d)",
        "questions" to "الأسئلة (Pertanyaan)",
        "vocabulary" to "المفردات (Kosakata)",
        "home" to "Beranda",
        "materi" to "Materi",
        "chat_ai" to "Tanya AI",
        "chat_empty_title" to "Siap mulai obrolan",
        "chat_empty_body" to "Ketik pertanyaanmu atau kirim voice note. AI akan menjawab dalam bahasa yang kamu pakai.",
        "settings" to "Pengaturan",
        "listen" to "Dengarkan",
        "speak" to "Ucapkan",
        "next" to "Berikutnya",
        "ai_feedback" to "Umpan Balik AI",
        "feedback_placeholder_title" to "Belum ada evaluasi",
        "feedback_placeholder_body" to "Rekam atau kirim jawaban dulu, lalu AI akan memberikan umpan balik.",
        "feedback_placeholder_hint" to "AI akan menilai setelah jawaban masuk",
        "see_all" to "Lihat Semua >",
        "ai_note" to "Fitur AI memerlukan koneksi internet",
        "pronunciation" to "Pelafalan",
        "sign_in_google" to "Masuk dengan Google",
        "login_failed" to "Gagal masuk",
        "no_materi_found" to "Materi tidak ditemukan",
        "exercises_completed" to "%d/5 latihan selesai",
        "hiwar_practice" to "Latihan Percakapan",
        "hiwar_headline" to "Latihan berbicara dengan AI.",
        "conversation" to "Percakapan",
        "answer_voice" to "Jawab dengan Voice Note",
        "previous" to "Sebelumnya",
        "current_topic" to "Topik Saat Ini",
        "hiwar_title" to "Al-Hiwar",
        "hiwar_subtitle" to "Bicara interaktif dengan bantuan AI",
        "chat_ai_hint" to "Tanya Taba AI..."
    )

    private val enStrings = mapOf(
        "settings_title" to "Settings",
        "active_learner" to "Active Learner",
        "level" to "Level",
        "general_settings" to "General Settings",
        "user_profile" to "User Profile",
        "app_language" to "App Language",
        "sound_volume" to "Sound & Volume",
        "learning_notifications" to "Learning Notifications",
        "microphone_permission" to "Microphone Permissions",
        "full_harakat_mode" to "Full Harakat Mode",
        "full_harakat_subtitle" to "Show all harakat in Arabic text",
        "progress_history" to "Progress History",
        "learning_preferences" to "Learning Preferences",
        "audio_speed" to "Audio Speed",
        "voice_gender" to "Male / Female Voice",
        "help" to "Help",
        "privacy_policy" to "Privacy Policy",
        "sign_out" to "Sign Out",
        "indonesian" to "Indonesian",
        "english" to "English",
        "select_language" to "Select Language",
        "cancel" to "Cancel",
        "male" to "Male",
        "female" to "Female",
        "normal" to "Normal",
        "text_settings" to "Text Settings",
        "text_size" to "Text Size",
        "show_harakat" to "Show Harakat",
        "vertical_layout" to "Horizontal Screen (Landscape)",
        "back" to "Back",
        "practice_now" to "Practice Now",
        "see_all_vocab" to "See All Vocab (%d)",
        "questions" to "الأسئلة (Questions)",
        "vocabulary" to "المفردات (Vocabulary)",
        "home" to "Home",
        "materi" to "Lessons",
        "chat_ai" to "Ask AI",
        "chat_empty_title" to "Ready to start chatting",
        "chat_empty_body" to "Type your question or send a voice note. AI will reply in the language you use.",
        "settings" to "Settings",
        "listen" to "Listen",
        "speak" to "Speak",
        "next" to "Next",
        "ai_feedback" to "AI Feedback",
        "feedback_placeholder_title" to "No evaluation yet",
        "feedback_placeholder_body" to "Send your answer first, then AI will give feedback.",
        "feedback_placeholder_hint" to "AI will review after your answer is submitted",
        "see_all" to "See All >",
        "ai_note" to "AI features require internet connection",
        "pronunciation" to "Pronunciation",
        "sign_in_google" to "Sign in with Google",
        "login_failed" to "Login failed",
        "no_materi_found" to "Material not found",
        "exercises_completed" to "%d/5 exercises completed",
        "hiwar_practice" to "Conversation Practice",
        "hiwar_headline" to "Practice with AI to improve your speaking skills.",
        "conversation" to "Conversation",
        "answer_voice" to "Answer with Voice Note",
        "previous" to "Previous",
        "current_topic" to "Current Topic",
        "hiwar_title" to "Al-Hiwar",
        "hiwar_subtitle" to "Speak interactively with AI assistance",
        "chat_ai_hint" to "Ask Taba AI..."
    )
}
