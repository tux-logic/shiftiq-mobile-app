package com.tuxlogic.shiftiq.mobile.feature.core.application.usecase

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
            return AppResult.Failure(AppError.Validation(details = "userId", message = "User ID is required"))
        }
        return coreRepository.getOwnerProfile(userId)
    }
}

class GetOwnerByIdUseCase @Inject constructor(
    private val coreRepository: CoreRepository
) {
    suspend operator fun invoke(ownerId: String): AppResult<OwnerProfile> {
        if (ownerId.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "ownerId", message = "Owner ID is required"))
        }
        return coreRepository.getOwnerById(ownerId)
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
            return AppResult.Failure(AppError.Validation(details = "userId", message = "User ID is required"))
        }
        if (trimmedFirst.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "firstName", message = "First name is required"))
        }
        if (trimmedLast.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "lastName", message = "Last name is required"))
        }
        if (trimmedDocNum.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "documentNumber", message = "Document number is required"))
        }
        if (trimmedPhone.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "phone", message = "Phone number is required"))
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

class UpdateOwnerProfileUseCase @Inject constructor(
    private val coreRepository: CoreRepository
) {
    suspend operator fun invoke(
        ownerId: String,
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

        if (ownerId.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "ownerId", message = "Owner ID is required"))
        }
        if (trimmedFirst.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "firstName", message = "First name is required"))
        }
        if (trimmedLast.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "lastName", message = "Last name is required"))
        }
        if (trimmedDocNum.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "documentNumber", message = "Document number is required"))
        }
        if (trimmedPhone.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "phone", message = "Phone number is required"))
        }

        return coreRepository.updateOwnerProfile(
            ownerId = ownerId,
            firstName = trimmedFirst,
            lastName = trimmedLast,
            documentType = trimmedDocType,
            documentNumber = trimmedDocNum,
            phone = trimmedPhone
        )
    }
}

class DeleteOwnerProfileUseCase @Inject constructor(
    private val coreRepository: CoreRepository
) {
    suspend operator fun invoke(ownerId: String): AppResult<Unit> {
        if (ownerId.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "ownerId", message = "Owner ID is required"))
        }
        return coreRepository.deleteOwnerProfile(ownerId)
    }
}
