package com.tuxlogic.shiftiq.mobile.feature.operations

import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.core.model.Money
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.ServiceId
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.ServiceItem
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.repository.ServicesRepository
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.usecase.CreateServiceUseCase
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.usecase.DeleteServiceUseCase
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.usecase.GetServicesUseCase
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.usecase.UpdateServiceUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.UUID

class ServicesUseCasesTest {

    private val servicesRepository: ServicesRepository = mockk()
    private lateinit var getServicesUseCase: GetServicesUseCase
    private lateinit var createServiceUseCase: CreateServiceUseCase
    private lateinit var updateServiceUseCase: UpdateServiceUseCase
    private lateinit var deleteServiceUseCase: DeleteServiceUseCase

    private val branchId = BranchId(UUID.randomUUID())
    private val serviceId = ServiceId(UUID.randomUUID())

    @Before
    fun setUp() {
        getServicesUseCase = GetServicesUseCase(servicesRepository)
        createServiceUseCase = CreateServiceUseCase(servicesRepository)
        updateServiceUseCase = UpdateServiceUseCase(servicesRepository)
        deleteServiceUseCase = DeleteServiceUseCase(servicesRepository)
    }

    @Test
    fun `getServices returns list when repository succeeds`() = runTest {
        val expected = listOf(
            ServiceItem(id = serviceId, branchId = branchId, name = "Alineación", price = Money(80.0))
        )
        coEvery { servicesRepository.getServices(branchId) } returns AppResult.Success(expected)

        val result = getServicesUseCase(branchId)

        assertTrue(result is AppResult.Success)
        assertEquals(expected, (result as AppResult.Success).data)
        coVerify(exactly = 1) { servicesRepository.getServices(branchId) }
    }

    @Test
    fun `createService with blank name returns validation error`() = runTest {
        val result = createServiceUseCase(branchId, name = "   ", price = 80.0)

        assertTrue(result is AppResult.Failure)
        val error = (result as AppResult.Failure).error
        assertTrue(error is AppError.Validation)
        assertEquals("name", (error as AppError.Validation).details)
        coVerify(exactly = 0) { servicesRepository.createService(any(), any(), any()) }
    }

    @Test
    fun `createService with negative price returns validation error`() = runTest {
        val result = createServiceUseCase(branchId, name = "Cambio de Aceite", price = -10.0)

        assertTrue(result is AppResult.Failure)
        val error = (result as AppResult.Failure).error
        assertTrue(error is AppError.Validation)
        assertEquals("price", (error as AppError.Validation).details)
        coVerify(exactly = 0) { servicesRepository.createService(any(), any(), any()) }
    }

    @Test
    fun `createService with valid data calls repository and returns created item`() = runTest {
        val expected = ServiceItem(id = serviceId, branchId = branchId, name = "Cambio de Aceite", price = Money(120.0))
        coEvery { servicesRepository.createService(branchId, "Cambio de Aceite", 120.0) } returns AppResult.Success(expected)

        val result = createServiceUseCase(branchId, name = "Cambio de Aceite", price = 120.0)

        assertTrue(result is AppResult.Success)
        assertEquals(expected, (result as AppResult.Success).data)
        coVerify(exactly = 1) { servicesRepository.createService(branchId, "Cambio de Aceite", 120.0) }
    }

    @Test
    fun `updateService with blank name returns validation error`() = runTest {
        val result = updateServiceUseCase(serviceId, name = "", price = 100.0)

        assertTrue(result is AppResult.Failure)
        val error = (result as AppResult.Failure).error
        assertTrue(error is AppError.Validation)
        assertEquals("name", (error as AppError.Validation).details)
        coVerify(exactly = 0) { servicesRepository.updateService(any(), any(), any()) }
    }

    @Test
    fun `updateService with negative price returns validation error`() = runTest {
        val result = updateServiceUseCase(serviceId, name = "Mantenimiento", price = -5.0)

        assertTrue(result is AppResult.Failure)
        val error = (result as AppResult.Failure).error
        assertTrue(error is AppError.Validation)
        assertEquals("price", (error as AppError.Validation).details)
        coVerify(exactly = 0) { servicesRepository.updateService(any(), any(), any()) }
    }

    @Test
    fun `updateService with valid data calls repository`() = runTest {
        val expected = ServiceItem(id = serviceId, branchId = branchId, name = "Mantenimiento Pro", price = 150.0.let { Money(it) })
        coEvery { servicesRepository.updateService(serviceId, "Mantenimiento Pro", 150.0) } returns AppResult.Success(expected)

        val result = updateServiceUseCase(serviceId, name = "Mantenimiento Pro", price = 150.0)

        assertTrue(result is AppResult.Success)
        assertEquals(expected, (result as AppResult.Success).data)
        coVerify(exactly = 1) { servicesRepository.updateService(serviceId, "Mantenimiento Pro", 150.0) }
    }

    @Test
    fun `deleteService delegates to repository`() = runTest {
        coEvery { servicesRepository.deleteService(serviceId) } returns AppResult.Success(Unit)

        val result = deleteServiceUseCase(serviceId)

        assertTrue(result is AppResult.Success)
        coVerify(exactly = 1) { servicesRepository.deleteService(serviceId) }
    }
}
