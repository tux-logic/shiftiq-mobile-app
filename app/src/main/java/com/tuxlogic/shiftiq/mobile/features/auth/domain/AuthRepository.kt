package com.tuxlogic.shiftiq.mobile.features.auth.domain

interface AuthRepository {
    suspend fun signIn(email: String, password: String): Result<User>
    suspend fun googleSignIn(idToken: String): Result<User>
    suspend fun signUp(email: String, password: String): Result<User>
    suspend fun getActiveUser(): User?
    suspend fun signOut(): Unit
}
