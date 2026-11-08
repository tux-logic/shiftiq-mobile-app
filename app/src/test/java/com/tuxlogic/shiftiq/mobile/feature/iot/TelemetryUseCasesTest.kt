package com.tuxlogic.shiftiq.mobile.feature.iot

import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.core.model.VehicleId
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.DeviceId
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.DtcAlert
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.DtcAlertId
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.DtcSeverity
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.RegistrationId
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.SnapshotId
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.TelemetryBatch
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.TelemetryBatchItem
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.TelemetrySnapshot
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.repository.IotRepository
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.usecase.telemetry.GetDeviceTelemetryHistoryUseCase
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.usecase.telemetry.GetLatestTelemetrySnapshotUseCase
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.usecase.telemetry.GetVehicleDtcAlertsUseCase
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.usecase.telemetry.GetVehicleTelemetryHistoryUseCase
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.usecase.telemetry.IngestTelemetryBatchUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.UUID

class TelemetryUseCasesTest {

    private val iotRepository: IotRepository = mockk()

    private lateinit var getLatestTelemetrySnapshotUseCase: GetLatestTelemetrySnapshotUseCase
    private lateinit var getDeviceTelemetryHistoryUseCase: GetDeviceTelemetryHistoryUseCase
    private lateinit var getVehicleTelemetryHistoryUseCase: GetVehicleTelemetryHistoryUseCase
    private lateinit var getVehicleDtcAlertsUseCase: GetVehicleDtcAlertsUseCase
    private lateinit var ingestTelemetryBatchUseCase: IngestTelemetryBatchUseCase

    private val deviceId = DeviceId(UUID.randomUUID())
    private val vehicleId = VehicleId(UUID.randomUUID())
    private val branchId = BranchId(UUID.randomUUID())
    private val snapshotId = SnapshotId(UUID.randomUUID())
    private val registrationId = RegistrationId(UUID.randomUUID())
    private val dtcAlertId = DtcAlertId(UUID.randomUUID())

    private val sampleSnapshot = TelemetrySnapshot(
        id = snapshotId,
        obd2DeviceRegistrationId = registrationId,
        branchId = branchId,
        rpm = 2450,
        temperature = 89,
        speedKmh = 62.5,
        odometerKm = 24890.0,
        fuelLevelPercent = 74.0,
        createdAt = "2026-10-15T10:30:00Z"
    )

    private val sampleDtcAlert = DtcAlert(
        id = dtcAlertId,
        telemetrySnapshotId = snapshotId,
        branchId = branchId,
        dtcCode = "P0300",
        description = "Random Misfire",
        severity = DtcSeverity.HIGH,
        createdAt = "2026-10-15T10:30:00Z"
    )

    @Before
    fun setUp() {
        getLatestTelemetrySnapshotUseCase = GetLatestTelemetrySnapshotUseCase(iotRepository)
        getDeviceTelemetryHistoryUseCase = GetDeviceTelemetryHistoryUseCase(iotRepository)
        getVehicleTelemetryHistoryUseCase = GetVehicleTelemetryHistoryUseCase(iotRepository)
        getVehicleDtcAlertsUseCase = GetVehicleDtcAlertsUseCase(iotRepository)
        ingestTelemetryBatchUseCase = IngestTelemetryBatchUseCase(iotRepository)
    }

    @Test
    fun `GetLatestTelemetrySnapshotUseCase queries repository`() = runTest {
        coEvery { iotRepository.getLatestTelemetrySnapshot(deviceId) } returns AppResult.Success(sampleSnapshot)

        val result = getLatestTelemetrySnapshotUseCase(deviceId)

        assertTrue(result is AppResult.Success)
        assertEquals(sampleSnapshot, (result as AppResult.Success).data)
        coVerify(exactly = 1) { iotRepository.getLatestTelemetrySnapshot(deviceId) }
    }

    @Test
    fun `GetDeviceTelemetryHistoryUseCase queries device history`() = runTest {
        coEvery { iotRepository.getDeviceTelemetryHistory(deviceId, 0, 20) } returns AppResult.Success(listOf(sampleSnapshot))

        val result = getDeviceTelemetryHistoryUseCase(deviceId, 0, 20)

        assertTrue(result is AppResult.Success)
        assertEquals(listOf(sampleSnapshot), (result as AppResult.Success).data)
        coVerify(exactly = 1) { iotRepository.getDeviceTelemetryHistory(deviceId, 0, 20) }
    }

    @Test
    fun `GetVehicleTelemetryHistoryUseCase queries vehicle history`() = runTest {
        coEvery { iotRepository.getVehicleTelemetryHistory(vehicleId, 0, 20) } returns AppResult.Success(listOf(sampleSnapshot))

        val result = getVehicleTelemetryHistoryUseCase(vehicleId, 0, 20)

        assertTrue(result is AppResult.Success)
        assertEquals(listOf(sampleSnapshot), (result as AppResult.Success).data)
        coVerify(exactly = 1) { iotRepository.getVehicleTelemetryHistory(vehicleId, 0, 20) }
    }

    @Test
    fun `GetVehicleDtcAlertsUseCase queries DTC alerts`() = runTest {
        coEvery { iotRepository.getVehicleDtcAlerts(vehicleId, 0, 20) } returns AppResult.Success(listOf(sampleDtcAlert))

        val result = getVehicleDtcAlertsUseCase(vehicleId, 0, 20)

        assertTrue(result is AppResult.Success)
        assertEquals(listOf(sampleDtcAlert), (result as AppResult.Success).data)
        coVerify(exactly = 1) { iotRepository.getVehicleDtcAlerts(vehicleId, 0, 20) }
    }

    @Test
    fun `IngestTelemetryBatchUseCase with empty snapshots returns validation failure`() = runTest {
        val batch = TelemetryBatch(obd2DeviceId = deviceId, snapshots = emptyList())

        val result = ingestTelemetryBatchUseCase(batch)

        assertTrue(result is AppResult.Failure)
        assertTrue((result as AppResult.Failure).error is AppError.Validation)
        coVerify(exactly = 0) { iotRepository.ingestTelemetryBatch(any()) }
    }

    @Test
    fun `IngestTelemetryBatchUseCase with valid snapshots calls repository`() = runTest {
        val batch = TelemetryBatch(
            obd2DeviceId = deviceId,
            snapshots = listOf(
                TelemetryBatchItem(
                    rpm = 2200,
                    temperature = 91,
                    speedKmh = 55.4,
                    odometerKm = 25100.0,
                    fuelLevelPercent = 68.5,
                    createdAt = "2026-10-15T10:30:00Z"
                )
            )
        )
        coEvery { iotRepository.ingestTelemetryBatch(batch) } returns AppResult.Success(Unit)

        val result = ingestTelemetryBatchUseCase(batch)

        assertTrue(result is AppResult.Success)
        coVerify(exactly = 1) { iotRepository.ingestTelemetryBatch(batch) }
    }
}
