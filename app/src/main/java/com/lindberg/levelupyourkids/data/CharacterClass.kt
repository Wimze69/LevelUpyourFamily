package com.lindberg.levelupyourkids.data

enum class TraitType { RESIST_LEVEL_DOWN, EXTRA_LOOT, XP_BOOST }

data class CharacterClass(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val trait: Trait = Trait()
)

data class Trait(
    val type: TraitType = TraitType.XP_BOOST,
    val description: String = "",
    val chance: Double = 0.0,
    val value: Double = 0.0
)
