package com.tuxlogic.shiftiq.mobile.feature.iot.infrastructure.mapper

import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.core.model.CustomerId
import com.tuxlogic.shiftiq.mobile.core.model.VehicleId
import com.tuxlogic.shiftiq.mobile.feature.iot.infrastructure.remote.dto.BatchDtcItemDto
import com.tuxlogic.shiftiq.mobile.feature.iot.infrastructure.remote.dto.BatchSnapshotItemDto
import com.tuxlogic.shiftiq.mobile.feature.iot.infrastructure.remote.dto.DeviceRegistrationResourceDto
import com.tuxlogic.shiftiq.mobile.feature.iot.infrastructure.remote.dto.DtcAlertResourceDto
import com.tuxlogic.shiftiq.mobile.feature.iot.infrastructure.remote.dto.Obd2DeviceResourceDto
import com.tuxlogic.shiftiq.mobile.feature.iot.infrastructure.remote.dto.TelemetryBatchRequestDto
import com.tuxlogic.shiftiq.mobile.feature.iot.infrastructure.remote.dto.TelemetrySnapshotResourceDto
import com.tuxlogic.shiftiq.mobile.feature.iot.infrastructure.remote.dto.VehicleResourceDto
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.DeviceId
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.DeviceRegistration
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.DeviceStatus
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.DtcAlert
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.DtcAlertId
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.DtcSeverity
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.MacAddress
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.Obd2Device
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.PlateNumber
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.RegistrationId
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.RegistrationStatus
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.SnapshotId
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.TelemetryBatch
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.TelemetrySnapshot
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.Vehicle
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.Vin

fun VehicleResourceDto.toDomain(): Vehicle = Vehicle(
    id = VehicleId(id),
    plateNumber = PlateNumber(plateNumber),
    brand = brand,
    model = model,
    year = year,
    vin = Vin(vin),
    photoUrl = photoUrl,
    customerId = customerId?.let { CustomerId(it) }
)

fun Obd2DeviceResourceDto.toDomain(): Obd2Device = Obd2Device(
    id = DeviceId(id),
    branchId = BranchId(branchId),
    macAddress = MacAddress(macAddress),
    status = runCatching { DeviceStatus.valueOf(status.uppercase()) }.getOrDefault(DeviceStatus.AVAILABLE)
)

fun DeviceRegistrationResourceDto.toDomain(): DeviceRegistration = DeviceRegistration(
    id = RegistrationId(id),
    obd2DeviceId = DeviceId(obd2DeviceId),
    branchId = BranchId(branchId),
    vehicleId = VehicleId(vehicleId),
    status = runCatching { RegistrationStatus.valueOf(status.uppercase()) }.getOrDefault(RegistrationStatus.ACTIVE),
    createdAt = createdAt
)

fun TelemetrySnapshotResourceDto.toDomain(): TelemetrySnapshot = TelemetrySnapshot(
    id = SnapshotId(id),
    obd2DeviceRegistrationId = RegistrationId(obd2DeviceRegistrationId),
    branchId = BranchId(branchId),
    rpm = rpm,
    temperature = temperature,
    speedKmh = speedKmh,
    odometerKm = odometerKm,
    fuelLevelPercent = fuelLevelPercent,
    createdAt = createdAt
)

fun DtcAlertResourceDto.toDomain(): DtcAlert = DtcAlert(
    id = DtcAlertId(id),
    telemetrySnapshotId = SnapshotId(telemetrySnapshotId),
    branchId = BranchId(branchId),
    dtcCode = dtcCode,
    description = description,
    severity = runCatching { DtcSeverity.valueOf(severity.uppercase()) }.getOrDefault(DtcSeverity.MEDIUM),
    createdAt = createdAt
)

fun TelemetryBatch.toDto(): TelemetryBatchRequestDto = TelemetryBatchRequestDto(
    obd2DeviceId = obd2DeviceId.toString(),
    snapshots = snapshots.map { snapshot ->
        BatchSnapshotItemDto(
            rpm = snapshot.rpm,
            temperature = snapshot.temperature,
            speedKmh = snapshot.speedKmh,
            odometerKm = snapshot.odometerKm,
            fuelLevelPercent = snapshot.fuelLevelPercent,
            createdAt = snapshot.createdAt,
            dtcCodes = snapshot.dtcCodes.map { dtc ->
                BatchDtcItemDto(
                    dtcCode = dtc.dtcCode,
                    description = dtc.description,
                    severity = dtc.severity.name
                )
            }
        )
    }
)
