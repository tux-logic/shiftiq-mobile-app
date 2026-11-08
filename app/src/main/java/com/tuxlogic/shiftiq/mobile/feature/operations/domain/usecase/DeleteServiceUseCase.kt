package com.tuxlogic.shiftiq.mobile.feature.operations.domain.usecase

import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.ServiceId
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.repository.ServicesRepository
import javax.inject.Inject

class DeleteServiceUseCase @Inject constructor(
    private val servicesRepository: ServicesRepository
) {
    suspend operator fun invoke(serviceId: ServiceId): AppResult<Unit> {
        return servicesRepository.deleteService(serviceId)
    }
}
