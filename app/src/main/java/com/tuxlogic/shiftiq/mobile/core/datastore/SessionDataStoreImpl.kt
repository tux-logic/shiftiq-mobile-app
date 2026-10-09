package com.tuxlogic.shiftiq.mobile.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.core.model.Role
import com.tuxlogic.shiftiq.mobile.core.model.UserId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionDataStoreImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SessionDataStore {

    private object Keys {
        val ACCESS_TOKEN = stringPreferencesKey("session_access_token")
        val REFRESH_TOKEN = stringPreferencesKey("session_refresh_token")
        val USER_ID = stringPreferencesKey("session_user_id")
        val USER_EMAIL = stringPreferencesKey("session_user_email")
        val USER_ROLE = stringPreferencesKey("session_user_role")
        val ACTIVE_BRANCH_ID = stringPreferencesKey("session_active_branch_id")
    }

    private val safePreferences: Flow<Preferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }

    override val accessToken: Flow<String?> = safePreferences.map { prefs ->
        prefs[Keys.ACCESS_TOKEN]
    }

    override val refreshToken: Flow<String?> = safePreferences.map { prefs ->
        prefs[Keys.REFRESH_TOKEN]
    }

    override val userId: Flow<UserId?> = safePreferences.map { prefs ->
        prefs[Keys.USER_ID]?.let { runCatching { UserId(it) }.getOrNull() }
    }

    override val userEmail: Flow<String?> = safePreferences.map { prefs ->
        prefs[Keys.USER_EMAIL]
    }

    override val userRole: Flow<Role?> = safePreferences.map { prefs ->
        prefs[Keys.USER_ROLE]?.let { Role.fromName(it) }
    }

    override val activeBranchId: Flow<BranchId?> = safePreferences.map { prefs ->
        prefs[Keys.ACTIVE_BRANCH_ID]?.let { runCatching { BranchId(it) }.getOrNull() }
    }

    override suspend fun saveSession(
        accessToken: String,
        refreshToken: String,
        userId: UserId,
        email: String,
        role: Role
    ) {
        dataStore.edit { prefs ->
            prefs[Keys.ACCESS_TOKEN] = accessToken
            prefs[Keys.REFRESH_TOKEN] = refreshToken
            prefs[Keys.USER_ID] = userId.toString()
            prefs[Keys.USER_EMAIL] = email
            prefs[Keys.USER_ROLE] = role.name
        }
    }

    override suspend fun updateTokens(accessToken: String, refreshToken: String) {
        dataStore.edit { prefs ->
            prefs[Keys.ACCESS_TOKEN] = accessToken
            prefs[Keys.REFRESH_TOKEN] = refreshToken
        }
    }

    override suspend fun setActiveBranchId(branchId: BranchId?) {
        dataStore.edit { prefs ->
            if (branchId == null) {
                prefs.remove(Keys.ACTIVE_BRANCH_ID)
            } else {
                prefs[Keys.ACTIVE_BRANCH_ID] = branchId.toString()
            }
        }
    }

    override suspend fun clearSession() {
        dataStore.edit { prefs ->
            prefs.remove(Keys.ACCESS_TOKEN)
            prefs.remove(Keys.REFRESH_TOKEN)
            prefs.remove(Keys.USER_ID)
            prefs.remove(Keys.USER_EMAIL)
            prefs.remove(Keys.USER_ROLE)
            prefs.remove(Keys.ACTIVE_BRANCH_ID)
        }
    }
}
