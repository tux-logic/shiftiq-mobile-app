package com.tuxlogic.shiftiq.mobile.feature.operations.application.usecase

import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.WorkOrder
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.WorkOrderId
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.repository.WorkOrderRepository
import javax.inject.Inject

class UpdateWorkOrderUseCase @Inject constructor(
    private val workOrderRepository: WorkOrderRepository
) {
    suspend operator fun invoke(
        id: WorkOrderId,
        diagnosticSummary: String,
        mileageIn: Int
    ): AppResult<WorkOrder> {
        val trimmedSummary = diagnosticSummary.trim()
        if (trimmedSummary.isBlank()) {
            return AppResult.Failure(
                AppError.Validation(details = "diagnosticSummary", message = "Diagnostic summary cannot be blank")
            )
        }
        if (mileageIn < 0) {
            return AppResult.Failure(
                AppError.Validation(details = "mileageIn", message = "Mileage in cannot be negative")
            )
        }
        return workOrderRepository.updateWorkOrder(id, trimmedSummary, mileageIn)
    }
}
