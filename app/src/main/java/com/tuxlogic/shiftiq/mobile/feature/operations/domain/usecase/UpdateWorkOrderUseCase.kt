package com.tuxlogic.shiftiq.mobile.feature.operations.domain.usecase

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
                AppError.Validation(details = "diagnosticSummary", message = "El diagnóstico no puede estar vacío")
            )
        }
        if (mileageIn < 0) {
            return AppResult.Failure(
                AppError.Validation(details = "mileageIn", message = "El kilometraje no puede ser negativo")
            )
        }
        return workOrderRepository.updateWorkOrder(id, trimmedSummary, mileageIn)
    }
}
