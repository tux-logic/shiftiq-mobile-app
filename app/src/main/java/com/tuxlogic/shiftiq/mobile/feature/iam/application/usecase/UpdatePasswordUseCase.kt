package com.tuxlogic.shiftiq.mobile.feature.iam.application.usecase

import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.model.UserId
import com.tuxlogic.shiftiq.mobile.feature.iam.domain.repository.AuthRepository
import javax.inject.Inject

class UpdatePasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        userId: UserId,
        currentPassword: String,
        newPassword: String
    ): AppResult<Unit> {
        if (currentPassword.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "currentPassword", message = "Current password is required"))
        }
        if (newPassword.length < 8) {
            return AppResult.Failure(AppError.Validation(details = "newPassword", message = "New password must have at least 8 characters"))
        }
        return authRepository.updatePassword(userId, currentPassword, newPassword)
    }
}
