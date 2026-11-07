package com.tuxlogic.shiftiq.mobile.feature.operations

import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.core.model.CustomerId
import com.tuxlogic.shiftiq.mobile.core.model.EmployeeId
import com.tuxlogic.shiftiq.mobile.core.model.Money
import com.tuxlogic.shiftiq.mobile.core.model.VehicleId
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.ProductId
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.ServiceId
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.TaskId
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.WorkOrder
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.WorkOrderId
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.WorkOrderStatus
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.repository.WorkOrderRepository
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.usecase.AddTaskToWorkOrderUseCase
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.usecase.AssignMechanicToTaskUseCase
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.usecase.CompleteTaskUseCase
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.usecase.CompleteWorkOrderUseCase
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.usecase.CreateWorkOrderUseCase
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.usecase.DeleteWorkOrderUseCase
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.usecase.GetWorkOrderByIdUseCase
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.usecase.GetWorkOrdersUseCase
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.usecase.RemoveProductFromTaskUseCase
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.usecase.ReopenTaskUseCase
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.usecase.ReserveProductForTaskUseCase
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.usecase.StartTaskUseCase
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.usecase.UpdateTaskProductQuantityUseCase
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.usecase.UpdateWorkOrderUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.UUID

class WorkOrderUseCasesTest {

    private val workOrderRepository: WorkOrderRepository = mockk()

    private lateinit var getWorkOrdersUseCase: GetWorkOrdersUseCase
    private lateinit var getWorkOrderByIdUseCase: GetWorkOrderByIdUseCase
    private lateinit var createWorkOrderUseCase: CreateWorkOrderUseCase
    private lateinit var updateWorkOrderUseCase: UpdateWorkOrderUseCase
    private lateinit var completeWorkOrderUseCase: CompleteWorkOrderUseCase
    private lateinit var deleteWorkOrderUseCase: DeleteWorkOrderUseCase

    private lateinit var addTaskToWorkOrderUseCase: AddTaskToWorkOrderUseCase
    private lateinit var startTaskUseCase: StartTaskUseCase
    private lateinit var completeTaskUseCase: CompleteTaskUseCase
    private lateinit var reopenTaskUseCase: ReopenTaskUseCase
    private lateinit var assignMechanicToTaskUseCase: AssignMechanicToTaskUseCase

    private lateinit var reserveProductForTaskUseCase: ReserveProductForTaskUseCase
    private lateinit var updateTaskProductQuantityUseCase: UpdateTaskProductQuantityUseCase
    private lateinit var removeProductFromTaskUseCase: RemoveProductFromTaskUseCase

    private val branchId = BranchId(UUID.randomUUID())
    private val vehicleId = VehicleId(UUID.randomUUID())
    private val customerId = CustomerId(UUID.randomUUID())
    private val employeeId = EmployeeId(UUID.randomUUID())
    private val workOrderId = WorkOrderId(UUID.randomUUID())
    private val taskId = TaskId(UUID.randomUUID())
    private val serviceId = ServiceId(UUID.randomUUID())
    private val productId = ProductId(UUID.randomUUID())

    private val dummyWorkOrder = WorkOrder(
        id = workOrderId,
        appointmentId = "APP-1234",
        branchId = branchId,
        vehicleId = vehicleId,
        customerId = customerId,
        internalNumber = 101,
        formattedNumber = "OT-00101",
        status = WorkOrderStatus.PENDING,
        diagnosticSummary = "Revisión general de frenos",
        mileageIn = 45000,
        totalAmount = Money(250.0),
        tasks = emptyList(),
        entryInspectionImages = listOf("https://storage.shiftiq.com/img1.jpg"),
        createdAt = "2026-11-07T10:00:00Z",
        updatedAt = "2026-11-07T10:00:00Z"
    )

