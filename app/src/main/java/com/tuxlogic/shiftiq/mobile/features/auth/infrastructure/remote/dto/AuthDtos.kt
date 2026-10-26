package com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.remote.dto

import com.google.gson.annotations.SerializedName

data class SignInRequestDto(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class SignUpRequestDto(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class GoogleSignInRequestDto(
    @SerializedName("idToken") val idToken: String
)

data class AuthenticatedUserDto(
    @SerializedName("id") val id: String,
    @SerializedName("email") val email: String,
    @SerializedName("role") val role: String,
    @SerializedName("token") val token: String,
    @SerializedName("refreshToken") val refreshToken: String,
    @SerializedName("accessTokenExpiresInSeconds") val accessTokenExpiresInSeconds: Long
)

data class PasswordRecoveryRequestDto(
    @SerializedName("email") val email: String
)

data class ResetPasswordRequestDto(
    @SerializedName("token") val token: String,
    @SerializedName("newPassword") val newPassword: String
)

data class RefreshTokenRequestDto(
    @SerializedName("refreshToken") val refreshToken: String
)

data class SendOtpRequestDto(
    @SerializedName("email") val email: String
)

data class VerifyOtpRequestDto(
    @SerializedName("email") val email: String,
    @SerializedName("code") val code: String
)

data class UserResponseDto(
    @SerializedName("id") val id: String,
    @SerializedName("email") val email: String,
    @SerializedName("role") val role: String
)

data class CustomerRegistrationRequestDto(
    @SerializedName("userId") val userId: String,
    @SerializedName("isCorporate") val isCorporate: Boolean = false,
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("businessName") val businessName: String = "",
    @SerializedName("documentType") val documentType: String = "DNI",
    @SerializedName("documentNumber") val documentNumber: String,
    @SerializedName("phone") val phone: String
)

data class CustomerResponseDto(
    @SerializedName("id") val id: String,
    @SerializedName("userId") val userId: String,
    @SerializedName("isCorporate") val isCorporate: Boolean,
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("businessName") val businessName: String?,
    @SerializedName("documentType") val documentType: String,
    @SerializedName("documentNumber") val documentNumber: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("createdAt") val createdAt: String? = null
)

data class VehicleRegistrationRequestDto(
    @SerializedName("plateNumber") val plateNumber: String,
    @SerializedName("brand") val brand: String,
    @SerializedName("model") val model: String,
    @SerializedName("year") val year: Int,
    @SerializedName("vin") val vin: String,
    @SerializedName("photoUrl") val photoUrl: String? = null
)

data class VehicleRegistrationResponseDto(
    @SerializedName("id") val id: String,
    @SerializedName("userId") val userId: String,
    @SerializedName("vehicleId") val vehicleId: String,
    @SerializedName("status") val status: String,
    @SerializedName("createdAt") val createdAt: String? = null
)

typealias VehicleResponseDto = VehicleRegistrationResponseDto

data class IdentityLookupResponseDto(
    @SerializedName("documentType") val documentType: String,
    @SerializedName("documentNumber") val documentNumber: String,
    @SerializedName("firstName") val firstName: String?,
    @SerializedName("lastName") val lastName: String?,
    @SerializedName("fullName") val fullName: String?,
    @SerializedName("businessName") val businessName: String?,
    @SerializedName("valid") val valid: Boolean
)
