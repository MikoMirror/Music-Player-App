package com.dsw.pam.musicGlass.viewmodels

import LoginState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.dsw.pam.musicGlass.model.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val authRepository = AuthRepository(FirebaseAuth.getInstance())
    var state by mutableStateOf(LoginState())
        private set

    fun updateEmail(email: String) {
        state = state.copy(email = email.trim())
    }

    fun updatePassword(password: String) {
        state = state.copy(password = password.trim())
    }

    fun login(onSuccess: (String) -> Unit) {
        if (state.email.isBlank() || state.password.isBlank()) {
            state = state.copy(loginError = "Email and Password cannot be empty.")
            return
        }

        state = state.copy(isLoading = true, loginError = null)

        viewModelScope.launch {
            try {
                val result = authRepository.login(state.email, state.password)
                result.fold(
                    onSuccess = { 
                        state = state.copy(isLoading = false)
                        onSuccess(state.email)
                    },
                    onFailure = { exception ->
                        state = state.copy(
                            isLoading = false,
                            loginError = exception.message ?: "Unknown error occurred"
                        )
                    }
                )
            } catch (e: Exception) {
                state = state.copy(
                    isLoading = false,
                    loginError = e.message ?: "Unknown error occurred"
                )
            }
        }
    }
}