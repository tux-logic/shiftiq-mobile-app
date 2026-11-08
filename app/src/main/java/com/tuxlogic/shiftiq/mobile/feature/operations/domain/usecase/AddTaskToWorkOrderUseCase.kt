package com.tuxlogic.shiftiq.mobile.feature.operations.domain.usecase

import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.model.EmployeeId
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.ServiceId
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.WorkOrder
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.WorkOrderId
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.repository.WorkOrderRepository
import javax.inject.Inject

class AddTaskToWorkOrderUseCase @Inject constructor(
    private val workOrderRepository: WorkOrderRepository
) {
    suspend operator fun invoke(
        workOrderId: WorkOrderId,
        serviceId: ServiceId,
        assignedMechanicId: EmployeeId,
        description: String,
        evidenceImages: List<String> = emptyList()
    ): AppResult<WorkOrder> {
        val trimmedDescription = description.trim()
        if (trimmedDescription.length < 10) {
            return AppResult.Failure(
                AppError.Validation(
                    details = "description",
                    message = "Task description must be at least 10 characters long"
                )
            )
        }
        return workOrderRepository.addTask(
            workOrderId = workOrderId,
            serviceId = serviceId,
            assignedMechanicId = assignedMechanicId,
            description = trimmedDescription,
            evidenceImages = evidenceImages
        )
    }
}
