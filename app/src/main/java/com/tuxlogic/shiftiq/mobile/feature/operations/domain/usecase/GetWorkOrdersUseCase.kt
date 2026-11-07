package com.tuxlogic.shiftiq.mobile.feature.operations.domain.usecase

import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.core.model.VehicleId
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.WorkOrder
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.repository.WorkOrderRepository
import javax.inject.Inject

class GetWorkOrdersUseCase @Inject constructor(
    private val workOrderRepository: WorkOrderRepository
) {
    suspend operator fun invoke(
        branchId: BranchId? = null,
        vehicleId: VehicleId? = null
    ): AppResult<List<WorkOrder>> {
        return workOrderRepository.getWorkOrders(branchId, vehicleId)
    }
}
