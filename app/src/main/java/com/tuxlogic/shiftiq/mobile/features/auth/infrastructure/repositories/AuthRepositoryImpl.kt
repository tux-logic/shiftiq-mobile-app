package com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.repositories

import com.tuxlogic.shiftiq.mobile.features.auth.domain.AuthRepository
import com.tuxlogic.shiftiq.mobile.features.auth.domain.User
import com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.local.UserDao
import com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.local.UserEntity
import com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.remote.AuthApiService
import com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.remote.dto.CustomerRegistrationRequestDto
import com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.remote.dto.GoogleSignInRequestDto
import com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.remote.dto.IdentityLookupResponseDto
import com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.remote.dto.PasswordRecoveryRequestDto
import com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.remote.dto.RefreshTokenRequestDto
import com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.remote.dto.ResetPasswordRequestDto
import com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.remote.dto.SendOtpRequestDto
import com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.remote.dto.SignInRequestDto
import com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.remote.dto.SignUpRequestDto
import com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.remote.dto.VehicleRegistrationRequestDto
import com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.remote.dto.VerifyOtpRequestDto
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

    override suspend fun registerUserWithVehicle(
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
        dongleId: String?
    ): Result<User> {
        return try {
            // 1. Registro de usuario en IAM (POST /api/v1/users -> HTTP 201 Created)
            val userResponse = authApiService.registerUser(SignUpRequestDto(email.trim(), password))
            if (userResponse.isSuccessful && userResponse.body() != null) {
                val userDto = userResponse.body()!!
                val nameParts = fullName.trim().split(" ")
                val firstName = nameParts.firstOrNull() ?: fullName.trim()
                val lastName = if (nameParts.size > 1) nameParts.drop(1).joinToString(" ") else ""

                // 2. Autenticación inmediata para obtener Bearer token real
                var authToken = "token-${userDto.id}"
                var refreshToken = "refresh-${userDto.id}"
                var bearerHeader = ""
                try {
                    val authResponse = authApiService.signIn(SignInRequestDto(email.trim(), password))
                    if (authResponse.isSuccessful && authResponse.body() != null) {
                        val authBody = authResponse.body()!!
                        authToken = authBody.token
                        refreshToken = authBody.refreshToken
                        bearerHeader = "Bearer $authToken"
                    }
                } catch (_: Exception) { }

                // 3. Registrar perfil de cliente (requiere Bearer token)
                if (bearerHeader.isNotBlank()) {
                    try {
                        authApiService.createCustomer(
                            authorization = bearerHeader,
                            request = CustomerRegistrationRequestDto(
                                userId = userDto.id,
                                isCorporate = false,
                                firstName = firstName,
                                lastName = lastName,
                                documentType = "DNI",
                                documentNumber = documentNumber.trim(),
                                phone = phone.trim()
                            )
                        )
                    } catch (_: Exception) { }

                    // 4. Registrar vehículo (requiere Bearer token)
                    try {
                        authApiService.registerVehicle(
                            authorization = bearerHeader,
                            request = VehicleRegistrationRequestDto(
                                plateNumber = plateNumber.trim().uppercase(),
                                brand = brand.trim(),
                                model = model.trim(),
                                year = year,
                                vin = vin.trim().uppercase()
                            )
                        )
                    } catch (_: Exception) { }
                }

                // 5. Almacenar sesión de usuario activa en base de datos local Room
                val entity = UserEntity(
                    id = userDto.id,
                    email = userDto.email,
                    role = userDto.role,
                    token = authToken,
                    refreshToken = refreshToken
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
                val msg = when (userResponse.code()) {
                    409, 400 -> "El correo electrónico o DNI ya está registrado en ShiftIQ."
                    else -> "No se pudo completar el registro (${userResponse.code()}). Verifica tus datos."
                }
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(Exception(resolveErrorMessage(e)))
        }
    }

    override suspend fun sendOtp(email: String): Result<Unit> {
        return try {
            val response = authApiService.sendOtp(SendOtpRequestDto(email.trim()))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("No se pudo enviar el código OTP (${response.code()})."))
            }
        } catch (e: Exception) {
            Result.failure(Exception(resolveErrorMessage(e)))
        }
    }

    override suspend fun verifyOtp(email: String, code: String): Result<Unit> {
        return try {
            val response = authApiService.verifyOtp(VerifyOtpRequestDto(email.trim(), code.trim()))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val msg = when (response.code()) {
                    400, 404 -> "El código de verificación es inválido o ha expirado."
                    else -> "Error al verificar el código de seguridad (${response.code()})."
                }
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(Exception(resolveErrorMessage(e)))
        }
    }

    override suspend fun lookupIdentity(type: String, number: String): Result<IdentityLookupResponseDto> {
        return try {
            val response = authApiService.lookupIdentity(type.trim().uppercase(), number.trim())
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("No se pudo consultar el documento (${response.code()})."))
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
        val user = userDao.getActiveUser()
        if (user != null && user.refreshToken.isNotBlank()) {
            try {
                authApiService.revokeSession(RefreshTokenRequestDto(user.refreshToken))
            } catch (_: Exception) { }
        }
        userDao.clearUsers()
    }
}
