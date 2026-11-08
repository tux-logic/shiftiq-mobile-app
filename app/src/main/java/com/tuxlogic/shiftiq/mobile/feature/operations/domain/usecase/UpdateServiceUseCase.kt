package com.tuxlogic.shiftiq.mobile.feature.operations.domain.usecase

import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.ServiceId
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.ServiceItem
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.repository.ServicesRepository
import javax.inject.Inject

class UpdateServiceUseCase @Inject constructor(
    private val servicesRepository: ServicesRepository
) {
    suspend operator fun invoke(
        serviceId: ServiceId,
        name: String,
        price: Double
    ): AppResult<ServiceItem> {
        val trimmedName = name.trim()
        if (trimmedName.isBlank()) {
            return AppResult.Failure(
                AppError.Validation(details = "name", message = "Service name is required")
            )
        }
        if (price < 0.0) {
            return AppResult.Failure(
                AppError.Validation(details = "price", message = "Service price cannot be negative")
            )
        }
        return servicesRepository.updateService(serviceId, trimmedName, price)
    }
}
