package com.tuxlogic.shiftiq.mobile.feature.fleet.infrastructure.mapper

import com.tuxlogic.shiftiq.mobile.feature.fleet.infrastructure.remote.dto.AppointmentDto
import com.tuxlogic.shiftiq.mobile.feature.fleet.infrastructure.remote.dto.BranchAvailabilityDto
import com.tuxlogic.shiftiq.mobile.feature.fleet.infrastructure.remote.dto.CustomerRegistrationDto
import com.tuxlogic.shiftiq.mobile.feature.fleet.infrastructure.remote.dto.EmployeeRegistrationDto
import com.tuxlogic.shiftiq.mobile.feature.fleet.infrastructure.remote.dto.StaffCareerHistoryDto
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.Appointment
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.AppointmentStatus
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.BranchAvailability
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.BranchAvailabilitySlot
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.CustomerRegistration
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.EmployeeRegistration
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.StaffCareerHistory
import java.util.UUID

fun AppointmentDto.toDomain(): Appointment = Appointment(
    id = UUID.fromString(id),
    branchId = UUID.fromString(branchId),
    customerId = UUID.fromString(customerId),
    customerName = customerName,
    customerPhone = customerPhone,
    vehicleId = UUID.fromString(vehicleId),
    vehiclePlate = vehiclePlate,
    vehicleModel = vehicleModel,
    bayId = bayId?.let { runCatching { UUID.fromString(it) }.getOrNull() },
    scheduledStart = scheduledStart,
    scheduledEnd = scheduledEnd,
    serviceType = serviceType,
    status = AppointmentStatus.fromString(status),
    notes = notes,
    bayNumber = bayNumber,
    createdAt = createdAt
)

fun BranchAvailabilityDto.toDomain(): BranchAvailability = BranchAvailability(
    branchId = branchId,
    date = date,
    activeBaysCount = activeBaysCount,
    freeBaysNow = freeBaysNow,
    slots = slots.map {
        BranchAvailabilitySlot(
            start = it.start,
            end = it.end,
            availableBayIds = it.availableBayIds
        )
    }
)

fun EmployeeRegistrationDto.toDomain(): EmployeeRegistration = EmployeeRegistration(
    id = UUID.fromString(id),
    employeeId = UUID.fromString(employeeId),
    branchId = UUID.fromString(branchId),
    speciality = speciality,
    specialityName = specialityName,
    salary = salary,
    role = role,
    status = status
)

fun StaffCareerHistoryDto.toDomain(): StaffCareerHistory = StaffCareerHistory(
    id = id,
    employeeId = UUID.fromString(employeeId),
    branchId = UUID.fromString(branchId),
    speciality = speciality,
    specialityName = specialityName,
    salary = salary,
    role = role,
    status = status,
    changedAt = changedAt,
    changeReason = changeReason
)

fun CustomerRegistrationDto.toDomain(): CustomerRegistration = CustomerRegistration(
    id = UUID.fromString(id),
    customerId = UUID.fromString(customerId),
    branchId = UUID.fromString(branchId),
    status = status
)
