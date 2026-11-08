package com.tuxlogic.shiftiq.mobile.feature.iot.application.usecase.telemetry

import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.TelemetryBatch
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.repository.IotRepository
import javax.inject.Inject

class IngestTelemetryBatchUseCase @Inject constructor(
    private val iotRepository: IotRepository
) {
    suspend operator fun invoke(batch: TelemetryBatch): AppResult<Unit> {
        if (batch.snapshots.isEmpty()) {
            return AppResult.Failure(
                AppError.Validation(details = "snapshots", message = "Telemetry batch must contain at least one snapshot")
            )
        }
        if (batch.snapshots.size > 100) {
            return AppResult.Failure(
                AppError.Validation(details = "snapshots", message = "Telemetry batch cannot exceed 100 snapshots")
            )
        }
        return iotRepository.ingestTelemetryBatch(batch)
    }
}