    @Before
    fun setUp() {
        getWorkOrdersUseCase = GetWorkOrdersUseCase(workOrderRepository)
        getWorkOrderByIdUseCase = GetWorkOrderByIdUseCase(workOrderRepository)
        createWorkOrderUseCase = CreateWorkOrderUseCase(workOrderRepository)
        updateWorkOrderUseCase = UpdateWorkOrderUseCase(workOrderRepository)
        completeWorkOrderUseCase = CompleteWorkOrderUseCase(workOrderRepository)
        deleteWorkOrderUseCase = DeleteWorkOrderUseCase(workOrderRepository)

        addTaskToWorkOrderUseCase = AddTaskToWorkOrderUseCase(workOrderRepository)
        startTaskUseCase = StartTaskUseCase(workOrderRepository)
        completeTaskUseCase = CompleteTaskUseCase(workOrderRepository)
        reopenTaskUseCase = ReopenTaskUseCase(workOrderRepository)
        assignMechanicToTaskUseCase = AssignMechanicToTaskUseCase(workOrderRepository)

        reserveProductForTaskUseCase = ReserveProductForTaskUseCase(workOrderRepository)
        updateTaskProductQuantityUseCase = UpdateTaskProductQuantityUseCase(workOrderRepository)
        removeProductFromTaskUseCase = RemoveProductFromTaskUseCase(workOrderRepository)
    }

    // --- CreateWorkOrderUseCase Tests ---

