package com.tuxlogic.shiftiq.mobile.feature.core.domain.usecase

import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.OwnerProfile
import com.tuxlogic.shiftiq.mobile.feature.core.domain.repository.CoreRepository
import javax.inject.Inject

class GetOwnerProfileUseCase @Inject constructor(
    private val coreRepository: CoreRepository
) {
    suspend operator fun invoke(userId: String): AppResult<OwnerProfile> {
        if (userId.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "userId", message = "ID de usuario requerido"))
        }
        return coreRepository.getOwnerProfile(userId)
    }
}

class CreateOwnerProfileUseCase @Inject constructor(
    private val coreRepository: CoreRepository
) {
    suspend operator fun invoke(
        userId: String,
        firstName: String,
        lastName: String,
        documentType: String,
        documentNumber: String,
        phone: String
    ): AppResult<OwnerProfile> {
        val trimmedFirst = firstName.trim()
        val trimmedLast = lastName.trim()
        val trimmedDocType = documentType.trim().uppercase()
        val trimmedDocNum = documentNumber.trim()
        val trimmedPhone = phone.trim()

        if (userId.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "userId", message = "ID de usuario requerido"))
        }
        if (trimmedFirst.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "firstName", message = "Nombres requeridos"))
        }
        if (trimmedLast.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "lastName", message = "Apellidos requeridos"))
        }
        if (trimmedDocNum.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "documentNumber", message = "Número de documento requerido"))
        }
        if (trimmedPhone.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "phone", message = "Teléfono requerido"))
        }

        return coreRepository.createOwnerProfile(
            userId = userId,
            firstName = trimmedFirst,
            lastName = trimmedLast,
            documentType = trimmedDocType,
            documentNumber = trimmedDocNum,
            phone = trimmedPhone
        )
    }
}
