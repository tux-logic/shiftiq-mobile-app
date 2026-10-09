package com.tuxlogic.shiftiq.mobile.feature.fleet.domain.usecase

import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.Appointment
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.repository.FleetRepository
import java.util.UUID
import javax.inject.Inject

class GetAppointmentsUseCase @Inject constructor(
    private val repository: FleetRepository
) {
    suspend operator fun invoke(
        branchId: UUID? = null,
        status: String? = null,
        customerId: UUID? = null,
        vehicleId: UUID? = null
    ): AppResult<List<Appointment>> {
        return repository.getAppointments(branchId, status, customerId, vehicleId)
    }
}

class CreateAppointmentUseCase @Inject constructor(
    private val repository: FleetRepository
) {
    suspend operator fun invoke(
        branchId: UUID,
        customerId: UUID,
        vehicleId: UUID,
        scheduledStart: String,
        notes: String?
    ): AppResult<Appointment> {
        return repository.createAppointment(branchId, customerId, vehicleId, scheduledStart, notes)
    }
}

class UpdateAppointmentUseCase @Inject constructor(
    private val repository: FleetRepository
) {
    suspend operator fun invoke(
        appointmentId: UUID,
        branchId: UUID,
        customerId: UUID,
        vehicleId: UUID,
        scheduledStart: String,
        status: String,
        notes: String?
    ): AppResult<Appointment> {
        return repository.updateAppointment(
            appointmentId,
            branchId,
            customerId,
            vehicleId,
            scheduledStart,
            status,
            notes
        )
    }
}

class DeleteAppointmentUseCase @Inject constructor(
    private val repository: FleetRepository
) {
    suspend operator fun invoke(appointmentId: UUID): AppResult<Unit> {
        return repository.deleteAppointment(appointmentId)
    }
}
