package com.tuxlogic.shiftiq.mobile.feature.iot.application.usecase.telemetry

import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.DeviceId
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.TelemetrySnapshot
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.repository.IotRepository
import javax.inject.Inject

class GetDeviceTelemetryHistoryUseCase @Inject constructor(
    private val iotRepository: IotRepository
) {
    suspend operator fun invoke(
        deviceId: DeviceId,
        page: Int = 0,
        size: Int = 20
    ): AppResult<List<TelemetrySnapshot>> {
        return iotRepository.getDeviceTelemetryHistory(deviceId = deviceId, page = page, size = size)
    }
}
