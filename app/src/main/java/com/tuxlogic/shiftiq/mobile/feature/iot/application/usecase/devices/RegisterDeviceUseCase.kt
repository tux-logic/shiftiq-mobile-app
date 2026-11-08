package com.tuxlogic.shiftiq.mobile.feature.iot.application.usecase.devices

import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.MacAddress
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.Obd2Device
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.repository.IotRepository
import javax.inject.Inject

class RegisterDeviceUseCase @Inject constructor(
    private val iotRepository: IotRepository
) {
    suspend operator fun invoke(
        branchId: BranchId,
        macAddress: String
    ): AppResult<Obd2Device> {
        if (!MacAddress.isValid(macAddress)) {
            return AppResult.Failure(
                AppError.Validation(details = "macAddress", message = "Invalid MAC address format: $macAddress")
            )
        }
        return iotRepository.registerDevice(
            branchId = branchId,
            macAddress = MacAddress(macAddress)
        )
    }
}
