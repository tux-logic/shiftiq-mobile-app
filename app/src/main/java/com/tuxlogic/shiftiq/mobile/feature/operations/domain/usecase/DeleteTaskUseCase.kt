package com.tuxlogic.shiftiq.mobile.feature.operations.domain.usecase

import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.TaskId
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.WorkOrderId
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.repository.WorkOrderRepository
import javax.inject.Inject

class DeleteTaskUseCase @Inject constructor(
    private val workOrderRepository: WorkOrderRepository
) {
    suspend operator fun invoke(
        workOrderId: WorkOrderId,
        taskId: TaskId
    ): AppResult<Unit> {
        if (workOrderId.value.isBlank()) {
            return AppResult.Failure(
                AppError.Validation(
                    details = "workOrderId",
                    message = "Work order ID cannot be blank"
                )
            )
        }
        if (taskId.value.isBlank()) {
            return AppResult.Failure(
                AppError.Validation(
                    details = "taskId",
                    message = "Task ID cannot be blank"
                )
            )
        }
        return workOrderRepository.deleteTask(
            workOrderId = workOrderId,
            taskId = taskId
        )
    }
}
