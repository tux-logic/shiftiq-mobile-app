package com.tuxlogic.shiftiq.mobile.feature.core.data.remote.api

import com.tuxlogic.shiftiq.mobile.feature.core.data.remote.dto.BranchResourceDto
import com.tuxlogic.shiftiq.mobile.feature.core.data.remote.dto.CreateBranchRequestDto
import com.tuxlogic.shiftiq.mobile.feature.core.data.remote.dto.CreateOwnerRequestDto
import com.tuxlogic.shiftiq.mobile.feature.core.data.remote.dto.CreateWorkshopRequestDto
import com.tuxlogic.shiftiq.mobile.feature.core.data.remote.dto.CreateWorkshopSpecialtyRequestDto
import com.tuxlogic.shiftiq.mobile.feature.core.data.remote.dto.OwnerResourceDto
import com.tuxlogic.shiftiq.mobile.feature.core.data.remote.dto.WorkshopResourceDto
import com.tuxlogic.shiftiq.mobile.feature.core.data.remote.dto.WorkshopSpecialtyResourceDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CoreApiService {

    // Workshops
    @GET("api/v1/workshops")
    suspend fun getWorkshops(
        @Query("ownerId") ownerId: String
    ): Response<List<WorkshopResourceDto>>

    @GET("api/v1/workshops/{workshopId}")
    suspend fun getWorkshopById(
        @Path("workshopId") workshopId: String
    ): Response<WorkshopResourceDto>

    @POST("api/v1/workshops")
    suspend fun createWorkshop(
        @Body request: CreateWorkshopRequestDto
    ): Response<WorkshopResourceDto>

    // Branches
    @GET("api/v1/branches")
    suspend fun getBranches(
        @Query("workshopId") workshopId: String
    ): Response<List<BranchResourceDto>>

    @GET("api/v1/branches/{branchId}")
    suspend fun getBranchById(
        @Path("branchId") branchId: String
    ): Response<BranchResourceDto>

    @POST("api/v1/branches")
    suspend fun createBranch(
        @Body request: CreateBranchRequestDto
    ): Response<BranchResourceDto>

    // Specialties
    @GET("api/v1/workshops/{workshopId}/specialties")
    suspend fun getSpecialties(
        @Path("workshopId") workshopId: String,
        @Query("activeOnly") activeOnly: Boolean = true
    ): Response<List<WorkshopSpecialtyResourceDto>>

    @POST("api/v1/workshops/{workshopId}/specialties")
    suspend fun createSpecialty(
        @Path("workshopId") workshopId: String,
        @Body request: CreateWorkshopSpecialtyRequestDto
    ): Response<WorkshopSpecialtyResourceDto>

    // Owner Profile
    @GET("api/v1/owners")
    suspend fun getOwnerByUserId(
        @Query("userId") userId: String
    ): Response<OwnerResourceDto>

    @POST("api/v1/owners")
    suspend fun createOwner(
        @Body request: CreateOwnerRequestDto
    ): Response<OwnerResourceDto>
}
