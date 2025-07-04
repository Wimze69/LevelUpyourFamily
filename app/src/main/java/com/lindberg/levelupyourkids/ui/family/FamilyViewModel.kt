package com.lindberg.levelupyourkids.ui.family

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.lindberg.levelupyourkids.repository.ChoreQuestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class FamilyUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)

class FamilyViewModel : ViewModel() {
    private val repository = ChoreQuestRepository()
    private val auth = Firebase.auth

    private val _uiState = MutableStateFlow(FamilyUiState())
    val uiState = _uiState.asStateFlow()

    fun createFamily(familyName: String) {
        viewModelScope.launch {
            _uiState.value = FamilyUiState(isLoading = true)
            val currentUser = auth.currentUser
            if (currentUser == null) {
                _uiState.value = FamilyUiState(error = "Ingen användare inloggad", isLoading = false)
                return@launch
            }
            if (familyName.isBlank()) {
                _uiState.value = FamilyUiState(error = "Familjenamnet kan inte vara tomt", isLoading = false)
                return@launch
            }

            try {
                repository.createFamily(familyName, currentUser)
                // Om det lyckas, gör ingenting. MainViewModel reagerar och navigerar.
            } catch (e: Exception) {
                _uiState.value = FamilyUiState(error = "Kunde inte skapa familj: ${e.message}", isLoading = false)
            }
        }
    }

    fun joinFamily(inviteCode: String) {
        viewModelScope.launch {
            _uiState.value = FamilyUiState(isLoading = true)
            val currentUser = auth.currentUser ?: return@launch
            if (inviteCode.isBlank()) {
                _uiState.value = FamilyUiState(error = "Inbjudningskoden kan inte vara tom", isLoading = false)
                return@launch
            }
            try {
                val joinedFamilyId = repository.joinFamily(inviteCode, currentUser)
                if (joinedFamilyId == null) {
                    _uiState.value = FamilyUiState(error = "Kunde inte hitta familj med den koden", isLoading = false)
                }
                // Om det lyckas, gör ingenting. MainViewModel reagerar och navigerar.
            } catch (e: Exception) {
                _uiState.value = FamilyUiState(error = "Kunde inte gå med i familj: ${e.message}", isLoading = false)
            }
        }
    }
}