    @Test
    fun `createWorkOrder with blank appointmentId returns validation error`() = runTest {
        val result = createWorkOrderUseCase(
            appointmentId = "  ",
            branchId = branchId,
            vehicleId = vehicleId,
            customerId = customerId,
            diagnosticSummary = "Cambio de pastillas",
            mileageIn = 15000
        )

        assertTrue(result is AppResult.Failure)
        val error = (result as AppResult.Failure).error
        assertTrue(error is AppError.Validation)
        assertEquals("appointmentId", (error as AppError.Validation).details)
        coVerify(exactly = 0) { workOrderRepository.createWorkOrder(any(), any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `createWorkOrder with blank summary returns validation error`() = runTest {
        val result = createWorkOrderUseCase(
            appointmentId = "APP-1234",
            branchId = branchId,
            vehicleId = vehicleId,
            customerId = customerId,
            diagnosticSummary = "   ",
            mileageIn = 15000
        )

        assertTrue(result is AppResult.Failure)
        val error = (result as AppResult.Failure).error
        assertTrue(error is AppError.Validation)
        assertEquals("diagnosticSummary", (error as AppError.Validation).details)
        coVerify(exactly = 0) { workOrderRepository.createWorkOrder(any(), any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `createWorkOrder with negative mileage returns validation error`() = runTest {
        val result = createWorkOrderUseCase(
            appointmentId = "APP-1234",
            branchId = branchId,
            vehicleId = vehicleId,
            customerId = customerId,
            diagnosticSummary = "Cambio de bujías",
            mileageIn = -5
        )

        assertTrue(result is AppResult.Failure)
        val error = (result as AppResult.Failure).error
        assertTrue(error is AppError.Validation)
        assertEquals("mileageIn", (error as AppError.Validation).details)
        coVerify(exactly = 0) { workOrderRepository.createWorkOrder(any(), any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `createWorkOrder with valid inputs calls repository and returns order`() = runTest {
        coEvery {
            workOrderRepository.createWorkOrder(
                appointmentId = "APP-1234",
                branchId = branchId,
                vehicleId = vehicleId,
                customerId = customerId,
                diagnosticSummary = "Revisión general de frenos",
                mileageIn = 45000,
                entryInspectionImages = listOf("https://storage.shiftiq.com/img1.jpg")
            )
        } returns AppResult.Success(dummyWorkOrder)

        val result = createWorkOrderUseCase(
            appointmentId = "APP-1234",
            branchId = branchId,
            vehicleId = vehicleId,
            customerId = customerId,
            diagnosticSummary = "Revisión general de frenos",
            mileageIn = 45000,
            entryInspectionImages = listOf("https://storage.shiftiq.com/img1.jpg")
        )

        assertTrue(result is AppResult.Success)
        assertEquals(dummyWorkOrder, (result as AppResult.Success).data)
        coVerify(exactly = 1) {
            workOrderRepository.createWorkOrder(
                appointmentId = "APP-1234",
                branchId = branchId,
                vehicleId = vehicleId,
                customerId = customerId,
                diagnosticSummary = "Revisión general de frenos",
                mileageIn = 45000,
                entryInspectionImages = listOf("https://storage.shiftiq.com/img1.jpg")
            )
        }
    }

    // --- UpdateWorkOrderUseCase Tests ---

    @Test
    fun `updateWorkOrder with blank summary returns validation error`() = runTest {
        val result = updateWorkOrderUseCase(workOrderId, diagnosticSummary = "  ", mileageIn = 10000)

        assertTrue(result is AppResult.Failure)
        val error = (result as AppResult.Failure).error
        assertTrue(error is AppError.Validation)
        assertEquals("diagnosticSummary", (error as AppError.Validation).details)
        coVerify(exactly = 0) { workOrderRepository.updateWorkOrder(any(), any(), any()) }
    }

    @Test
    fun `updateWorkOrder with negative mileage returns validation error`() = runTest {
        val result = updateWorkOrderUseCase(workOrderId, diagnosticSummary = "Nuevo diagnóstico", mileageIn = -1)

        assertTrue(result is AppResult.Failure)
        val error = (result as AppResult.Failure).error
        assertTrue(error is AppError.Validation)
        assertEquals("mileageIn", (error as AppError.Validation).details)
        coVerify(exactly = 0) { workOrderRepository.updateWorkOrder(any(), any(), any()) }
    }

    @Test
    fun `updateWorkOrder with valid inputs calls repository`() = runTest {
        coEvery {
            workOrderRepository.updateWorkOrder(workOrderId, "Diagnóstico actualizado", 50000)
        } returns AppResult.Success(dummyWorkOrder)

        val result = updateWorkOrderUseCase(workOrderId, "Diagnóstico actualizado", 50000)

        assertTrue(result is AppResult.Success)
        assertEquals(dummyWorkOrder, (result as AppResult.Success).data)
        coVerify(exactly = 1) {
            workOrderRepository.updateWorkOrder(workOrderId, "Diagnóstico actualizado", 50000)
        }
    }

    // --- Work Order Queries & Lifecycle Tests ---

    @Test
    fun `getWorkOrders delegates to repository`() = runTest {
        val list = listOf(dummyWorkOrder)
        coEvery { workOrderRepository.getWorkOrders(branchId, vehicleId) } returns AppResult.Success(list)

        val result = getWorkOrdersUseCase(branchId, vehicleId)

        assertTrue(result is AppResult.Success)
        assertEquals(list, (result as AppResult.Success).data)
        coVerify(exactly = 1) { workOrderRepository.getWorkOrders(branchId, vehicleId) }
    }

    @Test
    fun `getWorkOrderById delegates to repository`() = runTest {
        coEvery { workOrderRepository.getWorkOrderById(workOrderId) } returns AppResult.Success(dummyWorkOrder)

        val result = getWorkOrderByIdUseCase(workOrderId)

        assertTrue(result is AppResult.Success)
        assertEquals(dummyWorkOrder, (result as AppResult.Success).data)
        coVerify(exactly = 1) { workOrderRepository.getWorkOrderById(workOrderId) }
    }

    @Test
    fun `completeWorkOrder delegates to repository`() = runTest {
        val completedOrder = dummyWorkOrder.copy(status = WorkOrderStatus.COMPLETED)
        coEvery { workOrderRepository.completeWorkOrder(workOrderId) } returns AppResult.Success(completedOrder)

        val result = completeWorkOrderUseCase(workOrderId)

        assertTrue(result is AppResult.Success)
        assertEquals(WorkOrderStatus.COMPLETED, (result as AppResult.Success).data.status)
        coVerify(exactly = 1) { workOrderRepository.completeWorkOrder(workOrderId) }
    }

    @Test
    fun `deleteWorkOrder delegates to repository`() = runTest {
        coEvery { workOrderRepository.deleteWorkOrder(workOrderId) } returns AppResult.Success(Unit)

        val result = deleteWorkOrderUseCase(workOrderId)

        assertTrue(result is AppResult.Success)
        coVerify(exactly = 1) { workOrderRepository.deleteWorkOrder(workOrderId) }
    }

    // --- Task Lifecycle Tests ---

    @Test
    fun `addTask with short description returns validation error`() = runTest {
        val result = addTaskToWorkOrderUseCase(
            workOrderId = workOrderId,
            serviceId = serviceId,
            assignedMechanicId = employeeId,
            description = "Short"
        )

        assertTrue(result is AppResult.Failure)
        val error = (result as AppResult.Failure).error
        assertTrue(error is AppError.Validation)
        assertEquals("description", (error as AppError.Validation).details)
        coVerify(exactly = 0) { workOrderRepository.addTask(any(), any(), any(), any(), any()) }
    }

    @Test
    fun `addTask with valid description calls repository`() = runTest {
        val validDescription = "Cambio de filtro de aire y aceite"
        coEvery {
            workOrderRepository.addTask(workOrderId, serviceId, employeeId, validDescription, emptyList())
        } returns AppResult.Success(dummyWorkOrder)

        val result = addTaskToWorkOrderUseCase(
            workOrderId = workOrderId,
            serviceId = serviceId,
            assignedMechanicId = employeeId,
            description = validDescription
        )

        assertTrue(result is AppResult.Success)
        coVerify(exactly = 1) {
            workOrderRepository.addTask(workOrderId, serviceId, employeeId, validDescription, emptyList())
        }
    }

    @Test
    fun `startTask delegates to repository`() = runTest {
        coEvery { workOrderRepository.startTask(taskId) } returns AppResult.Success(dummyWorkOrder)

        val result = startTaskUseCase(taskId)

        assertTrue(result is AppResult.Success)
        coVerify(exactly = 1) { workOrderRepository.startTask(taskId) }
    }

    @Test
    fun `completeTask delegates to repository`() = runTest {
        coEvery { workOrderRepository.completeTask(taskId) } returns AppResult.Success(dummyWorkOrder)

        val result = completeTaskUseCase(taskId)

        assertTrue(result is AppResult.Success)
        coVerify(exactly = 1) { workOrderRepository.completeTask(taskId) }
    }

    @Test
    fun `reopenTask delegates to repository`() = runTest {
        coEvery { workOrderRepository.reopenTask(taskId) } returns AppResult.Success(dummyWorkOrder)

        val result = reopenTaskUseCase(taskId)

        assertTrue(result is AppResult.Success)
        coVerify(exactly = 1) { workOrderRepository.reopenTask(taskId) }
    }

    @Test
    fun `assignMechanicToTask delegates to repository`() = runTest {
        coEvery { workOrderRepository.assignMechanic(taskId, employeeId) } returns AppResult.Success(dummyWorkOrder)

        val result = assignMechanicToTaskUseCase(taskId, employeeId)

        assertTrue(result is AppResult.Success)
        coVerify(exactly = 1) { workOrderRepository.assignMechanic(taskId, employeeId) }
    }

    // --- Product Reservation Tests ---

    @Test
    fun `reserveProduct with zero or negative quantity returns validation error`() = runTest {
        val result = reserveProductForTaskUseCase(taskId, productId, quantity = 0)

        assertTrue(result is AppResult.Failure)
        val error = (result as AppResult.Failure).error
        assertTrue(error is AppError.Validation)
        assertEquals("quantity", (error as AppError.Validation).details)
        coVerify(exactly = 0) { workOrderRepository.reserveProduct(any(), any(), any()) }
    }

    @Test
    fun `reserveProduct with positive quantity calls repository`() = runTest {
        coEvery { workOrderRepository.reserveProduct(taskId, productId, 2) } returns AppResult.Success(dummyWorkOrder)

        val result = reserveProductForTaskUseCase(taskId, productId, quantity = 2)

        assertTrue(result is AppResult.Success)
        coVerify(exactly = 1) { workOrderRepository.reserveProduct(taskId, productId, 2) }
    }

    @Test
    fun `updateProductQuantity with non-positive quantity returns validation error`() = runTest {
        val result = updateTaskProductQuantityUseCase(taskId, productId, quantity = -2)

        assertTrue(result is AppResult.Failure)
        val error = (result as AppResult.Failure).error
        assertTrue(error is AppError.Validation)
        assertEquals("quantity", (error as AppError.Validation).details)
        coVerify(exactly = 0) { workOrderRepository.updateProductQuantity(any(), any(), any()) }
    }

    @Test
    fun `updateProductQuantity with positive quantity calls repository`() = runTest {
        coEvery { workOrderRepository.updateProductQuantity(taskId, productId, 4) } returns AppResult.Success(dummyWorkOrder)

        val result = updateTaskProductQuantityUseCase(taskId, productId, quantity = 4)

        assertTrue(result is AppResult.Success)
        coVerify(exactly = 1) { workOrderRepository.updateProductQuantity(taskId, productId, 4) }
    }

    @Test
    fun `removeProductFromTask delegates to repository`() = runTest {
        coEvery { workOrderRepository.removeProduct(taskId, productId) } returns AppResult.Success(Unit)

        val result = removeProductFromTaskUseCase(taskId, productId)

        assertTrue(result is AppResult.Success)
        coVerify(exactly = 1) { workOrderRepository.removeProduct(taskId, productId) }
    }
}
