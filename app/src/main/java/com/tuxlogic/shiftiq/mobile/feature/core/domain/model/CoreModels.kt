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
