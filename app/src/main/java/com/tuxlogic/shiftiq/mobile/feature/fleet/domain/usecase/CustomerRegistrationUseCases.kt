package com.tuxlogic.shiftiq.mobile.feature.fleet.domain.usecase

import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.CustomerRegistration
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.repository.FleetRepository
import java.util.UUID
import javax.inject.Inject

class GetCustomerRegistrationsUseCase @Inject constructor(
    private val repository: FleetRepository
) {
    suspend operator fun invoke(
        branchId: UUID? = null,
        status: String? = null,
        customerId: UUID? = null
    ): AppResult<List<CustomerRegistration>> {
        if (branchId == null && status == null && customerId == null) {
            return AppResult.Failure(AppError.Validation(details = "filters", message = "At least one filter (branchId, status, or customerId) is required"))
        }
        return repository.getCustomerRegistrations(branchId, status?.trim()?.uppercase(), customerId)
    }
}

class GetCustomerRegistrationByIdUseCase @Inject constructor(
    private val repository: FleetRepository
) {
    suspend operator fun invoke(id: UUID): AppResult<CustomerRegistration> {
        return repository.getCustomerRegistrationById(id)
    }
}

class CreateCustomerRegistrationUseCase @Inject constructor(
    private val repository: FleetRepository
) {
    suspend operator fun invoke(
        customerId: UUID,
        branchId: UUID
    ): AppResult<CustomerRegistration> {
        return repository.createCustomerRegistration(customerId, branchId)
    }
}

class UpdateCustomerRegistrationUseCase @Inject constructor(
    private val repository: FleetRepository
) {
    suspend operator fun invoke(
        id: UUID,
        status: String
    ): AppResult<CustomerRegistration> {
        val trimmedStatus = status.trim().uppercase()
        if (trimmedStatus != "ACTIVE" && trimmedStatus != "INACTIVE") {
            return AppResult.Failure(AppError.Validation(details = "status", message = "Status must be ACTIVE or INACTIVE"))
        }
        return repository.updateCustomerRegistration(id, trimmedStatus)
    }
}

class DeleteCustomerRegistrationUseCase @Inject constructor(
    private val repository: FleetRepository
) {
    suspend operator fun invoke(id: UUID): AppResult<Unit> {
        return repository.deleteCustomerRegistration(id)
    }
}
