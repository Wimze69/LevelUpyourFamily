package com.lindberg.levelupyourkids.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// En mer robust state-klass som kan representera alla tillstånd
sealed interface SignInResult {
    object Idle : SignInResult
    object Loading : SignInResult
    object Success : SignInResult
    data class Error(val message: String) : SignInResult
}

class AuthViewModel(private val auth: FirebaseAuth = Firebase.auth) : ViewModel() {

    private val _signInResult = MutableStateFlow<SignInResult>(SignInResult.Idle)
    val signInResult = _signInResult.asStateFlow()

    fun signInWithCredential(credential: AuthCredential) {
        viewModelScope.launch {
            _signInResult.value = SignInResult.Loading
            try {
                auth.signInWithCredential(credential).await()
                _signInResult.value = SignInResult.Success
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                _signInResult.value = SignInResult.Error("Inloggningsuppgifterna är ogiltiga.")
            } catch (e: Exception) {
                _signInResult.value = SignInResult.Error(e.message ?: "Ett okänt fel uppstod.")
            }
        }
    }

    fun resetState() {
        _signInResult.value = SignInResult.Idle
    }
}
