package com.fadhil.taba.data.model

data class Module(
    val id: Int,
    val title: String,
    val arabicTitle: String,
    val content: String,
    val vocabularies: List<ModuleVocabulary>,
    val questions: List<String>,
    val imageResId: Int
)

data class ModuleVocabulary(
    val arabic: String,
    val indonesian: String,
    val imageResId: Int? = null
)
