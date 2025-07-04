package com.lindberg.levelupyourkids

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.ktx.Firebase
import com.lindberg.levelupyourkids.data.PlayerProfile
import com.lindberg.levelupyourkids.repository.ChoreQuestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class AuthState { LOADING, LOGGED_OUT, NEEDS_FAMILY, NEEDS_CHARACTER, LOGGED_IN }

data class UserSession(
    val profile: PlayerProfile? = null,
    val authState: AuthState = AuthState.LOADING
)

class MainViewModel : ViewModel() {
    private val auth = Firebase.auth
    private val repository = ChoreQuestRepository()

    private val _session = MutableStateFlow(UserSession())
    val session = _session.asStateFlow()

    private var profileListener: ListenerRegistration? = null

    init {
        auth.addAuthStateListener { firebaseAuth ->
            // Ta bort en eventuell gammal lyssnare för att undvika dubbletter
            profileListener?.remove()

            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser == null) {
                // Om ingen användare är inloggad, sätt state till LOGGED_OUT.
                // Detta kommer också att köras efter att auth.signOut() är klar.
                if (_session.value.authState != AuthState.LOGGED_OUT) {
                    Log.d("AuthDebug", "AuthStateListener: User is null. Setting state to LOGGED_OUT.")
                    _session.update { it.copy(authState = AuthState.LOGGED_OUT, profile = null) }
                }
            } else {
                // Om en användare är inloggad, börja lyssna på deras profil.
                Log.d("AuthDebug", "AuthStateListener: User found (${firebaseUser.uid}). Listening to profile.")
                listenToUserProfile(firebaseUser.uid)
            }
        }
    }

    private fun listenToUserProfile(userId: String) {
        _session.update { it.copy(authState = AuthState.LOADING) }
        profileListener = repository.addUserProfileListener(userId) { profile ->
            Log.d("AuthDebug", "ProfileListener fired. Profile is: $profile")

            val newAuthState = when {
                profile == null -> AuthState.LOADING // Väntar fortfarande eller fel
                profile.familyId == null -> AuthState.NEEDS_FAMILY
                profile.character == null -> AuthState.NEEDS_CHARACTER
                else -> AuthState.LOGGED_IN
            }
            _session.update { it.copy(profile = profile, authState = newAuthState) }
        }
    }

    /**
     * KORRIGERAD UTLOGGNINGSFUNKTION
     * Denna funktion tar nu full kontroll över utloggningsprocessen.
     */
    fun signOut() {
        // 1. Ta bort Firestore-lyssnaren OMEDELBART.
        //    Detta förhindrar att den sätter fel state efter att vi loggat ut.
        profileListener?.remove()
        profileListener = null

        // 2. Sätt state till LOGGED_OUT manuellt.
        //    Nu har vi full kontroll och säkerställer att appen navigerar rätt.
        _session.value = UserSession(authState = AuthState.LOGGED_OUT)

        // 3. Logga ut från Firebase.
        auth.signOut()
    }

    override fun onCleared() {
        super.onCleared()
        // Säkerställ att lyssnaren alltid tas bort när ViewModel förstörs.
        profileListener?.remove()
    }
}