package com.tuxlogic.shiftiq.mobile.features.auth.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuxlogic.shiftiq.mobile.features.auth.application.RegisterUserUseCase
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
class RegisterViewModel @Inject constructor(
    private val registerUserUseCase: RegisterUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    // ==========================================
    // Modificadores de Datos del Propietario (Paso 1)
    // ==========================================

    fun onFullNameChange(value: String) {
        _uiState.update { it.copy(fullName = value, fullNameError = null, errorMessage = null) }
    }

    fun onDocumentNumberChange(value: String) {
        val filtered = value.filter { it.isDigit() }.take(8)
        _uiState.update { it.copy(documentNumber = filtered, documentError = null, errorMessage = null) }
    }

    fun onPhoneChange(value: String) {
        val filtered = value.filter { it.isDigit() }.take(9)
        _uiState.update { it.copy(phone = filtered, phoneError = null, errorMessage = null) }
    }

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, emailError = null, errorMessage = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, passwordError = null, errorMessage = null) }
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.update { it.copy(confirmPassword = value, confirmPasswordError = null, errorMessage = null) }
    }

    fun onTogglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun onToggleConfirmPasswordVisibility() {
        _uiState.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
    }

    fun onToggleAcceptsNotifications(value: Boolean) {
        _uiState.update { it.copy(notificationsAccepted = value, errorMessage = null) }
    }

    fun onToggleAcceptsTerms(value: Boolean) {
        _uiState.update { it.copy(termsAccepted = value, errorMessage = null) }
    }

    // ==========================================
    // Modificadores del Vehículo y Dongle (Paso 2)
    // ==========================================

    fun onBrandChange(value: String) {
        _uiState.update { it.copy(brand = value) }
    }

    fun onModelChange(value: String) {
        _uiState.update { it.copy(model = value) }
    }

    fun onYearChange(value: String) {
        val filtered = value.filter { it.isDigit() }.take(4)
        _uiState.update { it.copy(year = filtered) }
    }

    fun onPlateNumberChange(value: String) {
        val uppercase = value.uppercase().take(8)
        _uiState.update { it.copy(plateNumber = uppercase, plateError = null) }
    }

    fun onVinChange(value: String) {
        val uppercase = value.uppercase().take(17)
        _uiState.update { it.copy(vin = uppercase, vinError = null) }
    }

    fun onScanBleDevices() {
        viewModelScope.launch {
            _uiState.update { it.copy(isScanningBle = true) }
            delay(1200)
            _uiState.update {
                it.copy(
                    isScanningBle = false,
                    selectedDongleId = "OBD2-BT-9821",
                    dongleSignal = "Excelente (-42 dBm)",
                    dongleBattery = 98
                )
            }
        }
    }

    fun onSelectDongle(dongleId: String) {
        _uiState.update { it.copy(selectedDongleId = dongleId) }
    }

    // ==========================================
    // Modificadores OTP (si aplica)
    // ==========================================

    fun onOtpChange(value: String) {
        val filtered = value.filter { it.isDigit() }.take(6)
        _uiState.update { it.copy(otpCode = filtered, isOtpValid = filtered.length == 6) }
    }

    fun onVerifyOtp() {
        if (_uiState.value.otpCode.length == 6) {
            _uiState.update { it.copy(step = RegisterStep.SUCCESS, errorMessage = null) }
        } else {
            _uiState.update { it.copy(errorMessage = "Ingresa los 6 dígitos de verificación.") }
        }
    }

    // ==========================================
    // Navegación del Wizard
    // ==========================================

    fun onNextStep() {
        val state = _uiState.value
        when (state.step) {
            RegisterStep.STEP_1_USER -> {
                if (state.fullName.trim().length < 3) {
                    _uiState.update { it.copy(fullNameError = "Ingresa tu nombre y apellido completo.") }
                    return
                }
                if (!state.isDniValid) {
                    _uiState.update { it.copy(documentError = "El DNI debe contener 8 dígitos numéricos.") }
                    return
                }
                if (!state.isPhoneValid) {
                    _uiState.update { it.copy(phoneError = "Ingresa un número de celular válido (9 dígitos).") }
                    return
                }
                if (!state.isEmailValid) {
                    _uiState.update { it.copy(emailError = "Ingresa un correo electrónico con formato válido.") }
                    return
                }
                if (!state.isPasswordValid) {
                    val missing = mutableListOf<String>()
                    if (!state.isPasswordLengthValid) missing.add("8+ caracteres")
                    if (!state.hasUppercase) missing.add("1 mayúscula")
                    if (!state.hasNumber) missing.add("1 número")
                    _uiState.update { it.copy(passwordError = "Contraseña débil. Requiere: ${missing.joinToString(", ")}.") }
                    return
                }
                if (!state.doPasswordsMatch) {
                    _uiState.update { it.copy(confirmPasswordError = "Las contraseñas no coinciden.") }
                    return
                }
                if (!state.termsAccepted) {
                    _uiState.update { it.copy(errorMessage = "Debes aceptar los Términos de Servicio para continuar.") }
                    return
                }
                if (!state.notificationsAccepted) {
                    _uiState.update { it.copy(errorMessage = "Debes aceptar las Notificaciones de telemetría para continuar.") }
                    return
                }

                _uiState.update { it.copy(step = RegisterStep.STEP_2_VEHICLE, errorMessage = null) }
            }

            RegisterStep.STEP_2_VEHICLE -> {
                if (!state.isYearValid) {
                    _uiState.update { it.copy(errorMessage = "Ingresa un año de fabricación válido (1980 - 2026).") }
                    return
                }
                if (!state.isPlateValid) {
                    _uiState.update { it.copy(plateError = "Ingresa una placa vehicular válida (ej. ABC-123).") }
                    return
                }
                if (!state.isVinValid) {
                    _uiState.update { it.copy(vinError = "El número de chasis (VIN) debe tener exactamente 17 caracteres.") }
                    return
                }

                _uiState.update { it.copy(step = RegisterStep.STEP_3_CONFIRM, errorMessage = null) }
            }

            RegisterStep.STEP_3_CONFIRM -> {
                onSubmitRegistration()
            }

            RegisterStep.OTP -> onVerifyOtp()
            RegisterStep.SUCCESS -> { /* Navegación a login / dashboard */ }
            RegisterStep.ERROR -> {
                _uiState.update { it.copy(step = RegisterStep.STEP_1_USER, errorMessage = null) }
            }
        }
    }

    fun onPreviousStep(onExit: () -> Unit) {
        when (_uiState.value.step) {
            RegisterStep.STEP_1_USER -> onExit()
            RegisterStep.STEP_2_VEHICLE -> _uiState.update { it.copy(step = RegisterStep.STEP_1_USER, errorMessage = null) }
            RegisterStep.STEP_3_CONFIRM -> _uiState.update { it.copy(step = RegisterStep.STEP_2_VEHICLE, errorMessage = null) }
            RegisterStep.OTP -> _uiState.update { it.copy(step = RegisterStep.STEP_3_CONFIRM, errorMessage = null) }
            RegisterStep.ERROR -> _uiState.update { it.copy(step = RegisterStep.STEP_1_USER, errorMessage = null) }
            RegisterStep.SUCCESS -> onExit()
        }
    }

    fun onCorrectData() {
        _uiState.update { it.copy(step = RegisterStep.STEP_1_USER, errorMessage = null) }
    }

    fun onSubmitRegistration(onSuccess: () -> Unit = {}) {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = registerUserUseCase(
                fullName = state.fullName.trim(),
                documentNumber = state.documentNumber.trim(),
                phone = state.phone.trim(),
                email = state.email.trim(),
                password = state.password,
                plateNumber = state.plateNumber.trim(),
                brand = state.brand.trim(),
                model = state.model.trim(),
                year = state.year.toIntOrNull() ?: 2020,
                vin = state.vin.trim(),
                dongleId = state.selectedDongleId
            )

            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            step = RegisterStep.SUCCESS,
                            errorMessage = null
                        )
                    }
                    onSuccess()
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            step = RegisterStep.ERROR,
                            errorMessage = error.message ?: "No se pudo crear la cuenta. Verifica tus datos o intenta más tarde."
                        )
                    }
                }
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
