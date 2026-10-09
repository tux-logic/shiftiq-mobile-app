package com.tuxlogic.shiftiq.mobile.core.datastore

import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.core.model.Role
import com.tuxlogic.shiftiq.mobile.core.model.UserId
import kotlinx.coroutines.flow.Flow

/**
 * Fuente de verdad local para la sesión activa del usuario.
 * Almacena de forma persistente y asíncrona los tokens JWT, identidad y contexto de sede.
 */
interface SessionDataStore {
    val accessToken: Flow<String?>
    val refreshToken: Flow<String?>
    val userId: Flow<UserId?>
    val userEmail: Flow<String?>
    val userRole: Flow<Role?>
    val activeBranchId: Flow<BranchId?>

    /**
     * Guarda la sesión completa obtenida tras un inicio de sesión exitoso.
     */
    suspend fun saveSession(
        accessToken: String,
        refreshToken: String,
        userId: UserId,
        email: String,
        role: Role
    )

    /**
     * Actualiza el par de tokens tras una rotación de refresh token exitosa.
     */
    suspend fun updateTokens(accessToken: String, refreshToken: String)

    /**
     * Establece o cambia la sede activa en la que opera el usuario.
     */
    suspend fun setActiveBranchId(branchId: BranchId?)

    /**
     * Elimina todos los datos de sesión almacenados (logout).
     */
    suspend fun clearSession()
}
