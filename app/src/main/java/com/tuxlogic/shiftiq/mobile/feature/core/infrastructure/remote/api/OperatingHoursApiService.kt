package com.tuxlogic.shiftiq.mobile.feature.core.infrastructure.remote.api

import com.tuxlogic.shiftiq.mobile.feature.core.infrastructure.remote.dto.OperatingHoursDayDto
import com.tuxlogic.shiftiq.mobile.feature.core.infrastructure.remote.dto.OperatingHoursExceptionDto
import com.tuxlogic.shiftiq.mobile.feature.core.infrastructure.remote.dto.OperatingHoursExceptionRequestDto
import com.tuxlogic.shiftiq.mobile.feature.core.infrastructure.remote.dto.OperatingHoursRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface OperatingHoursApiService {
    @GET("api/v1/branches/{branchId}/operating-hours")
    suspend fun getBranchHours(@Path("branchId") branchId: String): Response<List<OperatingHoursDayDto>>

    @PUT("api/v1/branches/{branchId}/operating-hours")
    suspend fun updateBranchHours(@Path("branchId") branchId: String, @Body request: OperatingHoursRequestDto): Response<List<OperatingHoursDayDto>>

    @DELETE("api/v1/branches/{branchId}/operating-hours")
    suspend fun deleteBranchHours(@Path("branchId") branchId: String): Response<Unit>

    @GET("api/v1/branches/{branchId}/operating-hours/exceptions")
    suspend fun getBranchExceptions(@Path("branchId") branchId: String): Response<List<OperatingHoursExceptionDto>>

    @POST("api/v1/branches/{branchId}/operating-hours/exceptions")
    suspend fun createBranchException(@Path("branchId") branchId: String, @Body request: OperatingHoursExceptionRequestDto): Response<OperatingHoursExceptionDto>

    @DELETE("api/v1/branches/{branchId}/operating-hours/exceptions/{exceptionId}")
    suspend fun deleteBranchException(@Path("branchId") branchId: String, @Path("exceptionId") exceptionId: String): Response<Unit>

    @GET("api/v1/workshops/{workshopId}/operating-hours")
    suspend fun getWorkshopHours(@Path("workshopId") workshopId: String): Response<List<OperatingHoursDayDto>>

    @PUT("api/v1/workshops/{workshopId}/operating-hours")
    suspend fun updateWorkshopHours(@Path("workshopId") workshopId: String, @Body request: OperatingHoursRequestDto): Response<List<OperatingHoursDayDto>>

    @GET("api/v1/workshops/{workshopId}/operating-hours/exceptions")
    suspend fun getWorkshopExceptions(@Path("workshopId") workshopId: String): Response<List<OperatingHoursExceptionDto>>

    @POST("api/v1/workshops/{workshopId}/operating-hours/exceptions")
    suspend fun createWorkshopException(@Path("workshopId") workshopId: String, @Body request: OperatingHoursExceptionRequestDto): Response<OperatingHoursExceptionDto>

    @DELETE("api/v1/workshops/{workshopId}/operating-hours/exceptions/{exceptionId}")
    suspend fun deleteWorkshopException(@Path("workshopId") workshopId: String, @Path("exceptionId") exceptionId: String): Response<Unit>
}
