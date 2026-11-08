package com.tuxlogic.shiftiq.mobile.feature.iam.domain.usecase

import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.feature.iam.domain.model.AuthenticatedUser
import com.tuxlogic.shiftiq.mobile.feature.iam.domain.repository.AuthRepository
import javax.inject.Inject

class LoginWithGoogleUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(idToken: String): AppResult<AuthenticatedUser> {
        if (idToken.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "idToken", message = "Google ID Token is required"))
        }
        return authRepository.loginWithGoogle(idToken.trim())
    }
}
