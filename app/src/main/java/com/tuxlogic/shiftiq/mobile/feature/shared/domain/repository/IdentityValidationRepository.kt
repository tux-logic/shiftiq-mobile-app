package com.tuxlogic.shiftiq.mobile.feature.shared.domain.repository

import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.feature.shared.domain.model.IdentityDocumentData
import com.tuxlogic.shiftiq.mobile.feature.shared.domain.model.IdentityDocumentType

interface IdentityValidationRepository {
    suspend fun lookupDocument(type: IdentityDocumentType, number: String): AppResult<IdentityDocumentData>
}
