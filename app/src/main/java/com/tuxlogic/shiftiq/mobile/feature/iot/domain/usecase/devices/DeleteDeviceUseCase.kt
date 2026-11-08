package com.tuxlogic.shiftiq.mobile.feature.iot.domain.usecase.devices

import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.DeviceId
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.repository.IotRepository
import javax.inject.Inject

class DeleteDeviceUseCase @Inject constructor(
    private val iotRepository: IotRepository
) {
    suspend operator fun invoke(id: DeviceId): AppResult<Unit> {
        return iotRepository.deleteDevice(id)
    }
}
