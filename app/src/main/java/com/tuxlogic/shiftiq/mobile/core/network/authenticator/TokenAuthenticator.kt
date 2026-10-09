package com.tuxlogic.shiftiq.mobile.core.network.authenticator

import com.google.gson.Gson
import com.tuxlogic.shiftiq.mobile.core.datastore.SessionDataStore
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Authenticator
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

/**
 * Autenticador de OkHttp que intercepta respuestas 401 Unauthorized y ejecuta
 * la renovación transparente del access token mediante el refresh token (single-flight con Mutex).
 */
@Singleton
class TokenAuthenticator @Inject constructor(
    private val sessionDataStore: SessionDataStore,
    private val gson: Gson,
    @Named("BaseUrl") private val baseUrl: String
) : Authenticator {

    private val refreshMutex = Mutex()

    private data class RefreshRequest(val refreshToken: String)
    private data class RefreshResponse(
        val token: String,
        val refreshToken: String
    )

    override fun authenticate(route: Route?, response: Response): Request? {
        // Evitar bucles infinitos si la petición que falló con 401 es el propio refresh o login
        val path = response.request.url.encodedPath
        if (path.contains("/authentication/sessions")) {
            return null
        }

        // Si ya se intentó más de 3 veces reautenticar esta petición, abortar
        if (responseCount(response) >= 3) {
            return null
        }

        return runBlocking {
            refreshMutex.withLock {
                val currentAccessToken = sessionDataStore.accessToken.firstOrNull()
                val requestHeaderToken = response.request.header("Authorization")?.removePrefix("Bearer ")

                // Si otro hilo ya renovó el token mientras esperábamos el mutex, reintentar de inmediato con el nuevo
                if (!currentAccessToken.isNullOrBlank() && currentAccessToken != requestHeaderToken) {
                    return@withLock response.request.newBuilder()
                        .header("Authorization", "Bearer $currentAccessToken")
                        .build()
                }

                val currentRefreshToken = sessionDataStore.refreshToken.firstOrNull()
                if (currentRefreshToken.isNullOrBlank()) {
                    sessionDataStore.clearSession()
                    return@withLock null
                }

                // Ejecutar petición de refresh síncrona en cliente OkHttp aislado (sin interceptors de sesión)
                val refreshSuccess = executeRefresh(currentRefreshToken)
                if (refreshSuccess != null) {
                    sessionDataStore.updateTokens(
                        accessToken = refreshSuccess.token,
                        refreshToken = refreshSuccess.refreshToken
                    )

                    response.request.newBuilder()
                        .header("Authorization", "Bearer ${refreshSuccess.token}")
                        .build()
                } else {
                    // El refresh token caducó o fue revocado -> limpiar sesión
                    sessionDataStore.clearSession()
                    null
                }
            }
        }
    }

    private fun executeRefresh(refreshToken: String): RefreshResponse? {
        return try {
            val client = OkHttpClient.Builder().build()
            val jsonBody = gson.toJson(RefreshRequest(refreshToken))
            val requestBody = jsonBody.toRequestBody("application/json".toMediaType())

            val cleanBaseUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
            val refreshUrl = "${cleanBaseUrl}api/v1/authentication/sessions/refresh"

            val refreshRequest = Request.Builder()
                .url(refreshUrl)
                .post(requestBody)
                .build()

            client.newCall(refreshRequest).execute().use { refreshResponse ->
                if (refreshResponse.isSuccessful) {
                    val responseBody = refreshResponse.body?.string() ?: return null
                    gson.fromJson(responseBody, RefreshResponse::class.java)
                } else {
                    null
                }
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }
}
