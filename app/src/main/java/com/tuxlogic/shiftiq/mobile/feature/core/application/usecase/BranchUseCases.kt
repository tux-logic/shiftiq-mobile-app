package com.tuxlogic.shiftiq.mobile.feature.core.application.usecase

import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.Branch
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.BranchSubscription
import com.tuxlogic.shiftiq.mobile.feature.core.domain.repository.CoreRepository
import javax.inject.Inject

class GetBranchesUseCase @Inject constructor(
    private val coreRepository: CoreRepository
) {
    suspend operator fun invoke(workshopId: String): AppResult<List<Branch>> {
        if (workshopId.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "workshopId", message = "Workshop ID is required"))
        }
        return coreRepository.getBranches(workshopId)
    }
}

class GetBranchByIdUseCase @Inject constructor(
    private val coreRepository: CoreRepository
) {
    suspend operator fun invoke(branchId: String): AppResult<Branch> {
        if (branchId.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "branchId", message = "Branch ID is required"))
        }
        return coreRepository.getBranchById(branchId)
    }
}

class CreateBranchUseCase @Inject constructor(
    private val coreRepository: CoreRepository
) {
    suspend operator fun invoke(
        workshopId: String,
        code: String,
        name: String,
        address: String,
        phone: String
    ): AppResult<Branch> {
        val trimmedCode = code.trim().uppercase()
        val trimmedName = name.trim()
        val trimmedAddress = address.trim()
        val trimmedPhone = phone.trim()

        if (workshopId.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "workshopId", message = "Workshop ID is required"))
        }
        if (trimmedCode.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "code", message = "Branch code is required"))
        }
        if (trimmedName.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "name", message = "Branch name is required"))
        }
        if (trimmedAddress.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "address", message = "Physical address is required"))
        }
        if (trimmedPhone.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "phone", message = "Phone number is required"))
        }

        return coreRepository.createBranch(
            workshopId = workshopId,
            code = trimmedCode,
            name = trimmedName,
            address = trimmedAddress,
            phone = trimmedPhone
        )
    }
}

class UpdateBranchUseCase @Inject constructor(
    private val coreRepository: CoreRepository
) {
    suspend operator fun invoke(
        branchId: String,
        code: String,
        name: String,
        address: String,
        phone: String
    ): AppResult<Branch> {
        val trimmedCode = code.trim().uppercase()
        val trimmedName = name.trim()
        val trimmedAddress = address.trim()
        val trimmedPhone = phone.trim()

        if (branchId.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "branchId", message = "Branch ID is required"))
        }
        if (trimmedCode.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "code", message = "Branch code is required"))
        }
        if (trimmedName.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "name", message = "Branch name is required"))
        }
        if (trimmedAddress.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "address", message = "Physical address is required"))
        }
        if (trimmedPhone.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "phone", message = "Phone number is required"))
        }

        return coreRepository.updateBranch(
            branchId = branchId,
            code = trimmedCode,
            name = trimmedName,
            address = trimmedAddress,
            phone = trimmedPhone
        )
    }
}

class SetActiveBranchUseCase @Inject constructor(
    private val coreRepository: CoreRepository
) {
    suspend operator fun invoke(branchId: BranchId): AppResult<Unit> {
        return coreRepository.setActiveBranch(branchId)
    }
}

class AssignBranchSubscriptionUseCase @Inject constructor(
    private val coreRepository: CoreRepository
) {
    suspend operator fun invoke(
        branchId: String,
        planId: String,
        billingCycle: String,
        cardNumber: String,
        cardHolderName: String,
        expirationDate: String,
        cvv: String
    ): AppResult<BranchSubscription> {
        if (branchId.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "branchId", message = "Branch ID is required"))
        }
        if (planId.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "planId", message = "Plan ID is required"))
        }
        if (billingCycle != "MONTHLY" && billingCycle != "ANNUAL") {
            return AppResult.Failure(AppError.Validation(details = "billingCycle", message = "Billing cycle must be MONTHLY or ANNUAL"))
        }
        if (cardNumber.length != 16 || !cardNumber.all { it.isDigit() }) {
            return AppResult.Failure(AppError.Validation(details = "cardNumber", message = "Card number must contain exactly 16 digits"))
        }
        if (cardHolderName.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "cardHolderName", message = "Cardholder name is required"))
        }
        if (expirationDate.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "expirationDate", message = "Expiration date is required"))
        }
        if (cvv.length !in 3..4 || !cvv.all { it.isDigit() }) {
            return AppResult.Failure(AppError.Validation(details = "cvv", message = "CVV must be 3 or 4 digits"))
        }

        return coreRepository.assignSubscription(
            branchId = branchId,
            planId = planId,
            billingCycle = billingCycle,
            cardNumber = cardNumber,
            cardHolderName = cardHolderName.trim(),
            expirationDate = expirationDate.trim(),
            cvv = cvv.trim()
        )
    }
}

class CancelBranchSubscriptionUseCase @Inject constructor(
    private val coreRepository: CoreRepository
) {
    suspend operator fun invoke(branchId: String): AppResult<Unit> {
        if (branchId.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "branchId", message = "Branch ID is required"))
        }
        return coreRepository.cancelSubscription(branchId)
    }
}
