package com.tuxlogic.shiftiq.mobile.feature.iot.domain.model

import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.core.model.VehicleId

enum class RegistrationStatus {
    ACTIVE,
    INACTIVE
}

data class DeviceRegistration(
    val id: RegistrationId,
    val obd2DeviceId: DeviceId,
    val branchId: BranchId,
    val vehicleId: VehicleId,
    val status: RegistrationStatus,
    val createdAt: String? = null
)
