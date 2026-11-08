package com.tuxlogic.shiftiq.mobile.feature.core.domain.usecase

import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.DocumentProfileLookup
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.UnifiedProfile
import com.tuxlogic.shiftiq.mobile.feature.core.domain.repository.CoreRepository
import javax.inject.Inject

class GetUnifiedProfileUseCase @Inject constructor(
    private val coreRepository: CoreRepository
) {
    suspend operator fun invoke(): AppResult<UnifiedProfile> {
        return coreRepository.getProfileMe()
    }
}

class GetRolesByUserIdUseCase @Inject constructor(
    private val coreRepository: CoreRepository
) {
    suspend operator fun invoke(userId: String): AppResult<List<String>> {
        val trimmedUserId = userId.trim()
        if (trimmedUserId.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "userId", message = "User ID is required"))
        }
        return coreRepository.getRolesByUserId(trimmedUserId)
    }
}

class LookupProfileByDocumentUseCase @Inject constructor(
    private val coreRepository: CoreRepository
) {
    suspend operator fun invoke(documentNumber: String): AppResult<DocumentProfileLookup> {
        val trimmedDoc = documentNumber.trim()
        if (trimmedDoc.isBlank()) {
            return AppResult.Failure(AppError.Validation(details = "documentNumber", message = "Document number is required"))
        }
        return coreRepository.lookupProfileByDocumentNumber(trimmedDoc)
    }
}
