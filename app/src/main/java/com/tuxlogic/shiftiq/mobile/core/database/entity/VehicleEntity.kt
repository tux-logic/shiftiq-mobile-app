package com.tuxlogic.shiftiq.mobile.core.database.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "vehicles")
data class VehicleEntity(
    @PrimaryKey
    val id: String,
    val plateNumber: String,
    val brand: String,
    val model: String,
    val year: Int,
    val vin: String,
    val photoUrl: String? = null,
    val isPrimary: Boolean = false,
    val lastSyncTimestamp: Long = System.currentTimeMillis()
)
