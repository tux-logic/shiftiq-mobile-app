package com.tuxlogic.shiftiq.mobile.feature.iot.data.remote.dto

import com.google.gson.annotations.SerializedName

// --- Vehicle DTOs ---

data class RegisterVehicleRequestDto(
    @SerializedName("plateNumber") val plateNumber: String,
    @SerializedName("brand") val brand: String,
    @SerializedName("model") val model: String,
    @SerializedName("year") val year: Int,
    @SerializedName("vin") val vin: String,
    @SerializedName("photoUrl") val photoUrl: String? = null,
    @SerializedName("customerId") val customerId: String? = null
)

data class UpdateVehicleRequestDto(
    @SerializedName("plateNumber") val plateNumber: String,
    @SerializedName("brand") val brand: String,
    @SerializedName("model") val model: String,
    @SerializedName("year") val year: Int,
    @SerializedName("vin") val vin: String,
    @SerializedName("photoUrl") val photoUrl: String? = null
)

data class VehicleResourceDto(
    @SerializedName("id") val id: String,
    @SerializedName("plateNumber") val plateNumber: String,
    @SerializedName("brand") val brand: String,
    @SerializedName("model") val model: String,
    @SerializedName("year") val year: Int,
    @SerializedName("vin") val vin: String,
    @SerializedName("photoUrl") val photoUrl: String? = null,
    @SerializedName("customerId") val customerId: String? = null
)

// --- Device DTOs ---

data class RegisterDeviceRequestDto(
    @SerializedName("branchId") val branchId: String,
    @SerializedName("macAddress") val macAddress: String
)

data class UpdateDeviceRequestDto(
    @SerializedName("macAddress") val macAddress: String
)

data class Obd2DeviceResourceDto(
    @SerializedName("id") val id: String,
    @SerializedName("branchId") val branchId: String,
    @SerializedName("macAddress") val macAddress: String,
    @SerializedName("status") val status: String
)

// --- Device Registration DTOs ---

data class LinkDeviceRequestDto(
    @SerializedName("obd2DeviceId") val obd2DeviceId: String,
    @SerializedName("branchId") val branchId: String,
    @SerializedName("vehicleId") val vehicleId: String
)

data class UpdateRegistrationStatusRequestDto(
    @SerializedName("status") val status: String
)

data class DeviceRegistrationResourceDto(
    @SerializedName("id") val id: String,
    @SerializedName("obd2DeviceId") val obd2DeviceId: String,
    @SerializedName("branchId") val branchId: String,
    @SerializedName("vehicleId") val vehicleId: String,
    @SerializedName("status") val status: String,
    @SerializedName("createdAt") val createdAt: String? = null
)

// --- Telemetry & DTC DTOs ---

data class TelemetrySnapshotResourceDto(
    @SerializedName("id") val id: String,
    @SerializedName("obd2DeviceRegistrationId") val obd2DeviceRegistrationId: String,
    @SerializedName("branchId") val branchId: String,
    @SerializedName("rpm") val rpm: Int,
    @SerializedName("temperature") val temperature: Int,
    @SerializedName("speedKmh") val speedKmh: Double,
    @SerializedName("odometerKm") val odometerKm: Double,
    @SerializedName("fuelLevelPercent") val fuelLevelPercent: Double,
    @SerializedName("createdAt") val createdAt: String
)

data class DtcAlertResourceDto(
    @SerializedName("id") val id: String,
    @SerializedName("telemetrySnapshotId") val telemetrySnapshotId: String,
    @SerializedName("branchId") val branchId: String,
    @SerializedName("dtcCode") val dtcCode: String,
    @SerializedName("description") val description: String,
    @SerializedName("severity") val severity: String,
    @SerializedName("createdAt") val createdAt: String
)

data class BatchDtcItemDto(
    @SerializedName("dtcCode") val dtcCode: String,
    @SerializedName("description") val description: String,
    @SerializedName("severity") val severity: String
)

data class BatchSnapshotItemDto(
    @SerializedName("rpm") val rpm: Int,
    @SerializedName("temperature") val temperature: Int,
    @SerializedName("speedKmh") val speedKmh: Double,
    @SerializedName("odometerKm") val odometerKm: Double,
    @SerializedName("fuelLevelPercent") val fuelLevelPercent: Double,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("dtcCodes") val dtcCodes: List<BatchDtcItemDto>? = null
)

data class TelemetryBatchRequestDto(
    @SerializedName("obd2DeviceId") val obd2DeviceId: String,
    @SerializedName("snapshots") val snapshots: List<BatchSnapshotItemDto>
)

data class TelemetryBatchResponseDto(
    @SerializedName("message") val message: String? = null
)
