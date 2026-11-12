package com.tuxlogic.shiftiq.mobile.feature.core.application.usecase

import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.EmployeeProfile
import com.tuxlogic.shiftiq.mobile.feature.core.domain.repository.CoreRepository
import javax.inject.Inject

class CreateEmployeeProfileUseCase @Inject constructor(
    private val coreRepository: CoreRepository
) {
    suspend operator fun invoke(
        userId: String,
        firstName: String,
        lastName: String,
        documentType: String,
        documentNumber: String,
        phone: String
    ): AppResult<EmployeeProfile> {
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

        return coreRepository.createEmployeeProfile(
            userId = userId,
            firstName = trimmedFirst,
            lastName = trimmedLast,
            documentType = trimmedDocType,
            documentNumber = trimmedDocNum,
            phone = trimmedPhone
        )
    }
}

class GetEmployeeByIdUseCase @Inject constructor(
    private val coreRepository: CoreRepository
) {
    suspend operator fun invoke(employeeId: String): AppResult<EmployeeProfile> {
        if (employeeId.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "employeeId", message = "Employee ID is required"))
        }
        return coreRepository.getEmployeeById(employeeId)
    }
}

class GetEmployeeByUserIdUseCase @Inject constructor(
    private val coreRepository: CoreRepository
) {
    suspend operator fun invoke(userId: String): AppResult<EmployeeProfile> {
        if (userId.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "userId", message = "User ID is required"))
        }
        return coreRepository.getEmployeeByUserId(userId)
    }
}

class GetEmployeeByDocumentNumberUseCase @Inject constructor(
    private val coreRepository: CoreRepository
) {
    suspend operator fun invoke(documentNumber: String): AppResult<EmployeeProfile> {
        val trimmedDoc = documentNumber.trim()
        if (trimmedDoc.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "documentNumber", message = "Document number is required"))
        }
        return coreRepository.getEmployeeByDocumentNumber(trimmedDoc)
    }
}

class UpdateEmployeeProfileUseCase @Inject constructor(
    private val coreRepository: CoreRepository
) {
    suspend operator fun invoke(
        employeeId: String,
        firstName: String,
        lastName: String,
        documentType: String,
        documentNumber: String,
        phone: String
    ): AppResult<EmployeeProfile> {
        val trimmedFirst = firstName.trim()
        val trimmedLast = lastName.trim()
        val trimmedDocType = documentType.trim().uppercase()
        val trimmedDocNum = documentNumber.trim()
        val trimmedPhone = phone.trim()

        if (employeeId.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "employeeId", message = "Employee ID is required"))
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

        return coreRepository.updateEmployeeProfile(
            employeeId = employeeId,
            firstName = trimmedFirst,
            lastName = trimmedLast,
            documentType = trimmedDocType,
            documentNumber = trimmedDocNum,
            phone = trimmedPhone
        )
    }
}

class DeleteEmployeeProfileUseCase @Inject constructor(
    private val coreRepository: CoreRepository
) {
    suspend operator fun invoke(employeeId: String): AppResult<Unit> {
        if (employeeId.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "employeeId", message = "Employee ID is required"))
        }
        return coreRepository.deleteEmployeeProfile(employeeId)
    }
}
