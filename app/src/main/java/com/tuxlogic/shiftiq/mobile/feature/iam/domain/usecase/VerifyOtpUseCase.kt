package com.tuxlogic.shiftiq.mobile.feature.iam.domain.usecase

import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.feature.iam.domain.repository.AuthRepository
import javax.inject.Inject

class VerifyOtpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, code: String): AppResult<Boolean> {
        val trimmedEmail = email.trim()
        val trimmedCode = code.trim()
        if (trimmedEmail.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "email", message = "Email is required"))
        }
        if (trimmedCode.length != 6) {
            return AppResult.Failure(AppError.Validation(details = "code", message = "OTP code must be 6 digits"))
        }
        return authRepository.verifyOtp(trimmedEmail, trimmedCode)
    }
}
