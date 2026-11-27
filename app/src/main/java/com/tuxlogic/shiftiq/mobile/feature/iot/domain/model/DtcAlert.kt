package com.tuxlogic.shiftiq.mobile.feature.iot.domain.model

import com.tuxlogic.shiftiq.mobile.core.model.BranchId

enum class DtcSeverity {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

enum class DtcStatus {
    ACTIVE,
    IN_INSPECTION,
    RESOLVED
}

data class DtcAlert(
    val id: DtcAlertId,
    val telemetrySnapshotId: SnapshotId,
    val branchId: BranchId,
    val dtcCode: String,
    val description: String,
    val severity: DtcSeverity,
    val createdAt: String,
    val vehicleId: String? = null,
    val vehiclePlate: String = "S/P",
    val vehicleModel: String = "",
    val customerName: String = "",
    val systemCategory: String = "",
    val aiDiagnosticRecommendation: String = "",
    val status: DtcStatus = DtcStatus.ACTIVE,
    val freezeFrame: Map<String, String> = emptyMap()
)
