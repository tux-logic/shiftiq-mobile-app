package com.tuxlogic.shiftiq.mobile

import android.app.Application
import com.tuxlogic.shiftiq.mobile.core.localization.AppLanguageStore
import com.tuxlogic.shiftiq.mobile.core.localization.AppTranslations
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ShiftIQApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppTranslations.initialize(this)
        AppLanguageStore.initialize(this)
    }
}
