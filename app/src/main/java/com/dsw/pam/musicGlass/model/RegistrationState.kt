package com.dsw.pam.musicGlass.model

data class RegistrationState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val registrationError: String? = null,
    val isLoading: Boolean = false
)
