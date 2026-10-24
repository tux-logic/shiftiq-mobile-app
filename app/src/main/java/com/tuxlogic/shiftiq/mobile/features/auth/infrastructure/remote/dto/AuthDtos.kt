package com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.remote.dto

data class SignInRequestDto(
    val email: String,
    val password: String
)

data class SignUpRequestDto(
    val email: String,
    val password: String
)

data class GoogleSignInRequestDto(
    val idToken: String
)

data class AuthenticatedUserDto(
    val id: String,
    val email: String,
    val role: String,
    val token: String,
    val refreshToken: String,
    val accessTokenExpiresInSeconds: Long
)

data class PasswordRecoveryRequestDto(
    val email: String
)

data class ResetPasswordRequestDto(
    val token: String,
    val newPassword: String
)

data class UserResponseDto(
    val id: String,
    val email: String,
    val role: String
)

data class VehicleRegistrationRequestDto(
    val plateNumber: String,
    val brand: String,
    val model: String,
    val year: Int,
    val vin: String,
    val photoUrl: String? = null
)

data class VehicleResponseDto(
    val id: String? = null,
    val plateNumber: String,
    val brand: String,
    val model: String,
    val year: Int,
    val vin: String
)

data class CustomerRegistrationRequestDto(
    val userId: String,
    val isCorporate: Boolean = false,
    val firstName: String,
    val lastName: String,
    val businessName: String = "",
    val documentType: String = "DNI",
    val documentNumber: String,
    val phone: String
)
