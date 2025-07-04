package com.lindberg.levelupyourkids.data

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties

enum class PlayerRole {
    PLAYER, GM, ADMIN
}

@Keep
@IgnoreExtraProperties
data class PlayerProfile(
    val id: String = "",
    val familyId: String? = null,
    val displayName: String = "",
    val email: String = "",
    val photoUrl: String? = null,
    val role: PlayerRole = PlayerRole.PLAYER,
    val character: Character? = null,

    // KORRIGERING: Lade till det saknade fältet för level.
    val seasonalLevel: Int = 1,
    val seasonalXp: Int = 0,
    val totalXp: Int = 0,
    val inventory: List<String> = emptyList(),

    val lastSeasonReset: Timestamp? = null
)
