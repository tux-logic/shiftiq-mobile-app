package com.tuxlogic.shiftiq.mobile.feature.iot

import com.tuxlogic.shiftiq.mobile.feature.iot.data.mapper.toDomain
import com.tuxlogic.shiftiq.mobile.feature.iot.data.mapper.toDto
import com.tuxlogic.shiftiq.mobile.feature.iot.data.remote.dto.DeviceRegistrationResourceDto
import com.tuxlogic.shiftiq.mobile.feature.iot.data.remote.dto.DtcAlertResourceDto
import com.tuxlogic.shiftiq.mobile.feature.iot.data.remote.dto.Obd2DeviceResourceDto
import com.tuxlogic.shiftiq.mobile.feature.iot.data.remote.dto.TelemetrySnapshotResourceDto
import com.tuxlogic.shiftiq.mobile.feature.iot.data.remote.dto.VehicleResourceDto
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.BatchDtcItem
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.DeviceId
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.DeviceStatus
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.DtcSeverity
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.RegistrationStatus
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.TelemetryBatch
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.TelemetryBatchItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.util.UUID

class IotMappersTest {

    @Test
    fun `VehicleResourceDto maps correctly to Domain`() {
        val vehUuid = UUID.randomUUID().toString()
        val custUuid = UUID.randomUUID().toString()

        val dto = VehicleResourceDto(
            id = vehUuid,
            plateNumber = "ABC-123",
            brand = "Toyota",
            model = "Corolla Cross",
            year = 2023,
            vin = "9BRBL3HE2P0123456",
            photoUrl = "https://example.com/photo.jpg",
            customerId = custUuid
        )
        val domain = dto.toDomain()

        assertEquals(vehUuid, domain.id.toString())
        assertEquals("ABC-123", domain.plateNumber.toString())
        assertEquals("Toyota", domain.brand)
        assertEquals("Corolla Cross", domain.model)
        assertEquals(2023, domain.year)
        assertEquals("9BRBL3HE2P0123456", domain.vin.toString())
        assertEquals("https://example.com/photo.jpg", domain.photoUrl)
        assertEquals(custUuid, domain.customerId?.toString())
    }

    @Test
    fun `Obd2DeviceResourceDto maps correctly to Domain`() {
        val devUuid = UUID.randomUUID().toString()
        val branchUuid = UUID.randomUUID().toString()

        val dto = Obd2DeviceResourceDto(
            id = devUuid,
            branchId = branchUuid,
            macAddress = "00:1B:44:11:3A:B7",
            status = "AVAILABLE"
        )
        val domain = dto.toDomain()

        assertEquals(devUuid, domain.id.toString())
        assertEquals(branchUuid, domain.branchId.toString())
        assertEquals("00:1B:44:11:3A:B7", domain.macAddress.toString())
        assertEquals(DeviceStatus.AVAILABLE, domain.status)
    }

    @Test
    fun `DeviceRegistrationResourceDto maps correctly to Domain`() {
        val regUuid = UUID.randomUUID().toString()
        val devUuid = UUID.randomUUID().toString()
        val branchUuid = UUID.randomUUID().toString()
        val vehUuid = UUID.randomUUID().toString()

        val dto = DeviceRegistrationResourceDto(
            id = regUuid,
            obd2DeviceId = devUuid,
            branchId = branchUuid,
            vehicleId = vehUuid,
            status = "ACTIVE",
            createdAt = "2026-10-15T10:30:00Z"
        )
        val domain = dto.toDomain()

        assertEquals(regUuid, domain.id.toString())
        assertEquals(devUuid, domain.obd2DeviceId.toString())
        assertEquals(branchUuid, domain.branchId.toString())
        assertEquals(vehUuid, domain.vehicleId.toString())
        assertEquals(RegistrationStatus.ACTIVE, domain.status)
        assertEquals("2026-10-15T10:30:00Z", domain.createdAt)
    }

    @Test
    fun `TelemetrySnapshotResourceDto maps correctly to Domain`() {
        val snapUuid = UUID.randomUUID().toString()
        val regUuid = UUID.randomUUID().toString()
        val branchUuid = UUID.randomUUID().toString()

        val dto = TelemetrySnapshotResourceDto(
            id = snapUuid,
            obd2DeviceRegistrationId = regUuid,
            branchId = branchUuid,
            rpm = 2500,
            temperature = 90,
            speedKmh = 65.0,
            odometerKm = 25000.0,
            fuelLevelPercent = 75.0,
            createdAt = "2026-10-15T10:30:00Z"
        )
        val domain = dto.toDomain()

        assertEquals(snapUuid, domain.id.toString())
        assertEquals(regUuid, domain.obd2DeviceRegistrationId.toString())
        assertEquals(branchUuid, domain.branchId.toString())
        assertEquals(2500, domain.rpm)
        assertEquals(90, domain.temperature)
        assertEquals(65.0, domain.speedKmh, 0.001)
        assertEquals(25000.0, domain.odometerKm, 0.001)
        assertEquals(75.0, domain.fuelLevelPercent, 0.001)
        assertEquals("2026-10-15T10:30:00Z", domain.createdAt)
    }

    @Test
    fun `DtcAlertResourceDto maps correctly to Domain`() {
        val dtcUuid = UUID.randomUUID().toString()
        val snapUuid = UUID.randomUUID().toString()
        val branchUuid = UUID.randomUUID().toString()

        val dto = DtcAlertResourceDto(
            id = dtcUuid,
            telemetrySnapshotId = snapUuid,
            branchId = branchUuid,
            dtcCode = "P0300",
            description = "Multiple Cylinder Misfire",
            severity = "HIGH",
            createdAt = "2026-10-15T10:30:00Z"
        )
        val domain = dto.toDomain()

        assertEquals(dtcUuid, domain.id.toString())
        assertEquals(snapUuid, domain.telemetrySnapshotId.toString())
        assertEquals(branchUuid, domain.branchId.toString())
        assertEquals("P0300", domain.dtcCode)
        assertEquals("Multiple Cylinder Misfire", domain.description)
        assertEquals(DtcSeverity.HIGH, domain.severity)
        assertEquals("2026-10-15T10:30:00Z", domain.createdAt)
    }

    @Test
    fun `TelemetryBatch maps correctly to DTO`() {
        val devId = DeviceId(UUID.randomUUID())
        val batch = TelemetryBatch(
            obd2DeviceId = devId,
            snapshots = listOf(
                TelemetryBatchItem(
                    rpm = 2000,
                    temperature = 88,
                    speedKmh = 50.0,
                    odometerKm = 10000.0,
                    fuelLevelPercent = 80.0,
                    createdAt = "2026-10-15T10:30:00Z",
                    dtcCodes = listOf(
                        BatchDtcItem("P0420", "Catalyst Efficiency", DtcSeverity.MEDIUM)
                    )
                )
            )
        )
        val dto = batch.toDto()

        assertEquals(devId.toString(), dto.obd2DeviceId)
        assertEquals(1, dto.snapshots.size)
        assertEquals(2000, dto.snapshots[0].rpm)
        assertEquals(1, dto.snapshots[0].dtcCodes?.size)
        assertEquals("P0420", dto.snapshots[0].dtcCodes?.get(0)?.dtcCode)
        assertEquals("MEDIUM", dto.snapshots[0].dtcCodes?.get(0)?.severity)
    }
}
