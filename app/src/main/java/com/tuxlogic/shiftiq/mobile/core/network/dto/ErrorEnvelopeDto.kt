package com.tuxlogic.shiftiq.mobile.core.network.dto

import com.google.gson.annotations.SerializedName
import com.tuxlogic.shiftiq.mobile.core.common.result.AppError

/**
 * DTO que soporta tanto el envelope estándar de ShiftIQ Platform
 * { "code", "message", "details" } como RFC 7807 ProblemDetail { "title", "status", "detail" }.
 */
data class ErrorEnvelopeDto(
    @SerializedName("code")
    val code: String? = null,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("details")
    val details: String? = null,

    // Soporte para Spring ProblemDetail
    @SerializedName("title")
    val title: String? = null,

    @SerializedName("detail")
    val detail: String? = null,

    @SerializedName("status")
    val status: Int? = null
) {
    fun toAppError(statusCode: Int): AppError {
        val resolvedCode = code ?: title ?: "HTTP_$statusCode"
        val resolvedMessage = message ?: detail ?: title ?: "Error del servidor ($statusCode)"
        val resolvedDetails = details ?: detail

        return when (statusCode) {
            401 -> AppError.Unauthorized(code = resolvedCode, message = resolvedMessage, details = resolvedDetails)
            403 -> AppError.Forbidden(code = resolvedCode, message = resolvedMessage, details = resolvedDetails)
            404 -> AppError.NotFound(code = resolvedCode, message = resolvedMessage, details = resolvedDetails)
            409 -> AppError.Conflict(code = resolvedCode, message = resolvedMessage, details = resolvedDetails)
            400 -> AppError.Validation(code = resolvedCode, message = resolvedMessage, details = resolvedDetails)
            in 500..599 -> AppError.Server(code = resolvedCode, message = resolvedMessage, statusCode = statusCode, details = resolvedDetails)
            else -> AppError.Server(code = resolvedCode, message = resolvedMessage, statusCode = statusCode, details = resolvedDetails)
        }
    }
}
