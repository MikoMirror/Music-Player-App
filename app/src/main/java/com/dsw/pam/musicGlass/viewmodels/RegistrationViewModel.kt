package com.dsw.pam.musicGlass.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.dsw.pam.musicGlass.model.AuthRepository
import com.dsw.pam.musicGlass.model.RegistrationState
import kotlinx.coroutines.launch

class RegistrationViewModel : ViewModel() {
    private val authRepository = AuthRepository(FirebaseAuth.getInstance())
    var state by mutableStateOf(RegistrationState())
        private set

    fun updateEmail(email: String) {
        state = state.copy(email = email)
    }

    fun updatePassword(password: String) {
        state = state.copy(password = password)
    }

    fun updateConfirmPassword(confirmPassword: String) {
        state = state.copy(confirmPassword = confirmPassword)
    }

    fun register(onSuccess: () -> Unit) {
        if (state.email.isBlank() || state.password.isBlank() || state.confirmPassword.isBlank()) {
            state = state.copy(registrationError = "All fields are required.")
            return
        }
        
        if (state.password != state.confirmPassword) {
            state = state.copy(registrationError = "Passwords do not match.")
            return
        }

        state = state.copy(isLoading = true, registrationError = null)
        
        viewModelScope.launch {
            try {
                val result = authRepository.register(state.email, state.password)
                result.fold(
                    onSuccess = {
                        state = state.copy(isLoading = false)
                        onSuccess()
                    },
                    onFailure = { exception ->
                        state = state.copy(
                            isLoading = false,
                            registrationError = exception.message
                        )
                    }
                )
            } catch (e: Exception) {
                state = state.copy(
                    isLoading = false,
                    registrationError = e.message
                )
            }
        }
    }
}
