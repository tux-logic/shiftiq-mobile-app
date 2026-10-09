package com.tuxlogic.shiftiq.mobile.core.network.interceptor

import com.tuxlogic.shiftiq.mobile.core.datastore.SessionDataStore
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interceptor de OkHttp que adjunta el encabezado `Authorization: Bearer <token>`
 * en las peticiones que lo requieran a partir de la sesión almacenada en DataStore.
 */
@Singleton
class AuthInterceptor @Inject constructor(
    private val sessionDataStore: SessionDataStore
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val path = originalRequest.url.encodedPath

        // Rutas públicas que no deben llevar Bearer token
        val isPublicEndpoint = path.contains("/authentication/sessions") ||
                path.contains("/authentication/password-recoveries") ||
                path.contains("/authentication/password-resets") ||
                path == "/health" ||
                path == "/"

        // Si ya tiene Authorization o es pública, proceder directamente
        if (isPublicEndpoint || originalRequest.header("Authorization") != null) {
            return chain.proceed(originalRequest)
        }

        // Obtener el access token actual
        val token = runBlocking { sessionDataStore.accessToken.firstOrNull() }

        val authenticatedRequest = if (!token.isNullOrBlank()) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(authenticatedRequest)
    }
}
