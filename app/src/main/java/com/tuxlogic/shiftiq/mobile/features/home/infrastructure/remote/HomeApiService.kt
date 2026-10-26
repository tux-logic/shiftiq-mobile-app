package com.tuxlogic.shiftiq.mobile.features.home.infrastructure.remote

import com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.remote.dto.CustomerResponseDto
import com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.remote.dto.VehicleRegistrationResponseDto
import com.tuxlogic.shiftiq.mobile.features.home.infrastructure.remote.dto.DtcAlertResponseDto
import com.tuxlogic.shiftiq.mobile.features.home.infrastructure.remote.dto.TelemetrySnapshotDto
import com.tuxlogic.shiftiq.mobile.features.home.infrastructure.remote.dto.WorkOrderResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface HomeApiService {

    @GET("customers")
    suspend fun getCustomerByUserId(
        @Header("Authorization") authorization: String,
        @Query("userId") userId: String
    ): Response<CustomerResponseDto>

    @GET("customers/{customerId}/vehicles")
    suspend fun getCustomerVehicles(
        @Header("Authorization") authorization: String,
        @Path("customerId") customerId: String
    ): Response<List<VehicleRegistrationResponseDto>>

    @GET("vehicles/{vehicleId}/telemetry-snapshots")
    suspend fun getVehicleTelemetry(
        @Header("Authorization") authorization: String,
        @Path("vehicleId") vehicleId: String
    ): Response<List<TelemetrySnapshotDto>>

    @GET("vehicles/{vehicleId}/dtc-alerts")
    suspend fun getVehicleDtcAlerts(
        @Header("Authorization") authorization: String,
        @Path("vehicleId") vehicleId: String
    ): Response<List<DtcAlertResponseDto>>

    @GET("work-orders")
    suspend fun getWorkOrdersByVehicle(
        @Header("Authorization") authorization: String,
        @Query("vehicleId") vehicleId: String
    ): Response<List<WorkOrderResponseDto>>
}
