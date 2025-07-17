package com.example.cryptosolver.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    private val _authState = MutableStateFlow(
        if (auth.currentUser != null) AuthState.AUTHENTICATED else AuthState.UNAUTHENTICATED
    )
    val authState: StateFlow<AuthState> = _authState

    private val _isAuthenticated = MutableStateFlow(auth.currentUser != null)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    enum class AuthState {
        LOADING,
        AUTHENTICATED,
        UNAUTHENTICATED
    }

    fun handleGoogleAccessToken(idToken: String, onComplete: (Boolean, String?) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.AUTHENTICATED
                    _isAuthenticated.value = true
                    onComplete(true, null)
                } else {
                    _authState.value = AuthState.UNAUTHENTICATED
                    onComplete(false, task.exception?.message)
                }
            }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.LOADING
            _errorMessage.value = null

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _authState.value = AuthState.AUTHENTICATED
                        _isAuthenticated.value = true
                    } else {
                        _authState.value = AuthState.UNAUTHENTICATED
                        _errorMessage.value = task.exception?.message ?: "Sign in failed"
                    }
                }
        }
    }

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.LOADING
            _errorMessage.value = null

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _authState.value = AuthState.AUTHENTICATED
                        _isAuthenticated.value = true
                        // Create user profile in database
                        createUserProfile()
                    } else {
                        _authState.value = AuthState.UNAUTHENTICATED
                        _errorMessage.value = task.exception?.message ?: "Sign up failed"
                    }
                }
        }
    }

    fun signInWithGoogle() {
        // This would require Google Sign-In implementation
        // For now, we'll show a placeholder
        _errorMessage.value = "Google Sign-In not implemented yet"
    }

    fun signOut() {
        auth.signOut()
        _authState.value = AuthState.UNAUTHENTICATED
        _isAuthenticated.value = false
    }

    fun setError(message: String) {
        _errorMessage.value = message
    }

    private fun createUserProfile() {
        // Create initial user profile in Firebase Database
        val user = auth.currentUser
        user?.let {
            // Implementation for creating user profile
        }
    }
}
