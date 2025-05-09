package com.example.talana_poke_app.presentation.auth

import com.google.firebase.auth.FirebaseUser

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentUser: FirebaseUser? = null, // Para saber si ya hay un usuario logueado
    val googleSignInTriggered: Boolean = false // Para manejar el lanzamiento del intent de Google Sign-In
) 