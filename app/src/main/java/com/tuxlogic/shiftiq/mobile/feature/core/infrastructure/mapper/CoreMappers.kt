package com.tuxlogic.shiftiq.mobile.feature.core.infrastructure.mapper

import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.core.model.UserId
import com.tuxlogic.shiftiq.mobile.feature.core.infrastructure.remote.dto.BranchBayResourceDto
import com.tuxlogic.shiftiq.mobile.feature.core.infrastructure.remote.dto.BranchCatalogResourceDto
import com.tuxlogic.shiftiq.mobile.feature.core.infrastructure.remote.dto.BranchResourceDto
import com.tuxlogic.shiftiq.mobile.feature.core.infrastructure.remote.dto.BranchSubscriptionResourceDto
import com.tuxlogic.shiftiq.mobile.feature.core.infrastructure.remote.dto.CustomerResourceDto
import com.tuxlogic.shiftiq.mobile.feature.core.infrastructure.remote.dto.DocumentProfileLookupDto
import com.tuxlogic.shiftiq.mobile.feature.core.infrastructure.remote.dto.EmployeeResourceDto
import com.tuxlogic.shiftiq.mobile.feature.core.infrastructure.remote.dto.OwnerResourceDto
import com.tuxlogic.shiftiq.mobile.feature.core.infrastructure.remote.dto.SpecialtyRevisionResourceDto
import com.tuxlogic.shiftiq.mobile.feature.core.infrastructure.remote.dto.UnifiedEmployeeSummaryDto
import com.tuxlogic.shiftiq.mobile.feature.core.infrastructure.remote.dto.UnifiedProfileResourceDto
import com.tuxlogic.shiftiq.mobile.feature.core.infrastructure.remote.dto.WorkshopResourceDto
import com.tuxlogic.shiftiq.mobile.feature.core.infrastructure.remote.dto.WorkshopSpecialtyResourceDto
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.Branch
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.BranchBay
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.BranchCatalogItem
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.BranchSubscription
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.CustomerProfile
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.DocumentProfileLookup
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.EmployeeProfile
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.OwnerProfile
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.SpecialtyRevision
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.UnifiedEmployeeSummary
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.UnifiedProfile
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.Workshop
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.WorkshopSpecialty

fun WorkshopResourceDto.toDomain(): Workshop = Workshop(
    id = id,
    ownerId = ownerId,
    businessName = businessName,
    brandName = brandName,
    taxId = taxId,
    mileageIntervalConfig = mileageIntervalConfig ?: 5000
)

fun WorkshopSpecialtyResourceDto.toDomain(): WorkshopSpecialty = WorkshopSpecialty(
    id = id,
    workshopId = workshopId,
    name = name,
    code = code,
    description = description,
    active = active ?: true
)

fun SpecialtyRevisionResourceDto.toDomain(): SpecialtyRevision = SpecialtyRevision(
    id = id,
    specialtyId = specialtyId,
    fieldName = fieldName,
    oldValue = oldValue,
    newValue = newValue,
    changedBy = changedBy,
    changedAt = changedAt
)

fun BranchResourceDto.toDomain(): Branch = Branch(
    id = BranchId(id),
    workshopId = workshopId,
    code = code,
    name = name,
    address = address,
    phone = phone,
    latitude = latitude,
    longitude = longitude
)

fun BranchCatalogResourceDto.toDomain(): BranchCatalogItem = BranchCatalogItem(
    branchId = branchId,
    workshopId = workshopId,
    workshopBrandName = workshopBrandName.orEmpty(),
    workshopBusinessName = workshopBusinessName.orEmpty(),
    branchCode = branchCode.orEmpty(),
    branchName = branchName.orEmpty(),
    address = address.orEmpty(),
    phone = phone.orEmpty(),
    latitude = latitude,
    longitude = longitude,
    averageRating = averageRating ?: 0.0,
    reviewCount = reviewCount ?: 0,
    activeBaysCount = activeBaysCount ?: 0,
    freeBaysToday = freeBaysToday ?: 0
)

fun BranchBayResourceDto.toDomain(): BranchBay = BranchBay(
    id = id,
    branchId = branchId,
    code = code,
    name = name,
    type = type,
    active = active
)

fun BranchSubscriptionResourceDto.toDomain(): BranchSubscription = BranchSubscription(
    id = id,
    branchId = BranchId(branchId),
    planId = planId,
    billingCycle = billingCycle,
    status = status,
    startDate = startDate,
    endDate = endDate
)

fun OwnerResourceDto.toDomain(): OwnerProfile = OwnerProfile(
    id = id,
    userId = UserId(userId),
    firstName = firstName,
    lastName = lastName,
    documentType = documentType,
    documentNumber = documentNumber,
    phone = phone
)

fun CustomerResourceDto.toDomain(): CustomerProfile = CustomerProfile(
    id = id,
    userId = UserId(userId),
    isCorporate = isCorporate ?: false,
    firstName = firstName,
    lastName = lastName,
    businessName = businessName,
    documentType = documentType,
    documentNumber = documentNumber,
    phone = phone
)

fun EmployeeResourceDto.toDomain(): EmployeeProfile = EmployeeProfile(
    id = id,
    userId = UserId(userId),
    firstName = firstName,
    lastName = lastName,
    documentType = documentType,
    documentNumber = documentNumber,
    phone = phone
)

fun UnifiedEmployeeSummaryDto.toDomain(): UnifiedEmployeeSummary = UnifiedEmployeeSummary(
    employeeId = employeeId,
    workshopId = workshopId,
    firstName = firstName,
    lastName = lastName,
    documentType = documentType,
    documentNumber = documentNumber,
    phone = phone
)

fun UnifiedProfileResourceDto.toDomain(): UnifiedProfile = UnifiedProfile(
    userId = UserId(userId),
    profileImageUrl = profileImageUrl,
    roles = roles,
    owner = owner?.toDomain(),
    employee = employee?.toDomain(),
    customer = customer?.toDomain()
)

fun DocumentProfileLookupDto.toDomain(): DocumentProfileLookup = DocumentProfileLookup(
    profileType = profileType,
    userId = UserId(userId),
    firstName = firstName,
    lastName = lastName,
    documentType = documentType,
    documentNumber = documentNumber,
    phone = phone,
    employeeId = employeeId,
    roles = roles ?: emptyList()
)
