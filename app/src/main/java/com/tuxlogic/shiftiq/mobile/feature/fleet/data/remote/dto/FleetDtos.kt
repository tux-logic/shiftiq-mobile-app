package com.tuxlogic.shiftiq.mobile.feature.fleet.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.Appointment
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.AppointmentStatus
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.CustomerRegistration
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.EmployeeRegistration
import java.util.UUID

// === DTOs de Citas ===

data class AppointmentDto(
    @SerializedName("id") val id: String,
    @SerializedName("branchId") val branchId: String,
    @SerializedName("customerId") val customerId: String,
    @SerializedName("vehicleId") val vehicleId: String,
    @SerializedName("scheduledStart") val scheduledStart: String,
    @SerializedName("status") val status: String,
    @SerializedName("notes") val notes: String? = null
) {
    fun toDomain(): Appointment {
        return Appointment(
            id = UUID.fromString(id),
            branchId = UUID.fromString(branchId),
            customerId = UUID.fromString(customerId),
            vehicleId = UUID.fromString(vehicleId),
            scheduledStart = scheduledStart,
            status = AppointmentStatus.fromString(status),
            notes = notes
        )
    }
}

data class CreateAppointmentRequestDto(
    @SerializedName("branchId") val branchId: String,
    @SerializedName("customerId") val customerId: String,
    @SerializedName("vehicleId") val vehicleId: String,
    @SerializedName("scheduledStart") val scheduledStart: String,
    @SerializedName("notes") val notes: String? = null
)

data class UpdateAppointmentRequestDto(
    @SerializedName("branchId") val branchId: String,
    @SerializedName("customerId") val customerId: String,
    @SerializedName("vehicleId") val vehicleId: String,
    @SerializedName("scheduledStart") val scheduledStart: String,
    @SerializedName("status") val status: String,
    @SerializedName("notes") val notes: String? = null
)

// === DTOs de Personal (Employee Registrations) ===

data class EmployeeRegistrationDto(
    @SerializedName("id") val id: String,
    @SerializedName("employeeId") val employeeId: String,
    @SerializedName("branchId") val branchId: String,
    @SerializedName("speciality") val speciality: String,
    @SerializedName("specialityName") val specialityName: String? = null,
    @SerializedName("salary") val salary: Double,
    @SerializedName("role") val role: String? = null,
    @SerializedName("status") val status: String
) {
    fun toDomain(): EmployeeRegistration {
        return EmployeeRegistration(
            id = UUID.fromString(id),
            employeeId = UUID.fromString(employeeId),
            branchId = UUID.fromString(branchId),
            speciality = speciality,
            specialityName = specialityName,
            salary = salary,
            role = role,
            status = status
        )
    }
}

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

// === DTOs de Clientes Vinculados (Customer Registrations) ===

data class CustomerRegistrationDto(
    @SerializedName("id") val id: String,
    @SerializedName("customerId") val customerId: String,
    @SerializedName("branchId") val branchId: String,
    @SerializedName("status") val status: String
) {
    fun toDomain(): CustomerRegistration {
        return CustomerRegistration(
            id = UUID.fromString(id),
            customerId = UUID.fromString(customerId),
            branchId = UUID.fromString(branchId),
            status = status
        )
    }
}

data class CreateCustomerRegistrationRequestDto(
    @SerializedName("customerId") val customerId: String,
    @SerializedName("branchId") val branchId: String
)
