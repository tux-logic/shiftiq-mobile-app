package com.tuxlogic.shiftiq.mobile.feature.core.domain.repository

import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.Branch
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.BranchSubscription
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.CustomerProfile
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.DocumentProfileLookup
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.EmployeeProfile
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.OwnerProfile
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.UnifiedProfile
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.Workshop
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.WorkshopSpecialty

interface CoreRepository {

    // === Workshops ===
    suspend fun getWorkshops(ownerId: String): AppResult<List<Workshop>>
    suspend fun getWorkshopById(workshopId: String): AppResult<Workshop>
    suspend fun createWorkshop(
        ownerId: String,
        businessName: String,
        brandName: String,
        taxId: String,
        mileageIntervalConfig: Int = 5000
    ): AppResult<Workshop>
    suspend fun updateWorkshop(
        workshopId: String,
        businessName: String,
        brandName: String,
        taxId: String,
        mileageIntervalConfig: Int
    ): AppResult<Workshop>

    // === Specialties ===
    suspend fun getSpecialties(workshopId: String, activeOnly: Boolean = true): AppResult<List<WorkshopSpecialty>>
    suspend fun getSpecialtyById(workshopId: String, specialtyId: String): AppResult<WorkshopSpecialty>
    suspend fun createSpecialty(
        workshopId: String,
        name: String,
        code: String = name.trim().uppercase().replace(" ", "_"),
        description: String? = null
    ): AppResult<WorkshopSpecialty>
    suspend fun updateSpecialty(
        workshopId: String,
        specialtyId: String,
        name: String,
        description: String?
    ): AppResult<WorkshopSpecialty>
    suspend fun deleteSpecialty(workshopId: String, specialtyId: String): AppResult<Unit>

    // === Branches ===
    suspend fun getBranches(workshopId: String): AppResult<List<Branch>>
    suspend fun getBranchById(branchId: String): AppResult<Branch>
    suspend fun createBranch(
        workshopId: String,
        code: String,
        name: String,
        address: String,
        phone: String
    ): AppResult<Branch>
    suspend fun updateBranch(
        branchId: String,
        code: String,
        name: String,
        address: String,
        phone: String
    ): AppResult<Branch>
    suspend fun setActiveBranch(branchId: BranchId): AppResult<Unit>

    // === Branch Subscriptions ===
    suspend fun assignSubscription(
        branchId: String,
        planId: String,
        billingCycle: String,
        cardNumber: String,
        cardHolderName: String,
        expirationDate: String,
        cvv: String
    ): AppResult<BranchSubscription>
    suspend fun cancelSubscription(branchId: String): AppResult<Unit>

    // === Owner Profiles ===
    suspend fun createOwnerProfile(
        userId: String,
        firstName: String,
        lastName: String,
        documentType: String,
        documentNumber: String,
        phone: String
    ): AppResult<OwnerProfile>
    suspend fun getOwnerProfile(userId: String): AppResult<OwnerProfile>
    suspend fun getOwnerById(ownerId: String): AppResult<OwnerProfile>
    suspend fun updateOwnerProfile(
        ownerId: String,
        firstName: String,
        lastName: String,
        documentType: String,
        documentNumber: String,
        phone: String
    ): AppResult<OwnerProfile>
    suspend fun deleteOwnerProfile(ownerId: String): AppResult<Unit>

    // === Customer Profiles ===
    suspend fun createCustomerProfile(
        userId: String,
        firstName: String?,
        lastName: String?,
        businessName: String?,
        documentType: String,
        documentNumber: String,
        phone: String
    ): AppResult<CustomerProfile>
    suspend fun getCustomerById(customerId: String): AppResult<CustomerProfile>
    suspend fun getCustomerByUserId(userId: String): AppResult<CustomerProfile>
    suspend fun updateCustomerProfile(
        customerId: String,
        firstName: String?,
        lastName: String?,
        businessName: String?,
        documentType: String,
        documentNumber: String,
        phone: String
    ): AppResult<CustomerProfile>
    suspend fun deleteCustomerProfile(customerId: String): AppResult<Unit>

    // === Employee Profiles ===
    suspend fun createEmployeeProfile(
        userId: String,
        firstName: String,
        lastName: String,
        documentType: String,
        documentNumber: String,
        phone: String
    ): AppResult<EmployeeProfile>
    suspend fun getEmployeeById(employeeId: String): AppResult<EmployeeProfile>
    suspend fun getEmployeeByUserId(userId: String): AppResult<EmployeeProfile>
    suspend fun getEmployeeByDocumentNumber(documentNumber: String): AppResult<EmployeeProfile>
    suspend fun updateEmployeeProfile(
        employeeId: String,
        firstName: String,
        lastName: String,
        documentType: String,
        documentNumber: String,
        phone: String
    ): AppResult<EmployeeProfile>
    suspend fun deleteEmployeeProfile(employeeId: String): AppResult<Unit>

    // === Unified Profiles & Lookups ===
    suspend fun getProfileMe(): AppResult<UnifiedProfile>
    suspend fun getRolesByUserId(userId: String): AppResult<List<String>>
    suspend fun lookupProfileByDocumentNumber(documentNumber: String): AppResult<DocumentProfileLookup>
}
