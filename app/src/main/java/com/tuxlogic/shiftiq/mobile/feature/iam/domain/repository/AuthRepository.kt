package com.tuxlogic.shiftiq.mobile.feature.iam.domain.repository

import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.datastore.SessionState
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.core.model.UserId
import com.tuxlogic.shiftiq.mobile.feature.iam.domain.model.AuthenticatedUser
import com.tuxlogic.shiftiq.mobile.feature.iam.domain.model.OtpDispatchResult
import com.tuxlogic.shiftiq.mobile.feature.iam.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String): AppResult<AuthenticatedUser>
    suspend fun loginWithGoogle(idToken: String): AppResult<AuthenticatedUser>
    suspend fun register(email: String, password: String): AppResult<User>
    suspend fun logout(): AppResult<Unit>
    suspend fun sendOtp(email: String): AppResult<OtpDispatchResult>
    suspend fun verifyOtp(email: String, code: String): AppResult<Boolean>
    suspend fun requestPasswordRecovery(email: String): AppResult<Unit>
    suspend fun resetPassword(token: String, newPassword: String): AppResult<Unit>
    suspend fun getUserById(userId: UserId): AppResult<User>
    suspend fun getUserByEmail(email: String): AppResult<User>
    suspend fun updateEmail(userId: UserId, newEmail: String): AppResult<AuthenticatedUser>
    suspend fun updatePassword(userId: UserId, currentPassword: String, newPassword: String): AppResult<Unit>
    fun observeSession(): Flow<SessionState>
    suspend fun selectBranch(branchId: BranchId): AppResult<Unit>
}
