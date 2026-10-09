package com.tuxlogic.shiftiq.mobile.core.common.result

/**
 * Jerarquía de errores tipados de la aplicación.
 */
sealed interface AppError {
    val message: String

    /**
     * Error de conectividad física, DNS o timeout de red.
     */
    data class Network(
        override val message: String = "No fue posible conectar con el servidor. Verifica tu conexión a internet.",
        val cause: Throwable? = null
    ) : AppError

    /**
     * Error de autenticación (401 Unauthorized): sesión expirada o credenciales incorrectas.
     */
    data class Unauthorized(
        val code: String = "UNAUTHORIZED",
        override val message: String = "Sesión no válida o expirada.",
        val details: String? = null
    ) : AppError

    /**
     * Error de permisos (403 Forbidden): usuario sin privilegios para el recurso.
     */
    data class Forbidden(
        val code: String = "FORBIDDEN",
        override val message: String = "No tienes permisos para realizar esta acción.",
        val details: String? = null
    ) : AppError

    /**
     * Recurso no encontrado (404 Not Found).
     */
    data class NotFound(
        val code: String = "NOT_FOUND",
        override val message: String = "El recurso solicitado no fue encontrado.",
        val details: String? = null
    ) : AppError

    /**
     * Conflicto de estado o regla de negocio (409 Conflict o 422 Unprocessable).
     */
    data class Conflict(
        val code: String = "CONFLICT",
        override val message: String = "Existe un conflicto con el estado actual del recurso.",
        val details: String? = null
    ) : AppError

    /**
     * Error de validación de campos enviados (400 Bad Request).
     */
    data class Validation(
        val code: String = "VALIDATION_ERROR",
        override val message: String = "Los datos ingresados son inválidos.",
        val details: String? = null
    ) : AppError

    /**
     * Error del servidor o respuesta inesperada de la API.
     */
    data class Server(
        val code: String = "INTERNAL_SERVER_ERROR",
        override val message: String = "Ocurrió un error en el servidor. Intenta de nuevo más tarde.",
        val statusCode: Int = 500,
        val details: String? = null
    ) : AppError

    /**
     * Error genérico / desconocido.
     */
    data class Unexpected(
        override val message: String = "Ocurrió un error inesperado.",
        val cause: Throwable? = null
    ) : AppError
}
