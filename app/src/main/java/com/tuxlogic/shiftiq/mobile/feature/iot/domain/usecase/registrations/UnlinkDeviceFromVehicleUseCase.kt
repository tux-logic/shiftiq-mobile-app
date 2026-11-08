package com.tuxlogic.shiftiq.mobile.feature.iot.domain.usecase.registrations

import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.DeviceRegistration
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.RegistrationId
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.repository.IotRepository
import javax.inject.Inject

class UnlinkDeviceFromVehicleUseCase @Inject constructor(
    private val iotRepository: IotRepository
) {
    suspend operator fun invoke(registrationId: RegistrationId): AppResult<DeviceRegistration> {
        return iotRepository.unlinkDeviceFromVehicle(registrationId)
    }
}
