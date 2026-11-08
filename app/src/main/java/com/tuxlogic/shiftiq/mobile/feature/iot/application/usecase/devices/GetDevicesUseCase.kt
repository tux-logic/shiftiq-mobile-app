package com.tuxlogic.shiftiq.mobile.feature.iot.application.usecase.devices

import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.Obd2Device
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.repository.IotRepository
import javax.inject.Inject

class GetDevicesUseCase @Inject constructor(
    private val iotRepository: IotRepository
) {
    suspend operator fun invoke(
        branchId: BranchId? = null,
        status: String? = null
    ): AppResult<List<Obd2Device>> {
        return iotRepository.getDevices(branchId = branchId, status = status)
    }
}
