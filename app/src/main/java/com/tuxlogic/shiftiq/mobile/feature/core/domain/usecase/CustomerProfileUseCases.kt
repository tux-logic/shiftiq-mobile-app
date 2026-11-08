package com.tuxlogic.shiftiq.mobile.feature.core.domain.usecase

import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.CustomerProfile
import com.tuxlogic.shiftiq.mobile.feature.core.domain.repository.CoreRepository
import javax.inject.Inject

class CreateCustomerProfileUseCase @Inject constructor(
    private val coreRepository: CoreRepository
) {
    suspend operator fun invoke(
        userId: String,
        firstName: String?,
        lastName: String?,
        businessName: String?,
        documentType: String,
        documentNumber: String,
        phone: String
    ): AppResult<CustomerProfile> {
        val trimmedDocType = documentType.trim().uppercase()
        val trimmedDocNum = documentNumber.trim()
        val trimmedPhone = phone.trim()

        if (userId.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "userId", message = "User ID is required"))
        }
        if (trimmedDocNum.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "documentNumber", message = "Document number is required"))
        }
        if (trimmedPhone.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "phone", message = "Phone number is required"))
        }

        val isCorporate = trimmedDocType == "RUC" || !businessName.isNullOrBlank()
        if (isCorporate && businessName.isNullOrBlank()) {
            return AppResult.Failure(AppError.Validation(details = "businessName", message = "Business name is required for corporate customers"))
        }
        if (!isCorporate && (firstName.isNullOrBlank() || lastName.isNullOrBlank())) {
            return AppResult.Failure(AppError.Validation(details = "names", message = "First and last name are required for individual customers"))
        }

        return coreRepository.createCustomerProfile(
            userId = userId,
            firstName = firstName?.trim(),
            lastName = lastName?.trim(),
            businessName = businessName?.trim(),
            documentType = trimmedDocType,
            documentNumber = trimmedDocNum,
            phone = trimmedPhone
        )
    }
}

class GetCustomerByIdUseCase @Inject constructor(
    private val coreRepository: CoreRepository
) {
    suspend operator fun invoke(customerId: String): AppResult<CustomerProfile> {
        if (customerId.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "customerId", message = "Customer ID is required"))
        }
        return coreRepository.getCustomerById(customerId)
    }
}

class GetCustomerByUserIdUseCase @Inject constructor(
    private val coreRepository: CoreRepository
) {
    suspend operator fun invoke(userId: String): AppResult<CustomerProfile> {
        if (userId.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "userId", message = "User ID is required"))
        }
        return coreRepository.getCustomerByUserId(userId)
    }
}

class UpdateCustomerProfileUseCase @Inject constructor(
    private val coreRepository: CoreRepository
) {
    suspend operator fun invoke(
        customerId: String,
        firstName: String?,
        lastName: String?,
        businessName: String?,
        documentType: String,
        documentNumber: String,
        phone: String
    ): AppResult<CustomerProfile> {
        val trimmedDocType = documentType.trim().uppercase()
        val trimmedDocNum = documentNumber.trim()
        val trimmedPhone = phone.trim()

        if (customerId.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "customerId", message = "Customer ID is required"))
        }
        if (trimmedDocNum.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "documentNumber", message = "Document number is required"))
        }
        if (trimmedPhone.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "phone", message = "Phone number is required"))
        }

        return coreRepository.updateCustomerProfile(
            customerId = customerId,
            firstName = firstName?.trim(),
            lastName = lastName?.trim(),
            businessName = businessName?.trim(),
            documentType = trimmedDocType,
            documentNumber = trimmedDocNum,
            phone = trimmedPhone
        )
    }
}

class DeleteCustomerProfileUseCase @Inject constructor(
    private val coreRepository: CoreRepository
) {
    suspend operator fun invoke(customerId: String): AppResult<Unit> {
        if (customerId.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "customerId", message = "Customer ID is required"))
        }
        return coreRepository.deleteCustomerProfile(customerId)
    }
}
