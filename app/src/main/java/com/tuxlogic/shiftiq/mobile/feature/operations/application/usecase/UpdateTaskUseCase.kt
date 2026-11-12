package com.tuxlogic.shiftiq.mobile.feature.operations.application.usecase

import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.model.EmployeeId
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.ServiceId
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.TaskId
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.WorkOrder
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.WorkOrderId
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.repository.WorkOrderRepository
import javax.inject.Inject

class UpdateTaskUseCase @Inject constructor(
    private val workOrderRepository: WorkOrderRepository
) {
    suspend operator fun invoke(
        workOrderId: WorkOrderId,
        taskId: TaskId,
        serviceId: ServiceId? = null,
        assignedMechanicId: EmployeeId? = null,
        description: String? = null,
        evidenceImages: List<String>? = null
    ): AppResult<WorkOrder> {
        val trimmedDescription = description?.trim()
        if (trimmedDescription != null && trimmedDescription.length < 10) {
            return AppResult.Failure(
                AppError.Validation(
                    details = "description",
                    message = "Task description must be at least 10 characters long"
                )
            )
        }
        return workOrderRepository.updateTask(
            workOrderId = workOrderId,
            taskId = taskId,
            serviceId = serviceId,
            assignedMechanicId = assignedMechanicId,
            description = trimmedDescription,
            evidenceImages = evidenceImages
        )
    }
}
