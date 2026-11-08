package com.tuxlogic.shiftiq.mobile.feature.shared.infrastructure.repository

import com.tuxlogic.shiftiq.mobile.core.common.dispatchers.DispatcherProvider
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.common.result.map
import com.tuxlogic.shiftiq.mobile.core.network.safeApiCall
import com.tuxlogic.shiftiq.mobile.feature.shared.infrastructure.mapper.toDomain
import com.tuxlogic.shiftiq.mobile.feature.shared.infrastructure.remote.api.IdentityValidationApiService
import com.tuxlogic.shiftiq.mobile.feature.shared.domain.model.IdentityDocumentData
import com.tuxlogic.shiftiq.mobile.feature.shared.domain.model.IdentityDocumentType
import com.tuxlogic.shiftiq.mobile.feature.shared.domain.repository.IdentityValidationRepository
import kotlinx.coroutines.withContext
import javax.inject.Inject

class IdentityValidationRepositoryImpl @Inject constructor(
    private val identityValidationApiService: IdentityValidationApiService,
    private val dispatchers: DispatcherProvider
) : IdentityValidationRepository {

    override suspend fun lookupDocument(
        type: IdentityDocumentType,
        number: String
    ): AppResult<IdentityDocumentData> = withContext(dispatchers.io) {
        safeApiCall {
            identityValidationApiService.lookupDocument(
                type = type.name,
                number = number.trim()
            )
        }.map { it.toDomain() }
    }
}
