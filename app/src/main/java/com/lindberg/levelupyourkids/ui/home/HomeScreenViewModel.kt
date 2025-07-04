package com.lindberg.levelupyourkids.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.ktx.Firebase
import com.lindberg.levelupyourkids.data.PlayerProfile
import com.lindberg.levelupyourkids.data.Quest
import com.lindberg.levelupyourkids.data.QuestType
import com.lindberg.levelupyourkids.data.xpForNextSeasonalLevel
import com.lindberg.levelupyourkids.repository.ChoreQuestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeState(
    val isLoadingQuests: Boolean = true,
    val playerProfile: PlayerProfile? = null,
    val dailyQuests: List<Quest> = emptyList(),
    val weeklyQuests: List<Quest> = emptyList(),
    val specialQuests: List<Quest> = emptyList(),
    val error: String? = null
)

class HomeScreenViewModel(private val familyId: String) : ViewModel() {
    private val repository = ChoreQuestRepository()
    private val auth = Firebase.auth

    private val _uiState = MutableStateFlow(HomeState())
    val uiState = _uiState.asStateFlow()

    private var playerProfileListener: ListenerRegistration? = null

    init {
        loadQuests()
        listenToPlayerProfile()
    }

    private fun loadQuests() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingQuests = true) }
            try {
                val allQuests = repository.getQuestsForFamily(familyId)
                _uiState.update {
                    it.copy(
                        isLoadingQuests = false,
                        dailyQuests = allQuests.filter { quest -> quest.questType == QuestType.DAILY.name },
                        weeklyQuests = allQuests.filter { quest -> quest.questType == QuestType.WEEKLY.name },
                        specialQuests = allQuests.filter { quest -> quest.questType == QuestType.SPECIAL.name }
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoadingQuests = false, error = "Kunde inte ladda uppdrag.") }
            }
        }
    }

    private fun listenToPlayerProfile() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            _uiState.update { it.copy(error = "Kunde inte hitta användar-ID.") }
            return
        }
        playerProfileListener?.remove()
        playerProfileListener = repository.addPlayerProfileListener(familyId, userId) { profile ->
            // KORRIGERING: Lade till logik för att kontrollera och hantera level up.
            if (profile != null) {
                // Kontrollera om spelaren har tillräckligt med XP för att gå upp i level.
                if (profile.seasonalXp >= profile.xpForNextSeasonalLevel) {
                    // Om ja, starta en ny coroutine för att hantera level up-processen.
                    viewModelScope.launch {
                        try {
                            repository.levelUpPlayer(familyId, userId, profile)
                        } catch (e: Exception) {
                            // Hantera eventuella fel som kan uppstå under level up.
                            _uiState.update { it.copy(error = "Fel vid level up!") }
                        }
                    }
                }
            }
            // Uppdatera alltid UI med den senaste profilen.
            _uiState.update { it.copy(playerProfile = profile) }
        }
    }

    fun completeQuest(quest: Quest) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            _uiState.update { it.copy(error = "Du måste vara inloggad för att slutföra uppdrag.") }
            return
        }

        viewModelScope.launch {
            try {
                repository.completeQuest(familyId, userId, quest)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Ett fel uppstod. Försök igen.") }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        playerProfileListener?.remove()
    }
}

class HomeScreenViewModelFactory(private val familyId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeScreenViewModel(familyId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
