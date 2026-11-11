package com.tuxlogic.shiftiq.mobile.feature.iot

import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.core.model.VehicleId
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.DeviceId
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.DeviceRegistration
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.DeviceStatus
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.MacAddress
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.Obd2Device
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.RegistrationId
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.RegistrationStatus
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.repository.IotRepository
import com.tuxlogic.shiftiq.mobile.feature.iot.application.usecase.devices.DeleteDeviceUseCase
import com.tuxlogic.shiftiq.mobile.feature.iot.application.usecase.devices.GetDeviceByIdUseCase
import com.tuxlogic.shiftiq.mobile.feature.iot.application.usecase.devices.GetDevicesUseCase
import com.tuxlogic.shiftiq.mobile.feature.iot.application.usecase.devices.RegisterDeviceUseCase
import com.tuxlogic.shiftiq.mobile.feature.iot.application.usecase.devices.UpdateDeviceUseCase
import com.tuxlogic.shiftiq.mobile.feature.iot.application.usecase.registrations.GetRegistrationsUseCase
import com.tuxlogic.shiftiq.mobile.feature.iot.application.usecase.registrations.LinkDeviceToVehicleUseCase
import com.tuxlogic.shiftiq.mobile.feature.iot.application.usecase.registrations.UnlinkDeviceFromVehicleUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.UUID

class DeviceUseCasesTest {

    private val iotRepository: IotRepository = mockk()

    private lateinit var registerDeviceUseCase: RegisterDeviceUseCase
    private lateinit var getDevicesUseCase: GetDevicesUseCase
    private lateinit var getDeviceByIdUseCase: GetDeviceByIdUseCase
    private lateinit var updateDeviceUseCase: UpdateDeviceUseCase
    private lateinit var deleteDeviceUseCase: DeleteDeviceUseCase
    private lateinit var linkDeviceToVehicleUseCase: LinkDeviceToVehicleUseCase
    private lateinit var unlinkDeviceFromVehicleUseCase: UnlinkDeviceFromVehicleUseCase
    private lateinit var getRegistrationsUseCase: GetRegistrationsUseCase

    private val deviceId = DeviceId(UUID.randomUUID())
    private val branchId = BranchId(UUID.randomUUID())
    private val vehicleId = VehicleId(UUID.randomUUID())
    private val registrationId = RegistrationId(UUID.randomUUID())

    private val sampleDevice = Obd2Device(
        id = deviceId,
        branchId = branchId,
        macAddress = MacAddress("00:1B:44:11:3A:B7"),
        status = DeviceStatus.AVAILABLE
    )

    private val sampleRegistration = DeviceRegistration(
        id = registrationId,
        obd2DeviceId = deviceId,
        branchId = branchId,
        vehicleId = vehicleId,
        status = RegistrationStatus.ACTIVE,
        createdAt = "2026-10-15T10:30:00Z"
    )

    @Before
    fun setUp() {
        registerDeviceUseCase = RegisterDeviceUseCase(iotRepository)
        getDevicesUseCase = GetDevicesUseCase(iotRepository)
        getDeviceByIdUseCase = GetDeviceByIdUseCase(iotRepository)
        updateDeviceUseCase = UpdateDeviceUseCase(iotRepository)
        deleteDeviceUseCase = DeleteDeviceUseCase(iotRepository)
        linkDeviceToVehicleUseCase = LinkDeviceToVehicleUseCase(iotRepository)
        unlinkDeviceFromVehicleUseCase = UnlinkDeviceFromVehicleUseCase(iotRepository)
        getRegistrationsUseCase = GetRegistrationsUseCase(iotRepository)
    }

    @Test
    fun `RegisterDeviceUseCase with invalid MAC returns validation failure`() = runTest {
        val result = registerDeviceUseCase(branchId, "invalid-mac")

        assertTrue(result is AppResult.Failure)
        assertTrue((result as AppResult.Failure).error is AppError.Validation)
        coVerify(exactly = 0) { iotRepository.registerDevice(any(), any()) }
    }

