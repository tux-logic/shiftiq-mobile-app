package com.tuxlogic.shiftiq.mobile.features.auth.application

import com.tuxlogic.shiftiq.mobile.features.auth.domain.AuthRepository
import com.tuxlogic.shiftiq.mobile.features.auth.domain.User
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        return repository.signIn(email, password)
    }
}

class SignUpUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        return repository.signUp(email, password)
    }
}

class GoogleSignInUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(idToken: String): Result<User> {
        return repository.googleSignIn(idToken)
    }
}

class RequestPasswordResetUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String): Result<Unit> {
        return repository.requestPasswordRecovery(email)
    }
}

class VerifyOtpUseCase @Inject constructor() {
    operator fun invoke(otpCode: String): Result<Unit> {
        return if (otpCode.length == 6 && otpCode.all { it.isDigit() }) {
            Result.success(Unit)
        } else {
            Result.failure(IllegalArgumentException("El código de verificación debe contener 6 dígitos numéricos."))
        }
    }
}

class UpdatePasswordUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(token: String, newPassword: String): Result<Unit> {
        return repository.resetPassword(token, newPassword)
    }
}
