package com.tuxlogic.shiftiq.mobile.feature.iot

import com.tuxlogic.shiftiq.mobile.core.common.dispatchers.DispatcherProvider
import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.datastore.SessionDataStore
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.core.model.CustomerId
import com.tuxlogic.shiftiq.mobile.core.model.VehicleId
import com.tuxlogic.shiftiq.mobile.feature.iot.infrastructure.remote.api.IotApiService
import com.tuxlogic.shiftiq.mobile.feature.iot.infrastructure.remote.dto.DeviceRegistrationResourceDto
import com.tuxlogic.shiftiq.mobile.feature.iot.infrastructure.remote.dto.DtcAlertResourceDto
import com.tuxlogic.shiftiq.mobile.feature.iot.infrastructure.remote.dto.Obd2DeviceResourceDto
import com.tuxlogic.shiftiq.mobile.feature.iot.infrastructure.remote.dto.TelemetryBatchResponseDto
import com.tuxlogic.shiftiq.mobile.feature.iot.infrastructure.remote.dto.TelemetrySnapshotResourceDto
import com.tuxlogic.shiftiq.mobile.feature.iot.infrastructure.remote.dto.VehicleResourceDto
import com.tuxlogic.shiftiq.mobile.feature.iot.infrastructure.repository.IotRepositoryImpl
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.BatchDtcItem
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.DeviceId
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.DeviceStatus
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.DtcSeverity
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.MacAddress
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.PlateNumber
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.RegistrationId
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.RegistrationStatus
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.SnapshotId
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.TelemetryBatch
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.TelemetryBatchItem
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.Vin
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.util.UUID

class IotRepositoryTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = object : DispatcherProvider {
        override val main: CoroutineDispatcher = testDispatcher
        override val io: CoroutineDispatcher = testDispatcher
        override val default: CoroutineDispatcher = testDispatcher
        override val unconfined: CoroutineDispatcher = testDispatcher
    }

    private val iotApiService: IotApiService = mockk()
    private val sessionDataStore: SessionDataStore = mockk()

    private lateinit var repository: IotRepositoryImpl

    private val branchUuid = UUID.randomUUID()
    private val branchId = BranchId(branchUuid)
    private val vehicleUuid = UUID.randomUUID()
    private val vehicleId = VehicleId(vehicleUuid)
    private val customerUuid = UUID.randomUUID()
    private val customerId = CustomerId(customerUuid)
    private val deviceUuid = UUID.randomUUID()
    private val deviceId = DeviceId(deviceUuid)
    private val registrationUuid = UUID.randomUUID()
    private val registrationId = RegistrationId(registrationUuid)
    private val snapshotUuid = UUID.randomUUID()
    private val snapshotId = SnapshotId(snapshotUuid)

    private val sampleVehicleDto = VehicleResourceDto(
        id = vehicleUuid.toString(),
        plateNumber = "ABC-123",
        brand = "Toyota",
        model = "Corolla",
        year = 2021,
        vin = "1HGCR2F83HA000000",
        photoUrl = "https://example.com/car.jpg",
        customerId = customerUuid.toString()
    )

    private val sampleDeviceDto = Obd2DeviceResourceDto(
        id = deviceUuid.toString(),
        branchId = branchUuid.toString(),
        macAddress = "AA:BB:CC:11:22:33",
        status = "AVAILABLE"
    )

    private val sampleRegistrationDto = DeviceRegistrationResourceDto(
        id = registrationUuid.toString(),
        obd2DeviceId = deviceUuid.toString(),
        branchId = branchUuid.toString(),
        vehicleId = vehicleUuid.toString(),
        status = "ACTIVE",
        createdAt = "2026-11-08T00:00:00Z"
    )

    private val sampleSnapshotDto = TelemetrySnapshotResourceDto(
        id = snapshotUuid.toString(),
        obd2DeviceRegistrationId = registrationUuid.toString(),
        branchId = branchUuid.toString(),
        rpm = 2500,
        temperature = 88,
        speedKmh = 60.0,
        odometerKm = 45000.0,
        fuelLevelPercent = 75.0,
        createdAt = "2026-11-08T00:00:00Z"
    )

    private val sampleDtcAlertDto = DtcAlertResourceDto(
        id = UUID.randomUUID().toString(),
        telemetrySnapshotId = snapshotUuid.toString(),
        branchId = branchUuid.toString(),
        dtcCode = "P0300",
        description = "Random Misfire Detected",
        severity = "HIGH",
        createdAt = "2026-11-08T00:00:00Z"
    )

    @Before
    fun setUp() {
        repository = IotRepositoryImpl(iotApiService, sessionDataStore, dispatchers)
    }

    @Test
    fun `registerVehicle sends request and returns mapped Vehicle`() = runTest(testDispatcher) {
        coEvery { iotApiService.registerVehicle(any()) } returns Response.success(sampleVehicleDto)

        val result = repository.registerVehicle(
            plateNumber = PlateNumber("ABC-123"),
            brand = "Toyota",
            model = "Corolla",
            year = 2021,
            vin = Vin("1HGCR2F83HA000000"),
            photoUrl = "https://example.com/car.jpg",
            customerId = customerId
        )

        assertTrue(result is AppResult.Success)
        val vehicle = (result as AppResult.Success).data
        assertEquals(vehicleId, vehicle.id)
        assertEquals("Toyota", vehicle.brand)
        assertEquals(PlateNumber("ABC-123"), vehicle.plateNumber)
        coVerify(exactly = 1) { iotApiService.registerVehicle(match {
            it.plateNumber == "ABC-123" && it.customerId == customerUuid.toString()
        }) }
    }

    @Test
    fun `getVehicleById returns mapped Vehicle`() = runTest(testDispatcher) {
        coEvery { iotApiService.getVehicleById(vehicleUuid.toString()) } returns Response.success(sampleVehicleDto)

        val result = repository.getVehicleById(vehicleId)

        assertTrue(result is AppResult.Success)
        assertEquals(vehicleId, (result as AppResult.Success).data.id)
        coVerify(exactly = 1) { iotApiService.getVehicleById(vehicleUuid.toString()) }
    }

    @Test
    fun `getVehiclesByBranch with explicit branchId returns vehicles list`() = runTest(testDispatcher) {
        coEvery { iotApiService.getVehicles(branchUuid.toString()) } returns Response.success(listOf(sampleVehicleDto))

        val result = repository.getVehiclesByBranch(branchId)

        assertTrue(result is AppResult.Success)
        val list = (result as AppResult.Success).data
        assertEquals(1, list.size)
        assertEquals(vehicleId, list[0].id)
        coVerify(exactly = 1) { iotApiService.getVehicles(branchUuid.toString()) }
    }

    @Test
    fun `getVehiclesByBranch fallback to sessionDataStore when branchId is null`() = runTest(testDispatcher) {
        coEvery { sessionDataStore.activeBranchId } returns flowOf(branchId)
        coEvery { iotApiService.getVehicles(branchUuid.toString()) } returns Response.success(listOf(sampleVehicleDto))

        val result = repository.getVehiclesByBranch(null)

        assertTrue(result is AppResult.Success)
        assertEquals(1, (result as AppResult.Success).data.size)
        coVerify(exactly = 1) { iotApiService.getVehicles(branchUuid.toString()) }
    }

    @Test
    fun `getVehiclesByBranch returns failure when no active branch in session`() = runTest(testDispatcher) {
        coEvery { sessionDataStore.activeBranchId } returns flowOf(null)

        val result = repository.getVehiclesByBranch(null)

        assertTrue(result is AppResult.Failure)
        assertTrue((result as AppResult.Failure).error is AppError.Validation)
        coVerify(exactly = 0) { iotApiService.getVehicles(any()) }
    }

    @Test
    fun `getCustomerVehicles returns customer vehicles list`() = runTest(testDispatcher) {
        coEvery { iotApiService.getCustomerVehicles(customerUuid.toString()) } returns Response.success(listOf(sampleVehicleDto))

        val result = repository.getCustomerVehicles(customerId)

        assertTrue(result is AppResult.Success)
        assertEquals(1, (result as AppResult.Success).data.size)
        coVerify(exactly = 1) { iotApiService.getCustomerVehicles(customerUuid.toString()) }
    }

    @Test
    fun `updateVehicle sends request and returns updated Vehicle`() = runTest(testDispatcher) {
        val updatedDto = sampleVehicleDto.copy(model = "Corolla Cross", year = 2023)
        coEvery { iotApiService.updateVehicle(vehicleUuid.toString(), any()) } returns Response.success(updatedDto)

        val result = repository.updateVehicle(
            id = vehicleId,
            plateNumber = PlateNumber("ABC-123"),
            brand = "Toyota",
            model = "Corolla Cross",
            year = 2023,
            vin = Vin("1HGCR2F83HA000000")
        )

        assertTrue(result is AppResult.Success)
        val vehicle = (result as AppResult.Success).data
        assertEquals("Corolla Cross", vehicle.model)
        assertEquals(2023, vehicle.year)
        coVerify(exactly = 1) { iotApiService.updateVehicle(vehicleUuid.toString(), match { it.model == "Corolla Cross" }) }
    }

    @Test
    fun `deleteVehicle calls delete endpoint and returns Success Unit`() = runTest(testDispatcher) {
        coEvery { iotApiService.deleteVehicle(vehicleUuid.toString()) } returns Response.success(204, Unit)

        val result = repository.deleteVehicle(vehicleId)

        assertTrue(result is AppResult.Success)
        coVerify(exactly = 1) { iotApiService.deleteVehicle(vehicleUuid.toString()) }
    }

    @Test
    fun `registerDevice sends request and returns mapped Obd2Device`() = runTest(testDispatcher) {
        coEvery { iotApiService.registerDevice(any()) } returns Response.success(sampleDeviceDto)

        val result = repository.registerDevice(branchId, MacAddress("AA:BB:CC:11:22:33"))

        assertTrue(result is AppResult.Success)
        val device = (result as AppResult.Success).data
        assertEquals(deviceId, device.id)
        assertEquals(DeviceStatus.AVAILABLE, device.status)
        assertEquals(MacAddress("AA:BB:CC:11:22:33"), device.macAddress)
        coVerify(exactly = 1) { iotApiService.registerDevice(match {
            it.branchId == branchUuid.toString() && it.macAddress == "AA:BB:CC:11:22:33"
        }) }
    }

    @Test
    fun `getDevices with explicit branchId returns devices list`() = runTest(testDispatcher) {
        coEvery { iotApiService.getDevices(branchUuid.toString(), "AVAILABLE") } returns Response.success(listOf(sampleDeviceDto))

        val result = repository.getDevices(branchId, "AVAILABLE")

        assertTrue(result is AppResult.Success)
        assertEquals(1, (result as AppResult.Success).data.size)
        coVerify(exactly = 1) { iotApiService.getDevices(branchUuid.toString(), "AVAILABLE") }
    }

    @Test
    fun `getDevices fallback to sessionDataStore when branchId is null`() = runTest(testDispatcher) {
        coEvery { sessionDataStore.activeBranchId } returns flowOf(branchId)
        coEvery { iotApiService.getDevices(branchUuid.toString(), null) } returns Response.success(listOf(sampleDeviceDto))

        val result = repository.getDevices(null, null)

        assertTrue(result is AppResult.Success)
        assertEquals(1, (result as AppResult.Success).data.size)
        coVerify(exactly = 1) { iotApiService.getDevices(branchUuid.toString(), null) }
    }

    @Test
    fun `getDevices returns failure when branchId is null and no active branch in session`() = runTest(testDispatcher) {
        coEvery { sessionDataStore.activeBranchId } returns flowOf(null)

        val result = repository.getDevices(null, null)

        assertTrue(result is AppResult.Failure)
        coVerify(exactly = 0) { iotApiService.getDevices(any(), any()) }
    }

    @Test
    fun `getDeviceById returns mapped Obd2Device`() = runTest(testDispatcher) {
        coEvery { iotApiService.getDeviceById(deviceUuid.toString()) } returns Response.success(sampleDeviceDto)

        val result = repository.getDeviceById(deviceId)

        assertTrue(result is AppResult.Success)
        assertEquals(deviceId, (result as AppResult.Success).data.id)
        coVerify(exactly = 1) { iotApiService.getDeviceById(deviceUuid.toString()) }
    }

    @Test
    fun `updateDevice sends update request and returns updated Obd2Device`() = runTest(testDispatcher) {
        val updatedDto = sampleDeviceDto.copy(macAddress = "11:22:33:44:55:66")
        coEvery { iotApiService.updateDevice(deviceUuid.toString(), any()) } returns Response.success(updatedDto)

        val result = repository.updateDevice(deviceId, MacAddress("11:22:33:44:55:66"))

        assertTrue(result is AppResult.Success)
        assertEquals(MacAddress("11:22:33:44:55:66"), (result as AppResult.Success).data.macAddress)
        coVerify(exactly = 1) { iotApiService.updateDevice(deviceUuid.toString(), match { it.macAddress == "11:22:33:44:55:66" }) }
    }

    @Test
    fun `deleteDevice calls delete endpoint and returns Success Unit`() = runTest(testDispatcher) {
        coEvery { iotApiService.deleteDevice(deviceUuid.toString()) } returns Response.success(204, Unit)

        val result = repository.deleteDevice(deviceId)

        assertTrue(result is AppResult.Success)
        coVerify(exactly = 1) { iotApiService.deleteDevice(deviceUuid.toString()) }
    }

    @Test
    fun `linkDeviceToVehicle sends link request and returns DeviceRegistration`() = runTest(testDispatcher) {
        coEvery { iotApiService.linkDevice(any()) } returns Response.success(sampleRegistrationDto)

        val result = repository.linkDeviceToVehicle(deviceId, branchId, vehicleId)

        assertTrue(result is AppResult.Success)
        val registration = (result as AppResult.Success).data
        assertEquals(registrationId, registration.id)
        assertEquals(RegistrationStatus.ACTIVE, registration.status)
        coVerify(exactly = 1) { iotApiService.linkDevice(match {
            it.obd2DeviceId == deviceUuid.toString() &&
            it.branchId == branchUuid.toString() &&
            it.vehicleId == vehicleUuid.toString()
        }) }
    }

    @Test
    fun `unlinkDeviceFromVehicle sends PATCH INACTIVE request and returns updated registration`() = runTest(testDispatcher) {
        val inactiveDto = sampleRegistrationDto.copy(status = "INACTIVE")
        coEvery { iotApiService.updateRegistrationStatus(registrationUuid.toString(), any()) } returns Response.success(inactiveDto)

        val result = repository.unlinkDeviceFromVehicle(registrationId)

        assertTrue(result is AppResult.Success)
        assertEquals(RegistrationStatus.INACTIVE, (result as AppResult.Success).data.status)
        coVerify(exactly = 1) { iotApiService.updateRegistrationStatus(registrationUuid.toString(), match {
            it.status == "INACTIVE"
        }) }
    }

    @Test
    fun `getRegistrations with explicit branchId returns registrations list`() = runTest(testDispatcher) {
        coEvery { iotApiService.getRegistrations(branchUuid.toString(), "ACTIVE") } returns Response.success(listOf(sampleRegistrationDto))

        val result = repository.getRegistrations(branchId, RegistrationStatus.ACTIVE)

        assertTrue(result is AppResult.Success)
        assertEquals(1, (result as AppResult.Success).data.size)
        coVerify(exactly = 1) { iotApiService.getRegistrations(branchUuid.toString(), "ACTIVE") }
    }

    @Test
    fun `getRegistrations fallback to sessionDataStore when branchId is null`() = runTest(testDispatcher) {
        coEvery { sessionDataStore.activeBranchId } returns flowOf(branchId)
        coEvery { iotApiService.getRegistrations(branchUuid.toString(), "ACTIVE") } returns Response.success(listOf(sampleRegistrationDto))

        val result = repository.getRegistrations(null, RegistrationStatus.ACTIVE)

        assertTrue(result is AppResult.Success)
        assertEquals(1, (result as AppResult.Success).data.size)
        coVerify(exactly = 1) { iotApiService.getRegistrations(branchUuid.toString(), "ACTIVE") }
    }

    @Test
    fun `getLatestTelemetrySnapshot returns latest snapshot for device`() = runTest(testDispatcher) {
        coEvery { iotApiService.getLatestTelemetrySnapshot(deviceUuid.toString()) } returns Response.success(sampleSnapshotDto)

        val result = repository.getLatestTelemetrySnapshot(deviceId)

        assertTrue(result is AppResult.Success)
        val snapshot = (result as AppResult.Success).data
        assertEquals(snapshotId, snapshot.id)
        assertEquals(2500, snapshot.rpm)
        assertEquals(88, snapshot.temperature)
        coVerify(exactly = 1) { iotApiService.getLatestTelemetrySnapshot(deviceUuid.toString()) }
    }

    @Test
    fun `getDeviceTelemetryHistory returns list of snapshots`() = runTest(testDispatcher) {
        coEvery { iotApiService.getDeviceTelemetrySnapshots(deviceUuid.toString(), 0, 20) } returns Response.success(listOf(sampleSnapshotDto))

        val result = repository.getDeviceTelemetryHistory(deviceId, 0, 20)

        assertTrue(result is AppResult.Success)
        assertEquals(1, (result as AppResult.Success).data.size)
        coVerify(exactly = 1) { iotApiService.getDeviceTelemetrySnapshots(deviceUuid.toString(), 0, 20) }
    }

    @Test
    fun `getVehicleTelemetryHistory returns list of snapshots`() = runTest(testDispatcher) {
        coEvery { iotApiService.getVehicleTelemetrySnapshots(vehicleUuid.toString(), 0, 20) } returns Response.success(listOf(sampleSnapshotDto))

        val result = repository.getVehicleTelemetryHistory(vehicleId, 0, 20)

        assertTrue(result is AppResult.Success)
        assertEquals(1, (result as AppResult.Success).data.size)
        coVerify(exactly = 1) { iotApiService.getVehicleTelemetrySnapshots(vehicleUuid.toString(), 0, 20) }
    }

    @Test
    fun `getRegistrationTelemetryHistory returns list of snapshots`() = runTest(testDispatcher) {
        coEvery { iotApiService.getRegistrationTelemetrySnapshots(registrationUuid.toString(), 0, 20) } returns Response.success(listOf(sampleSnapshotDto))

        val result = repository.getRegistrationTelemetryHistory(registrationId, 0, 20)

        assertTrue(result is AppResult.Success)
        assertEquals(1, (result as AppResult.Success).data.size)
        coVerify(exactly = 1) { iotApiService.getRegistrationTelemetrySnapshots(registrationUuid.toString(), 0, 20) }
    }

    @Test
    fun `getVehicleDtcAlerts returns list of DTC alerts`() = runTest(testDispatcher) {
        coEvery { iotApiService.getVehicleDtcAlerts(vehicleUuid.toString(), 0, 20) } returns Response.success(listOf(sampleDtcAlertDto))

        val result = repository.getVehicleDtcAlerts(vehicleId, 0, 20)

        assertTrue(result is AppResult.Success)
        val alerts = (result as AppResult.Success).data
        assertEquals(1, alerts.size)
        assertEquals("P0300", alerts[0].dtcCode)
        assertEquals(DtcSeverity.HIGH, alerts[0].severity)
        coVerify(exactly = 1) { iotApiService.getVehicleDtcAlerts(vehicleUuid.toString(), 0, 20) }
    }

    @Test
    fun `getRegistrationDtcAlerts returns list of DTC alerts`() = runTest(testDispatcher) {
        coEvery { iotApiService.getRegistrationDtcAlerts(registrationUuid.toString(), 0, 20) } returns Response.success(listOf(sampleDtcAlertDto))

        val result = repository.getRegistrationDtcAlerts(registrationId, 0, 20)

        assertTrue(result is AppResult.Success)
        assertEquals(1, (result as AppResult.Success).data.size)
        coVerify(exactly = 1) { iotApiService.getRegistrationDtcAlerts(registrationUuid.toString(), 0, 20) }
    }

    @Test
    fun `ingestTelemetryBatch sends batch request and returns Success Unit`() = runTest(testDispatcher) {
        val responseDto = TelemetryBatchResponseDto(
            message = "Processed"
        )
        coEvery { iotApiService.ingestTelemetryBatch(any()) } returns Response.success(responseDto)

        val batch = TelemetryBatch(
            obd2DeviceId = deviceId,
            snapshots = listOf(
                TelemetryBatchItem(
                    rpm = 2500,
                    temperature = 88,
                    speedKmh = 60.0,
                    odometerKm = 45000.0,
                    fuelLevelPercent = 75.0,
                    createdAt = "2026-11-08T00:00:00Z",
                    dtcCodes = listOf(
                        BatchDtcItem(
                            dtcCode = "P0300",
                            description = "Random Misfire Detected",
                            severity = DtcSeverity.HIGH
                        )
                    )
                )
            )
        )

        val result = repository.ingestTelemetryBatch(batch)

        assertTrue(result is AppResult.Success)
        coVerify(exactly = 1) { iotApiService.ingestTelemetryBatch(match {
            it.obd2DeviceId == deviceUuid.toString() && it.snapshots.size == 1
        }) }
    }

    @Test
    fun `server error 404 returns AppResult Failure with NotFound error`() = runTest(testDispatcher) {
        val errorResponse = Response.error<VehicleResourceDto>(
            404,
            "{\"code\":\"NOT_FOUND\",\"message\":\"Vehicle not found\"}".toResponseBody("application/json".toMediaType())
        )
        coEvery { iotApiService.getVehicleById(vehicleUuid.toString()) } returns errorResponse

        val result = repository.getVehicleById(vehicleId)

        assertTrue(result is AppResult.Failure)
        assertTrue((result as AppResult.Failure).error is AppError.NotFound)
    }
}
