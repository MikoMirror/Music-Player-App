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
    private val authRepository: AuthRepository = AuthRepository(FirebaseAuth.getInstance())

    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var loginError by mutableStateOf<String?>(null)
    var isLoading by mutableStateOf(false)

    fun login(onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            loginError = "Email and Password cannot be empty."
            return
        }

        isLoading = true
        loginError = null
        val user = User(email, password)
        viewModelScope.launch {
            try {
                authRepository.login(user.email, user.password)
                onSuccess()
                Log.d("Login","Login +")
            } catch (exception: Exception) {
                loginError = exception.message
            } finally {
                isLoading = false
            }
        }
    }
}
