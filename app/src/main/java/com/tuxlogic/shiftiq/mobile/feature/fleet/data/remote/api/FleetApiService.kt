package com.tuxlogic.shiftiq.mobile.feature.fleet.data.remote.api

import com.tuxlogic.shiftiq.mobile.feature.fleet.data.remote.dto.AppointmentDto
import com.tuxlogic.shiftiq.mobile.feature.fleet.data.remote.dto.CreateAppointmentRequestDto
import com.tuxlogic.shiftiq.mobile.feature.fleet.data.remote.dto.CreateCustomerRegistrationRequestDto
import com.tuxlogic.shiftiq.mobile.feature.fleet.data.remote.dto.CreateEmployeeRegistrationRequestDto
import com.tuxlogic.shiftiq.mobile.feature.fleet.data.remote.dto.CustomerRegistrationDto
import com.tuxlogic.shiftiq.mobile.feature.fleet.data.remote.dto.EmployeeRegistrationDto
import com.tuxlogic.shiftiq.mobile.feature.fleet.data.remote.dto.RejectEmployeeRegistrationRequestDto
import com.tuxlogic.shiftiq.mobile.feature.fleet.data.remote.dto.RequestEmployeeJoinRequestDto
import com.tuxlogic.shiftiq.mobile.feature.fleet.data.remote.dto.UpdateAppointmentRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Servicio Retrofit para endpoints del contexto Fleet en ShiftIQ Platform.
 */
interface FleetApiService {

    // === Citas (Appointments) ===

    @GET("api/v1/appointments")
    suspend fun getAppointments(
        @Query("branchId") branchId: String? = null,
        @Query("status") status: String? = null,
        @Query("customerId") customerId: String? = null,
        @Query("vehicleId") vehicleId: String? = null
    ): Response<List<AppointmentDto>>

    @GET("api/v1/appointments/{appointmentId}")
    suspend fun getAppointmentById(
        @Path("appointmentId") appointmentId: String
    ): Response<AppointmentDto>

    @POST("api/v1/appointments")
    suspend fun createAppointment(
        @Body request: CreateAppointmentRequestDto
    ): Response<AppointmentDto>

    @PUT("api/v1/appointments/{appointmentId}")
    suspend fun updateAppointment(
        @Path("appointmentId") appointmentId: String,
        @Body request: UpdateAppointmentRequestDto
    ): Response<AppointmentDto>

    @DELETE("api/v1/appointments/{appointmentId}")
    suspend fun deleteAppointment(
        @Path("appointmentId") appointmentId: String
    ): Response<Unit>

    // === Personal Técnico (Employee Registrations) ===

    @GET("api/v1/employee-registrations")
    suspend fun getEmployeeRegistrations(
        @Query("branchId") branchId: String? = null,
        @Query("status") status: String? = null,
        @Query("employeeId") employeeId: String? = null
    ): Response<List<EmployeeRegistrationDto>>

    @GET("api/v1/employee-registrations/{id}")
    suspend fun getEmployeeRegistrationById(
        @Path("id") id: String
    ): Response<EmployeeRegistrationDto>

    @POST("api/v1/employee-registrations")
    suspend fun createEmployeeRegistration(
        @Body request: CreateEmployeeRegistrationRequestDto
    ): Response<EmployeeRegistrationDto>

    @POST("api/v1/employee-registrations/request-join")
    suspend fun requestEmployeeJoin(
        @Body request: RequestEmployeeJoinRequestDto
    ): Response<EmployeeRegistrationDto>

    @POST("api/v1/employee-registrations/{id}/approve")
    suspend fun approveEmployeeRegistration(
        @Path("id") id: String
    ): Response<EmployeeRegistrationDto>

    @POST("api/v1/employee-registrations/{id}/reject")
    suspend fun rejectEmployeeRegistration(
        @Path("id") id: String,
        @Body request: RejectEmployeeRegistrationRequestDto
    ): Response<EmployeeRegistrationDto>

    // === Clientes Vinculados (Customer Registrations) ===

    @GET("api/v1/customer-registrations")
    suspend fun getCustomerRegistrations(
        @Query("branchId") branchId: String? = null,
        @Query("status") status: String? = null,
        @Query("customerId") customerId: String? = null
    ): Response<List<CustomerRegistrationDto>>

    @POST("api/v1/customer-registrations")
    suspend fun createCustomerRegistration(
        @Body request: CreateCustomerRegistrationRequestDto
    ): Response<CustomerRegistrationDto>
}
