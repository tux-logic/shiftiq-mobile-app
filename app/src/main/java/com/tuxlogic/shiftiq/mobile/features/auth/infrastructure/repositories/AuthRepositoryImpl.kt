package com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.repositories

import com.tuxlogic.shiftiq.mobile.features.auth.domain.AuthRepository
import com.tuxlogic.shiftiq.mobile.features.auth.domain.User
import com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.local.UserDao
import com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.local.UserEntity
import com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.remote.AuthApiService
import com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.remote.dto.GoogleSignInRequestDto
import com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.remote.dto.PasswordRecoveryRequestDto
import com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.remote.dto.ResetPasswordRequestDto
import com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.remote.dto.SignInRequestDto
import com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.remote.dto.SignUpRequestDto
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val userDao: UserDao
) : AuthRepository {

    override suspend fun signIn(email: String, password: String): Result<User> {
        return try {
            val response = authApiService.signIn(SignInRequestDto(email, password))
            if (response.isSuccessful && response.body() != null) {
                val dto = response.body()!!
                val entity = UserEntity(
                    id = dto.id,
                    email = dto.email,
                    role = dto.role,
                    token = dto.token,
                    refreshToken = dto.refreshToken
                )
                userDao.clearUsers()
                userDao.insertUser(entity)
                Result.success(
                    User(
                        id = entity.id,
                        email = entity.email,
                        role = entity.role,
                        token = entity.token,
                        refreshToken = entity.refreshToken
                    )
                )
            } else {
                val msg = when (response.code()) {
                    401 -> "Credenciales incorrectas. Verifica tu correo y contraseña."
                    404 -> "Usuario no encontrado."
                    else -> "Error al iniciar sesión (${response.code()})."
                }
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(Exception(resolveErrorMessage(e)))
        }
    }

    override suspend fun googleSignIn(idToken: String): Result<User> {
        return try {
            val response = authApiService.googleSignIn(GoogleSignInRequestDto(idToken))
            if (response.isSuccessful && response.body() != null) {
                val dto = response.body()!!
                val entity = UserEntity(
                    id = dto.id,
                    email = dto.email,
                    role = dto.role,
                    token = dto.token,
                    refreshToken = dto.refreshToken
                )
                userDao.clearUsers()
                userDao.insertUser(entity)
                Result.success(
                    User(
                        id = entity.id,
                        email = entity.email,
                        role = entity.role,
                        token = entity.token,
                        refreshToken = entity.refreshToken
                    )
                )
            } else {
                Result.failure(Exception("Error en Google Sign-In: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(resolveErrorMessage(e)))
        }
    }

    override suspend fun signUp(email: String, password: String): Result<User> {
        return try {
            val response = authApiService.signUp(SignUpRequestDto(email, password))
            if (response.isSuccessful && response.body() != null) {
                val dto = response.body()!!
                val entity = UserEntity(
                    id = dto.id,
                    email = dto.email,
                    role = dto.role,
                    token = dto.token,
                    refreshToken = dto.refreshToken
                )
                userDao.clearUsers()
                userDao.insertUser(entity)
                Result.success(
                    User(
                        id = entity.id,
                        email = entity.email,
                        role = entity.role,
                        token = entity.token,
                        refreshToken = entity.refreshToken
                    )
                )
            } else {
                val msg = when (response.code()) {
                    409 -> "Ya existe una cuenta registrada con este correo."
                    else -> "Error al registrar usuario (${response.code()})."
                }
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(Exception(resolveErrorMessage(e)))
        }
    }

    override suspend fun requestPasswordRecovery(email: String): Result<Unit> {
        return try {
            val response = authApiService.requestPasswordRecovery(PasswordRecoveryRequestDto(email))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val msg = when (response.code()) {
                    404 -> "No se encontró ninguna cuenta registrada con este correo electrónico."
                    400 -> "Solicitud inválida. Revisa el correo ingresado."
                    else -> "No se pudo procesar la solicitud de recuperación (${response.code()})."
                }
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(Exception(resolveErrorMessage(e)))
        }
    }

    override suspend fun resetPassword(token: String, newPassword: String): Result<Unit> {
        return try {
            val response = authApiService.resetPassword(ResetPasswordRequestDto(token, newPassword))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val msg = when (response.code()) {
                    400, 404 -> "El código de seguridad es inválido o ha expirado. Solicita uno nuevo."
                    else -> "No se pudo restablecer la contraseña (${response.code()})."
                }
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(Exception(resolveErrorMessage(e)))
        }
    }

    private fun resolveErrorMessage(e: Exception): String {
        return when (e) {
            is java.net.ConnectException -> "No se pudo conectar con el servidor local. Verifica que el backend esté ejecutándose."
            is java.net.SocketTimeoutException -> "El servidor tardó demasiado en responder. Inténtalo nuevamente."
            is java.net.UnknownHostException -> "No se pudo encontrar el servidor. Revisa tu conexión de red."
            else -> {
                val msg = e.message ?: ""
                if (msg.contains("CLEARTEXT", ignoreCase = true)) {
                    "Error de política de red: Tráfico HTTP no cifrado no permitido."
                } else {
                    msg.ifBlank { "Error de conexión. Inténtalo nuevamente." }
                }
            }
        }
    }

    override suspend fun getActiveUser(): User? {
        val entity = userDao.getActiveUser() ?: return null
        return User(
            id = entity.id,
            email = entity.email,
            role = entity.role,
            token = entity.token,
            refreshToken = entity.refreshToken
        )
    }

    override suspend fun signOut() {
        userDao.clearUsers()
    }
}
