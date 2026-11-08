package com.tuxlogic.shiftiq.mobile.feature.iot.infrastructure.remote.api

import com.tuxlogic.shiftiq.mobile.feature.iot.infrastructure.remote.dto.DeviceRegistrationResourceDto
import com.tuxlogic.shiftiq.mobile.feature.iot.infrastructure.remote.dto.DtcAlertResourceDto
import com.tuxlogic.shiftiq.mobile.feature.iot.infrastructure.remote.dto.LinkDeviceRequestDto
import com.tuxlogic.shiftiq.mobile.feature.iot.infrastructure.remote.dto.Obd2DeviceResourceDto
import com.tuxlogic.shiftiq.mobile.feature.iot.infrastructure.remote.dto.RegisterDeviceRequestDto
import com.tuxlogic.shiftiq.mobile.feature.iot.infrastructure.remote.dto.RegisterVehicleRequestDto
import com.tuxlogic.shiftiq.mobile.feature.iot.infrastructure.remote.dto.TelemetryBatchRequestDto
import com.tuxlogic.shiftiq.mobile.feature.iot.infrastructure.remote.dto.TelemetryBatchResponseDto
import com.tuxlogic.shiftiq.mobile.feature.iot.infrastructure.remote.dto.TelemetrySnapshotResourceDto
import com.tuxlogic.shiftiq.mobile.feature.iot.infrastructure.remote.dto.UpdateDeviceRequestDto
import com.tuxlogic.shiftiq.mobile.feature.iot.infrastructure.remote.dto.UpdateRegistrationStatusRequestDto
import com.tuxlogic.shiftiq.mobile.feature.iot.infrastructure.remote.dto.UpdateVehicleRequestDto
import com.tuxlogic.shiftiq.mobile.feature.iot.infrastructure.remote.dto.VehicleResourceDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface IotApiService {

    // --- Vehicles Endpoints ---

    @POST("api/v1/vehicles")
    suspend fun registerVehicle(
        @Body request: RegisterVehicleRequestDto
    ): Response<VehicleResourceDto>

    @GET("api/v1/vehicles")
    suspend fun getVehicles(
        @Query("branchId") branchId: String,
        @Query("status") status: String = "available-for-linking"
    ): Response<List<VehicleResourceDto>>

    @GET("api/v1/vehicles/{id}")
    suspend fun getVehicleById(
        @Path("id") id: String
    ): Response<VehicleResourceDto>

    @PUT("api/v1/vehicles/{id}")
    suspend fun updateVehicle(
        @Path("id") id: String,
        @Body request: UpdateVehicleRequestDto
    ): Response<VehicleResourceDto>

    @DELETE("api/v1/vehicles/{id}")
    suspend fun deleteVehicle(
        @Path("id") id: String
    ): Response<Unit>

    @GET("api/v1/customers/{customerId}/vehicles")
    suspend fun getCustomerVehicles(
        @Path("customerId") customerId: String
    ): Response<List<VehicleResourceDto>>

    @GET("api/v1/vehicles/{vehicleId}/telemetry-snapshots")
    suspend fun getVehicleTelemetrySnapshots(
        @Path("vehicleId") vehicleId: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<List<TelemetrySnapshotResourceDto>>

    @GET("api/v1/vehicles/{vehicleId}/dtc-alerts")
    suspend fun getVehicleDtcAlerts(
        @Path("vehicleId") vehicleId: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<List<DtcAlertResourceDto>>

    // --- OBD2 Devices Endpoints ---

    @POST("api/v1/obd2-devices")
    suspend fun registerDevice(
        @Body request: RegisterDeviceRequestDto
    ): Response<Obd2DeviceResourceDto>

    @GET("api/v1/obd2-devices")
    suspend fun getDevices(
        @Query("branchId") branchId: String,
        @Query("status") status: String? = null
    ): Response<List<Obd2DeviceResourceDto>>

    @GET("api/v1/obd2-devices/{id}")
    suspend fun getDeviceById(
        @Path("id") id: String
    ): Response<Obd2DeviceResourceDto>

    @PUT("api/v1/obd2-devices/{id}")
    suspend fun updateDevice(
        @Path("id") id: String,
        @Body request: UpdateDeviceRequestDto
    ): Response<Obd2DeviceResourceDto>

    @DELETE("api/v1/obd2-devices/{id}")
    suspend fun deleteDevice(
        @Path("id") id: String
    ): Response<Unit>

    @GET("api/v1/obd2-devices/{deviceId}/telemetry-snapshots/latest")
    suspend fun getLatestTelemetrySnapshot(
        @Path("deviceId") deviceId: String
    ): Response<TelemetrySnapshotResourceDto>

    @GET("api/v1/obd2-devices/{deviceId}/telemetry-snapshots")
    suspend fun getDeviceTelemetrySnapshots(
        @Path("deviceId") deviceId: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<List<TelemetrySnapshotResourceDto>>

    // --- Device Registrations Endpoints ---

    @POST("api/v1/obd2-device-registrations")
    suspend fun linkDevice(
        @Body request: LinkDeviceRequestDto
    ): Response<DeviceRegistrationResourceDto>

    @PATCH("api/v1/obd2-device-registrations/{id}")
    suspend fun updateRegistrationStatus(
        @Path("id") id: String,
        @Body request: UpdateRegistrationStatusRequestDto
    ): Response<DeviceRegistrationResourceDto>

    @GET("api/v1/obd2-device-registrations")
    suspend fun getRegistrations(
        @Query("branchId") branchId: String,
        @Query("status") status: String
    ): Response<List<DeviceRegistrationResourceDto>>

    @GET("api/v1/obd2-device-registrations/{id}/telemetry-snapshots")
    suspend fun getRegistrationTelemetrySnapshots(
        @Path("id") id: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<List<TelemetrySnapshotResourceDto>>

    @GET("api/v1/obd2-device-registrations/{id}/dtc-alerts")
    suspend fun getRegistrationDtcAlerts(
        @Path("id") id: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<List<DtcAlertResourceDto>>

    // --- Telemetry Batches Endpoints ---

    @POST("api/v1/telemetry-batches")
    suspend fun ingestTelemetryBatch(
        @Body request: TelemetryBatchRequestDto
    ): Response<TelemetryBatchResponseDto>
}
