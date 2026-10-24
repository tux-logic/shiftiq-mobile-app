package com.tuxlogic.shiftiq.mobile.features.auth.domain

interface AuthRepository {
    suspend fun signIn(email: String, password: String): Result<User>
    suspend fun googleSignIn(idToken: String): Result<User>
    suspend fun signUp(email: String, password: String): Result<User>
    suspend fun requestPasswordRecovery(email: String): Result<Unit>
    suspend fun resetPassword(token: String, newPassword: String): Result<Unit>
    suspend fun getActiveUser(): User?
    suspend fun signOut(): Unit
}
