package com.tuxlogic.shiftiq.mobile.core.security

import android.content.Context

object BiometricPreferences {
    private const val PREFERENCES = "shiftiq_biometric_preferences"

    fun isEnabled(context: Context, userId: String): Boolean =
        context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
            .getBoolean(key(userId), false)

    fun setEnabled(context: Context, userId: String, enabled: Boolean) {
        context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(key(userId), enabled)
            .apply()
    }

    private fun key(userId: String) = "biometric_enabled_$userId"
}
