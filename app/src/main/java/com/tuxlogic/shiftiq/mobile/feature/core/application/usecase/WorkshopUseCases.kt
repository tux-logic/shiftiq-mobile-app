package com.tuxlogic.shiftiq.mobile.feature.core.application.usecase

import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.SpecialtyRevision
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.Workshop
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.WorkshopSpecialty
import com.tuxlogic.shiftiq.mobile.feature.core.domain.repository.CoreRepository
import javax.inject.Inject

class GetWorkshopsUseCase @Inject constructor(
    private val coreRepository: CoreRepository
) {
    suspend operator fun invoke(ownerId: String): AppResult<List<Workshop>> {
        if (ownerId.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "ownerId", message = "Owner ID is required"))
        }
        return coreRepository.getWorkshops(ownerId)
    }
}

class GetWorkshopByIdUseCase @Inject constructor(
    private val coreRepository: CoreRepository
) {
    suspend operator fun invoke(workshopId: String): AppResult<Workshop> {
        if (workshopId.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "workshopId", message = "Workshop ID is required"))
        }
        return coreRepository.getWorkshopById(workshopId)
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
            return AppResult.Failure(AppError.Validation(details = "ownerId", message = "Owner ID is required"))
        }
        if (trimmedBusiness.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "businessName", message = "Business name is required"))
        }
        if (trimmedBrand.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "brandName", message = "Brand name is required"))
        }
        if (trimmedTaxId.length != 11 || !trimmedTaxId.all { it.isDigit() }) {
            return AppResult.Failure(AppError.Validation(details = "taxId", message = "RUC must contain exactly 11 digits"))
        }
        if (mileageIntervalConfig < 1000) {
            return AppResult.Failure(AppError.Validation(details = "mileageInterval", message = "Mileage interval must be greater than 1,000 km"))
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

class UpdateWorkshopUseCase @Inject constructor(
    private val coreRepository: CoreRepository
) {
    suspend operator fun invoke(
        workshopId: String,
        businessName: String,
        brandName: String,
        taxId: String,
        mileageIntervalConfig: Int
    ): AppResult<Workshop> {
        val trimmedBusiness = businessName.trim()
        val trimmedBrand = brandName.trim()
        val trimmedTaxId = taxId.trim()

        if (workshopId.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "workshopId", message = "Workshop ID is required"))
        }
        if (trimmedBusiness.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "businessName", message = "Business name is required"))
        }
        if (trimmedBrand.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "brandName", message = "Brand name is required"))
        }
        if (trimmedTaxId.length != 11 || !trimmedTaxId.all { it.isDigit() }) {
            return AppResult.Failure(AppError.Validation(details = "taxId", message = "RUC must contain exactly 11 digits"))
        }
        if (mileageIntervalConfig <= 0) {
            return AppResult.Failure(AppError.Validation(details = "mileageInterval", message = "Mileage interval must be greater than zero"))
        }

        return coreRepository.updateWorkshop(
            workshopId = workshopId,
            businessName = trimmedBusiness,
            brandName = trimmedBrand,
            taxId = trimmedTaxId,
            mileageIntervalConfig = mileageIntervalConfig
        )
    }
}

class GetSpecialtiesUseCase @Inject constructor(
    private val coreRepository: CoreRepository
) {
    suspend operator fun invoke(workshopId: String, activeOnly: Boolean = true): AppResult<List<WorkshopSpecialty>> {
        if (workshopId.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "workshopId", message = "Workshop ID is required"))
        }
        return coreRepository.getSpecialties(workshopId, activeOnly)
    }
}

class GetSpecialtyByIdUseCase @Inject constructor(
    private val coreRepository: CoreRepository
) {
    suspend operator fun invoke(workshopId: String, specialtyId: String): AppResult<WorkshopSpecialty> {
        if (workshopId.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "workshopId", message = "Workshop ID is required"))
        }
        if (specialtyId.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "specialtyId", message = "Specialty ID is required"))
        }
        return coreRepository.getSpecialtyById(workshopId, specialtyId)
    }
}

class CreateSpecialtyUseCase @Inject constructor(
    private val coreRepository: CoreRepository
) {
    suspend operator fun invoke(
        workshopId: String,
        name: String,
        code: String = name.trim().uppercase().replace(" ", "_"),
        description: String? = null
    ): AppResult<WorkshopSpecialty> {
        val trimmedName = name.trim()
        val trimmedCode = code.trim().uppercase()
        if (workshopId.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "workshopId", message = "Workshop ID is required"))
        }
        if (trimmedName.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "name", message = "Specialty name is required"))
        }
        if (trimmedCode.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "code", message = "Specialty code is required"))
        }

        return coreRepository.createSpecialty(
            workshopId = workshopId,
            name = trimmedName,
            code = trimmedCode,
            description = description?.trim()
        )
    }
}

class UpdateSpecialtyUseCase @Inject constructor(
    private val coreRepository: CoreRepository
) {
    suspend operator fun invoke(
        workshopId: String,
        specialtyId: String,
        name: String,
        description: String?
    ): AppResult<WorkshopSpecialty> {
        val trimmedName = name.trim()
        if (workshopId.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "workshopId", message = "Workshop ID is required"))
        }
        if (specialtyId.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "specialtyId", message = "Specialty ID is required"))
        }
        if (trimmedName.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "name", message = "Specialty name is required"))
        }

        return coreRepository.updateSpecialty(
            workshopId = workshopId,
            specialtyId = specialtyId,
            name = trimmedName,
            description = description?.trim()
        )
    }
}

class DeleteSpecialtyUseCase @Inject constructor(
    private val coreRepository: CoreRepository
) {
    suspend operator fun invoke(workshopId: String, specialtyId: String): AppResult<Unit> {
        if (workshopId.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "workshopId", message = "Workshop ID is required"))
        }
        if (specialtyId.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "specialtyId", message = "Specialty ID is required"))
        }
        return coreRepository.deleteSpecialty(workshopId, specialtyId)
    }
}

class GetSpecialtyRevisionHistoryUseCase @Inject constructor(
    private val coreRepository: CoreRepository
) {
    suspend operator fun invoke(
        workshopId: String,
        specialtyId: String
    ): AppResult<List<SpecialtyRevision>> {
        if (workshopId.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "workshopId", message = "Workshop ID is required"))
        }
        if (specialtyId.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "specialtyId", message = "Specialty ID is required"))
        }
        return coreRepository.getSpecialtyHistory(workshopId.trim(), specialtyId.trim())
    }
}
