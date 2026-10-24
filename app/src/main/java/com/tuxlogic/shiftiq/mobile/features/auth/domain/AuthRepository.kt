package com.tuxlogic.shiftiq.mobile.features.auth.domain

import com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.remote.dto.IdentityLookupResponseDto

interface AuthRepository {
    suspend fun signIn(email: String, password: String): Result<User>
    suspend fun googleSignIn(idToken: String): Result<User>
    suspend fun signUp(email: String, password: String): Result<User>
    suspend fun registerUserWithVehicle(
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
    ): Result<User>
    suspend fun sendOtp(email: String): Result<Unit>
    suspend fun verifyOtp(email: String, code: String): Result<Unit>
    suspend fun lookupIdentity(type: String, number: String): Result<IdentityLookupResponseDto>
    suspend fun requestPasswordRecovery(email: String): Result<Unit>
    suspend fun resetPassword(token: String, newPassword: String): Result<Unit>
    suspend fun getActiveUser(): User?
    suspend fun signOut(): Unit
}
