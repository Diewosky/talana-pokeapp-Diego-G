package com.example.talana_poke_app.presentation.auth

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

// Este es el Web Client ID que obtuvimos del google-services.json
private const val WEB_CLIENT_ID = "809267029366-g762dg6li0q2fh2552dape8calbb90pv.apps.googleusercontent.com"
private const val TAG = "AuthViewModel"

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val application: Application
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val firebaseAuth: FirebaseAuth = Firebase.auth
    val googleSignInClient: GoogleSignInClient

    init {
        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(WEB_CLIENT_ID) // Solicita el token de ID para Firebase
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(application, gso)

        // Comprobar si ya hay un usuario logueado al iniciar
        _uiState.update { it.copy(currentUser = firebaseAuth.currentUser) }
    }

    fun triggerGoogleSignIn() {
        _uiState.update { it.copy(isLoading = true, googleSignInTriggered = true, error = null) }
    }

    // Llama a esta función después de que el intent de Google Sign-In se haya completado
    fun handleGoogleSignInResult(data: Intent?) {
        _uiState.update { it.copy(googleSignInTriggered = false) } // Reseteamos el trigger
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
            Log.d(TAG, "Google Sign-In successful, got account: ${account.id}")
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            Log.w(TAG, "Google Sign-In failed", e)
            _uiState.update { it.copy(isLoading = false, error = "Google Sign-In failed: ${e.statusCode}") }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        viewModelScope.launch {
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = firebaseAuth.signInWithCredential(credential).await()
                val firebaseUser = authResult.user
                Log.d(TAG, "Firebase authentication with Google successful: ${firebaseUser?.uid}")
                _uiState.update {
                    it.copy(isLoading = false, currentUser = firebaseUser, error = null)
                }
            } catch (e: Exception) {
                Log.w(TAG, "Firebase authentication with Google failed", e)
                _uiState.update {
                    it.copy(isLoading = false, error = "Firebase Auth failed: ${e.message}")
                }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                firebaseAuth.signOut()
                googleSignInClient.signOut().await() // También cerrar sesión de Google
                _uiState.update { AuthUiState() } // Resetear al estado inicial
                Log.d(TAG, "User signed out successfully.")
            } catch (e: Exception) {
                Log.w(TAG, "Sign out failed", e)
                _uiState.update { it.copy(error = "Sign out failed: ${e.message}") }
            }
        }
    }

    fun consumeSignInTrigger() {
        // Para resetear el trigger después de que la Activity lo haya manejado
        if (_uiState.value.googleSignInTriggered) {
            _uiState.update { it.copy(googleSignInTriggered = false) }
        }
    }
} 