package com.dsw.pam.musicGlass.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.dsw.pam.musicGlass.model.AuthRepository
import com.dsw.pam.musicGlass.model.RegistrationIntent
import com.dsw.pam.musicGlass.model.RegistrationState
import kotlinx.coroutines.launch

class RegistrationViewModel : ViewModel() {
    private val _state = mutableStateOf(RegistrationState())
    val state: RegistrationState get() = _state.value
    private val authRepository = AuthRepository(FirebaseAuth.getInstance())

    fun processIntent(intent: RegistrationIntent, onSuccess: () -> Unit = {}) {
        when (intent) {
            is RegistrationIntent.EmailChanged -> {
                _state.value = _state.value.copy(email = intent.email)
            }
            is RegistrationIntent.PasswordChanged -> {
                _state.value = _state.value.copy(password = intent.password)
            }
            is RegistrationIntent.ConfirmPasswordChanged -> {
                _state.value = _state.value.copy(confirmPassword = intent.confirmPassword)
            }
            is RegistrationIntent.Register -> {
                register(onSuccess)
            }
        }
    }

    private fun register(onSuccess: () -> Unit) {
        if (state.email.isBlank() || state.password.isBlank() || state.confirmPassword.isBlank()) {
            _state.value = _state.value.copy(registrationError = "All fields are required.")
            return
        }
        if (state.password != state.confirmPassword) {
            _state.value = _state.value.copy(registrationError = "Passwords do not match.")
            return
        }

        _state.value = _state.value.copy(isLoading = true, registrationError = null)
        viewModelScope.launch {
            try {
                val result = authRepository.register(state.email, state.password)
                result.fold(
                    onSuccess = {
                        _state.value = _state.value.copy(isLoading = false)
                        onSuccess()
                    },
                    onFailure = { exception ->
                        _state.value = _state.value.copy(
                            isLoading = false,
                            registrationError = exception.message
                        )
                    }
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    registrationError = e.message
                )
            }
        }
    }
}
