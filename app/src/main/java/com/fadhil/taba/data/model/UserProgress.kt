package com.fadhil.taba.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserProgress(
    val id: Long? = null,
    @SerialName("user_id") val userId: String,
    @SerialName("lesson_id") val lessonId: Long,
    @SerialName("completed_vocabulary_ids") val completedVocabularyIds: List<Long> = emptyList(),
    @SerialName("is_hiwar_completed") val isHiwarCompleted: Boolean = false,
    @SerialName("is_lesson_completed") val isLessonCompleted: Boolean = false,
    @SerialName("updated_at") val updatedAt: String? = null
)
