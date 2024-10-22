package com.dsw.pam.musicGlass.model

sealed class RegistrationIntent {
    data class EmailChanged(val email: String) : RegistrationIntent()
    data class PasswordChanged(val password: String) :
        RegistrationIntent()
    data class ConfirmPasswordChanged(val confirmPassword: String) :
        RegistrationIntent()
    object Register : RegistrationIntent()
}
