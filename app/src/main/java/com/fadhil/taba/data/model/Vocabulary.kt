package com.fadhil.taba.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Vocabulary(
    val id: Long? = null,
    @SerialName("lesson_id") val lessonId: Long,
    @SerialName("word_ar") val wordAr: String,
    @SerialName("word_ar_clean") val wordArClean: String,
    @SerialName("word_id") val wordId: String,
    @SerialName("illustration_url") val illustrationUrl: String? = null,
    @SerialName("audio_male_url") val audioMaleUrl: String? = null,
    @SerialName("audio_female_url") val audioFemaleUrl: String? = null
)
