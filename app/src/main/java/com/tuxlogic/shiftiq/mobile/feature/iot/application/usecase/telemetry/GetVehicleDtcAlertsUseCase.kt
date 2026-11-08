package com.tuxlogic.shiftiq.mobile.feature.iot.application.usecase.telemetry

import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.model.VehicleId
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.DtcAlert
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.repository.IotRepository
import javax.inject.Inject

class GetVehicleDtcAlertsUseCase @Inject constructor(
    private val iotRepository: IotRepository
) {
    suspend operator fun invoke(
        vehicleId: VehicleId,
        page: Int = 0,
        size: Int = 20
    ): AppResult<List<DtcAlert>> {
        return iotRepository.getVehicleDtcAlerts(vehicleId = vehicleId, page = page, size = size)
    }
}