    @Test
    fun `RegisterDeviceUseCase with valid MAC calls repository`() = runTest {
        coEvery { iotRepository.registerDevice(branchId, any()) } returns AppResult.Success(sampleDevice)

        val result = registerDeviceUseCase(branchId, "00:1B:44:11:3A:B7")

        assertTrue(result is AppResult.Success)
        assertEquals(sampleDevice, (result as AppResult.Success).data)
        coVerify(exactly = 1) { iotRepository.registerDevice(branchId, MacAddress("00:1B:44:11:3A:B7")) }
    }

    @Test
    fun `GetDevicesUseCase queries branch devices`() = runTest {
        coEvery { iotRepository.getDevices(branchId, "available") } returns AppResult.Success(listOf(sampleDevice))

        val result = getDevicesUseCase(branchId, "available")

        assertTrue(result is AppResult.Success)
        assertEquals(listOf(sampleDevice), (result as AppResult.Success).data)
        coVerify(exactly = 1) { iotRepository.getDevices(branchId, "available") }
    }

    @Test
    fun `GetDeviceByIdUseCase returns device details`() = runTest {
        coEvery { iotRepository.getDeviceById(deviceId) } returns AppResult.Success(sampleDevice)

        val result = getDeviceByIdUseCase(deviceId)

        assertTrue(result is AppResult.Success)
        assertEquals(sampleDevice, (result as AppResult.Success).data)
        coVerify(exactly = 1) { iotRepository.getDeviceById(deviceId) }
    }

    @Test
    fun `UpdateDeviceUseCase updates MAC address`() = runTest {
        val updated = sampleDevice.copy(macAddress = MacAddress("00:1B:44:11:3A:B8"))
        coEvery { iotRepository.updateDevice(deviceId, any()) } returns AppResult.Success(updated)

        val result = updateDeviceUseCase(deviceId, "00:1B:44:11:3A:B8")

        assertTrue(result is AppResult.Success)
        assertEquals(MacAddress("00:1B:44:11:3A:B8"), (result as AppResult.Success).data.macAddress)
        coVerify(exactly = 1) { iotRepository.updateDevice(deviceId, MacAddress("00:1B:44:11:3A:B8")) }
    }

    @Test
    fun `DeleteDeviceUseCase calls repository`() = runTest {
        coEvery { iotRepository.deleteDevice(deviceId) } returns AppResult.Success(Unit)

        val result = deleteDeviceUseCase(deviceId)

        assertTrue(result is AppResult.Success)
        coVerify(exactly = 1) { iotRepository.deleteDevice(deviceId) }
    }

    @Test
    fun `LinkDeviceToVehicleUseCase calls repository`() = runTest {
        coEvery { iotRepository.linkDeviceToVehicle(deviceId, branchId, vehicleId) } returns AppResult.Success(sampleRegistration)

        val result = linkDeviceToVehicleUseCase(deviceId, branchId, vehicleId)

        assertTrue(result is AppResult.Success)
        assertEquals(sampleRegistration, (result as AppResult.Success).data)
        coVerify(exactly = 1) { iotRepository.linkDeviceToVehicle(deviceId, branchId, vehicleId) }
    }

    @Test
    fun `UnlinkDeviceFromVehicleUseCase calls repository`() = runTest {
        val inactive = sampleRegistration.copy(status = RegistrationStatus.INACTIVE)
        coEvery { iotRepository.unlinkDeviceFromVehicle(registrationId) } returns AppResult.Success(inactive)

        val result = unlinkDeviceFromVehicleUseCase(registrationId)

        assertTrue(result is AppResult.Success)
        assertEquals(RegistrationStatus.INACTIVE, (result as AppResult.Success).data.status)
        coVerify(exactly = 1) { iotRepository.unlinkDeviceFromVehicle(registrationId) }
    }

    @Test
    fun `GetRegistrationsUseCase calls repository`() = runTest {
        coEvery { iotRepository.getRegistrations(branchId, RegistrationStatus.ACTIVE) } returns AppResult.Success(listOf(sampleRegistration))

        val result = getRegistrationsUseCase(branchId, RegistrationStatus.ACTIVE)

        assertTrue(result is AppResult.Success)
        assertEquals(listOf(sampleRegistration), (result as AppResult.Success).data)
        coVerify(exactly = 1) { iotRepository.getRegistrations(branchId, RegistrationStatus.ACTIVE) }
    }
}
