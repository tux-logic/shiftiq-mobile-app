package com.tuxlogic.shiftiq.mobile.feature.iot.application.usecase.telemetry

import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.DeviceId
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.TelemetrySnapshot
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.repository.IotRepository
import javax.inject.Inject

class GetLatestTelemetrySnapshotUseCase @Inject constructor(
    private val iotRepository: IotRepository
) {
    suspend operator fun invoke(deviceId: DeviceId): AppResult<TelemetrySnapshot> {
        return iotRepository.getLatestTelemetrySnapshot(deviceId)
    }
}
