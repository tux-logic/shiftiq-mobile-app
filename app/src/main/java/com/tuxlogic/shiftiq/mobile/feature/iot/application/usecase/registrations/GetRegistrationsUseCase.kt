package com.tuxlogic.shiftiq.mobile.feature.iot.application.usecase.registrations

import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.DeviceRegistration
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.RegistrationStatus
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.repository.IotRepository
import javax.inject.Inject

class GetRegistrationsUseCase @Inject constructor(
    private val iotRepository: IotRepository
) {
    suspend operator fun invoke(
        branchId: BranchId? = null,
        status: RegistrationStatus = RegistrationStatus.ACTIVE
    ): AppResult<List<DeviceRegistration>> {
        return iotRepository.getRegistrations(branchId = branchId, status = status)
    }
}
