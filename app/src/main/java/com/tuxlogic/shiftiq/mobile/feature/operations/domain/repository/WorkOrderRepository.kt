package com.tuxlogic.shiftiq.mobile.feature.operations.domain.repository

import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.core.model.CustomerId
import com.tuxlogic.shiftiq.mobile.core.model.EmployeeId
import com.tuxlogic.shiftiq.mobile.core.model.VehicleId
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.ProductId
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.ServiceId
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.TaskId
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.WorkOrder
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.WorkOrderId

interface WorkOrderRepository {
    suspend fun getWorkOrders(
        branchId: BranchId? = null,
        vehicleId: VehicleId? = null
    ): AppResult<List<WorkOrder>>

    suspend fun getWorkOrderById(id: WorkOrderId): AppResult<WorkOrder>

    suspend fun createWorkOrder(
        appointmentId: String,
        branchId: BranchId,
        vehicleId: VehicleId,
        customerId: CustomerId,
        diagnosticSummary: String,
        mileageIn: Int,
        entryInspectionImages: List<String> = emptyList()
    ): AppResult<WorkOrder>

    suspend fun updateWorkOrder(
        id: WorkOrderId,
        diagnosticSummary: String,
        mileageIn: Int
    ): AppResult<WorkOrder>

    suspend fun completeWorkOrder(id: WorkOrderId): AppResult<WorkOrder>

    suspend fun deleteWorkOrder(id: WorkOrderId): AppResult<Unit>

    suspend fun addTask(
        workOrderId: WorkOrderId,
        serviceId: ServiceId,
        assignedMechanicId: EmployeeId,
        description: String,
        evidenceImages: List<String> = emptyList()
    ): AppResult<WorkOrder>

    suspend fun updateTask(
        workOrderId: WorkOrderId,
        taskId: TaskId,
        serviceId: ServiceId? = null,
        assignedMechanicId: EmployeeId? = null,
        description: String? = null,
        evidenceImages: List<String>? = null
    ): AppResult<WorkOrder>

    suspend fun deleteTask(
        workOrderId: WorkOrderId,
        taskId: TaskId
    ): AppResult<Unit>

    suspend fun startTask(taskId: TaskId): AppResult<WorkOrder>

    suspend fun completeTask(taskId: TaskId): AppResult<WorkOrder>

    suspend fun reopenTask(taskId: TaskId): AppResult<WorkOrder>

    suspend fun assignMechanic(taskId: TaskId, mechanicId: EmployeeId): AppResult<WorkOrder>

    suspend fun reserveProduct(
        taskId: TaskId,
        productId: ProductId,
        quantity: Int
    ): AppResult<WorkOrder>

    suspend fun updateProductQuantity(
        taskId: TaskId,
        productId: ProductId,
        quantity: Int
    ): AppResult<WorkOrder>

    suspend fun removeProduct(
        taskId: TaskId,
        productId: ProductId
    ): AppResult<Unit>
}
