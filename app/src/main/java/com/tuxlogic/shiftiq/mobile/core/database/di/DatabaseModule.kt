package com.tuxlogic.shiftiq.mobile.core.database.di

import android.content.Context
import androidx.room3.Room
import com.tuxlogic.shiftiq.mobile.core.database.ShiftIQDatabase
import com.tuxlogic.shiftiq.mobile.core.database.dao.TelemetryDao
import com.tuxlogic.shiftiq.mobile.core.database.dao.VehicleDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideShiftIQDatabase(
        @ApplicationContext context: Context
    ): ShiftIQDatabase {
        return Room.databaseBuilder(
            context,
            ShiftIQDatabase::class.java,
            ShiftIQDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration(true)
            .build()
    }

    @Provides
    fun provideVehicleDao(database: ShiftIQDatabase): VehicleDao = database.vehicleDao()

    @Provides
    fun provideTelemetryDao(database: ShiftIQDatabase): TelemetryDao = database.telemetryDao()
}
