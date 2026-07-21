package com.fadhil.taba.data.model

data class Module(
    val id: Int,
    val title: String,
    val titleEn: String,
    val arabicTitle: String,
    val content: String,
    val vocabularies: List<ModuleVocabulary>,
    val questions: List<ModuleQuestion>,
    val imageResId: Int
)

data class ModuleQuestion(
    val arabic: String,
    val indonesian: String,
    val english: String
)

data class ModuleVocabulary(
    val arabic: String,
    val indonesian: String,
    val english: String,
    val imageResId: Int? = null,
    val isStarred: Boolean = false
)
