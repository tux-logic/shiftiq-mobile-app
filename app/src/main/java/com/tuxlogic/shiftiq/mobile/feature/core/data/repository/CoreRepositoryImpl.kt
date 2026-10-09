package com.tuxlogic.shiftiq.mobile.feature.core.data.repository

import com.tuxlogic.shiftiq.mobile.core.common.dispatchers.DispatcherProvider
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.common.result.map
import com.tuxlogic.shiftiq.mobile.core.datastore.SessionDataStore
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.core.network.safeApiCall
import com.tuxlogic.shiftiq.mobile.feature.core.data.remote.api.CoreApiService
import com.tuxlogic.shiftiq.mobile.feature.core.data.remote.dto.CreateBranchRequestDto
import com.tuxlogic.shiftiq.mobile.feature.core.data.remote.dto.CreateOwnerRequestDto
import com.tuxlogic.shiftiq.mobile.feature.core.data.remote.dto.CreateWorkshopRequestDto
import com.tuxlogic.shiftiq.mobile.feature.core.data.remote.dto.CreateWorkshopSpecialtyRequestDto
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.Branch
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.OwnerProfile
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

    override suspend fun getSpecialties(workshopId: String, activeOnly: Boolean): AppResult<List<WorkshopSpecialty>> =
        withContext(dispatchers.io) {
            safeApiCall { coreApiService.getSpecialties(workshopId, activeOnly) }
                .map { dtoList -> dtoList.map { it.toDomain() } }
        }

    override suspend fun createSpecialty(
        workshopId: String,
        name: String,
        description: String?
    ): AppResult<WorkshopSpecialty> = withContext(dispatchers.io) {
        val request = CreateWorkshopSpecialtyRequestDto(name = name, description = description)
        safeApiCall { coreApiService.createSpecialty(workshopId, request) }
            .map { it.toDomain() }
    }

    override suspend fun getOwnerProfile(userId: String): AppResult<OwnerProfile> =
        withContext(dispatchers.io) {
            safeApiCall { coreApiService.getOwnerByUserId(userId) }
                .map { it.toDomain() }
        }

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

    override suspend fun setActiveBranch(branchId: BranchId): AppResult<Unit> =
        withContext(dispatchers.io) {
            sessionDataStore.setActiveBranchId(branchId)
            AppResult.Success(Unit)
        }
}
