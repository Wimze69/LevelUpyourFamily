package com.lindberg.levelupyourkids.data

enum class ItemType { SKIN, WEAPON, CONSUMABLE, MATERIAL }

data class Item(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val type: ItemType = ItemType.MATERIAL
)
