package com.lindberg.levelupyourkids.data

import kotlin.math.ceil

/**
 * Beräknar hur mycket XP som krävs för att nå nästa säsongs-level.
 * Baserat på formeln från Flutter-projektet: 15 + (seasonalLevel / 2).ceil()
 */
val PlayerProfile.xpForNextSeasonalLevel: Int
    // Vi lägger till 1 för att seasonalLevel representerar den *nuvarande* nivån.
    get() = 15 + ceil((this.seasonalLevel + 1).toDouble() / 2).toInt()

/**
 * Beräknar nuvarande framsteg (mellan 0.0 och 1.0) till nästa level.
 * Detta värde används för att rita upp den cirkulära mätaren.
 */
val PlayerProfile.progressToNextLevel: Float
    get() {
        val xpNeeded = xpForNextSeasonalLevel
        // Säkerställer att vi inte delar med noll och att värdet är mellan 0 och 1.
        return if (xpNeeded > 0) {
            (this.seasonalXp.toFloat() / xpNeeded).coerceIn(0f, 1f)
        } else {
            0f
        }
    }
