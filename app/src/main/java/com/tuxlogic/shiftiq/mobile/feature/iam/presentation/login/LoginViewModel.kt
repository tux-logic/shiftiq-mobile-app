package com.tuxlogic.shiftiq.mobile.feature.iam.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.onFailure
import com.tuxlogic.shiftiq.mobile.core.common.result.onSuccess
import com.tuxlogic.shiftiq.mobile.core.model.Role
import com.tuxlogic.shiftiq.mobile.feature.iam.application.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val isPasswordVisible: Boolean = false,
    val rememberMe: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val userRole: Role? = null
) {
    val isEmailFormatValid: Boolean
        get() {
            val trimmed = email.trim()
            if (trimmed.isBlank()) return false
            val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
            return emailRegex.matches(trimmed)
        }
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChanged(value: String) {
        _uiState.update { it.copy(email = value, emailError = null, errorMessage = null) }
    }

    fun onPasswordChanged(value: String) {
        _uiState.update { it.copy(password = value, passwordError = null, errorMessage = null) }
    }

    fun onPasswordVisibilityToggle() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun onRememberMeToggle(value: Boolean) {
        _uiState.update { it.copy(rememberMe = value) }
    }

    fun onLogin(onSuccess: (Role) -> Unit = {}) {
        login(onSuccess)
    }

    fun login(onSuccess: ((Role) -> Unit)? = null) {
        val currentState = _uiState.value
        _uiState.update { it.copy(isLoading = true, errorMessage = null, emailError = null, passwordError = null) }

        viewModelScope.launch {
            val result = loginUseCase(currentState.email, currentState.password)
            result.onSuccess { user ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = true,
                        userRole = user.role
                    )
                }
                onSuccess?.invoke(user.role)
            }.onFailure { error ->
                when (error) {
                    is AppError.Validation -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                emailError = if (error.details == "email") error.message else null,
                                passwordError = if (error.details == "password") error.message else null,
                                errorMessage = if (error.details != "email" && error.details != "password") error.message else null
                            )
                        }
                    }
                    is AppError.Unauthorized -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "Credenciales incorrectas. Verifica tu email y contraseña."
                            )
                        }
                    }
                    is AppError.Network -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "Sin conexión a internet. Verifica tu red."
                            )
                        }
                    }
                    else -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = error.message
                            )
                        }
                    }
                }
            }
        }
    }
}
