package com.tuxlogic.shiftiq.mobile.feature.iot.domain.usecase.vehicles

import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.Vehicle
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.repository.IotRepository
import javax.inject.Inject

class GetVehiclesByBranchUseCase @Inject constructor(
    private val iotRepository: IotRepository
) {
    suspend operator fun invoke(branchId: BranchId? = null): AppResult<List<Vehicle>> {
        return iotRepository.getVehiclesByBranch(branchId)
    }
}
