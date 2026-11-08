package com.tuxlogic.shiftiq.mobile.feature.iam.domain.usecase

import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.feature.iam.domain.model.User
import com.tuxlogic.shiftiq.mobile.feature.iam.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        @Suppress("UNUSED_PARAMETER") role: String = "ROLE_USER"
    ): AppResult<User> {
        val trimmedEmail = email.trim()
        if (trimmedEmail.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "email", message = "Email is required"))
        }
        if (!EMAIL_REGEX.matches(trimmedEmail)) {
            return AppResult.Failure(AppError.Validation(details = "email", message = "Invalid email format"))
        }
        if (password.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "password", message = "Password is required"))
        }
        if (password.length < 8) {
            return AppResult.Failure(AppError.Validation(details = "password", message = "Password must have at least 8 characters"))
        }
        return authRepository.register(trimmedEmail, password)
    }

    companion object {
        private val EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
    }
}
