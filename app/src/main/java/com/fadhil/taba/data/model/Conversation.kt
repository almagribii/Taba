package com.fadhil.taba.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Conversation(
    val id: Long? = null,
    @SerialName("lesson_id") val lessonId: Long,
    @SerialName("speaker_role") val speakerRole: String, // 'speaker_1' or 'speaker_2'
    @SerialName("text_ar") val textAr: String,
    @SerialName("text_id") val textId: String,
    @SerialName("audio_male_url") val audioMaleUrl: String? = null,
    @SerialName("audio_female_url") val audioFemaleUrl: String? = null,
    @SerialName("order_index") val orderIndex: Int
)
