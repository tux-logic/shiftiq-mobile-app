package com.tuxlogic.shiftiq.mobile.feature.iot

import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.core.model.CustomerId
import com.tuxlogic.shiftiq.mobile.core.model.VehicleId
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.PlateNumber
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.Vehicle
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.Vin
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.repository.IotRepository
import com.tuxlogic.shiftiq.mobile.feature.iot.application.usecase.vehicles.DeleteVehicleUseCase
import com.tuxlogic.shiftiq.mobile.feature.iot.application.usecase.vehicles.GetCustomerVehiclesUseCase
import com.tuxlogic.shiftiq.mobile.feature.iot.application.usecase.vehicles.GetVehicleByIdUseCase
import com.tuxlogic.shiftiq.mobile.feature.iot.application.usecase.vehicles.GetVehiclesByBranchUseCase
import com.tuxlogic.shiftiq.mobile.feature.iot.application.usecase.vehicles.RegisterVehicleUseCase
import com.tuxlogic.shiftiq.mobile.feature.iot.application.usecase.vehicles.UpdateVehicleUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.UUID

class VehicleUseCasesTest {

    private val iotRepository: IotRepository = mockk()

    private lateinit var registerVehicleUseCase: RegisterVehicleUseCase
    private lateinit var getVehicleByIdUseCase: GetVehicleByIdUseCase
    private lateinit var getVehiclesByBranchUseCase: GetVehiclesByBranchUseCase
    private lateinit var getCustomerVehiclesUseCase: GetCustomerVehiclesUseCase
    private lateinit var updateVehicleUseCase: UpdateVehicleUseCase
    private lateinit var deleteVehicleUseCase: DeleteVehicleUseCase

    private val vehicleId = VehicleId(UUID.randomUUID())
    private val branchId = BranchId(UUID.randomUUID())
    private val customerId = CustomerId(UUID.randomUUID())

    private val sampleVehicle = Vehicle(
        id = vehicleId,
        plateNumber = PlateNumber("ABC-123"),
        brand = "Toyota",
        model = "Corolla Cross",
        year = 2023,
        vin = Vin("9BRBL3HE2P0123456"),
        photoUrl = "https://example.com/photo.jpg",
        customerId = customerId
    )

    @Before
    fun setUp() {
        registerVehicleUseCase = RegisterVehicleUseCase(iotRepository)
        getVehicleByIdUseCase = GetVehicleByIdUseCase(iotRepository)
        getVehiclesByBranchUseCase = GetVehiclesByBranchUseCase(iotRepository)
        getCustomerVehiclesUseCase = GetCustomerVehiclesUseCase(iotRepository)
        updateVehicleUseCase = UpdateVehicleUseCase(iotRepository)
        deleteVehicleUseCase = DeleteVehicleUseCase(iotRepository)
    }

    @Test
    fun `RegisterVehicleUseCase with invalid VIN returns validation failure`() = runTest {
        val result = registerVehicleUseCase(
            plateNumber = "ABC-123",
            brand = "Toyota",
            model = "Corolla",
            year = 2023,
            vin = "INVALID_VIN"
        )

        assertTrue(result is AppResult.Failure)
        assertTrue((result as AppResult.Failure).error is AppError.Validation)
        coVerify(exactly = 0) { iotRepository.registerVehicle(any(), any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `RegisterVehicleUseCase with invalid year returns validation failure`() = runTest {
        val result = registerVehicleUseCase(
            plateNumber = "ABC-123",
            brand = "Toyota",
            model = "Corolla",
            year = 1850,
            vin = "9BRBL3HE2P0123456"
        )

        assertTrue(result is AppResult.Failure)
        assertTrue((result as AppResult.Failure).error is AppError.Validation)
        coVerify(exactly = 0) { iotRepository.registerVehicle(any(), any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `RegisterVehicleUseCase with valid data calls repository`() = runTest {
        coEvery {
            iotRepository.registerVehicle(
                any(), any(), any(), any(), any(), any(), any()
            )
        } returns AppResult.Success(sampleVehicle)

        val result = registerVehicleUseCase(
            plateNumber = "ABC-123",
            brand = "Toyota",
            model = "Corolla Cross",
            year = 2023,
            vin = "9BRBL3HE2P0123456",
            photoUrl = "https://example.com/photo.jpg",
            customerId = customerId
        )

        assertTrue(result is AppResult.Success)
        assertEquals(sampleVehicle, (result as AppResult.Success).data)
        coVerify(exactly = 1) {
            iotRepository.registerVehicle(
                PlateNumber("ABC-123"),
                "Toyota",
                "Corolla Cross",
                2023,
                Vin("9BRBL3HE2P0123456"),
                "https://example.com/photo.jpg",
                customerId
            )
        }
    }

    @Test
    fun `GetVehicleByIdUseCase returns vehicle from repository`() = runTest {
        coEvery { iotRepository.getVehicleById(vehicleId) } returns AppResult.Success(sampleVehicle)

        val result = getVehicleByIdUseCase(vehicleId)

        assertTrue(result is AppResult.Success)
        assertEquals(sampleVehicle, (result as AppResult.Success).data)
        coVerify(exactly = 1) { iotRepository.getVehicleById(vehicleId) }
    }

    @Test
    fun `GetVehiclesByBranchUseCase returns branch vehicles`() = runTest {
        coEvery { iotRepository.getVehiclesByBranch(branchId) } returns AppResult.Success(listOf(sampleVehicle))

        val result = getVehiclesByBranchUseCase(branchId)

        assertTrue(result is AppResult.Success)
        assertEquals(listOf(sampleVehicle), (result as AppResult.Success).data)
        coVerify(exactly = 1) { iotRepository.getVehiclesByBranch(branchId) }
    }

    @Test
    fun `GetCustomerVehiclesUseCase returns customer vehicles`() = runTest {
        coEvery { iotRepository.getCustomerVehicles(customerId) } returns AppResult.Success(listOf(sampleVehicle))

        val result = getCustomerVehiclesUseCase(customerId)

        assertTrue(result is AppResult.Success)
        assertEquals(listOf(sampleVehicle), (result as AppResult.Success).data)
        coVerify(exactly = 1) { iotRepository.getCustomerVehicles(customerId) }
    }

    @Test
    fun `UpdateVehicleUseCase with valid data calls repository`() = runTest {
        val updated = sampleVehicle.copy(model = "RAV4")
        coEvery { iotRepository.updateVehicle(vehicleId, any(), any(), any(), any(), any(), any()) } returns AppResult.Success(updated)

        val result = updateVehicleUseCase(
            id = vehicleId,
            plateNumber = "ABC-123",
            brand = "Toyota",
            model = "RAV4",
            year = 2023,
            vin = "9BRBL3HE2P0123456"
        )

        assertTrue(result is AppResult.Success)
        assertEquals("RAV4", (result as AppResult.Success).data.model)
        coVerify(exactly = 1) { iotRepository.updateVehicle(vehicleId, PlateNumber("ABC-123"), "Toyota", "RAV4", 2023, Vin("9BRBL3HE2P0123456"), null) }
    }

    @Test
    fun `DeleteVehicleUseCase calls repository`() = runTest {
        coEvery { iotRepository.deleteVehicle(vehicleId) } returns AppResult.Success(Unit)

        val result = deleteVehicleUseCase(vehicleId)

        assertTrue(result is AppResult.Success)
        coVerify(exactly = 1) { iotRepository.deleteVehicle(vehicleId) }
    }
}
