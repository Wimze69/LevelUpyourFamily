@file:Suppress("KotlinConstantConditions")

package com.lindberg.levelupyourkids.ui.character

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lindberg.levelupyourkids.R
import com.lindberg.levelupyourkids.data.*
import com.lindberg.levelupyourkids.ui.theme.ChoreQuestTheme

/**
 * "Smart" Composable (Stateful)
 * Denna hanterar logiken och anropas av navigationen.
 */
@Composable
fun CharacterCreationRoute(
    familyId: String,
    userId: String,
    viewModel: CharacterCreationViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    CharacterCreationScreen(
        uiState = uiState,
        onCreateCharacter = { character ->
            viewModel.createCharacter(familyId, userId, character)
        },
        onClearError = viewModel::clearError
    )
}

/**
 * "Dum" Composable (Stateless)
 * Denna hanterar endast UI och kan därför förhandsgranskas.
 */
@Composable
fun CharacterCreationScreen(
    uiState: CharacterCreationState,
    onCreateCharacter: (Character) -> Unit,
    onClearError: () -> Unit
) {
    var selectedAvatar by remember { mutableStateOf(availableAvatars.first()) }
    var selectedColor by remember { mutableStateOf(availableBackgroundColors.first()) }
    var selectedClass by remember { mutableStateOf(availableClasses.first()) }

    val context = LocalContext.current

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            onClearError()
        }
    }

    // Bakgrund med gradient för en "coolare" känsla
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    )

    Box(modifier = Modifier.background(backgroundBrush)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // Tillåter scroll på små skärmar
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Skapa din hjälte", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(24.dp))

            // Förhandsvisning av hjälten
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(selectedColor)
                    .border(6.dp, MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = selectedAvatar.imageResId),
                    contentDescription = selectedAvatar.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(Modifier.height(24.dp))

            // Väljare för Avatar med rutnät
            SectionTitle("Välj din avatar")
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 90.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 240.dp), // Begränsar höjden för att undvika att den tar över hela skärmen
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(availableAvatars) { avatar ->
                    AvatarItem(
                        avatar = avatar,
                        isSelected = avatar.id == selectedAvatar.id,
                        onSelect = { selectedAvatar = avatar }
                    )
                }
            }
            Spacer(Modifier.height(24.dp))

            // Väljare för Bakgrundsfärg
            SectionTitle("Välj bakgrundsfärg")
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)
            ) {
                items(availableBackgroundColors) { color ->
                    ColorItem(
                        color = color,
                        isSelected = color.value == selectedColor.value,
                        onSelect = { selectedColor = color }
                    )
                }
            }
            Spacer(Modifier.height(24.dp))

            // Väljare för Klass med rutnät
            SectionTitle("Välj din klass")
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 150.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(availableClasses) { characterClass ->
                    ClassItem(
                        characterClass = characterClass,
                        isSelected = characterClass.id == selectedClass.id,
                        onSelect = { selectedClass = characterClass }
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // Knapp för att skapa karaktären
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = {
                        val newCharacter = Character(
                            avatarId = selectedAvatar.id,
                            characterClass = selectedClass,
                            backgroundColorValue = selectedColor.value.toLong()
                        )
                        onCreateCharacter(newCharacter)
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !uiState.isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Bli en hjälte!", style = MaterialTheme.typography.titleMedium, fontSize = 18.sp)
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
fun AvatarItem(avatar: Avatar, isSelected: Boolean, onSelect: () -> Unit) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
        label = "Avatar Border Color"
    )
    Image(
        painter = painterResource(id = avatar.imageResId),
        contentDescription = avatar.name,
        modifier = Modifier
            .size(80.dp)
            .clip(CircleShape)
            .clickable(onClick = onSelect)
            .border(
                width = 4.dp,
                color = borderColor,
                shape = CircleShape
            ),
        contentScale = ContentScale.Fit
    )
}

@Composable
fun ColorItem(color: Color, isSelected: Boolean, onSelect: () -> Unit) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
        label = "Color Border"
    )
    Box(
        modifier = Modifier
            .size(50.dp)
            .clip(CircleShape)
            .background(color)
            .clickable(onClick = onSelect)
            .border(width = 3.dp, color = borderColor, shape = CircleShape)
    )
}

@Composable
fun ClassItem(characterClass: CharacterClass, isSelected: Boolean, onSelect: () -> Unit) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
        label = "Class Border"
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        ),
        border = BorderStroke(2.dp, borderColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = characterClass.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = characterClass.description,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )
        }
    }
}

@Preview(showBackground = true, name = "Character Creation Preview")
@Composable
fun CharacterCreationScreenPreview() {
    ChoreQuestTheme(darkTheme = true) {
        CharacterCreationScreen(
            uiState = CharacterCreationState(isLoading = false, error = null),
            onCreateCharacter = {},
            onClearError = {}
        )
    }
}
