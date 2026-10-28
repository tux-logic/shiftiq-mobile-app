package com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.repositories

import com.google.gson.Gson
import com.tuxlogic.shiftiq.mobile.features.auth.domain.AuthRepository
import com.tuxlogic.shiftiq.mobile.features.auth.domain.User
import com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.local.UserDao
import com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.local.UserEntity
import com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.remote.AuthApiService
import com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.remote.dto.ApiErrorResponseDto
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
import retrofit2.Response
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val userDao: UserDao,
    private val gson: Gson
) : AuthRepository {

    override suspend fun signIn(email: String, password: String): Result<User> {
        return try {
            val response = authApiService.signIn(SignInRequestDto(email.trim(), password))
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
                Result.failure(Exception(parseError(response)))
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
                Result.failure(Exception(parseError(response)))
            }
        } catch (e: Exception) {
            Result.failure(Exception(resolveErrorMessage(e)))
        }
    }

    override suspend fun signUp(email: String, password: String): Result<User> {
        return try {
            val userResponse = authApiService.signUp(SignUpRequestDto(email.trim(), password))
            if (!userResponse.isSuccessful || userResponse.body() == null) {
                return Result.failure(Exception(parseError(userResponse)))
            }
            // Iniciar sesión inmediatamente para obtener tokens de acceso
            val authResponse = authApiService.signIn(SignInRequestDto(email.trim(), password))
            if (!authResponse.isSuccessful || authResponse.body() == null) {
                return Result.failure(Exception(parseError(authResponse)))
            }
            val dto = authResponse.body()!!
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
            if (!userResponse.isSuccessful || userResponse.body() == null) {
                return Result.failure(Exception(parseError(userResponse)))
            }
            val userDto = userResponse.body()!!

            // 2. Autenticación inmediata para obtener Bearer token real
            val authResponse = authApiService.signIn(SignInRequestDto(email.trim(), password))
            if (!authResponse.isSuccessful || authResponse.body() == null) {
                return Result.failure(Exception("Error al iniciar sesión tras el registro: ${parseError(authResponse)}"))
            }
            val authBody = authResponse.body()!!
            val authToken = authBody.token
            val refreshToken = authBody.refreshToken
            val bearerHeader = "Bearer $authToken"

            // 3. Registrar perfil de cliente (POST /api/v1/customers)
            val nameParts = fullName.trim().split(" ")
            val firstName = nameParts.firstOrNull()?.ifBlank { "Conductor" } ?: "Conductor"
            val lastName = if (nameParts.size > 1) nameParts.drop(1).joinToString(" ") else "ShiftIQ"

            val customerResponse = authApiService.createCustomer(
                authorization = bearerHeader,
                request = CustomerRegistrationRequestDto(
                    userId = userDto.id,
                    isCorporate = false,
                    firstName = firstName,
                    lastName = lastName,
                    businessName = "",
                    documentType = "DNI",
                    documentNumber = documentNumber.trim(),
                    phone = phone.trim()
                )
            )
            if (!customerResponse.isSuccessful) {
                return Result.failure(Exception(parseError(customerResponse)))
            }

            // 4. Registrar vehículo (POST /api/v1/vehicles)
            val vehicleResponse = authApiService.registerVehicle(
                authorization = bearerHeader,
                request = VehicleRegistrationRequestDto(
                    plateNumber = plateNumber.trim().uppercase(),
                    brand = brand.trim(),
                    model = model.trim(),
                    year = year,
                    vin = vin.trim().uppercase(),
                    photoUrl = null
                )
            )
            if (!vehicleResponse.isSuccessful) {
                return Result.failure(Exception(parseError(vehicleResponse)))
            }

            // 5. Almacenar sesión de usuario activa en Room
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
                Result.failure(Exception(parseError(response)))
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
                Result.failure(Exception(parseError(response)))
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
                Result.failure(Exception(parseError(response)))
            }
        } catch (e: Exception) {
            Result.failure(Exception(resolveErrorMessage(e)))
        }
    }

    override suspend fun requestPasswordRecovery(email: String): Result<Unit> {
        return try {
            val response = authApiService.requestPasswordRecovery(PasswordRecoveryRequestDto(email.trim()))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(parseError(response)))
            }
        } catch (e: Exception) {
            Result.failure(Exception(resolveErrorMessage(e)))
        }
    }

    override suspend fun resetPassword(token: String, newPassword: String): Result<Unit> {
        return try {
            val response = authApiService.resetPassword(ResetPasswordRequestDto(token.trim(), newPassword))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(parseError(response)))
            }
        } catch (e: Exception) {
            Result.failure(Exception(resolveErrorMessage(e)))
        }
    }

    /**
     * Parsea de manera exhaustiva el cuerpo de error HTTP devuelto por el backend.
     * Soporta tanto el formato ErrorResource (code, message, details) como el formato
     * Spring ProblemDetail RFC 7807 (title, detail, status) y errores estándar de Spring.
     */
    private fun parseError(response: Response<*>): String {
        val statusCode = response.code()
        val errorJson = try {
            response.errorBody()?.string()
        } catch (_: Exception) {
            null
        }

        if (!errorJson.isNullOrBlank()) {
            try {
                val errorDto = gson.fromJson(errorJson, ApiErrorResponseDto::class.java)
                val rawMsg = errorDto.details
                    ?: errorDto.detail
                    ?: errorDto.message
                    ?: errorDto.title
                    ?: errorDto.error

                if (!rawMsg.isNullOrBlank()) {
                    return translateServerMessage(rawMsg, statusCode)
                }
            } catch (_: Exception) {
                // Si no es JSON estándar, procesar con código de estado
            }
        }

        return when (statusCode) {
            400 -> "Solicitud inválida. Revisa los datos ingresados."
            401 -> "Credenciales inválidas o sesión no autorizada."
            403 -> "No tienes permisos para realizar esta operación."
            404 -> "El recurso solicitado no fue encontrado."
            409 -> "El registro genera un conflicto o ya existe en el sistema."
            422 -> "Los datos proporcionados no pudieron ser procesados."
            500, 502, 503 -> "El servidor de ShiftIQ no se encuentra disponible temporalmente."
            else -> "Error en el servidor ($statusCode)."
        }
    }

    /**
     * Traduce los códigos y mensajes del backend a explicaciones claras y accionables en español.
     */
    private fun translateServerMessage(raw: String, statusCode: Int): String {
        val lower = raw.lowercase()
        return when {
            lower.contains("invalidaddress") || lower.contains("undeliverable") ->
                "El correo electrónico ingresado no es válido o no existe. Por favor ingresa una dirección de correo real."
            lower.contains("alreadyinuse") || lower.contains("already in use") ->
                "El correo electrónico ya se encuentra registrado en ShiftIQ. Inicia sesión o recupera tu contraseña."
            lower.contains("unverifiable") ->
                "No se pudo verificar el correo electrónico en este momento. Inténtalo más tarde."
            lower.contains("identity.notfound") || lower.contains("not found in the official registry") || lower.contains("padron") ->
                "El número de DNI no fue encontrado en el padrón oficial (RENIEC)."
            lower.contains("identity.invalidnumber") || lower.contains("invalid document number") ->
                "El número de documento de identidad debe tener un formato válido (8 dígitos numéricos para DNI)."
            lower.contains("profilealreadyexists") || lower.contains("profile already exists") ->
                "Ya existe un perfil de cliente registrado para este usuario o documento."
            lower.contains("vehicle.conflict") || lower.contains("matches different vehicles") || lower.contains("vehicle conflict") ->
                "Conflicto vehicular: La placa o el número de chasis (VIN) ya se encuentra vinculado a otro vehículo."
            lower.contains("integrity") || lower.contains("conflicts with an existing record") ->
                "Los datos vehiculares o de usuario ya existen en el sistema (placa, VIN o documento duplicado)."
            lower.contains("credentials.invalid") || lower.contains("invalid email or password") ->
                "Credenciales incorrectas. Verifica tu correo y contraseña."
            lower.contains("otp.invalid") || lower.contains("invalid verification code") ->
                "El código de verificación OTP es incorrecto."
            lower.contains("otp.expired") || lower.contains("verification code has expired") ->
                "El código de verificación OTP ha expirado. Solicita un nuevo código."
            lower.contains("otp.alreadyused") || lower.contains("already been used") ->
                "El código de verificación ya fue utilizado."
            lower.contains("otp.notfound") || lower.contains("no active verification code") ->
                "No hay un código de verificación activo para este correo."
            lower.contains("user.notfound") || lower.contains("user not found") ->
                "No se encontró ninguna cuenta registrada con este correo."
            lower.contains("token.invalidorexpired") || lower.contains("token.expiredorused") ->
                "El código de seguridad ha expirado o es inválido. Solicita uno nuevo."
            raw.startsWith("Validation failed: ") -> {
                val stripped = raw.removePrefix("Validation failed: ").trim()
                if (stripped.isNotBlank()) stripped else "Error de validación en los datos enviados."
            }
            raw.isNotBlank() -> raw
            else -> when (statusCode) {
                400 -> "Solicitud inválida. Revisa los datos ingresados."
                401 -> "Credenciales inválidas."
                404 -> "Recurso no encontrado."
                409 -> "Conflicto con un registro existente."
                else -> "Error del servidor ($statusCode)."
            }
        }
    }

    private fun resolveErrorMessage(e: Exception): String {
        return when (e) {
            is java.net.ConnectException -> "No se pudo conectar con el servidor. Verifica que tengas conexión a internet."
            is java.net.SocketTimeoutException -> "El servidor tardó demasiado en responder. Inténtalo nuevamente."
            is java.net.UnknownHostException -> "No se pudo encontrar el servidor de ShiftIQ. Revisa tu conexión de red."
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
