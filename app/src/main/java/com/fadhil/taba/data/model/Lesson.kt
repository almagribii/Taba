package com.fadhil.taba.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Lesson(
    val id: Long? = null,
    @SerialName("title_id") val titleId: String,
    @SerialName("title_ar") val titleAr: String,
    val description: String? = null,
    @SerialName("illustration_url") val illustrationUrl: String? = null,
    @SerialName("order_index") val orderIndex: Int,
    @SerialName("total_mufradat_count") val totalMufradatCount: Int = 0,
    @SerialName("total_hiwar_count") val totalHiwarCount: Int = 0
)
