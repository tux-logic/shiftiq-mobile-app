package com.tuxlogic.shiftiq.mobile.feature.operations.infrastructure.remote.api

import com.tuxlogic.shiftiq.mobile.feature.operations.infrastructure.remote.dto.CreateServiceRequestDto
import com.tuxlogic.shiftiq.mobile.feature.operations.infrastructure.remote.dto.ServiceResourceDto
import com.tuxlogic.shiftiq.mobile.feature.operations.infrastructure.remote.dto.UpdateServiceRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ServicesApiService {

    @GET("api/v1/services")
    suspend fun getServices(
        @Query("branchId") branchId: String
    ): Response<List<ServiceResourceDto>>

    @POST("api/v1/services")
    suspend fun createService(
        @Body request: CreateServiceRequestDto
    ): Response<ServiceResourceDto>

    @PUT("api/v1/services/{serviceId}")
    suspend fun updateService(
        @Path("serviceId") serviceId: String,
        @Body request: UpdateServiceRequestDto
    ): Response<ServiceResourceDto>

    @DELETE("api/v1/services/{serviceId}")
    suspend fun deleteService(
        @Path("serviceId") serviceId: String
    ): Response<Unit>
}
