package com.tuxlogic.shiftiq.mobile.feature.iot.domain.model

import com.tuxlogic.shiftiq.mobile.core.model.BranchId

enum class DtcSeverity {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

data class DtcAlert(
    val id: DtcAlertId,
    val telemetrySnapshotId: SnapshotId,
    val branchId: BranchId,
    val dtcCode: String,
    val description: String,
    val severity: DtcSeverity,
    val createdAt: String
)
