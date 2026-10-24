package com.tuxlogic.shiftiq.mobile.features.auth.application

import com.tuxlogic.shiftiq.mobile.features.auth.domain.AuthRepository
import com.tuxlogic.shiftiq.mobile.features.auth.domain.User
import com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.remote.dto.IdentityLookupResponseDto
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

class RegisterUserUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        fullName: String,
        documentNumber: String,
        phone: String,
        email: String,
        password: String,
        plateNumber: String,
        brand: String,
        model: String,
        year: Int,
        vin: String,
        dongleId: String? = null
    ): Result<User> {
        return repository.registerUserWithVehicle(
            fullName = fullName,
            documentNumber = documentNumber,
            phone = phone,
            email = email,
            password = password,
            plateNumber = plateNumber,
            brand = brand,
            model = model,
            year = year,
            vin = vin,
            dongleId = dongleId
        )
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

class SendOtpUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String): Result<Unit> {
        return repository.sendOtp(email)
    }
}

class VerifyOtpUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, otpCode: String): Result<Unit> {
        if (otpCode.length != 6 || !otpCode.all { it.isDigit() }) {
            return Result.failure(IllegalArgumentException("El código de verificación debe contener 6 dígitos numéricos."))
        }
        return repository.verifyOtp(email, otpCode)
    }
}

class LookupIdentityUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(type: String, number: String): Result<IdentityLookupResponseDto> {
        return repository.lookupIdentity(type, number)
    }
}

class UpdatePasswordUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(token: String, newPassword: String): Result<Unit> {
        return repository.resetPassword(token, newPassword)
    }
}
