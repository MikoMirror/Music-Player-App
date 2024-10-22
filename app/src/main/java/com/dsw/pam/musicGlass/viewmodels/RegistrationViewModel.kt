package com.dsw.pam.musicGlass.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsw.pam.musicGlass.model.RegistrationIntent
import com.dsw.pam.musicGlass.model.RegistrationState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RegistrationViewModel : ViewModel() {
    private val _state = mutableStateOf(RegistrationState())
    val state: RegistrationState get() = _state.value
    fun processIntent(intent: RegistrationIntent) {
        when (intent) {
            is RegistrationIntent.EmailChanged -> {
                _state.value = _state.value.copy(email = intent.email)
            }
            is RegistrationIntent.PasswordChanged -> {
                _state.value = _state.value.copy(password =
                intent.password)
            }
            is RegistrationIntent.ConfirmPasswordChanged -> {
                _state.value = _state.value.copy(confirmPassword =
                intent.confirmPassword)
            }
            is RegistrationIntent.Register -> {
                register()
            }
        }
    }
    private fun register() {
        _state.value = _state.value.copy(isLoading = true,
            registrationError = null)
        viewModelScope.launch {
            delay(2000)
        }
        if (state.email.isBlank() || state.password.isBlank() ||
            state.confirmPassword.isBlank()) {
            _state.value = _state.value.copy(registrationError = "All fields are required.")
                return
        }
        if (state.password != state.confirmPassword) {
            _state.value = _state.value.copy(registrationError =
            "Passwords do not match.")
            return
        }
    }
}
