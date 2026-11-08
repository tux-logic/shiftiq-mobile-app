package com.tuxlogic.shiftiq.mobile.feature.iot.domain.usecase.telemetry

import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.model.VehicleId
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.TelemetrySnapshot
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.repository.IotRepository
import javax.inject.Inject

class GetVehicleTelemetryHistoryUseCase @Inject constructor(
    private val iotRepository: IotRepository
) {
    suspend operator fun invoke(
        vehicleId: VehicleId,
        page: Int = 0,
        size: Int = 20
    ): AppResult<List<TelemetrySnapshot>> {
        return iotRepository.getVehicleTelemetryHistory(vehicleId = vehicleId, page = page, size = size)
    }
}
