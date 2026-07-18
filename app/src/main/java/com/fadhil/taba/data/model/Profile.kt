package com.fadhil.taba.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val id: String,
    val username: String,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    @SerialName("xp_points") val xpPoints: Int = 0,
    val level: Int = 1,
    @SerialName("streak_count") val streakCount: Int = 0,
    
    // Preferensi Belajar
    @SerialName("is_full_harakat") val isFullHarakat: Boolean = true,
    @SerialName("audio_speed") val audioSpeed: String = "normal",
    @SerialName("voice_gender") val voiceGender: String = "male",
    @SerialName("is_notification_enabled") val isNotificationEnabled: Boolean = true,
    @SerialName("is_mic_permission_granted") val isMicPermissionGranted: Boolean = false,
    
    @SerialName("last_active_at") val lastActiveAt: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)
