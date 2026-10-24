package com.tuxlogic.shiftiq.mobile.features.auth.presentation.login

data class LoginUiState(
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val isPasswordVisible: Boolean = false,
    val rememberMe: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
) {
    val isEmailFormatValid: Boolean
        get() = email.isNotBlank() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

    val canSubmit: Boolean
        get() = isEmailFormatValid && password.isNotBlank() && !isLoading
}
