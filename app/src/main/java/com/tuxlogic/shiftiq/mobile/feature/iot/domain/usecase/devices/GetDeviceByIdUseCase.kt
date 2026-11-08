package com.tuxlogic.shiftiq.mobile.feature.iot.domain.usecase.devices

import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.DeviceId
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.Obd2Device
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.repository.IotRepository
import javax.inject.Inject

class GetDeviceByIdUseCase @Inject constructor(
    private val iotRepository: IotRepository
) {
    suspend operator fun invoke(id: DeviceId): AppResult<Obd2Device> {
        return iotRepository.getDeviceById(id)
    }
}
