package com.tuxlogic.shiftiq.mobile.feature.iam.infrastructure.repository

import com.tuxlogic.shiftiq.mobile.core.common.dispatchers.DispatcherProvider
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.common.result.map
import com.tuxlogic.shiftiq.mobile.core.datastore.SessionDataStore
import com.tuxlogic.shiftiq.mobile.core.datastore.SessionState
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.core.model.UserId
import com.tuxlogic.shiftiq.mobile.core.network.safeApiCall
import com.tuxlogic.shiftiq.mobile.feature.iam.infrastructure.mapper.toDomain
import com.tuxlogic.shiftiq.mobile.feature.iam.infrastructure.remote.api.AuthApiService
import com.tuxlogic.shiftiq.mobile.feature.iam.infrastructure.remote.dto.GoogleSignInRequestDto
import com.tuxlogic.shiftiq.mobile.feature.iam.infrastructure.remote.dto.PasswordRecoveryRequestDto
import com.tuxlogic.shiftiq.mobile.feature.iam.infrastructure.remote.dto.PasswordResetRequestDto
import com.tuxlogic.shiftiq.mobile.feature.iam.infrastructure.remote.dto.RefreshTokenRequestDto
import com.tuxlogic.shiftiq.mobile.feature.iam.infrastructure.remote.dto.SendOtpRequestDto
import com.tuxlogic.shiftiq.mobile.feature.iam.infrastructure.remote.dto.SignInRequestDto
import com.tuxlogic.shiftiq.mobile.feature.iam.infrastructure.remote.dto.SignUpRequestDto
import com.tuxlogic.shiftiq.mobile.feature.iam.infrastructure.remote.dto.UpdateEmailRequestDto
import com.tuxlogic.shiftiq.mobile.feature.iam.infrastructure.remote.dto.UpdatePasswordRequestDto
import com.tuxlogic.shiftiq.mobile.feature.iam.infrastructure.remote.dto.VerifyOtpRequestDto
import com.tuxlogic.shiftiq.mobile.feature.iam.domain.model.AuthenticatedUser
import com.tuxlogic.shiftiq.mobile.feature.iam.domain.model.OtpDispatchResult
import com.tuxlogic.shiftiq.mobile.feature.iam.domain.model.User
import com.tuxlogic.shiftiq.mobile.feature.iam.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val sessionDataStore: SessionDataStore,
    private val dispatchers: DispatcherProvider
) : AuthRepository {

    override suspend fun login(email: String, password: String): AppResult<AuthenticatedUser> =
        withContext(dispatchers.io) {
            val result = safeApiCall {
                authApiService.login(SignInRequestDto(email = email, password = password))
            }
            result.map { dto ->
                val authenticatedUser = dto.toDomain()
                sessionDataStore.saveSession(
                    accessToken = authenticatedUser.accessToken,
                    refreshToken = authenticatedUser.refreshToken,
                    userId = authenticatedUser.id,
                    email = authenticatedUser.email,
                    role = authenticatedUser.role
                )
                authenticatedUser
            }
        }

    override suspend fun loginWithGoogle(idToken: String): AppResult<AuthenticatedUser> =
        withContext(dispatchers.io) {
            val result = safeApiCall {
                authApiService.loginWithGoogle(GoogleSignInRequestDto(idToken = idToken))
            }
            result.map { dto ->
                val authenticatedUser = dto.toDomain()
                sessionDataStore.saveSession(
                    accessToken = authenticatedUser.accessToken,
                    refreshToken = authenticatedUser.refreshToken,
                    userId = authenticatedUser.id,
                    email = authenticatedUser.email,
                    role = authenticatedUser.role
                )
                authenticatedUser
            }
        }

    override suspend fun register(
        email: String,
        password: String
    ): AppResult<User> = withContext(dispatchers.io) {
        safeApiCall {
            authApiService.register(
                SignUpRequestDto(
                    email = email,
                    password = password
                )
            )
        }.map { it.toDomain() }
    }

    override suspend fun logout(): AppResult<Unit> = withContext(dispatchers.io) {
        val session = sessionDataStore.sessionState.first()
        val refreshToken = session.refreshToken
        if (refreshToken != null) {
            safeApiCall {
                authApiService.logout(RefreshTokenRequestDto(refreshToken))
            }
        }
        sessionDataStore.clearSession()
        AppResult.Success(Unit)
    }

    override suspend fun sendOtp(email: String): AppResult<OtpDispatchResult> =
        withContext(dispatchers.io) {
            safeApiCall {
                authApiService.sendOtp(SendOtpRequestDto(email = email))
            }.map { it.toDomain() }
        }

    override suspend fun verifyOtp(email: String, code: String): AppResult<Boolean> =
        withContext(dispatchers.io) {
            safeApiCall {
                authApiService.verifyOtp(VerifyOtpRequestDto(email = email, code = code))
            }.map { it.verified }
        }

    override suspend fun requestPasswordRecovery(email: String): AppResult<Unit> =
        withContext(dispatchers.io) {
            safeApiCall {
                authApiService.requestPasswordRecovery(PasswordRecoveryRequestDto(email = email))
            }
        }

    override suspend fun resetPassword(token: String, newPassword: String): AppResult<Unit> =
        withContext(dispatchers.io) {
            safeApiCall {
                authApiService.resetPassword(PasswordResetRequestDto(token = token, newPassword = newPassword))
            }.map { }
        }

    override suspend fun getUserById(userId: UserId): AppResult<User> =
        withContext(dispatchers.io) {
            safeApiCall {
                authApiService.getUserById(userId.toString())
            }.map { it.toDomain() }
        }

    override suspend fun getUserByEmail(email: String): AppResult<User> =
        withContext(dispatchers.io) {
            safeApiCall {
                authApiService.getUserByEmail(email)
            }.map { it.toDomain() }
        }

    override suspend fun updateEmail(userId: UserId, newEmail: String): AppResult<AuthenticatedUser> =
        withContext(dispatchers.io) {
            val result = safeApiCall {
                authApiService.updateEmail(userId.toString(), UpdateEmailRequestDto(email = newEmail))
            }
            result.map { dto ->
                val authenticatedUser = dto.toDomain()
                sessionDataStore.saveSession(
                    accessToken = authenticatedUser.accessToken,
                    refreshToken = authenticatedUser.refreshToken,
                    userId = authenticatedUser.id,
                    email = authenticatedUser.email,
                    role = authenticatedUser.role
                )
                authenticatedUser
            }
        }

    override suspend fun updatePassword(
        userId: UserId,
        currentPassword: String,
        newPassword: String
    ): AppResult<Unit> = withContext(dispatchers.io) {
        safeApiCall {
            authApiService.updatePassword(
                userId = userId.toString(),
                request = UpdatePasswordRequestDto(
                    currentPassword = currentPassword,
                    newPassword = newPassword
                )
            )
        }.map { }
    }

    override fun observeSession(): Flow<SessionState> = sessionDataStore.sessionState

    override suspend fun selectBranch(branchId: BranchId): AppResult<Unit> = withContext(dispatchers.io) {
        sessionDataStore.setActiveBranchId(branchId)
        AppResult.Success(Unit)
    }
}
