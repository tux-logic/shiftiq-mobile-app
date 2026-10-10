package com.tuxlogic.shiftiq.mobile.feature.fleet.domain.usecase

import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.EmployeeRegistration
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.repository.FleetRepository
import java.util.UUID
import javax.inject.Inject

class GetEmployeeRegistrationsUseCase @Inject constructor(
    private val repository: FleetRepository
) {
    suspend operator fun invoke(
        branchId: UUID? = null,
        status: String? = null,
        employeeId: UUID? = null
    ): AppResult<List<EmployeeRegistration>> {
        return repository.getEmployeeRegistrations(branchId, status, employeeId)
    }
}

class CreateEmployeeRegistrationUseCase @Inject constructor(
    private val repository: FleetRepository
) {
    suspend operator fun invoke(
        employeeId: UUID,
        branchId: UUID,
        speciality: String,
        specialityName: String?,
        salary: Double,
        role: String?
    ): AppResult<EmployeeRegistration> {
        return repository.createEmployeeRegistration(
            employeeId = employeeId,
            branchId = branchId,
            speciality = speciality,
            specialityName = specialityName,
            salary = salary,
            role = role
        )
    }
}

class RequestEmployeeJoinUseCase @Inject constructor(
    private val repository: FleetRepository
) {
    suspend operator fun invoke(
        employeeId: UUID,
        branchId: UUID,
        speciality: String?,
        specialityName: String?,
        salary: Double?
    ): AppResult<EmployeeRegistration> {
        return repository.requestEmployeeJoin(
            employeeId = employeeId,
            branchId = branchId,
            speciality = speciality,
            specialityName = specialityName,
            salary = salary
        )
    }
}

class ApproveEmployeeRegistrationUseCase @Inject constructor(
    private val repository: FleetRepository
) {
    suspend operator fun invoke(id: UUID): AppResult<EmployeeRegistration> {
        return repository.approveEmployeeRegistration(id)
    }
}

class RejectEmployeeRegistrationUseCase @Inject constructor(
    private val repository: FleetRepository
) {
    suspend operator fun invoke(id: UUID, reason: String? = null): AppResult<EmployeeRegistration> {
        return repository.rejectEmployeeRegistration(id, reason)
    }
}
