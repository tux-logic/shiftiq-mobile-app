package com.tuxlogic.shiftiq.mobile.feature.iam.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuxlogic.shiftiq.mobile.core.common.result.onFailure
import com.tuxlogic.shiftiq.mobile.core.common.result.onSuccess
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.CreateCustomerProfileUseCase
import com.tuxlogic.shiftiq.mobile.feature.iam.application.usecase.LoginUseCase
import com.tuxlogic.shiftiq.mobile.feature.iam.application.usecase.RegisterUseCase
import com.tuxlogic.shiftiq.mobile.feature.iam.application.usecase.SendOtpUseCase
import com.tuxlogic.shiftiq.mobile.feature.iam.application.usecase.VerifyOtpUseCase
import com.tuxlogic.shiftiq.mobile.feature.iot.application.usecase.vehicles.RegisterVehicleUseCase
import com.tuxlogic.shiftiq.mobile.feature.shared.application.usecase.LookupIdentityDocumentUseCase
import com.tuxlogic.shiftiq.mobile.feature.shared.domain.model.IdentityDocumentType
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
    private val registerUseCase: RegisterUseCase,
    private val loginUseCase: LoginUseCase,
    private val createCustomerProfileUseCase: CreateCustomerProfileUseCase,
    private val registerVehicleUseCase: RegisterVehicleUseCase,
    private val lookupIdentityDocumentUseCase: LookupIdentityDocumentUseCase,
    private val sendOtpUseCase: SendOtpUseCase,
    private val verifyOtpUseCase: VerifyOtpUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var dniLookupJob: Job? = null

    // Modificadores de Datos del Propietario (Paso 1)
    fun onFullNameChange(value: String) {
        _uiState.update { it.copy(fullName = value, fullNameError = null, errorMessage = null) }
    }

    fun onDocumentNumberChange(value: String) {
        val filtered = value.filter { it.isDigit() }.take(8)
        _uiState.update {
            it.copy(
                documentNumber = filtered,
                documentError = null,
                errorMessage = null,
                dniVerified = false
            )
        }
        if (filtered.length == 8) {
            validateDniWithReniec(filtered)
        }
    }

    private fun validateDniWithReniec(dni: String) {
        dniLookupJob?.cancel()
        dniLookupJob = viewModelScope.launch {
            _uiState.update { it.copy(isLookingUpDni = true, documentError = null) }
            val result = lookupIdentityDocumentUseCase(IdentityDocumentType.DNI, dni)
            result.onSuccess { lookup ->
                if (lookup is com.tuxlogic.shiftiq.mobile.feature.shared.domain.model.IdentityDocumentData.DniData && lookup.verified && lookup.fullName.isNotBlank()) {
                    _uiState.update { current ->
                        current.copy(
                            isLookingUpDni = false,
                            dniVerified = true,
                            fullName = if (current.fullName.isBlank()) lookup.fullName else current.fullName,
                            documentError = null
                        )
                    }
                } else {
                    _uiState.update { current ->
                        current.copy(
                            isLookingUpDni = false,
                            dniVerified = false,
                            documentError = "El DNI no fue encontrado en el padrón oficial (RENIEC)."
                        )
                    }
                }
            }.onFailure {
                _uiState.update { current ->
                    current.copy(
                        isLookingUpDni = false,
                        dniVerified = false
                    )
                }
            }
        }
    }

    fun onPhoneChange(value: String) {
        val filtered = value.filter { it.isDigit() }.take(9)
        _uiState.update { it.copy(phone = filtered, phoneError = null, errorMessage = null) }
    }

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, emailError = null, errorMessage = null) }
    }

    fun onEmailChanged(value: String) = onEmailChange(value)

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, passwordError = null, errorMessage = null) }
    }

    fun onPasswordChanged(value: String) = onPasswordChange(value)

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

    fun onRoleSelected(role: String) {
        _uiState.update { it.copy(selectedRole = role) }
    }

    // Modificadores del Vehículo y Dongle (Paso 2)
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
        val uppercase = value.uppercase().take(10)
        _uiState.update { it.copy(plateNumber = uppercase, plateError = null) }
    }

    fun onVinChange(value: String) {
        val uppercase = value.uppercase().filter { it !in "IOQ" }.take(17)
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

    // Modificadores de OTP (Paso de Verificación)
    fun onOtpCodeChanged(code: String) {
        val filtered = code.filter { it.isDigit() }.take(6)
        _uiState.update {
            it.copy(
                otpCode = filtered,
                isOtpValid = filtered.length == 6,
                errorMessage = null
            )
        }
    }

    private fun startOtpTimer() {
        timerJob?.cancel()
        _uiState.update { it.copy(timeLeft = 60, isResendEnabled = false) }
        timerJob = viewModelScope.launch {
            while (_uiState.value.timeLeft > 0) {
                delay(1000)
                _uiState.update { current ->
                    val newTime = current.timeLeft - 1
                    current.copy(
                        timeLeft = newTime,
                        isResendEnabled = newTime <= 0
                    )
                }
            }
        }
    }

    fun onResendOtp() {
        val state = _uiState.value
        if (!state.isResendEnabled && state.timeLeft > 0) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = sendOtpUseCase(state.email.trim())
            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, errorMessage = null) }
                startOtpTimer()
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error al reenviar código: ${error.message}"
                    )
                }
            }
        }
    }

    fun requestOtpAndProceed() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = sendOtpUseCase(state.email.trim())
            result.onSuccess {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        step = RegisterStep.OTP,
                        errorMessage = null,
                        otpCode = "",
                        isOtpValid = false
                    )
                }
                startOtpTimer()
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        step = RegisterStep.ERROR,
                        errorMessage = "No se pudo enviar el código de verificación al correo: ${error.message}"
                    )
                }
            }
        }
    }

    fun onVerifyOtpAndRegister(onSuccess: () -> Unit = {}) {
        val state = _uiState.value
        if (!state.isOtpCodeComplete) {
            _uiState.update { it.copy(errorMessage = "Ingresa el código completo de 6 dígitos.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val verifyResult = verifyOtpUseCase(state.email.trim(), state.otpCode.trim())
            verifyResult.onSuccess { verified ->
                if (verified) {
                    executeFinalRegistration(onSuccess)
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "El código ingresado es incorrecto o ha expirado."
                        )
                    }
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error de validación OTP: ${error.message}"
                    )
                }
            }
        }
    }

    // Navegación del Wizard
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
                if (state.documentError != null) {
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
                if (state.brand.trim().isBlank()) {
                    _uiState.update { it.copy(errorMessage = "Ingresa la marca del vehículo.") }
                    return
                }
                if (state.model.trim().isBlank()) {
                    _uiState.update { it.copy(errorMessage = "Ingresa el modelo del vehículo.") }
                    return
                }
                if (!state.isYearValid) {
                    _uiState.update { it.copy(errorMessage = "Ingresa un año de fabricación válido (1980 - 2026).") }
                    return
                }
                if (!state.isPlateValid) {
                    _uiState.update { it.copy(plateError = "Ingresa una placa vehicular válida (entre 5 y 10 caracteres).") }
                    return
                }
                if (!state.isVinValid) {
                    _uiState.update { it.copy(vinError = "El número de chasis (VIN) debe tener exactamente 17 caracteres sin I, O, Q.") }
                    return
                }

                _uiState.update { it.copy(step = RegisterStep.STEP_3_CONFIRM, errorMessage = null) }
            }

            RegisterStep.STEP_3_CONFIRM -> {
                requestOtpAndProceed()
            }

            RegisterStep.OTP -> {
                onVerifyOtpAndRegister()
            }

            RegisterStep.SUCCESS -> { /* Finalizado */ }

            RegisterStep.ERROR -> {
                onCorrectData()
            }
        }
    }

    fun onPreviousStep(onExit: () -> Unit) {
        when (_uiState.value.step) {
            RegisterStep.STEP_1_USER -> onExit()
            RegisterStep.STEP_2_VEHICLE -> _uiState.update { it.copy(step = RegisterStep.STEP_1_USER, errorMessage = null) }
            RegisterStep.STEP_3_CONFIRM -> _uiState.update { it.copy(step = RegisterStep.STEP_2_VEHICLE, errorMessage = null) }
            RegisterStep.OTP -> _uiState.update { it.copy(step = RegisterStep.STEP_3_CONFIRM, errorMessage = null) }
            RegisterStep.ERROR -> onCorrectData()
            RegisterStep.SUCCESS -> onExit()
        }
    }

    fun onCorrectData() {
        val state = _uiState.value
        val isVehicleError = state.errorMessage?.let { msg ->
            msg.contains("placa", ignoreCase = true) ||
            msg.contains("vin", ignoreCase = true) ||
            msg.contains("vehículo", ignoreCase = true) ||
            msg.contains("vehicular", ignoreCase = true)
        } ?: false

        val targetStep = if (isVehicleError) {
            RegisterStep.STEP_2_VEHICLE
        } else {
            RegisterStep.STEP_1_USER
        }
        _uiState.update { it.copy(step = targetStep, errorMessage = null) }
    }

    fun register() = requestOtpAndProceed()

    fun onSubmitRegistration(onSuccess: () -> Unit = {}) {
        requestOtpAndProceed()
    }

    private fun executeFinalRegistration(onSuccess: () -> Unit) {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // 1. Registro de usuario en IAM (ahora con OTP validado previamente)
            val regResult = registerUseCase(
                email = state.email.trim(),
                password = state.password,
                role = state.selectedRole
            )

            regResult.onSuccess { user ->
                // 2. Iniciar sesión para obtener tokens e inyectar Bearer
                val loginResult = loginUseCase(state.email.trim(), state.password)
                loginResult.onSuccess {
                    // 3. Crear perfil de cliente en Core
                    val nameParts = state.fullName.trim().split(" ")
                    val firstName = nameParts.firstOrNull()?.ifBlank { "Conductor" } ?: "Conductor"
                    val lastName = if (nameParts.size > 1) nameParts.drop(1).joinToString(" ") else "ShiftIQ"

                    val custResult = createCustomerProfileUseCase(
                        userId = user.id.value.toString(),
                        firstName = firstName,
                        lastName = lastName,
                        businessName = "",
                        documentType = "DNI",
                        documentNumber = state.documentNumber.trim(),
                        phone = state.phone.trim()
                    )

                    custResult.onSuccess {
                        // 4. Registrar vehículo en IoT
                        val vehResult = registerVehicleUseCase(
                            plateNumber = state.plateNumber.trim().uppercase(),
                            brand = state.brand.trim(),
                            model = state.model.trim(),
                            year = state.year.toIntOrNull() ?: 2020,
                            vin = state.vin.trim().uppercase()
                        )

                        vehResult.onSuccess {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    step = RegisterStep.SUCCESS,
                                    isSuccess = true,
                                    errorMessage = null
                                )
                            }
                            onSuccess()
                        }.onFailure { vehError ->
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    step = RegisterStep.ERROR,
                                    errorMessage = "Error al registrar vehículo: ${vehError.message}"
                                )
                            }
                        }
                    }.onFailure { custError ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                step = RegisterStep.ERROR,
                                errorMessage = "Error al crear perfil de conductor: ${custError.message}"
                            )
                        }
                    }
                }.onFailure { loginError ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            step = RegisterStep.ERROR,
                            errorMessage = "Cuenta creada, pero falló el inicio de sesión: ${loginError.message}"
                        )
                    }
                }
            }.onFailure { regError ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        step = RegisterStep.ERROR,
                        errorMessage = regError.message
                    )
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        dniLookupJob?.cancel()
    }
}
