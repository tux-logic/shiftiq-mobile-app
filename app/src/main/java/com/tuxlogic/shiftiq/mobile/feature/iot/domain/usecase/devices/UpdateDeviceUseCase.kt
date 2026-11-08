package com.tuxlogic.shiftiq.mobile.feature.iot.domain.usecase.devices

import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.DeviceId
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.MacAddress
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.Obd2Device
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.repository.IotRepository
import javax.inject.Inject

class UpdateDeviceUseCase @Inject constructor(
    private val iotRepository: IotRepository
) {
    suspend operator fun invoke(
        id: DeviceId,
        macAddress: String
    ): AppResult<Obd2Device> {
        if (!MacAddress.isValid(macAddress)) {
            return AppResult.Failure(
                AppError.Validation(details = "macAddress", message = "Invalid MAC address format: $macAddress")
            )
        }
        return iotRepository.updateDevice(
            id = id,
            macAddress = MacAddress(macAddress)
        )
    }
}
