package com.lindberg.levelupyourkids.ui.family

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun FamilyOnboardingScreen(
    familyViewModel: FamilyViewModel = viewModel()
) {
    val uiState by familyViewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.error) {
        uiState.error?.let { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
    }

    var familyName by remember { mutableStateOf("") }
    var inviteCode by remember { mutableStateOf("") }
    var showCreateView by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            if (showCreateView) "Skapa din familj" else "Gå med i en familj",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(24.dp))

        if (showCreateView) {
            OutlinedTextField(
                value = familyName,
                onValueChange = { familyName = it },
                label = { Text("Familjenamn") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { familyViewModel.createFamily(familyName) },
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Skapa familj") }
            TextButton(onClick = { showCreateView = false }) {
                Text("Har du redan en inbjudningskod?")
            }
        } else {
            OutlinedTextField(
                value = inviteCode,
                onValueChange = { inviteCode = it },
                label = { Text("Inbjudningskod") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { familyViewModel.joinFamily(inviteCode) },
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Gå med") }
            TextButton(onClick = { showCreateView = true }) {
                Text("Vill du skapa en ny familj?")
            }
        }

        if (uiState.isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }
    }
}
