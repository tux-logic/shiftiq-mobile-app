package com.tuxlogic.shiftiq.mobile.feature.operations.application.usecase

import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.model.EmployeeId
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.TaskId
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.WorkOrder
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.repository.WorkOrderRepository
import javax.inject.Inject

class AssignMechanicToTaskUseCase @Inject constructor(
    private val workOrderRepository: WorkOrderRepository
) {
    suspend operator fun invoke(
        taskId: TaskId,
        mechanicId: EmployeeId
    ): AppResult<WorkOrder> {
        return workOrderRepository.assignMechanic(taskId, mechanicId)
    }
}
