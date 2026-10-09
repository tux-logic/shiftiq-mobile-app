package com.tuxlogic.shiftiq.mobile.feature.core.domain.usecase

import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.Branch
import com.tuxlogic.shiftiq.mobile.feature.core.domain.repository.CoreRepository
import javax.inject.Inject

class GetBranchesUseCase @Inject constructor(
    private val coreRepository: CoreRepository
) {
    suspend operator fun invoke(workshopId: String): AppResult<List<Branch>> {
        if (workshopId.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "workshopId", message = "ID de taller requerido"))
        }
        return coreRepository.getBranches(workshopId)
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
            return AppResult.Failure(AppError.Validation(details = "workshopId", message = "ID de taller requerido"))
        }
        if (trimmedCode.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "code", message = "Código de sede requerido (ej. SEDE-01)"))
        }
        if (trimmedName.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "name", message = "Nombre de sede requerido"))
        }
        if (trimmedAddress.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "address", message = "Dirección física requerida"))
        }
        if (trimmedPhone.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "phone", message = "Teléfono de contacto requerido"))
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

class SetActiveBranchUseCase @Inject constructor(
    private val coreRepository: CoreRepository
) {
    suspend operator fun invoke(branchId: BranchId): AppResult<Unit> {
        return coreRepository.setActiveBranch(branchId)
    }
}
