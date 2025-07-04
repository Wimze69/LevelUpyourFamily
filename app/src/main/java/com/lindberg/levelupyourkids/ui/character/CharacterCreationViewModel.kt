package com.lindberg.levelupyourkids.ui.character

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lindberg.levelupyourkids.data.Character
import com.lindberg.levelupyourkids.repository.ChoreQuestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CharacterCreationState(
    val isLoading: Boolean = false,
    val error: String? = null
)

class CharacterCreationViewModel : ViewModel() {
    private val repository = ChoreQuestRepository()

    private val _uiState = MutableStateFlow(CharacterCreationState())
    val uiState = _uiState.asStateFlow()

    fun createCharacter(familyId: String, userId: String, character: Character) {
        viewModelScope.launch {
            // Använd .update för att säkert uppdatera state
            _uiState.update { it.copy(isLoading = true) }
            try {
                repository.updatePlayerCharacter(familyId, userId, character)
                // Om det lyckas, kommer MainViewModel att reagera och navigera bort från den här skärmen.
                // Vi sätter isLoading till false ifall något skulle gå snett med navigeringen.
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = "Kunde inte skapa karaktär: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    /**
     * NY FUNKTION: Återställer felmeddelandet i uiState.
     * Detta anropas från skärmen efter att ett Toast-meddelande har visats,
     * för att förhindra att det visas flera gånger.
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
