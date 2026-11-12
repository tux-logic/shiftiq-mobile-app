package com.tuxlogic.shiftiq.mobile.feature.iot.application.usecase.registrations

import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.core.model.VehicleId
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.DeviceId
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.DeviceRegistration
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.repository.IotRepository
import javax.inject.Inject

class LinkDeviceToVehicleUseCase @Inject constructor(
    private val iotRepository: IotRepository
) {
    suspend operator fun invoke(
        obd2DeviceId: DeviceId,
        branchId: BranchId,
        vehicleId: VehicleId
    ): AppResult<DeviceRegistration> {
        return iotRepository.linkDeviceToVehicle(
            obd2DeviceId = obd2DeviceId,
            branchId = branchId,
            vehicleId = vehicleId
        )
    }
}
