package com.tuxlogic.shiftiq.mobile.feature.iam.domain.usecase

import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.feature.iam.data.remote.dto.UserResourceDto
import com.tuxlogic.shiftiq.mobile.feature.iam.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        role: String = "ROLE_OWNER"
    ): AppResult<UserResourceDto> {
        val trimmedEmail = email.trim()
        if (trimmedEmail.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "email", message = "El correo electrónico es requerido"))
        }
        if (!EMAIL_REGEX.matches(trimmedEmail)) {
            return AppResult.Failure(AppError.Validation(details = "email", message = "Formato de correo inválido"))
        }
        if (password.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "password", message = "La contraseña es requerida"))
        }
        if (password.length < 8) {
            return AppResult.Failure(AppError.Validation(details = "password", message = "La contraseña debe tener al menos 8 caracteres"))
        }
        return authRepository.register(trimmedEmail, password, listOf(role))
    }

    companion object {
        private val EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
    }
}
