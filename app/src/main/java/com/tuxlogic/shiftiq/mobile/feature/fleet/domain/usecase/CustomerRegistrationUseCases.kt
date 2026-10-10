package com.tuxlogic.shiftiq.mobile.feature.fleet.domain.usecase

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
        return repository.getCustomerRegistrations(branchId, status, customerId)
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
