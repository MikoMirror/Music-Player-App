package com.dsw.pam.musicGlass.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.dsw.pam.musicGlass.model.AuthRepository
import com.dsw.pam.musicGlass.model.User
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val authRepository: AuthRepository =
        AuthRepository(FirebaseAuth.getInstance())
    private var _email = mutableStateOf("")
    val email: String
        get() = _email.value
    private var _password = mutableStateOf("")
    val password: String
        get() = _password.value
    var loginError by mutableStateOf<String?>(null)
    var isLoading by mutableStateOf(false)
    fun login(onSuccess: (String) -> Unit) {
        if (_email.value.isBlank() || _password.value.isBlank()) {
            loginError = "Email and Password cannot be empty."
            return
        }
        isLoading = true
        loginError = null
        val user = User(_email.value, _password.value)
        viewModelScope.launch {
            try {
                authRepository.login(user.email, user.password)
                onSuccess(user.email)
                Log.d("Login", "Login successful")
            } catch (exception: Exception) {
                loginError = exception.message
            } finally {
                isLoading = false
            }
        }
    }
    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }
    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }
}