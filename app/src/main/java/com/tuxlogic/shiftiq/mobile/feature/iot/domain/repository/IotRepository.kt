package com.tuxlogic.shiftiq.mobile.feature.iot.domain.repository

import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.core.model.CustomerId
import com.tuxlogic.shiftiq.mobile.core.model.VehicleId
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.DeviceId
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.DeviceRegistration
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.DtcAlert
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.MacAddress
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.Obd2Device
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.PlateNumber
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.RegistrationId
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.RegistrationStatus
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.TelemetryBatch
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.TelemetrySnapshot
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.Vehicle
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.Vin

interface IotRepository {

    suspend fun registerVehicle(
        plateNumber: PlateNumber,
        brand: String,
        model: String,
        year: Int,
        vin: Vin,
        photoUrl: String? = null,
        customerId: CustomerId? = null
    ): AppResult<Vehicle>

    suspend fun getVehicleById(id: VehicleId): AppResult<Vehicle>

    suspend fun getVehiclesByBranch(branchId: BranchId? = null): AppResult<List<Vehicle>>

    suspend fun getCustomerVehicles(customerId: CustomerId): AppResult<List<Vehicle>>

    suspend fun updateVehicle(
        id: VehicleId,
        plateNumber: PlateNumber,
        brand: String,
        model: String,
        year: Int,
        vin: Vin,
        photoUrl: String? = null
    ): AppResult<Vehicle>

    suspend fun deleteVehicle(id: VehicleId): AppResult<Unit>

    suspend fun registerDevice(
        branchId: BranchId,
        macAddress: MacAddress
    ): AppResult<Obd2Device>

    suspend fun getDevices(
        branchId: BranchId? = null,
        status: String? = null
    ): AppResult<List<Obd2Device>>

    suspend fun getDeviceById(id: DeviceId): AppResult<Obd2Device>

    suspend fun updateDevice(
        id: DeviceId,
        macAddress: MacAddress
    ): AppResult<Obd2Device>

    suspend fun deleteDevice(id: DeviceId): AppResult<Unit>

    suspend fun linkDeviceToVehicle(
        obd2DeviceId: DeviceId,
        branchId: BranchId,
        vehicleId: VehicleId
    ): AppResult<DeviceRegistration>

    suspend fun unlinkDeviceFromVehicle(
        registrationId: RegistrationId
    ): AppResult<DeviceRegistration>

    suspend fun getRegistrations(
        branchId: BranchId? = null,
        status: RegistrationStatus
    ): AppResult<List<DeviceRegistration>>

    suspend fun getLatestTelemetrySnapshot(deviceId: DeviceId): AppResult<TelemetrySnapshot>

    suspend fun getDeviceTelemetryHistory(
        deviceId: DeviceId,
        page: Int = 0,
        size: Int = 20
    ): AppResult<List<TelemetrySnapshot>>

    suspend fun getVehicleTelemetryHistory(
        vehicleId: VehicleId,
        page: Int = 0,
        size: Int = 20
    ): AppResult<List<TelemetrySnapshot>>

    suspend fun getRegistrationTelemetryHistory(
        registrationId: RegistrationId,
        page: Int = 0,
        size: Int = 20
    ): AppResult<List<TelemetrySnapshot>>

    suspend fun getVehicleDtcAlerts(
        vehicleId: VehicleId,
        page: Int = 0,
        size: Int = 20
    ): AppResult<List<DtcAlert>>

    suspend fun getRegistrationDtcAlerts(
        registrationId: RegistrationId,
        page: Int = 0,
        size: Int = 20
    ): AppResult<List<DtcAlert>>

    suspend fun ingestTelemetryBatch(batch: TelemetryBatch): AppResult<Unit>
}
