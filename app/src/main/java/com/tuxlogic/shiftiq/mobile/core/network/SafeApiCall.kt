package com.tuxlogic.shiftiq.mobile.core.network

import com.google.gson.Gson
import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.network.dto.ErrorEnvelopeDto
import retrofit2.Response
import java.io.IOException

/**
 * Ejecuta una llamada de red segura con Retrofit, capturando excepciones de transporte
 * y deserializando errores con formato de ShiftIQ Platform en un [AppResult].
 */
suspend fun <T> safeApiCall(
    gson: Gson = Gson(),
    call: suspend () -> Response<T>
): AppResult<T> {
    return try {
        val response = call()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                AppResult.Success(body)
            } else if (response.code() == 204) {
                @Suppress("UNCHECKED_CAST")
                AppResult.Success(Unit as T)
            } else {
                AppResult.Failure(AppError.Unexpected(message = "El servidor respondió con cuerpo vacío."))
            }
        } else {
            val errorBodyString = response.errorBody()?.string()
            val appError = if (!errorBodyString.isNullOrBlank()) {
                try {
                    val envelope = gson.fromJson(errorBodyString, ErrorEnvelopeDto::class.java)
                    envelope.toAppError(response.code())
                } catch (_: Exception) {
                    AppError.Server(
                        statusCode = response.code(),
                        message = "Error en el servidor (${response.code()})",
                        details = errorBodyString
                    )
                }
            } else {
                when (response.code()) {
                    401 -> AppError.Unauthorized()
                    403 -> AppError.Forbidden()
                    404 -> AppError.NotFound()
                    else -> AppError.Server(statusCode = response.code())
                }
            }
            AppResult.Failure(appError)
        }
    } catch (e: IOException) {
        AppResult.Failure(AppError.Network(cause = e))
    } catch (e: Exception) {
        AppResult.Failure(AppError.Unexpected(cause = e))
    }
}
