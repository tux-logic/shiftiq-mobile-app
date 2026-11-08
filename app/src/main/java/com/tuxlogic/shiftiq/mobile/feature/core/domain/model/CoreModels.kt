package com.tuxlogic.shiftiq.mobile.feature.core.domain.model

import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.core.model.UserId

data class Workshop(
    val id: String,
    val ownerId: String,
    val businessName: String,
    val brandName: String,
    val taxId: String,
    val mileageIntervalConfig: Int = 5000
)

data class Branch(
    val id: BranchId,
    val workshopId: String,
    val code: String,
    val name: String,
    val address: String,
    val phone: String
)

data class WorkshopSpecialty(
    val id: String,
    val workshopId: String,
    val name: String,
    val code: String? = null,
    val description: String? = null,
    val active: Boolean = true
)

data class SpecialtyRevision(
    val id: String,
    val specialtyId: String,
    val fieldName: String,
    val oldValue: String? = null,
    val newValue: String? = null,
    val changedBy: String? = null,
    val changedAt: String? = null
)

data class BranchSubscription(
    val id: String,
    val branchId: BranchId,
    val planId: String,
    val billingCycle: String,
    val status: String,
    val startDate: String,
    val endDate: String
)

data class OwnerProfile(
    val id: String,
    val userId: UserId,
    val firstName: String,
    val lastName: String,
    val documentType: String,
    val documentNumber: String,
    val phone: String
) {
    val fullName: String get() = "$firstName $lastName".trim()
}

data class EmployeeProfile(
    val id: String,
    val userId: UserId,
    val firstName: String,
    val lastName: String,
    val documentType: String,
    val documentNumber: String,
    val phone: String
) {
    val fullName: String get() = "$firstName $lastName".trim()
}

data class CustomerProfile(
    val id: String,
    val userId: UserId,
    val isCorporate: Boolean = false,
    val firstName: String? = null,
    val lastName: String? = null,
    val businessName: String? = null,
    val documentType: String,
    val documentNumber: String,
    val phone: String
) {
    val displayName: String
        get() = if (isCorporate && !businessName.isNullOrBlank()) {
            businessName
        } else {
            "${firstName.orEmpty()} ${lastName.orEmpty()}".trim()
        }
}

data class UnifiedProfile(
    val userId: UserId,
    val roles: List<String>,
    val owner: OwnerProfile? = null,
    val employee: UnifiedEmployeeSummary? = null,
    val customer: CustomerProfile? = null
)

data class UnifiedEmployeeSummary(
    val employeeId: String,
    val workshopId: String? = null,
    val firstName: String,
    val lastName: String,
    val documentType: String,
    val documentNumber: String,
    val phone: String
) {
    val fullName: String get() = "$firstName $lastName".trim()
}

data class DocumentProfileLookup(
    val profileType: String,
    val userId: UserId,
    val firstName: String,
    val lastName: String,
    val documentType: String,
    val documentNumber: String,
    val phone: String,
    val employeeId: String? = null,
    val roles: List<String> = emptyList()
) {
    val fullName: String get() = "$firstName $lastName".trim()
}
