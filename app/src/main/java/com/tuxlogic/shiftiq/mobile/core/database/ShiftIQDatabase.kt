package com.tuxlogic.shiftiq.mobile.core.database

import androidx.room3.Database
import androidx.room3.RoomDatabase
import com.tuxlogic.shiftiq.mobile.core.database.dao.TelemetryDao
import com.tuxlogic.shiftiq.mobile.core.database.dao.VehicleDao
import com.tuxlogic.shiftiq.mobile.core.database.entity.TelemetrySnapshotEntity
import com.tuxlogic.shiftiq.mobile.core.database.entity.VehicleEntity

@Database(
    entities = [
        VehicleEntity::class,
        TelemetrySnapshotEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ShiftIQDatabase : RoomDatabase() {
    abstract fun vehicleDao(): VehicleDao
    abstract fun telemetryDao(): TelemetryDao

    companion object {
        const val DATABASE_NAME = "shiftiq_local.db"
    }
}
