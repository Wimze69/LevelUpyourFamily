package com.lindberg.levelupyourkids.navigation

sealed class AppScreen(val route: String) {
    object SignIn : AppScreen("sign_in_screen")
    object FamilyOnboarding : AppScreen("family_onboarding_screen")
    object CharacterCreation : AppScreen("character_creation_screen")
    object Home : AppScreen("home_screen")
}