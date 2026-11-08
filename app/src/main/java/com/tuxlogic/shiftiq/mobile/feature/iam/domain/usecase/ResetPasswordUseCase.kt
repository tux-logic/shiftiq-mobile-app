package com.tuxlogic.shiftiq.mobile.feature.iam.domain.usecase

import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.feature.iam.domain.repository.AuthRepository
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(token: String, newPassword: String): AppResult<Unit> {
        if (token.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "token", message = "Recovery token is required"))
        }
        if (newPassword.length < 8) {
            return AppResult.Failure(AppError.Validation(details = "newPassword", message = "Password must have at least 8 characters"))
        }
        return authRepository.resetPassword(token.trim(), newPassword)
    }
}
