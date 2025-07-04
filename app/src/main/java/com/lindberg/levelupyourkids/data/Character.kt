package com.lindberg.levelupyourkids.data

data class Character(
    val avatarId: String = "",
    val characterClass: CharacterClass? = null, // Lagra hela klassobjektet
    val backgroundColorValue: Long = 0L,
    val seasonalLevel: Int = 1,
    val seasonalXp: Int = 0,
    val totalXp: Int = 0,
    val inventory: List<String> = emptyList() // Lista med ID:n till Item-objekt
)