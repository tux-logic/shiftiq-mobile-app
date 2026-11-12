package com.tuxlogic.shiftiq.mobile.feature.core.infrastructure.repository

import com.tuxlogic.shiftiq.mobile.core.common.dispatchers.DispatcherProvider
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.common.result.map
import com.tuxlogic.shiftiq.mobile.core.datastore.SessionDataStore
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.core.network.safeApiCall
import com.tuxlogic.shiftiq.mobile.feature.core.infrastructure.mapper.toDomain
import com.tuxlogic.shiftiq.mobile.feature.core.infrastructure.remote.api.CoreApiService
import com.tuxlogic.shiftiq.mobile.feature.core.infrastructure.remote.dto.AssignBranchSubscriptionRequestDto
import com.tuxlogic.shiftiq.mobile.feature.core.infrastructure.remote.dto.CreateBranchRequestDto
import com.tuxlogic.shiftiq.mobile.feature.core.infrastructure.remote.dto.CreateCustomerRequestDto
import com.tuxlogic.shiftiq.mobile.feature.core.infrastructure.remote.dto.CreateEmployeeRequestDto
import com.tuxlogic.shiftiq.mobile.feature.core.infrastructure.remote.dto.CreateOwnerRequestDto
import com.tuxlogic.shiftiq.mobile.feature.core.infrastructure.remote.dto.CreateWorkshopRequestDto
import com.tuxlogic.shiftiq.mobile.feature.core.infrastructure.remote.dto.CreateWorkshopSpecialtyRequestDto
import com.tuxlogic.shiftiq.mobile.feature.core.infrastructure.remote.dto.UpdateBranchRequestDto
import com.tuxlogic.shiftiq.mobile.feature.core.infrastructure.remote.dto.UpdateCustomerRequestDto
import com.tuxlogic.shiftiq.mobile.feature.core.infrastructure.remote.dto.UpdateEmployeeRequestDto
import com.tuxlogic.shiftiq.mobile.feature.core.infrastructure.remote.dto.UpdateOwnerRequestDto
import com.tuxlogic.shiftiq.mobile.feature.core.infrastructure.remote.dto.UpdateWorkshopRequestDto
import com.tuxlogic.shiftiq.mobile.feature.core.infrastructure.remote.dto.UpdateWorkshopSpecialtyRequestDto
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.Branch
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.BranchSubscription
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.CustomerProfile
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.DocumentProfileLookup
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.EmployeeProfile
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.OwnerProfile
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.SpecialtyRevision
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.UnifiedProfile
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.Workshop
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.WorkshopSpecialty
import com.tuxlogic.shiftiq.mobile.feature.core.domain.repository.CoreRepository
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CoreRepositoryImpl @Inject constructor(
    private val coreApiService: CoreApiService,
    private val sessionDataStore: SessionDataStore,
    private val dispatchers: DispatcherProvider
) : CoreRepository {

    // === Workshops ===

    override suspend fun getWorkshops(ownerId: String): AppResult<List<Workshop>> =
        withContext(dispatchers.io) {
            safeApiCall { coreApiService.getWorkshops(ownerId) }
                .map { dtoList -> dtoList.map { it.toDomain() } }
        }

    override suspend fun getWorkshopById(workshopId: String): AppResult<Workshop> =
        withContext(dispatchers.io) {
            safeApiCall { coreApiService.getWorkshopById(workshopId) }
                .map { it.toDomain() }
        }

    override suspend fun createWorkshop(
        ownerId: String,
        businessName: String,
        brandName: String,
        taxId: String,
        mileageIntervalConfig: Int
    ): AppResult<Workshop> = withContext(dispatchers.io) {
        val request = CreateWorkshopRequestDto(
            ownerId = ownerId,
            businessName = businessName,
            brandName = brandName,
            taxId = taxId,
            mileageIntervalConfig = mileageIntervalConfig
        )
        safeApiCall { coreApiService.createWorkshop(request) }
            .map { it.toDomain() }
    }

    override suspend fun updateWorkshop(
        workshopId: String,
        businessName: String,
        brandName: String,
        taxId: String,
        mileageIntervalConfig: Int
    ): AppResult<Workshop> = withContext(dispatchers.io) {
        val request = UpdateWorkshopRequestDto(
            businessName = businessName,
            brandName = brandName,
            taxId = taxId,
            mileageIntervalConfig = mileageIntervalConfig
        )
        safeApiCall { coreApiService.updateWorkshop(workshopId, request) }
            .map { it.toDomain() }
    }

    // === Specialties ===

    override suspend fun getSpecialties(workshopId: String, activeOnly: Boolean): AppResult<List<WorkshopSpecialty>> =
        withContext(dispatchers.io) {
            safeApiCall { coreApiService.getSpecialties(workshopId, activeOnly) }
                .map { dtoList -> dtoList.map { it.toDomain() } }
        }

    override suspend fun getSpecialtyById(workshopId: String, specialtyId: String): AppResult<WorkshopSpecialty> =
        withContext(dispatchers.io) {
            safeApiCall { coreApiService.getSpecialtyById(workshopId, specialtyId) }
                .map { it.toDomain() }
        }

    override suspend fun createSpecialty(
        workshopId: String,
        name: String,
        code: String,
        description: String?
    ): AppResult<WorkshopSpecialty> = withContext(dispatchers.io) {
        val request = CreateWorkshopSpecialtyRequestDto(
            name = name,
            code = code,
            description = description
        )
        safeApiCall { coreApiService.createSpecialty(workshopId, request) }
            .map { it.toDomain() }
    }

    override suspend fun updateSpecialty(
        workshopId: String,
        specialtyId: String,
        name: String,
        description: String?
    ): AppResult<WorkshopSpecialty> = withContext(dispatchers.io) {
        val request = UpdateWorkshopSpecialtyRequestDto(name = name, description = description)
        safeApiCall { coreApiService.updateSpecialty(workshopId, specialtyId, request) }
            .map { it.toDomain() }
    }

    override suspend fun deleteSpecialty(workshopId: String, specialtyId: String): AppResult<Unit> =
        withContext(dispatchers.io) {
            safeApiCall { coreApiService.deleteSpecialty(workshopId, specialtyId) }
        }

    override suspend fun getSpecialtyHistory(
        workshopId: String,
        specialtyId: String
    ): AppResult<List<SpecialtyRevision>> = withContext(dispatchers.io) {
        safeApiCall { coreApiService.getSpecialtyHistory(workshopId, specialtyId) }
            .map { list -> list.map { it.toDomain() } }
    }

    // === Branches ===

    override suspend fun getBranches(workshopId: String): AppResult<List<Branch>> =
        withContext(dispatchers.io) {
            safeApiCall { coreApiService.getBranches(workshopId) }
                .map { dtoList -> dtoList.map { it.toDomain() } }
        }

    override suspend fun getBranchById(branchId: String): AppResult<Branch> =
        withContext(dispatchers.io) {
            safeApiCall { coreApiService.getBranchById(branchId) }
                .map { it.toDomain() }
        }

    override suspend fun createBranch(
        workshopId: String,
        code: String,
        name: String,
        address: String,
        phone: String
    ): AppResult<Branch> = withContext(dispatchers.io) {
        val request = CreateBranchRequestDto(
            workshopId = workshopId,
            code = code,
            name = name,
            address = address,
            phone = phone
        )
        safeApiCall { coreApiService.createBranch(request) }
            .map { it.toDomain() }
    }

    override suspend fun updateBranch(
        branchId: String,
        code: String,
        name: String,
        address: String,
        phone: String
    ): AppResult<Branch> = withContext(dispatchers.io) {
        val request = UpdateBranchRequestDto(
            code = code,
            name = name,
            address = address,
            phone = phone
        )
        safeApiCall { coreApiService.updateBranch(branchId, request) }
            .map { it.toDomain() }
    }

    override suspend fun setActiveBranch(branchId: BranchId): AppResult<Unit> =
        withContext(dispatchers.io) {
            sessionDataStore.setActiveBranchId(branchId)
            AppResult.Success(Unit)
        }

    // === Branch Subscriptions ===

    override suspend fun assignSubscription(
        branchId: String,
        planId: String,
        billingCycle: String,
        cardNumber: String,
        cardHolderName: String,
        expirationDate: String,
        cvv: String
    ): AppResult<BranchSubscription> = withContext(dispatchers.io) {
        val request = AssignBranchSubscriptionRequestDto(
            planId = planId,
            billingCycle = billingCycle,
            cardNumber = cardNumber,
            cardHolderName = cardHolderName,
            expirationDate = expirationDate,
            cvv = cvv
        )
        safeApiCall { coreApiService.assignBranchSubscription(branchId, request) }
            .map { it.toDomain() }
    }

    override suspend fun cancelSubscription(branchId: String): AppResult<Unit> =
        withContext(dispatchers.io) {
            safeApiCall { coreApiService.cancelBranchSubscription(branchId) }
        }

    // === Owner Profiles ===

    override suspend fun createOwnerProfile(
        userId: String,
        firstName: String,
        lastName: String,
        documentType: String,
        documentNumber: String,
        phone: String
    ): AppResult<OwnerProfile> = withContext(dispatchers.io) {
        val request = CreateOwnerRequestDto(
            userId = userId,
            firstName = firstName,
            lastName = lastName,
            documentType = documentType,
            documentNumber = documentNumber,
            phone = phone
        )
        safeApiCall { coreApiService.createOwner(request) }
            .map { it.toDomain() }
    }

    override suspend fun getOwnerProfile(userId: String): AppResult<OwnerProfile> =
        withContext(dispatchers.io) {
            safeApiCall { coreApiService.getOwnerByUserId(userId) }
                .map { it.toDomain() }
        }

    override suspend fun getOwnerById(ownerId: String): AppResult<OwnerProfile> =
        withContext(dispatchers.io) {
            safeApiCall { coreApiService.getOwnerById(ownerId) }
                .map { it.toDomain() }
        }

    override suspend fun updateOwnerProfile(
        ownerId: String,
        firstName: String,
        lastName: String,
        documentType: String,
        documentNumber: String,
        phone: String
    ): AppResult<OwnerProfile> = withContext(dispatchers.io) {
        val request = UpdateOwnerRequestDto(
            firstName = firstName,
            lastName = lastName,
            documentType = documentType,
            documentNumber = documentNumber,
            phone = phone
        )
        safeApiCall { coreApiService.updateOwner(ownerId, request) }
            .map { it.toDomain() }
    }

    override suspend fun deleteOwnerProfile(ownerId: String): AppResult<Unit> =
        withContext(dispatchers.io) {
            safeApiCall { coreApiService.deleteOwner(ownerId) }
        }

    // === Customer Profiles ===

    override suspend fun createCustomerProfile(
        userId: String,
        firstName: String?,
        lastName: String?,
        businessName: String?,
        documentType: String,
        documentNumber: String,
        phone: String
    ): AppResult<CustomerProfile> = withContext(dispatchers.io) {
        val request = CreateCustomerRequestDto(
            userId = userId,
            firstName = firstName,
            lastName = lastName,
            businessName = businessName,
            documentType = documentType,
            documentNumber = documentNumber,
            phone = phone
        )
        safeApiCall { coreApiService.createCustomer(request) }
            .map { it.toDomain() }
    }

    override suspend fun getCustomerById(customerId: String): AppResult<CustomerProfile> =
        withContext(dispatchers.io) {
            safeApiCall { coreApiService.getCustomerById(customerId) }
                .map { it.toDomain() }
        }

    override suspend fun getCustomerByUserId(userId: String): AppResult<CustomerProfile> =
        withContext(dispatchers.io) {
            safeApiCall { coreApiService.getCustomerByUserId(userId) }
                .map { it.toDomain() }
        }

    override suspend fun updateCustomerProfile(
        customerId: String,
        firstName: String?,
        lastName: String?,
        businessName: String?,
        documentType: String,
        documentNumber: String,
        phone: String
    ): AppResult<CustomerProfile> = withContext(dispatchers.io) {
        val request = UpdateCustomerRequestDto(
            firstName = firstName,
            lastName = lastName,
            businessName = businessName,
            documentType = documentType,
            documentNumber = documentNumber,
            phone = phone
        )
        safeApiCall { coreApiService.updateCustomer(customerId, request) }
            .map { it.toDomain() }
    }

    override suspend fun deleteCustomerProfile(customerId: String): AppResult<Unit> =
        withContext(dispatchers.io) {
            safeApiCall { coreApiService.deleteCustomer(customerId) }
        }

    // === Employee Profiles ===

    override suspend fun createEmployeeProfile(
        userId: String,
        firstName: String,
        lastName: String,
        documentType: String,
        documentNumber: String,
        phone: String
    ): AppResult<EmployeeProfile> = withContext(dispatchers.io) {
        val request = CreateEmployeeRequestDto(
            userId = userId,
            firstName = firstName,
            lastName = lastName,
            documentType = documentType,
            documentNumber = documentNumber,
            phone = phone
        )
        safeApiCall { coreApiService.createEmployee(request) }
            .map { it.toDomain() }
    }

    override suspend fun getEmployeeById(employeeId: String): AppResult<EmployeeProfile> =
        withContext(dispatchers.io) {
            safeApiCall { coreApiService.getEmployeeById(employeeId) }
                .map { it.toDomain() }
        }

    override suspend fun getEmployeeByUserId(userId: String): AppResult<EmployeeProfile> =
        withContext(dispatchers.io) {
            safeApiCall { coreApiService.getEmployeeByUserId(userId) }
                .map { it.toDomain() }
        }

    override suspend fun getEmployeeByDocumentNumber(documentNumber: String): AppResult<EmployeeProfile> =
        withContext(dispatchers.io) {
            safeApiCall { coreApiService.getEmployeeByDocumentNumber(documentNumber) }
                .map { it.toDomain() }
        }

    override suspend fun updateEmployeeProfile(
        employeeId: String,
        firstName: String,
        lastName: String,
        documentType: String,
        documentNumber: String,
        phone: String
    ): AppResult<EmployeeProfile> = withContext(dispatchers.io) {
        val request = UpdateEmployeeRequestDto(
            firstName = firstName,
            lastName = lastName,
            documentType = documentType,
            documentNumber = documentNumber,
            phone = phone
        )
        safeApiCall { coreApiService.updateEmployee(employeeId, request) }
            .map { it.toDomain() }
    }

    override suspend fun deleteEmployeeProfile(employeeId: String): AppResult<Unit> =
        withContext(dispatchers.io) {
            safeApiCall { coreApiService.deleteEmployee(employeeId) }
        }

    // === Unified Profiles & Lookups ===

    override suspend fun getProfileMe(): AppResult<UnifiedProfile> =
        withContext(dispatchers.io) {
            safeApiCall { coreApiService.getProfileMe() }
                .map { it.toDomain() }
        }

    override suspend fun getRolesByUserId(userId: String): AppResult<List<String>> =
        withContext(dispatchers.io) {
            safeApiCall { coreApiService.getRolesByUserId(userId) }
        }

    override suspend fun lookupProfileByDocumentNumber(documentNumber: String): AppResult<DocumentProfileLookup> =
        withContext(dispatchers.io) {
            safeApiCall { coreApiService.lookupProfileByDocumentNumber(documentNumber) }
                .map { it.toDomain() }
        }
}
