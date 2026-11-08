package com.tuxlogic.shiftiq.mobile.feature.operations.domain.usecase

import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.ProductId
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.TaskId
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.WorkOrder
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.repository.WorkOrderRepository
import javax.inject.Inject

class ReserveProductForTaskUseCase @Inject constructor(
    private val workOrderRepository: WorkOrderRepository
) {
    suspend operator fun invoke(
        taskId: TaskId,
        productId: ProductId,
        quantity: Int
    ): AppResult<WorkOrder> {
        if (quantity <= 0) {
            return AppResult.Failure(
                AppError.Validation(details = "quantity", message = "Reserved quantity must be greater than 0")
            )
        }
        return workOrderRepository.reserveProduct(taskId, productId, quantity)
    }
}
