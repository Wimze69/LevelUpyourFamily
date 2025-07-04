package com.lindberg.levelupyourkids.data

import androidx.annotation.DrawableRes

/**
 * Representerar en avatar.
 *
 * @param id Ett unikt ID för avataren.
 * @param name Avatarens namn som visas i UI.
 * @param imageResId Referensen (ID) till bildfilen i drawable-mappen.
 * @DrawableRes ser till att vi bara kan använda giltiga bildresurser.
 */
data class Avatar(
    val id: String,
    val name: String,
    @DrawableRes val imageResId: Int
)
