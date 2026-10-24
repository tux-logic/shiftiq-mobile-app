package com.tuxlogic.shiftiq.mobile.features.auth.presentation.register

enum class RegisterStep {
    STEP_1_USER,
    STEP_2_VEHICLE,
    STEP_3_CONFIRM,
    OTP,
    SUCCESS,
    ERROR
}

data class RegisterUiState(
    val step: RegisterStep = RegisterStep.STEP_1_USER,

    // Paso 1: Datos del Propietario
    val fullName: String = "",
    val fullNameError: String? = null,
    val documentNumber: String = "",
    val documentError: String? = null,
    val phone: String = "",
    val phoneError: String? = null,
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val confirmPassword: String = "",
    val confirmPasswordError: String? = null,
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val acceptsNotifications: Boolean = true,
    val acceptsTerms: Boolean = true,

    // Paso 2: Especificaciones del Vehículo y Dongle IoT
    val brand: String = "Kia",
    val model: String = "Rio",
    val year: String = "2019",
    val plateNumber: String = "ABC-123",
    val plateError: String? = null,
    val vin: String = "3KPA24AB1KE129841",
    val vinError: String? = null,
    val isScanningBle: Boolean = false,
    val selectedDongleId: String = "OBD2-BT-9821",
    val dongleSignal: String = "Excelente (-42 dBm)",
    val dongleBattery: Int = 98,

    // Paso OTP (opcional según flujo de verificación)
    val otpCode: String = "",
    val isOtpValid: Boolean = false,
    val timeLeft: Int = 60,
    val isResendEnabled: Boolean = false,

    // Estados Globales
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val isDniValid: Boolean
        get() = documentNumber.trim().length == 8 && documentNumber.trim().all { it.isDigit() }

    val isPhoneValid: Boolean
        get() = phone.filter { it.isDigit() }.length >= 9

    val isEmailValid: Boolean
        get() = email.isNotBlank() && android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()

    val isPasswordLengthValid: Boolean
        get() = password.length >= 8

    val hasUppercase: Boolean
        get() = password.any { it.isUpperCase() }

    val hasSpecialOrNumber: Boolean
        get() = password.any { it.isDigit() || !it.isLetterOrDigit() }

    val isPasswordValid: Boolean
        get() = isPasswordLengthValid && hasUppercase && hasSpecialOrNumber

    val doPasswordsMatch: Boolean
        get() = password.isNotBlank() && password == confirmPassword

    val isStep1Valid: Boolean
        get() = fullName.isNotBlank() && isDniValid && isPhoneValid && isEmailValid && isPasswordValid && doPasswordsMatch && acceptsTerms

    val isPlateValid: Boolean
        get() = plateNumber.trim().isNotBlank() && plateNumber.trim().length in 6..8

    val isVinValid: Boolean
        get() = vin.trim().length == 17

    val isYearValid: Boolean
        get() {
            val y = year.toIntOrNull() ?: return false
            return y in 1980..2026
        }

    val isStep2Valid: Boolean
        get() = brand.isNotBlank() && model.isNotBlank() && isYearValid && isPlateValid && isVinValid && selectedDongleId.isNotBlank()

    val formattedTimeLeft: String
        get() {
            val minutes = timeLeft / 60
            val seconds = timeLeft % 60
            return String.format("%02d:%02d", minutes, seconds)
        }
}
