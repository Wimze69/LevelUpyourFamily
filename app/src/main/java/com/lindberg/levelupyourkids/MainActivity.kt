package com.lindberg.levelupyourkids

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.lindberg.levelupyourkids.navigation.AppScreen
import com.lindberg.levelupyourkids.navigation.BottomNavItem
import com.lindberg.levelupyourkids.ui.auth.SignInScreen
import com.lindberg.levelupyourkids.ui.character.CharacterCreationRoute
import com.lindberg.levelupyourkids.ui.family.FamilyOnboardingScreen
import com.lindberg.levelupyourkids.ui.home.HomeScreenRoute
import com.lindberg.levelupyourkids.ui.profile.ProfileScreen
import com.lindberg.levelupyourkids.ui.settings.SettingsScreen
import com.lindberg.levelupyourkids.ui.theme.ChoreQuestTheme

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ChoreQuestTheme {
                val session by mainViewModel.session.collectAsStateWithLifecycle()
                val navController = rememberNavController()

                LaunchedEffect(session.authState) {
                    val destination = when (session.authState) {
                        AuthState.LOGGED_OUT -> AppScreen.SignIn.route
                        AuthState.NEEDS_FAMILY -> AppScreen.FamilyOnboarding.route
                        AuthState.NEEDS_CHARACTER -> AppScreen.CharacterCreation.route
                        AuthState.LOGGED_IN -> AppScreen.Home.route
                        else -> null
                    }
                    destination?.let {
                        navController.navigate(it) {
                            popUpTo(0)
                        }
                    }
                }

                NavHost(
                    navController = navController,
                    startDestination = "loading_screen"
                ) {
                    composable("loading_screen") {
                        Box(modifier = Modifier.fillMaxSize()) { CircularProgressIndicator(modifier = Modifier.align(Alignment.Center)) }
                    }
                    composable(AppScreen.SignIn.route) { SignInScreen(navController) }
                    composable(AppScreen.FamilyOnboarding.route) { FamilyOnboardingScreen() }
                    composable(AppScreen.CharacterCreation.route) {
                        val profile = session.profile
                        if (profile != null && profile.familyId != null) {
                            CharacterCreationRoute(familyId = profile.familyId, userId = profile.id)
                        }
                    }
                    composable(AppScreen.Home.route) {
                        val profile = session.profile
                        if (profile != null && profile.familyId != null) {
                            MainAppScreen(
                                familyId = profile.familyId,
                                onSignOut = { mainViewModel.signOut() }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(familyId: String, onSignOut: () -> Unit) {
    val navController = rememberNavController()
    val navItems = listOf(BottomNavItem.Home, BottomNavItem.Profile, BottomNavItem.Settings)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ChoreQuest") },
                actions = {
                    IconButton(onClick = onSignOut) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logga ut")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                navItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // KORRIGERING: Anropar nu den nya "Route"-funktionen
            composable(BottomNavItem.Home.route) { HomeScreenRoute(familyId = familyId) }
            composable(BottomNavItem.Profile.route) { ProfileScreen() }
            composable(BottomNavItem.Settings.route) { SettingsScreen() }
        }
    }
}
