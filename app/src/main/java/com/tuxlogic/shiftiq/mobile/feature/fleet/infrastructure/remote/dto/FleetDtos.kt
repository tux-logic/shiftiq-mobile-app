package com.tuxlogic.shiftiq.mobile.feature.fleet.infrastructure.remote.dto

import com.google.gson.annotations.SerializedName

// === Appointments DTOs ===

data class AppointmentDto(
    @SerializedName("id") val id: String,
    @SerializedName("branchId") val branchId: String,
    @SerializedName("customerId") val customerId: String,
    @SerializedName("customerName") val customerName: String = "Cliente del Taller",
    @SerializedName("customerPhone") val customerPhone: String = "",
    @SerializedName("vehicleId") val vehicleId: String,
    @SerializedName("vehiclePlate") val vehiclePlate: String = "S/P",
    @SerializedName("vehicleModel") val vehicleModel: String = "Vehículo General",
    @SerializedName("bayId") val bayId: String? = null,
    @SerializedName("scheduledStart") val scheduledStart: String,
    @SerializedName("scheduledEnd") val scheduledEnd: String? = null,
    @SerializedName("serviceType") val serviceType: String = "Mantenimiento General",
    @SerializedName("status") val status: String,
    @SerializedName("notes") val notes: String? = null,
    @SerializedName("bayNumber") val bayNumber: Int = 1,
    @SerializedName("createdAt") val createdAt: String? = null
)

data class CreateAppointmentRequestDto(
    @SerializedName("branchId") val branchId: String,
    @SerializedName("customerId") val customerId: String,
    @SerializedName("vehicleId") val vehicleId: String,
    @SerializedName("bayId") val bayId: String? = null,
    @SerializedName("scheduledStart") val scheduledStart: String,
    @SerializedName("scheduledEnd") val scheduledEnd: String? = null,
    @SerializedName("notes") val notes: String? = null
)

data class UpdateAppointmentRequestDto(
    @SerializedName("branchId") val branchId: String,
    @SerializedName("customerId") val customerId: String,
    @SerializedName("vehicleId") val vehicleId: String,
    @SerializedName("bayId") val bayId: String? = null,
    @SerializedName("scheduledStart") val scheduledStart: String,
    @SerializedName("status") val status: String,
    @SerializedName("notes") val notes: String? = null
)

data class BranchAvailabilityDto(
    @SerializedName("branchId") val branchId: String,
    @SerializedName("date") val date: String,
    @SerializedName("activeBaysCount") val activeBaysCount: Int = 0,
    @SerializedName("freeBaysNow") val freeBaysNow: Int = 0,
    @SerializedName("slots") val slots: List<BranchAvailabilitySlotDto> = emptyList()
)

data class BranchAvailabilitySlotDto(
    @SerializedName("start") val start: String,
    @SerializedName("end") val end: String,
    @SerializedName("availableBayIds") val availableBayIds: List<String> = emptyList()
)

// === Technical Staff (Employee Registrations) DTOs ===

data class EmployeeRegistrationDto(
    @SerializedName("id") val id: String,
    @SerializedName("employeeId") val employeeId: String,
    @SerializedName("branchId") val branchId: String,
    @SerializedName("speciality") val speciality: String,
    @SerializedName("specialityName") val specialityName: String? = null,
    @SerializedName("salary") val salary: Double,
    @SerializedName("role") val role: String? = null,
    @SerializedName("status") val status: String
)

data class CreateEmployeeRegistrationRequestDto(
    @SerializedName("employeeId") val employeeId: String,
    @SerializedName("branchId") val branchId: String,
    @SerializedName("speciality") val speciality: String,
    @SerializedName("specialityName") val specialityName: String? = null,
    @SerializedName("salary") val salary: Double,
    @SerializedName("role") val role: String? = null
)

data class RequestEmployeeJoinRequestDto(
    @SerializedName("employeeId") val employeeId: String,
    @SerializedName("branchId") val branchId: String,
    @SerializedName("speciality") val speciality: String? = null,
    @SerializedName("specialityName") val specialityName: String? = null,
    @SerializedName("salary") val salary: Double? = null
)

data class RejectEmployeeRegistrationRequestDto(
    @SerializedName("reason") val reason: String? = null
)

data class UpdateEmployeeRegistrationRequestDto(
    @SerializedName("speciality") val speciality: String,
    @SerializedName("specialityName") val specialityName: String? = null,
    @SerializedName("salary") val salary: Double
)

data class StaffCareerHistoryDto(
    @SerializedName("id") val id: String,
    @SerializedName("employeeId") val employeeId: String,
    @SerializedName("branchId") val branchId: String,
    @SerializedName("speciality") val speciality: String,
    @SerializedName("specialityName") val specialityName: String? = null,
    @SerializedName("salary") val salary: Double,
    @SerializedName("role") val role: String? = null,
    @SerializedName("status") val status: String,
    @SerializedName("changedAt") val changedAt: String,
    @SerializedName("changeReason") val changeReason: String? = null
)

// === Linked Customers (Customer Registrations) DTOs ===

data class CustomerRegistrationDto(
    @SerializedName("id") val id: String,
    @SerializedName("customerId") val customerId: String,
    @SerializedName("branchId") val branchId: String,
    @SerializedName("status") val status: String
)

data class CreateCustomerRegistrationRequestDto(
    @SerializedName("customerId") val customerId: String,
    @SerializedName("branchId") val branchId: String
)

data class UpdateCustomerRegistrationRequestDto(
    @SerializedName("status") val status: String
)
