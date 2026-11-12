package com.tuxlogic.shiftiq.mobile.feature.shared.infrastructure.remote.api

import com.tuxlogic.shiftiq.mobile.feature.shared.infrastructure.remote.dto.IdentityValidationResourceDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface IdentityValidationApiService {

    @GET("api/v1/identity-validations/lookup")
    suspend fun lookupDocument(
        @Query("type") type: String,
        @Query("number") number: String
    ): Response<IdentityValidationResourceDto>
}
