package com.tuxlogic.shiftiq.mobile.features.home.infrastructure.local

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "home_dashboard")
data class HomeDashboardEntity(
    @PrimaryKey
    val id: String = "primary",
    @ColumnInfo(name = "customer_name")
    val customerName: String,
    @ColumnInfo(name = "is_dongle_connected")
    val isDongleConnected: Boolean,
    @ColumnInfo(name = "vehicle_id")
    val vehicleId: String,
    @ColumnInfo(name = "plate_number")
    val plateNumber: String,
    @ColumnInfo(name = "brand")
    val brand: String,
    @ColumnInfo(name = "model")
    val model: String,
    @ColumnInfo(name = "year")
    val year: Int,
    @ColumnInfo(name = "vin")
    val vin: String,
    @ColumnInfo(name = "photo_url")
    val photoUrl: String? = null,
    @ColumnInfo(name = "health_score")
    val healthScore: Int,
    @ColumnInfo(name = "rpm")
    val rpm: Int,
    @ColumnInfo(name = "coolant_temp_celsius")
    val coolantTempCelsius: Int,
    @ColumnInfo(name = "battery_voltage")
    val batteryVoltage: Double,
    @ColumnInfo(name = "odometer_km")
    val odometerKm: Int,
    @ColumnInfo(name = "fuel_level_percent")
    val fuelLevelPercent: Double,
    @ColumnInfo(name = "speed_kmh")
    val speedKmh: Double,
    @ColumnInfo(name = "dtc_code")
    val dtcCode: String,
    @ColumnInfo(name = "dtc_title")
    val dtcTitle: String,
    @ColumnInfo(name = "dtc_description")
    val dtcDescription: String,
    @ColumnInfo(name = "dtc_severity")
    val dtcSeverity: String,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)
