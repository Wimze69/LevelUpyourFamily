package com.lindberg.levelupyourkids.ui.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lindberg.levelupyourkids.data.PlayerProfile
import com.lindberg.levelupyourkids.data.Quest
import com.lindberg.levelupyourkids.data.progressToNextLevel
import com.lindberg.levelupyourkids.data.xpForNextSeasonalLevel
import com.lindberg.levelupyourkids.ui.theme.ChoreQuestTheme
import kotlinx.coroutines.delay

/**
 * "Smart" Composable (Stateful)
 */
@Composable
fun HomeScreenRoute(
    familyId: String,
    viewModel: HomeScreenViewModel = viewModel(factory = HomeScreenViewModelFactory(familyId))
) {
    val uiState by viewModel.uiState.collectAsState()

    HomeScreen(
        uiState = uiState,
        onCompleteQuest = viewModel::completeQuest
    )
}

/**
 * "Dum" Composable (Stateless)
 */
@Composable
fun HomeScreen(
    uiState: HomeState,
    onCompleteQuest: (Quest) -> Unit
) {
    var selectedTabIndex by remember { mutableStateOf(0) }

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFE3F2FD), // Ljus himmelsblå
            Color(0xFFE0F7FA)  // Ljus cyan
        )
    )

    Column(modifier = Modifier.fillMaxSize().background(backgroundBrush)) {
        PlayerStatsHeader(playerProfile = uiState.playerProfile)

        val tabs = listOf("Dagliga", "Veckans", "Special")
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title, fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal) }
                )
            }
        }

        if (uiState.isLoadingQuests) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            when (selectedTabIndex) {
                0 -> QuestList(quests = uiState.dailyQuests, onComplete = onCompleteQuest)
                1 -> QuestList(quests = uiState.weeklyQuests, onComplete = onCompleteQuest)
                2 -> QuestList(quests = uiState.specialQuests, onComplete = onCompleteQuest)
            }
        }
    }
}

/**
 * NY, UPPDATERAD HEADER MED LEVEL UP-ANIMATION
 */
