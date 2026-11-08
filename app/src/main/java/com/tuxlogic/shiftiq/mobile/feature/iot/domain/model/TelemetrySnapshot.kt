package com.tuxlogic.shiftiq.mobile.feature.iot.domain.model

import com.tuxlogic.shiftiq.mobile.core.model.BranchId

data class TelemetrySnapshot(
    val id: SnapshotId,
    val obd2DeviceRegistrationId: RegistrationId,
    val branchId: BranchId,
    val rpm: Int,
    val temperature: Int,
    val speedKmh: Double,
    val odometerKm: Double,
    val fuelLevelPercent: Double,
    val createdAt: String
)
