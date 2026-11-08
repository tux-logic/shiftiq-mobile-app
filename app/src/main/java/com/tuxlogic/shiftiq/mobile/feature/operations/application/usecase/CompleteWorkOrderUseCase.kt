package com.tuxlogic.shiftiq.mobile.feature.operations.application.usecase

import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.WorkOrder
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.WorkOrderId
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.repository.WorkOrderRepository
import javax.inject.Inject

class CompleteWorkOrderUseCase @Inject constructor(
    private val workOrderRepository: WorkOrderRepository
) {
    suspend operator fun invoke(id: WorkOrderId): AppResult<WorkOrder> {
        return workOrderRepository.completeWorkOrder(id)
    }
}
