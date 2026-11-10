package com.tuxlogic.shiftiq.mobile.feature.shared.application.usecase

import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.feature.shared.domain.model.IdentityDocumentData
import com.tuxlogic.shiftiq.mobile.feature.shared.domain.model.IdentityDocumentType
import com.tuxlogic.shiftiq.mobile.feature.shared.domain.repository.IdentityValidationRepository
import javax.inject.Inject

class LookupIdentityDocumentUseCase @Inject constructor(
    private val identityValidationRepository: IdentityValidationRepository
) {
    suspend operator fun invoke(
        type: IdentityDocumentType,
        number: String
    ): AppResult<IdentityDocumentData> {
        val trimmed = number.trim()
        when (type) {
            IdentityDocumentType.DNI -> {
                if (trimmed.length != 8 || !trimmed.all { it.isDigit() }) {
                    return AppResult.Failure(
                        AppError.Validation(details = "number", message = "DNI must contain exactly 8 numeric digits")
                    )
                }
            }
            IdentityDocumentType.RUC -> {
                if (trimmed.length != 11 || !trimmed.all { it.isDigit() }) {
                    return AppResult.Failure(
                        AppError.Validation(details = "number", message = "RUC must contain exactly 11 numeric digits")
                    )
                }
            }
            IdentityDocumentType.CE -> {
                if (trimmed.length !in 7..12) {
                    return AppResult.Failure(
                        AppError.Validation(details = "number", message = "CE must be between 7 and 12 characters")
                    )
                }
            }
        }

        return identityValidationRepository.lookupDocument(type, trimmed)
    }
}
