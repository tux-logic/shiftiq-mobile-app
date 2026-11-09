package com.tuxlogic.shiftiq.mobile.feature.iot.domain.model

import com.tuxlogic.shiftiq.mobile.core.model.BranchId

enum class DeviceStatus {
    AVAILABLE,
    LINKED,
    DELETED
}

data class Obd2Device(
    val id: DeviceId,
    val branchId: BranchId,
    val macAddress: MacAddress,
    val status: DeviceStatus
)
