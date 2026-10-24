package com.tuxlogic.shiftiq.mobile.features.auth.presentation.recovery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuxlogic.shiftiq.mobile.features.auth.application.RequestPasswordResetUseCase
import com.tuxlogic.shiftiq.mobile.features.auth.application.UpdatePasswordUseCase
import com.tuxlogic.shiftiq.mobile.features.auth.application.VerifyOtpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecoveryViewModel @Inject constructor(
    private val requestPasswordResetUseCase: RequestPasswordResetUseCase,
    private val verifyOtpUseCase: VerifyOtpUseCase,
    private val updatePasswordUseCase: UpdatePasswordUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecoveryUiState())
    val uiState: StateFlow<RecoveryUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    fun onEmailChanged(newEmail: String) {
        _uiState.update {
            it.copy(
                email = newEmail,
                emailError = null,
                errorMessage = null
            )
        }
    }

    fun onClearEmail() {
        _uiState.update {
            it.copy(
                email = "",
                emailError = null,
                errorMessage = null
            )
        }
    }

    fun onOtpCodeChanged(newCode: String) {
        val filtered = newCode.filter { it.isDigit() }.take(6)
        _uiState.update {
            it.copy(
                otpCode = filtered,
                errorMessage = null,
                isOtpValid = filtered.length == 6
            )
        }
    }

    fun onNewPasswordChanged(password: String) {
        _uiState.update {
            it.copy(
                newPassword = password,
                newPasswordError = null,
                errorMessage = null
            )
        }
    }

    fun onConfirmPasswordChanged(password: String) {
        _uiState.update {
            it.copy(
                confirmPassword = password,
                confirmPasswordError = null,
                errorMessage = null
            )
        }
    }

    fun onToggleNewPasswordVisibility() {
        _uiState.update { it.copy(isNewPasswordVisible = !it.isNewPasswordVisible) }
    }

    fun onToggleConfirmPasswordVisibility() {
        _uiState.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
    }

    fun onRequestResetCode() {
        val currentState = _uiState.value
        if (currentState.email.isBlank()) {
            _uiState.update { it.copy(emailError = "El correo electrónico es requerido.") }
            return
        }

        if (!currentState.isEmailValid) {
            _uiState.update { it.copy(emailError = "Ingresa un correo electrónico con formato válido.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = requestPasswordResetUseCase(currentState.email.trim())
            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            step = RecoveryStep.OTP,
                            errorMessage = null
                        )
                    }
                    startCountdownTimer()
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Error al solicitar el código de seguridad."
                        )
                    }
                }
            )
        }
    }

    fun onResendOtp() {
        if (!_uiState.value.isResendEnabled && _uiState.value.timeLeft > 0) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = requestPasswordResetUseCase(_uiState.value.email.trim())
            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, otpCode = "", errorMessage = null) }
                    startCountdownTimer()
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "No se pudo reenviar el código. Inténtalo más tarde."
                        )
                    }
                }
            )
        }
    }

    fun onVerifyOtp() {
        val currentState = _uiState.value
        if (currentState.otpCode.length != 6) {
            _uiState.update {
                it.copy(
                    errorMessage = "Por favor ingresa los 6 dígitos del código de seguridad.",
                    isOtpValid = false
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = verifyOtpUseCase(currentState.email.trim(), currentState.otpCode)
            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            step = RecoveryStep.NEW_PASSWORD,
                            errorMessage = null,
                            isOtpValid = true
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "El código ha expirado o es incorrecto. Solicita un nuevo código de recuperación.",
                            isOtpValid = false
                        )
                    }
                }
            )
        }
    }

    fun onResetPassword(onSuccess: () -> Unit = {}) {
        val currentState = _uiState.value
        if (!currentState.isPasswordLengthValid) {
            _uiState.update { it.copy(newPasswordError = "Mínimo 8 caracteres alfanuméricos.") }
            return
        }
        if (!currentState.hasUppercase) {
            _uiState.update { it.copy(newPasswordError = "Debe incluir al menos una letra mayúscula.") }
            return
        }
        if (!currentState.hasSpecialOrNumber) {
            _uiState.update { it.copy(newPasswordError = "Debe incluir al menos un número o símbolo especial.") }
            return
        }
        if (!currentState.doPasswordsMatch) {
            _uiState.update { it.copy(confirmPasswordError = "Las contraseñas no coinciden.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = updatePasswordUseCase(
                token = currentState.otpCode,
                newPassword = currentState.newPassword
            )
            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            step = RecoveryStep.SUCCESS,
                            errorMessage = null
                        )
                    }
                    onSuccess()
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "No se pudo restablecer la contraseña. Verifica el token o intenta de nuevo."
                        )
                    }
                }
            )
        }
    }

    fun onBackClicked(onExitFlow: () -> Unit) {
        when (_uiState.value.step) {
            RecoveryStep.EMAIL -> onExitFlow()
            RecoveryStep.OTP -> {
                timerJob?.cancel()
                _uiState.update { it.copy(step = RecoveryStep.EMAIL, errorMessage = null) }
            }
            RecoveryStep.NEW_PASSWORD -> {
                _uiState.update { it.copy(step = RecoveryStep.OTP, errorMessage = null) }
            }
            RecoveryStep.SUCCESS -> onExitFlow()
        }
    }

    private fun startCountdownTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            _uiState.update { it.copy(timeLeft = 60, isResendEnabled = false) }
            while (_uiState.value.timeLeft > 0) {
                delay(1000)
                _uiState.update { it.copy(timeLeft = (it.timeLeft - 1).coerceAtLeast(0)) }
            }
            _uiState.update { it.copy(isResendEnabled = true) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
