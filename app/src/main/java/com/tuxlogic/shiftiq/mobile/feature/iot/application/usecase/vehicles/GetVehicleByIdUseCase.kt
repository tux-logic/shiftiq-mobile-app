package com.tuxlogic.shiftiq.mobile.feature.iot.application.usecase.vehicles

import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.model.VehicleId
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.Vehicle
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.repository.IotRepository
import javax.inject.Inject

class GetVehicleByIdUseCase @Inject constructor(
    private val iotRepository: IotRepository
) {
    suspend operator fun invoke(id: VehicleId): AppResult<Vehicle> {
        return iotRepository.getVehicleById(id)
    }
}
