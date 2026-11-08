package com.tuxlogic.shiftiq.mobile.feature.operations

import com.tuxlogic.shiftiq.mobile.core.common.dispatchers.DispatcherProvider
import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.datastore.SessionDataStore
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.core.model.CustomerId
import com.tuxlogic.shiftiq.mobile.core.model.EmployeeId
import com.tuxlogic.shiftiq.mobile.core.model.VehicleId
import com.tuxlogic.shiftiq.mobile.feature.operations.data.remote.api.ServicesApiService
import com.tuxlogic.shiftiq.mobile.feature.operations.data.remote.api.WorkOrdersApiService
import com.tuxlogic.shiftiq.mobile.feature.operations.data.remote.dto.CreateWorkOrderRequestDto
import com.tuxlogic.shiftiq.mobile.feature.operations.data.remote.dto.ServiceResourceDto
import com.tuxlogic.shiftiq.mobile.feature.operations.data.remote.dto.WorkOrderResourceDto
import com.tuxlogic.shiftiq.mobile.feature.operations.data.repository.ServicesRepositoryImpl
import com.tuxlogic.shiftiq.mobile.feature.operations.data.repository.WorkOrderRepositoryImpl
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.ProductId
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.TaskId
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.WorkOrderId
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.util.UUID

class OperationsRepositoriesTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = object : DispatcherProvider {
        override val main: CoroutineDispatcher = testDispatcher
        override val io: CoroutineDispatcher = testDispatcher
        override val default: CoroutineDispatcher = testDispatcher
        override val unconfined: CoroutineDispatcher = testDispatcher
    }

    private val servicesApiService: ServicesApiService = mockk()
    private val workOrdersApiService: WorkOrdersApiService = mockk()
    private val sessionDataStore: SessionDataStore = mockk()

    private lateinit var servicesRepository: ServicesRepositoryImpl
    private lateinit var workOrderRepository: WorkOrderRepositoryImpl

    private val branchUuid = UUID.randomUUID()
    private val branchId = BranchId(branchUuid)
    private val vehicleId = VehicleId(UUID.randomUUID())
    private val customerId = CustomerId(UUID.randomUUID())
    private val employeeId = EmployeeId(UUID.randomUUID())
    private val workOrderId = WorkOrderId(UUID.randomUUID())
    private val taskId = TaskId(UUID.randomUUID())
    private val productId = ProductId(UUID.randomUUID())

    private val sampleServiceDto = ServiceResourceDto(
        id = UUID.randomUUID().toString(),
        branchId = branchUuid.toString(),
        name = "Wheel Alignment and Balancing",
        price = 75.0
    )

    private val sampleWorkOrderDto = WorkOrderResourceDto(
        id = workOrderId.toString(),
        appointmentId = "APP-999",
        branchId = branchUuid.toString(),
        vehicleId = vehicleId.toString(),
        customerId = customerId.toString(),
        internalNumber = 50,
        formattedNumber = "OT-00050",
        status = "PENDING",
        diagnosticSummary = "Worn brake pads",
        mileageIn = 60000,
        totalAmount = 180.0,
        tasks = emptyList(),
        entryInspectionImages = emptyList(),
        createdAt = "2026-11-07T12:00:00Z",
        updatedAt = "2026-11-07T12:00:00Z"
    )

    @Before
    fun setUp() {
        servicesRepository = ServicesRepositoryImpl(servicesApiService, sessionDataStore, dispatchers)
        workOrderRepository = WorkOrderRepositoryImpl(workOrdersApiService, sessionDataStore, dispatchers)
    }

    @Test
    fun `ServicesRepository getServices uses session branchId when not passed`() = runTest(testDispatcher) {
        coEvery { sessionDataStore.activeBranchId } returns flowOf(branchId)
        coEvery { servicesApiService.getServices(branchUuid.toString()) } returns Response.success(listOf(sampleServiceDto))

        val result = servicesRepository.getServices(branchId = null)

        assertTrue(result is AppResult.Success)
        val data = (result as AppResult.Success).data
        assertEquals(1, data.size)
        assertEquals("Wheel Alignment and Balancing", data[0].name)
    }

    @Test
    fun `ServicesRepository getServices returns validation error when no active branch`() = runTest(testDispatcher) {
        coEvery { sessionDataStore.activeBranchId } returns flowOf(null)

        val result = servicesRepository.getServices(branchId = null)

        assertTrue(result is AppResult.Failure)
        val error = (result as AppResult.Failure).error
        assertTrue(error is AppError.Validation)
        assertEquals("branchId", (error as AppError.Validation).details)
    }

    @Test
    fun `WorkOrderRepository createWorkOrder sends request and maps domain entity`() = runTest(testDispatcher) {
        coEvery {
            workOrdersApiService.createWorkOrder(any())
        } returns Response.success(sampleWorkOrderDto)

        val result = workOrderRepository.createWorkOrder(
            appointmentId = "APP-999",
            branchId = branchId,
            vehicleId = vehicleId,
            customerId = customerId,
            diagnosticSummary = "Worn brake pads",
            mileageIn = 60000,
            entryInspectionImages = emptyList()
        )

        assertTrue(result is AppResult.Success)
        val data = (result as AppResult.Success).data
        assertEquals("APP-999", data.appointmentId)
        assertEquals(60000, data.mileageIn)
        coVerify(exactly = 1) { workOrdersApiService.createWorkOrder(any()) }
    }

    @Test
    fun `WorkOrderRepository startTask calls api and returns updated work order`() = runTest(testDispatcher) {
        val inProgressOrder = sampleWorkOrderDto.copy(status = "IN_PROGRESS")
        coEvery { workOrdersApiService.startTask(taskId.toString()) } returns Response.success(inProgressOrder)

        val result = workOrderRepository.startTask(taskId)

        assertTrue(result is AppResult.Success)
        coVerify(exactly = 1) { workOrdersApiService.startTask(taskId.toString()) }
    }
}
