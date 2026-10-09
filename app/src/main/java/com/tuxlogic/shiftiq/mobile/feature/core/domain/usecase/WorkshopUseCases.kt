package com.tuxlogic.shiftiq.mobile.feature.core.domain.usecase

import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.Workshop
import com.tuxlogic.shiftiq.mobile.feature.core.domain.repository.CoreRepository
import javax.inject.Inject

class GetWorkshopsUseCase @Inject constructor(
    private val coreRepository: CoreRepository
) {
    suspend operator fun invoke(ownerId: String): AppResult<List<Workshop>> {
        if (ownerId.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "ownerId", message = "ID de propietario requerido"))
        }
        return coreRepository.getWorkshops(ownerId)
    }
}

class CreateWorkshopUseCase @Inject constructor(
    private val coreRepository: CoreRepository
) {
    suspend operator fun invoke(
        ownerId: String,
        businessName: String,
        brandName: String,
        taxId: String,
        mileageIntervalConfig: Int = 5000
    ): AppResult<Workshop> {
        val trimmedBusiness = businessName.trim()
        val trimmedBrand = brandName.trim()
        val trimmedTaxId = taxId.trim()

        if (ownerId.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "ownerId", message = "ID de propietario requerido"))
        }
        if (trimmedBusiness.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "businessName", message = "Razón social requerida"))
        }
        if (trimmedBrand.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "brandName", message = "Nombre comercial requerido"))
        }
        if (trimmedTaxId.length != 11 || !trimmedTaxId.all { it.isDigit() }) {
            return AppResult.Failure(AppError.Validation(details = "taxId", message = "El RUC debe contener exactamente 11 dígitos numéricos"))
        }
        if (mileageIntervalConfig < 1000) {
            return AppResult.Failure(AppError.Validation(details = "mileageInterval", message = "El intervalo de kilometraje debe ser mayor a 1,000 km"))
        }

        return coreRepository.createWorkshop(
            ownerId = ownerId,
            businessName = trimmedBusiness,
            brandName = trimmedBrand,
            taxId = trimmedTaxId,
            mileageIntervalConfig = mileageIntervalConfig
        )
    }
}
