package com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.remote

import com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.remote.dto.AuthenticatedUserDto
import com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.remote.dto.CustomerRegistrationRequestDto
import com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.remote.dto.CustomerResponseDto
import com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.remote.dto.GoogleSignInRequestDto
import com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.remote.dto.IdentityLookupResponseDto
import com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.remote.dto.PasswordRecoveryRequestDto
import com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.remote.dto.RefreshTokenRequestDto
import com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.remote.dto.ResetPasswordRequestDto
import com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.remote.dto.SendOtpRequestDto
import com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.remote.dto.SignInRequestDto
import com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.remote.dto.SignUpRequestDto
import com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.remote.dto.UserResponseDto
import com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.remote.dto.VehicleRegistrationRequestDto
import com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.remote.dto.VehicleRegistrationResponseDto
import com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.remote.dto.VerifyOtpRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApiService {

    @POST("authentication/sessions")
    suspend fun signIn(@Body request: SignInRequestDto): Response<AuthenticatedUserDto>

    @POST("authentication/sessions/google")
    suspend fun googleSignIn(@Body request: GoogleSignInRequestDto): Response<AuthenticatedUserDto>

    @POST("authentication/sessions/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequestDto): Response<AuthenticatedUserDto>

    @HTTP(method = "DELETE", path = "authentication/sessions", hasBody = true)
    suspend fun revokeSession(@Body request: RefreshTokenRequestDto): Response<Unit>

    @POST("authentication/otp/send")
    suspend fun sendOtp(@Body request: SendOtpRequestDto): Response<Unit>

    @POST("authentication/otp/verify")
    suspend fun verifyOtp(@Body request: VerifyOtpRequestDto): Response<Unit>

    @POST("authentication/password-recoveries")
    suspend fun requestPasswordRecovery(@Body request: PasswordRecoveryRequestDto): Response<Unit>

    @POST("authentication/password-resets")
    suspend fun resetPassword(@Body request: ResetPasswordRequestDto): Response<Unit>

    @POST("users")
    suspend fun signUp(@Body request: SignUpRequestDto): Response<UserResponseDto>

    @POST("users")
    suspend fun registerUser(@Body request: SignUpRequestDto): Response<UserResponseDto>

    @POST("customers")
    suspend fun createCustomer(
        @Header("Authorization") authorization: String,
        @Body request: CustomerRegistrationRequestDto
    ): Response<CustomerResponseDto>

    @POST("vehicles")
    suspend fun registerVehicle(
        @Header("Authorization") authorization: String,
        @Body request: VehicleRegistrationRequestDto
    ): Response<VehicleRegistrationResponseDto>

    @GET("identity-validations/lookup")
    suspend fun lookupIdentity(
        @Query("type") type: String,
        @Query("number") number: String
    ): Response<IdentityLookupResponseDto>
}
