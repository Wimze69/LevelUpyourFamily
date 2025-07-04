@file:Suppress("SpellCheckingInspection")

package com.lindberg.levelupyourkids.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * En dataklass som representerar en länk i bottenmenyn.
 *
 * @param title Namnet på skärmen.
 * @param icon Ikonen som ska visas.
 * @param route Den unika "adressen" till skärmen.
 */
sealed class BottomNavItem(val title: String, val icon: ImageVector, val route: String) {
    object Home : BottomNavItem("Hem", Icons.Default.Home, "home_screen")
    object Profile : BottomNavItem("Profil", Icons.Default.Person, "profile_screen")
    object Settings : BottomNavItem("Inställningar", Icons.Default.Settings, "settings_screen")
}