@Composable
fun PlayerStatsHeader(playerProfile: PlayerProfile?) {
    // State för att hålla koll på den visuella framstegen i mätaren
    var displayProgress by remember { mutableFloatStateOf(0f) }
    // State för att hålla koll på den level som visas
    var displayLevel by remember { mutableIntStateOf(1) }
    // State för att trigga en "flash"-animation
    var isLevelUpAnimating by remember { mutableStateOf(false) }

    // Animerar själva mätaren
    val animatedProgress by animateFloatAsState(
        targetValue = displayProgress,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "XP Progress Animation"
    )

    // Animerar färgen på level-up-blixten
    val levelUpColor by animateColorAsState(
        targetValue = if (isLevelUpAnimating) Color(0xFFFFD700) else Color.Transparent,
        animationSpec = tween(500),
        label = "Level Up Flash Color"
    )

    // Denna effekt körs varje gång spelarprofilen från ViewModel ändras
    LaunchedEffect(playerProfile) {
        if (playerProfile == null) return@LaunchedEffect

        val newLevel = playerProfile.seasonalLevel
        val newProgress = playerProfile.progressToNextLevel

        // Om den nya leveln är högre än den vi för närvarande visar...
        if (newLevel > displayLevel) {
            // ...starta level up-sekvensen!
            // 1. Fyll den nuvarande mätaren till 100%
            displayProgress = 1f
            delay(1100) // Vänta på att animationen blir klar

            // 2. Starta "blixt"-animationen och uppdatera level-texten
            isLevelUpAnimating = true
            displayLevel = newLevel
            delay(500) // Låt blixten synas

            // 3. Nollställ mätaren och stäng av blixten
            displayProgress = 0f
            isLevelUpAnimating = false
            delay(200) // Kort paus vid noll

            // 4. Animera mätaren till den nya framstegen
            displayProgress = newProgress

        } else {
            // Annars, gör bara en vanlig uppdatering
            displayLevel = newLevel
            displayProgress = newProgress
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(80.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    strokeWidth = 8.dp,
                    strokeCap = StrokeCap.Round
                )
                CircularProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 8.dp,
                    strokeCap = StrokeCap.Round
                )
                // Den pulserande ramen vid level up
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(BorderStroke(4.dp, levelUpColor), CircleShape)
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("LVL", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = "$displayLevel",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.width(16.dp))

            Column {
                Text(
                    text = "Välkommen, ${playerProfile?.displayName ?: "Hjälte"}!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${playerProfile?.seasonalXp ?: 0} / ${playerProfile?.xpForNextSeasonalLevel ?: 15} XP",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}


@Composable
fun QuestList(quests: List<Quest>, onComplete: (Quest) -> Unit) {
    if (quests.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Inga uppdrag av den här typen just nu!")
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(quests) { quest ->
                QuestListItem(quest = quest, onCompleteClick = { onComplete(quest) })
            }
        }
    }
}

@Composable
fun QuestListItem(quest: Quest, onCompleteClick: () -> Unit) {
    val (icon, iconColor) = getIconForCategory(quest.category)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp, hoveredElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceBright)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = quest.category,
                    modifier = Modifier.size(28.dp),
                    tint = iconColor
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(quest.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(quest.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.width(16.dp))
            Button(
                onClick = onCompleteClick,
                shape = CircleShape,
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.size(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "+${quest.xp}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = "XP",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

fun getIconForCategory(category: String): Pair<ImageVector, Color> {
    return when {
        category.contains("Morgon", ignoreCase = true) -> Icons.Default.WbSunny to Color(0xFFFFB300)
        category.contains("Kväll", ignoreCase = true) -> Icons.Default.Bedtime to Color(0xFF5E35B1)
        category.contains("Ordning", ignoreCase = true) -> Icons.Default.Checklist to Color(0xFF43A047)
        category.contains("Städ", ignoreCase = true) -> Icons.Default.CleaningServices to Color(0xFF1E88E5)
        category.contains("Kök", ignoreCase = true) -> Icons.Default.Restaurant to Color(0xFFE53935)
        category.contains("Utomhus", ignoreCase = true) -> Icons.Default.Forest to Color(0xFF7CB342)
        else -> Icons.Default.Star to Color(0xFFFB8C00)
    }
}

/**
 * NYTT: En interaktiv förhandsgranskning.
 * Denna funktion skapar ett eget, lokalt state för att simulera hur appen beter sig.
 */
@Preview(showBackground = true, name = "Interactive Home Screen")
@Composable
fun InteractiveHomeScreenPreview() {
    ChoreQuestTheme(darkTheme = false) {
        // 1. Skapa ett lokalt, ihågkommet state för spelarprofilen.
        var playerProfile by remember {
            mutableStateOf(PlayerProfile(displayName = "Testspelare", seasonalLevel = 5, seasonalXp = 8))
        }

        // 2. Skapa en lista med test-uppdrag.
        val previewQuests = listOf(
            Quest("1", "Bädda sängen", "Snyggt och prydligt!", 10, "Morgon-Uppdrag", true, "DAILY"),
            Quest("2", "Gå ut med soporna", "Glöm inte att sortera!", 20, "Utomhus-Uppdrag", true, "DAILY")
        )

        // 3. Skapa ett uiState med vår lokala data.
        val uiState = HomeState(
            isLoadingQuests = false,
            playerProfile = playerProfile,
            dailyQuests = previewQuests
        )

        // 4. Anropa vår "dumma" UI-komponent.
        HomeScreen(
            uiState = uiState,
            // 5. Skicka med en lambda som faktiskt gör något!
            // Den uppdaterar vårt lokala state, vilket triggar omritning och animationer.
            onCompleteQuest = { quest ->
                var newXp = playerProfile.seasonalXp + quest.xp
                var newLevel = playerProfile.seasonalLevel
                val xpNeeded = playerProfile.xpForNextSeasonalLevel

                if (newXp >= xpNeeded) {
                    newLevel++
                    newXp -= xpNeeded
                }

                playerProfile = playerProfile.copy(
                    seasonalLevel = newLevel,
                    seasonalXp = newXp
                )
            }
        )
    }
}
