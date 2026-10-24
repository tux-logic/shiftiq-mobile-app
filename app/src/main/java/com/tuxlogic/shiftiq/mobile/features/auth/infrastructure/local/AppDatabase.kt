package com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.local

import androidx.room3.Database
import androidx.room3.RoomDatabase
import com.tuxlogic.shiftiq.mobile.features.home.infrastructure.local.HomeDao
import com.tuxlogic.shiftiq.mobile.features.home.infrastructure.local.HomeDashboardEntity

@Database(
    entities = [
        UserEntity::class,
        HomeDashboardEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun homeDao(): HomeDao
}
