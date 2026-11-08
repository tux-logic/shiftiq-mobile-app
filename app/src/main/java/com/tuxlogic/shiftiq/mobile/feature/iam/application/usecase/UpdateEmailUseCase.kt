package com.tuxlogic.shiftiq.mobile.feature.iam.application.usecase

import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.model.UserId
import com.tuxlogic.shiftiq.mobile.feature.iam.domain.model.AuthenticatedUser
import com.tuxlogic.shiftiq.mobile.feature.iam.domain.repository.AuthRepository
import javax.inject.Inject

class UpdateEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(userId: UserId, newEmail: String): AppResult<AuthenticatedUser> {
        val trimmed = newEmail.trim()
        if (trimmed.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "email", message = "New email is required"))
        }
        return authRepository.updateEmail(userId, trimmed)
    }
}
