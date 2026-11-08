package com.tuxlogic.shiftiq.mobile.feature.iot.domain.model

data class BatchDtcItem(
    val dtcCode: String,
    val description: String,
    val severity: DtcSeverity
)

data class TelemetryBatchItem(
    val rpm: Int,
    val temperature: Int,
    val speedKmh: Double,
    val odometerKm: Double,
    val fuelLevelPercent: Double,
    val createdAt: String,
    val dtcCodes: List<BatchDtcItem> = emptyList()
)

data class TelemetryBatch(
    val obd2DeviceId: DeviceId,
    val snapshots: List<TelemetryBatchItem>
)
