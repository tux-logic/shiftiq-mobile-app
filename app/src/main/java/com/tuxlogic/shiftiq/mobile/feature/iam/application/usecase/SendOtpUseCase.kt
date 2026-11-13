package com.tuxlogic.shiftiq.mobile.feature.iam.application.usecase

import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.feature.iam.domain.model.OtpDispatchResult
import com.tuxlogic.shiftiq.mobile.feature.iam.domain.repository.AuthRepository
import javax.inject.Inject

class SendOtpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String): AppResult<OtpDispatchResult> {
        val trimmed = email.trim()
        if (trimmed.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "email", message = "Email is required"))
        }
        return authRepository.sendOtp(trimmed)
    }
}
