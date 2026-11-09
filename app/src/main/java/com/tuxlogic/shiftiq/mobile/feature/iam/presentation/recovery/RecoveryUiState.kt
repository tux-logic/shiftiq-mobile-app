package com.tuxlogic.shiftiq.mobile.feature.iam.presentation.recovery

enum class RecoveryStep {
    EMAIL,
    OTP,
    NEW_PASSWORD,
    SUCCESS
}

data class RecoveryUiState(
    val step: RecoveryStep = RecoveryStep.EMAIL,
    val email: String = "",
    val emailError: String? = null,
    val otpCode: String = "",
    val newPassword: String = "",
    val newPasswordError: String? = null,
    val confirmPassword: String = "",
    val confirmPasswordError: String? = null,
    val isNewPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isOtpValid: Boolean = false,
    val timeLeft: Int = 60,
    val isResendEnabled: Boolean = false
) {
    val isEmailValid: Boolean
        get() = email.isNotBlank() && EMAIL_REGEX.matches(email.trim())

    val maskedEmail: String
        get() {
            val trimmed = email.trim()
            val atIndex = trimmed.indexOf('@')
            if (atIndex <= 3) return trimmed
            val prefix = trimmed.substring(0, 3)
            val domain = trimmed.substring(atIndex)
            return "$prefix***$domain"
        }

    val isOtpComplete: Boolean
        get() = otpCode.length == 6 && otpCode.all { it.isDigit() }

    val isPasswordLengthValid: Boolean
        get() = newPassword.length >= 8

    val hasUppercase: Boolean
        get() = newPassword.any { it.isUpperCase() }

    val hasNumber: Boolean
        get() = newPassword.any { it.isDigit() }

    val hasSpecialOrNumber: Boolean
        get() = hasNumber || newPassword.any { !it.isLetterOrDigit() }

    val isNewPasswordValid: Boolean
        get() = isPasswordLengthValid && hasUppercase && hasNumber

    val doPasswordsMatch: Boolean
        get() = newPassword.isNotBlank() && newPassword == confirmPassword

    val formattedTimeLeft: String
        get() {
            val minutes = timeLeft / 60
            val seconds = timeLeft % 60
            return String.format("%02d:%02d", minutes, seconds)
        }

    companion object {
        private val EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
    }
}
