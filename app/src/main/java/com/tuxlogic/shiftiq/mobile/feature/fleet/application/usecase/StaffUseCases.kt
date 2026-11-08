package com.tuxlogic.shiftiq.mobile.feature.fleet.application.usecase

import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.EmployeeRegistration
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.StaffCareerHistory
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

class GetEmployeeRegistrationByIdUseCase @Inject constructor(
    private val repository: FleetRepository
) {
    suspend operator fun invoke(id: UUID): AppResult<EmployeeRegistration> {
        return repository.getEmployeeRegistrationById(id)
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
        val trimmedSpeciality = speciality.trim()
        if (trimmedSpeciality.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "speciality", message = "Specialty code is required"))
        }
        if (salary < 0) {
            return AppResult.Failure(AppError.Validation(details = "salary", message = "Salary cannot be negative"))
        }

        return repository.createEmployeeRegistration(
            employeeId = employeeId,
            branchId = branchId,
            speciality = trimmedSpeciality,
            specialityName = specialityName?.trim(),
            salary = salary,
            role = role?.trim()
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
        if (salary != null && salary < 0) {
            return AppResult.Failure(AppError.Validation(details = "salary", message = "Desired salary cannot be negative"))
        }
        return repository.requestEmployeeJoin(
            employeeId = employeeId,
            branchId = branchId,
            speciality = speciality?.trim(),
            specialityName = specialityName?.trim(),
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
        return repository.rejectEmployeeRegistration(id, reason?.trim())
    }
}

class UpdateEmployeeRegistrationUseCase @Inject constructor(
    private val repository: FleetRepository
) {
    suspend operator fun invoke(
        id: UUID,
        speciality: String,
        specialityName: String?,
        salary: Double
    ): AppResult<EmployeeRegistration> {
        val trimmedSpeciality = speciality.trim()
        if (trimmedSpeciality.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "speciality", message = "Specialty code is required"))
        }
        if (salary < 0) {
            return AppResult.Failure(AppError.Validation(details = "salary", message = "Salary cannot be negative"))
        }
        return repository.updateEmployeeRegistration(
            id = id,
            speciality = trimmedSpeciality,
            specialityName = specialityName?.trim(),
            salary = salary
        )
    }
}

class DeleteEmployeeRegistrationUseCase @Inject constructor(
    private val repository: FleetRepository
) {
    suspend operator fun invoke(id: UUID): AppResult<Unit> {
        return repository.deleteEmployeeRegistration(id)
    }
}

class GetStaffCareerHistoryUseCase @Inject constructor(
    private val repository: FleetRepository
) {
    suspend operator fun invoke(branchId: UUID, employeeId: UUID): AppResult<List<StaffCareerHistory>> {
        return repository.getStaffCareerHistory(branchId, employeeId)
    }
}
