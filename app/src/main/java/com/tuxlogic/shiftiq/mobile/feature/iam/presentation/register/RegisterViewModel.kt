package com.tuxlogic.shiftiq.mobile.feature.iam.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.onFailure
import com.tuxlogic.shiftiq.mobile.core.common.result.onSuccess
import com.tuxlogic.shiftiq.mobile.feature.iam.domain.usecase.LoginUseCase
import com.tuxlogic.shiftiq.mobile.feature.iam.domain.usecase.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterUiState(
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val selectedRole: String = "ROLE_OWNER",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onEmailChanged(value: String) {
        _uiState.update { it.copy(email = value, emailError = null, errorMessage = null) }
    }

    fun onPasswordChanged(value: String) {
        _uiState.update { it.copy(password = value, passwordError = null, errorMessage = null) }
    }

    fun onRoleSelected(role: String) {
        _uiState.update { it.copy(selectedRole = role) }
    }

    fun register() {
        val currentState = _uiState.value
        _uiState.update { it.copy(isLoading = true, errorMessage = null, emailError = null, passwordError = null) }

        viewModelScope.launch {
            val result = registerUseCase(
                email = currentState.email,
                password = currentState.password,
                role = currentState.selectedRole
            )
            result.onSuccess {
                // Inicia sesión automáticamente tras el registro exitoso
                val loginResult = loginUseCase(currentState.email, currentState.password)
                loginResult.onSuccess {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                }.onFailure {
                    // Si el login automático falla, aún marcamos éxito para que inicie sesión manualmente
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                }
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
                    is AppError.Conflict -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "El correo ya está registrado en el sistema."
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
