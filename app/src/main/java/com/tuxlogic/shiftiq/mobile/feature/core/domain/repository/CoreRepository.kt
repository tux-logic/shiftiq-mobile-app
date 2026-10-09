package com.tuxlogic.shiftiq.mobile.feature.core.domain.repository

import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.Branch
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.OwnerProfile
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.Workshop
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.WorkshopSpecialty

interface CoreRepository {
    suspend fun getWorkshops(ownerId: String): AppResult<List<Workshop>>
    suspend fun getWorkshopById(workshopId: String): AppResult<Workshop>
    suspend fun createWorkshop(
        ownerId: String,
        businessName: String,
        brandName: String,
        taxId: String,
        mileageIntervalConfig: Int
    ): AppResult<Workshop>

    suspend fun getBranches(workshopId: String): AppResult<List<Branch>>
    suspend fun getBranchById(branchId: String): AppResult<Branch>
    suspend fun createBranch(
        workshopId: String,
        code: String,
        name: String,
        address: String,
        phone: String
    ): AppResult<Branch>

    suspend fun getSpecialties(workshopId: String, activeOnly: Boolean = true): AppResult<List<WorkshopSpecialty>>
    suspend fun createSpecialty(workshopId: String, name: String, description: String?): AppResult<WorkshopSpecialty>

    suspend fun getOwnerProfile(userId: String): AppResult<OwnerProfile>
    suspend fun createOwnerProfile(
        userId: String,
        firstName: String,
        lastName: String,
        documentType: String,
        documentNumber: String,
        phone: String
    ): AppResult<OwnerProfile>

    suspend fun setActiveBranch(branchId: BranchId): AppResult<Unit>
}
