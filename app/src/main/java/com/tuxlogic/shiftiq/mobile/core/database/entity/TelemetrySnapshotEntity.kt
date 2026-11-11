package com.tuxlogic.shiftiq.mobile.core.database.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "telemetry_snapshots")
data class TelemetrySnapshotEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val vehicleId: String,
    val rpm: Int,
    val temperatureCelsius: Int,
    val speedKmh: Double,
    val odometerKm: Int,
    val fuelLevelPercent: Double,
    val batteryVoltage: Double,
    val timestamp: Long = System.currentTimeMillis()
)
