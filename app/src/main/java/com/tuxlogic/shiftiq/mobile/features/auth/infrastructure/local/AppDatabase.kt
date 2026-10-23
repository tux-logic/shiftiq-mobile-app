package com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.local

import androidx.room3.Database
import androidx.room3.RoomDatabase

@Database(entities = [UserEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
