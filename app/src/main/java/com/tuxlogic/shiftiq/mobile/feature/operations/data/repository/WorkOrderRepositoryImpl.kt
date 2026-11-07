package com.tuxlogic.shiftiq.mobile.feature.operations.data.repository

import com.tuxlogic.shiftiq.mobile.core.common.dispatchers.DispatcherProvider
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.common.result.map
import com.tuxlogic.shiftiq.mobile.core.datastore.SessionDataStore
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.core.model.CustomerId
import com.tuxlogic.shiftiq.mobile.core.model.EmployeeId
import com.tuxlogic.shiftiq.mobile.core.model.VehicleId
import com.tuxlogic.shiftiq.mobile.core.network.safeApiCall
import com.tuxlogic.shiftiq.mobile.feature.operations.data.mapper.toDomain
import com.tuxlogic.shiftiq.mobile.feature.operations.data.remote.api.WorkOrdersApiService
import com.tuxlogic.shiftiq.mobile.feature.operations.data.remote.dto.AddProductToTaskRequestDto
import com.tuxlogic.shiftiq.mobile.feature.operations.data.remote.dto.AddTaskRequestDto
import com.tuxlogic.shiftiq.mobile.feature.operations.data.remote.dto.CreateWorkOrderRequestDto
import com.tuxlogic.shiftiq.mobile.feature.operations.data.remote.dto.UpdateProductQuantityRequestDto
import com.tuxlogic.shiftiq.mobile.feature.operations.data.remote.dto.UpdateTaskRequestDto
import com.tuxlogic.shiftiq.mobile.feature.operations.data.remote.dto.UpdateWorkOrderRequestDto
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.ProductId
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.ServiceId
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.TaskId
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.WorkOrder
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.WorkOrderId
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.repository.WorkOrderRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WorkOrderRepositoryImpl @Inject constructor(
    private val workOrdersApiService: WorkOrdersApiService,
    private val sessionDataStore: SessionDataStore,
    private val dispatchers: DispatcherProvider
) : WorkOrderRepository {

    override suspend fun getWorkOrders(
        branchId: BranchId?,
        vehicleId: VehicleId?
    ): AppResult<List<WorkOrder>> = withContext(dispatchers.io) {
        val effectiveBranchId = branchId ?: if (vehicleId == null) sessionDataStore.activeBranchId.first() else null
        safeApiCall {
            workOrdersApiService.getWorkOrders(
                branchId = effectiveBranchId?.toString(),
                vehicleId = vehicleId?.toString()
            )
        }.map { dtos ->
            dtos.map { it.toDomain() }
        }
    }

    override suspend fun getWorkOrderById(id: WorkOrderId): AppResult<WorkOrder> =
        withContext(dispatchers.io) {
            safeApiCall {
                workOrdersApiService.getWorkOrderById(id.toString())
            }.map { it.toDomain() }
        }

    override suspend fun createWorkOrder(
        appointmentId: String,
        branchId: BranchId,
        vehicleId: VehicleId,
        customerId: CustomerId,
        diagnosticSummary: String,
        mileageIn: Int,
        entryInspectionImages: List<String>
    ): AppResult<WorkOrder> = withContext(dispatchers.io) {
        safeApiCall {
            workOrdersApiService.createWorkOrder(
                CreateWorkOrderRequestDto(
                    appointmentId = appointmentId,
                    branchId = branchId.toString(),
                    vehicleId = vehicleId.toString(),
                    customerId = customerId.toString(),
                    diagnosticSummary = diagnosticSummary,
                    mileageIn = mileageIn,
                    entryInspectionImages = entryInspectionImages
                )
            )
        }.map { it.toDomain() }
    }

    override suspend fun updateWorkOrder(
        id: WorkOrderId,
        diagnosticSummary: String,
        mileageIn: Int
    ): AppResult<WorkOrder> = withContext(dispatchers.io) {
        safeApiCall {
            workOrdersApiService.updateWorkOrder(
                id = id.toString(),
                request = UpdateWorkOrderRequestDto(
                    diagnosticSummary = diagnosticSummary,
                    mileageIn = mileageIn
                )
            )
        }.map { it.toDomain() }
    }

    override suspend fun completeWorkOrder(id: WorkOrderId): AppResult<WorkOrder> =
        withContext(dispatchers.io) {
            safeApiCall {
                workOrdersApiService.completeWorkOrder(id.toString())
            }.map { it.toDomain() }
        }

    override suspend fun deleteWorkOrder(id: WorkOrderId): AppResult<Unit> =
        withContext(dispatchers.io) {
            safeApiCall {
                workOrdersApiService.deleteWorkOrder(id.toString())
            }
        }

    override suspend fun addTask(
        workOrderId: WorkOrderId,
        serviceId: ServiceId,
        assignedMechanicId: EmployeeId,
        description: String,
        evidenceImages: List<String>
    ): AppResult<WorkOrder> = withContext(dispatchers.io) {
        safeApiCall {
            workOrdersApiService.addTask(
                id = workOrderId.toString(),
                request = AddTaskRequestDto(
                    serviceId = serviceId.toString(),
                    assignedMechanicId = assignedMechanicId.toString(),
                    description = description,
                    evidenceImages = evidenceImages
                )
            )
        }.map { it.toDomain() }
    }

    override suspend fun updateTask(
        workOrderId: WorkOrderId,
        taskId: TaskId,
        description: String?,
        evidenceImages: List<String>?
    ): AppResult<WorkOrder> = withContext(dispatchers.io) {
        safeApiCall {
            workOrdersApiService.updateTask(
                id = workOrderId.toString(),
                taskId = taskId.toString(),
                request = UpdateTaskRequestDto(
                    description = description,
                    evidenceImages = evidenceImages
                )
            )
        }.map { it.toDomain() }
    }

    override suspend fun deleteTask(
        workOrderId: WorkOrderId,
        taskId: TaskId
    ): AppResult<Unit> = withContext(dispatchers.io) {
        safeApiCall {
            workOrdersApiService.deleteTask(
                id = workOrderId.toString(),
                taskId = taskId.toString()
            )
        }
    }

    override suspend fun startTask(taskId: TaskId): AppResult<WorkOrder> =
        withContext(dispatchers.io) {
            safeApiCall {
                workOrdersApiService.startTask(taskId.toString())
            }.map { it.toDomain() }
        }

    override suspend fun completeTask(taskId: TaskId): AppResult<WorkOrder> =
        withContext(dispatchers.io) {
            safeApiCall {
                workOrdersApiService.completeTask(taskId.toString())
            }.map { it.toDomain() }
        }

    override suspend fun reopenTask(taskId: TaskId): AppResult<WorkOrder> =
        withContext(dispatchers.io) {
            safeApiCall {
                workOrdersApiService.reopenTask(taskId.toString())
            }.map { it.toDomain() }
        }

    override suspend fun assignMechanic(
        taskId: TaskId,
        mechanicId: EmployeeId
    ): AppResult<WorkOrder> = withContext(dispatchers.io) {
        safeApiCall {
            workOrdersApiService.assignMechanic(
                taskId = taskId.toString(),
                mechanicId = mechanicId.toString()
            )
        }.map { it.toDomain() }
    }

    override suspend fun reserveProduct(
        taskId: TaskId,
        productId: ProductId,
        quantity: Int
    ): AppResult<WorkOrder> = withContext(dispatchers.io) {
        safeApiCall {
            workOrdersApiService.reserveProduct(
                taskId = taskId.toString(),
                request = AddProductToTaskRequestDto(
                    productId = productId.toString(),
                    quantity = quantity
                )
            )
        }.map { it.toDomain() }
    }

    override suspend fun updateProductQuantity(
        taskId: TaskId,
        productId: ProductId,
        quantity: Int
    ): AppResult<WorkOrder> = withContext(dispatchers.io) {
        safeApiCall {
            workOrdersApiService.updateProductQuantity(
                taskId = taskId.toString(),
                productId = productId.toString(),
                request = UpdateProductQuantityRequestDto(quantity = quantity)
            )
        }.map { it.toDomain() }
    }

    override suspend fun removeProduct(
        taskId: TaskId,
        productId: ProductId
    ): AppResult<Unit> = withContext(dispatchers.io) {
        safeApiCall {
            workOrdersApiService.removeProduct(
                taskId = taskId.toString(),
                productId = productId.toString()
            )
        }
    }
}
