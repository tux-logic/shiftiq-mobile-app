package com.tuxlogic.shiftiq.mobile.feature.core.data.remote.api

import com.tuxlogic.shiftiq.mobile.feature.core.data.remote.dto.AssignBranchSubscriptionRequestDto
import com.tuxlogic.shiftiq.mobile.feature.core.data.remote.dto.BranchResourceDto
import com.tuxlogic.shiftiq.mobile.feature.core.data.remote.dto.BranchSubscriptionResourceDto
import com.tuxlogic.shiftiq.mobile.feature.core.data.remote.dto.CreateBranchRequestDto
import com.tuxlogic.shiftiq.mobile.feature.core.data.remote.dto.CreateCustomerRequestDto
import com.tuxlogic.shiftiq.mobile.feature.core.data.remote.dto.CreateEmployeeRequestDto
import com.tuxlogic.shiftiq.mobile.feature.core.data.remote.dto.CreateOwnerRequestDto
import com.tuxlogic.shiftiq.mobile.feature.core.data.remote.dto.CreateWorkshopRequestDto
import com.tuxlogic.shiftiq.mobile.feature.core.data.remote.dto.CreateWorkshopSpecialtyRequestDto
import com.tuxlogic.shiftiq.mobile.feature.core.data.remote.dto.CustomerResourceDto
import com.tuxlogic.shiftiq.mobile.feature.core.data.remote.dto.DocumentProfileLookupDto
import com.tuxlogic.shiftiq.mobile.feature.core.data.remote.dto.EmployeeResourceDto
import com.tuxlogic.shiftiq.mobile.feature.core.data.remote.dto.OwnerResourceDto
import com.tuxlogic.shiftiq.mobile.feature.core.data.remote.dto.UnifiedProfileResourceDto
import com.tuxlogic.shiftiq.mobile.feature.core.data.remote.dto.UpdateBranchRequestDto
import com.tuxlogic.shiftiq.mobile.feature.core.data.remote.dto.UpdateCustomerRequestDto
import com.tuxlogic.shiftiq.mobile.feature.core.data.remote.dto.UpdateEmployeeRequestDto
import com.tuxlogic.shiftiq.mobile.feature.core.data.remote.dto.UpdateOwnerRequestDto
import com.tuxlogic.shiftiq.mobile.feature.core.data.remote.dto.UpdateWorkshopRequestDto
import com.tuxlogic.shiftiq.mobile.feature.core.data.remote.dto.UpdateWorkshopSpecialtyRequestDto
import com.tuxlogic.shiftiq.mobile.feature.core.data.remote.dto.WorkshopResourceDto
import com.tuxlogic.shiftiq.mobile.feature.core.data.remote.dto.WorkshopSpecialtyResourceDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface CoreApiService {

    // === Workshops ===

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

    @PUT("api/v1/workshops/{workshopId}")
    suspend fun updateWorkshop(
        @Path("workshopId") workshopId: String,
        @Body request: UpdateWorkshopRequestDto
    ): Response<WorkshopResourceDto>

    // === Workshop Specialties ===

    @GET("api/v1/workshops/{workshopId}/specialties")
    suspend fun getSpecialties(
        @Path("workshopId") workshopId: String,
        @Query("activeOnly") activeOnly: Boolean = true
    ): Response<List<WorkshopSpecialtyResourceDto>>

    @GET("api/v1/workshops/{workshopId}/specialties/{specialtyId}")
    suspend fun getSpecialtyById(
        @Path("workshopId") workshopId: String,
        @Path("specialtyId") specialtyId: String
    ): Response<WorkshopSpecialtyResourceDto>

    @POST("api/v1/workshops/{workshopId}/specialties")
    suspend fun createSpecialty(
        @Path("workshopId") workshopId: String,
        @Body request: CreateWorkshopSpecialtyRequestDto
    ): Response<WorkshopSpecialtyResourceDto>

    @PUT("api/v1/workshops/{workshopId}/specialties/{specialtyId}")
    suspend fun updateSpecialty(
        @Path("workshopId") workshopId: String,
        @Path("specialtyId") specialtyId: String,
        @Body request: UpdateWorkshopSpecialtyRequestDto
    ): Response<WorkshopSpecialtyResourceDto>

    @DELETE("api/v1/workshops/{workshopId}/specialties/{specialtyId}")
    suspend fun deleteSpecialty(
        @Path("workshopId") workshopId: String,
        @Path("specialtyId") specialtyId: String
    ): Response<Unit>

    // === Branches ===

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

    @PUT("api/v1/branches/{branchId}")
    suspend fun updateBranch(
        @Path("branchId") branchId: String,
        @Body request: UpdateBranchRequestDto
    ): Response<BranchResourceDto>

    // === Branch Subscriptions ===

    @POST("api/v1/branches/{branchId}/subscriptions")
    suspend fun assignBranchSubscription(
        @Path("branchId") branchId: String,
        @Body request: AssignBranchSubscriptionRequestDto
    ): Response<BranchSubscriptionResourceDto>

    @DELETE("api/v1/branches/{branchId}/subscription")
    suspend fun cancelBranchSubscription(
        @Path("branchId") branchId: String
    ): Response<Unit>

    // === Owner Profiles ===

    @POST("api/v1/owners")
    suspend fun createOwner(
        @Body request: CreateOwnerRequestDto
    ): Response<OwnerResourceDto>

    @GET("api/v1/owners/{ownerId}")
    suspend fun getOwnerById(
        @Path("ownerId") ownerId: String
    ): Response<OwnerResourceDto>

    @GET("api/v1/owners")
    suspend fun getOwnerByUserId(
        @Query("userId") userId: String
    ): Response<OwnerResourceDto>

    @PUT("api/v1/owners/{ownerId}")
    suspend fun updateOwner(
        @Path("ownerId") ownerId: String,
        @Body request: UpdateOwnerRequestDto
    ): Response<OwnerResourceDto>

    @DELETE("api/v1/owners/{ownerId}")
    suspend fun deleteOwner(
        @Path("ownerId") ownerId: String
    ): Response<Unit>

    // === Customer Profiles ===

    @POST("api/v1/customers")
    suspend fun createCustomer(
        @Body request: CreateCustomerRequestDto
    ): Response<CustomerResourceDto>

    @GET("api/v1/customers/{customerId}")
    suspend fun getCustomerById(
        @Path("customerId") customerId: String
    ): Response<CustomerResourceDto>

    @GET("api/v1/customers")
    suspend fun getCustomerByUserId(
        @Query("userId") userId: String
    ): Response<CustomerResourceDto>

    @PUT("api/v1/customers/{customerId}")
    suspend fun updateCustomer(
        @Path("customerId") customerId: String,
        @Body request: UpdateCustomerRequestDto
    ): Response<CustomerResourceDto>

    @DELETE("api/v1/customers/{customerId}")
    suspend fun deleteCustomer(
        @Path("customerId") customerId: String
    ): Response<Unit>

    // === Employee Profiles ===

    @POST("api/v1/employees")
    suspend fun createEmployee(
        @Body request: CreateEmployeeRequestDto
    ): Response<EmployeeResourceDto>

    @GET("api/v1/employees/{employeeId}")
    suspend fun getEmployeeById(
        @Path("employeeId") employeeId: String
    ): Response<EmployeeResourceDto>

    @GET("api/v1/employees")
    suspend fun getEmployeeByUserId(
        @Query("userId") userId: String
    ): Response<EmployeeResourceDto>

    @GET("api/v1/employees")
    suspend fun getEmployeeByDocumentNumber(
        @Query("documentNumber") documentNumber: String
    ): Response<EmployeeResourceDto>

    @PUT("api/v1/employees/{employeeId}")
    suspend fun updateEmployee(
        @Path("employeeId") employeeId: String,
        @Body request: UpdateEmployeeRequestDto
    ): Response<EmployeeResourceDto>

    @DELETE("api/v1/employees/{employeeId}")
    suspend fun deleteEmployee(
        @Path("employeeId") employeeId: String
    ): Response<Unit>

    // === Unified Profiles & Lookups ===

    @GET("api/v1/profiles/me")
    suspend fun getProfileMe(): Response<UnifiedProfileResourceDto>

    @GET("api/v1/profiles/roles")
    suspend fun getRolesByUserId(
        @Query("userId") userId: String
    ): Response<List<String>>

    @GET("api/v1/profiles")
    suspend fun lookupProfileByDocumentNumber(
        @Query("documentNumber") documentNumber: String
    ): Response<DocumentProfileLookupDto>
}
