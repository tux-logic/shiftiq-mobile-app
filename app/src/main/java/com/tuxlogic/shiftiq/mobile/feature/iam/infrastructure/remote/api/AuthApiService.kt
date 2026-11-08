package com.tuxlogic.shiftiq.mobile.feature.iam.infrastructure.remote.api

import com.tuxlogic.shiftiq.mobile.feature.iam.infrastructure.remote.dto.AuthenticatedUserResponseDto
import com.tuxlogic.shiftiq.mobile.feature.iam.infrastructure.remote.dto.GoogleSignInRequestDto
import com.tuxlogic.shiftiq.mobile.feature.iam.infrastructure.remote.dto.MessageResponseDto
import com.tuxlogic.shiftiq.mobile.feature.iam.infrastructure.remote.dto.PasswordRecoveryRequestDto
import com.tuxlogic.shiftiq.mobile.feature.iam.infrastructure.remote.dto.PasswordResetRequestDto
import com.tuxlogic.shiftiq.mobile.feature.iam.infrastructure.remote.dto.RefreshTokenRequestDto
import com.tuxlogic.shiftiq.mobile.feature.iam.infrastructure.remote.dto.SendOtpRequestDto
import com.tuxlogic.shiftiq.mobile.feature.iam.infrastructure.remote.dto.SendOtpResponseDto
import com.tuxlogic.shiftiq.mobile.feature.iam.infrastructure.remote.dto.SignInRequestDto
import com.tuxlogic.shiftiq.mobile.feature.iam.infrastructure.remote.dto.SignUpRequestDto
import com.tuxlogic.shiftiq.mobile.feature.iam.infrastructure.remote.dto.TokenResponseDto
import com.tuxlogic.shiftiq.mobile.feature.iam.infrastructure.remote.dto.UpdateEmailRequestDto
import com.tuxlogic.shiftiq.mobile.feature.iam.infrastructure.remote.dto.UpdatePasswordRequestDto
import com.tuxlogic.shiftiq.mobile.feature.iam.infrastructure.remote.dto.UserResourceDto
import com.tuxlogic.shiftiq.mobile.feature.iam.infrastructure.remote.dto.VerifyOtpRequestDto
import com.tuxlogic.shiftiq.mobile.feature.iam.infrastructure.remote.dto.VerifyOtpResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface AuthApiService {

    @POST("api/v1/authentication/sessions")
    suspend fun login(
        @Body request: SignInRequestDto
    ): Response<AuthenticatedUserResponseDto>

    @POST("api/v1/authentication/sessions/google")
    suspend fun loginWithGoogle(
        @Body request: GoogleSignInRequestDto
    ): Response<AuthenticatedUserResponseDto>

    @POST("api/v1/authentication/sessions/refresh")
    suspend fun refresh(
        @Body request: RefreshTokenRequestDto
    ): Response<TokenResponseDto>

    @HTTP(method = "DELETE", path = "api/v1/authentication/sessions", hasBody = true)
    suspend fun logout(
        @Body request: RefreshTokenRequestDto
    ): Response<Unit>

    @POST("api/v1/authentication/otp/send")
    suspend fun sendOtp(
        @Body request: SendOtpRequestDto
    ): Response<SendOtpResponseDto>

    @POST("api/v1/authentication/otp/verify")
    suspend fun verifyOtp(
        @Body request: VerifyOtpRequestDto
    ): Response<VerifyOtpResponseDto>

    @POST("api/v1/authentication/password-recoveries")
    suspend fun requestPasswordRecovery(
        @Body request: PasswordRecoveryRequestDto
    ): Response<Unit>

    @POST("api/v1/authentication/password-resets")
    suspend fun resetPassword(
        @Body request: PasswordResetRequestDto
    ): Response<MessageResponseDto>

    @POST("api/v1/users")
    suspend fun register(
        @Body request: SignUpRequestDto
    ): Response<UserResourceDto>

    @GET("api/v1/users/{userId}")
    suspend fun getUserById(
        @Path("userId") userId: String
    ): Response<UserResourceDto>

    @GET("api/v1/users")
    suspend fun getUserByEmail(
        @Query("email") email: String
    ): Response<UserResourceDto>

    @PUT("api/v1/users/{userId}/email")
    suspend fun updateEmail(
        @Path("userId") userId: String,
        @Body request: UpdateEmailRequestDto
    ): Response<AuthenticatedUserResponseDto>

    @PUT("api/v1/users/{userId}/password")
    suspend fun updatePassword(
        @Path("userId") userId: String,
        @Body request: UpdatePasswordRequestDto
    ): Response<MessageResponseDto>
}
