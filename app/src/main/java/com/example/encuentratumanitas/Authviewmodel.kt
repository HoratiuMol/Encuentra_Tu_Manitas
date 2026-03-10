package com.example.encuentratumanitas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ─── Estado de la pantalla Auth ───────────────────────────────────────────────
sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class NavigateToRole(val role: UserRole) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // ─── Comprobación inicial de sesión ──────────────────────────────────────
    fun checkExistingSession() {
        viewModelScope.launch {
            if (repository.isLoggedIn()) {
                val role = repository.getCurrentRole() ?: UserRole.CLIENT
                _uiState.value = AuthUiState.NavigateToRole(role)
            }
        }
    }

    // ─── Login ───────────────────────────────────────────────────────────────
    fun signIn(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("Email y contraseña son obligatorios")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            when (val result = repository.signIn(email, password)) {
                is AuthResult.Success -> _uiState.value = AuthUiState.NavigateToRole(result.data)
                is AuthResult.Error   -> _uiState.value = AuthUiState.Error(result.message)
            }
        }
    }

    // ─── Registro ────────────────────────────────────────────────────────────
    fun signUp(
        email: String,
        password: String,
        confirmPassword: String,
        fullName: String,
        selectedRole: UserRole
    ) {
        when {
            fullName.isBlank()          -> { _uiState.value = AuthUiState.Error("El nombre es obligatorio"); return }
            email.isBlank()             -> { _uiState.value = AuthUiState.Error("El email es obligatorio"); return }
            password.length < 6         -> { _uiState.value = AuthUiState.Error("La contraseña debe tener al menos 6 caracteres"); return }
            password != confirmPassword -> { _uiState.value = AuthUiState.Error("Las contraseñas no coinciden"); return }
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            when (val result = repository.signUp(email, password, fullName, selectedRole)) {
                is AuthResult.Success -> _uiState.value = AuthUiState.NavigateToRole(selectedRole)
                is AuthResult.Error   -> _uiState.value = AuthUiState.Error(result.message)
            }
        }
    }

    // ─── Logout ──────────────────────────────────────────────────────────────
    fun signOut() {
        viewModelScope.launch {
            repository.signOut()
            _uiState.value = AuthUiState.Idle
        }
    }

    fun clearError() {
        _uiState.value = AuthUiState.Idle
    }
}
