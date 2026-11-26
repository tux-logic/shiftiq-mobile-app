package com.tuxlogic.shiftiq.mobile.core.localization

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.Locale

enum class AppLanguage(val tag: String) {
    SPANISH("es"),
    ENGLISH("en");

    companion object {
        fun fromTag(tag: String?): AppLanguage =
            if (tag?.startsWith("en", ignoreCase = true) == true) ENGLISH else SPANISH

        fun systemDefault(): AppLanguage = fromTag(Locale.getDefault().language)
    }
}

object AppLanguageStore {
    private const val PREFERENCES = "shiftiq_app_preferences"
    private const val LANGUAGE_KEY = "app_language"

    private var selectedLanguage by mutableStateOf(AppLanguage.systemDefault())

    val current: AppLanguage
        get() = selectedLanguage

    fun initialize(context: Context) {
        val preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
        selectedLanguage = AppLanguage.fromTag(
            preferences.getString(LANGUAGE_KEY, Locale.getDefault().language)
        )
    }

    fun set(context: Context, language: AppLanguage) {
        if (selectedLanguage == language) return
        selectedLanguage = language
        context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
            .edit()
            .putString(LANGUAGE_KEY, language.tag)
            .apply()
    }
}
