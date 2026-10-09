package com.tuxlogic.shiftiq.mobile.feature.core.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.core.model.UserId
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.Branch
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.OwnerProfile
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.Workshop
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.WorkshopSpecialty

data class WorkshopResourceDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("ownerId")
    val ownerId: String,
    @SerializedName("businessName")
    val businessName: String,
    @SerializedName("brandName")
    val brandName: String,
    @SerializedName("taxId")
    val taxId: String,
    @SerializedName("mileageIntervalConfig")
    val mileageIntervalConfig: Int? = 5000
) {
    fun toDomain(): Workshop = Workshop(
        id = id,
        ownerId = ownerId,
        businessName = businessName,
        brandName = brandName,
        taxId = taxId,
        mileageIntervalConfig = mileageIntervalConfig ?: 5000
    )
}

data class CreateWorkshopRequestDto(
    @SerializedName("ownerId")
    val ownerId: String,
    @SerializedName("businessName")
    val businessName: String,
    @SerializedName("brandName")
    val brandName: String,
    @SerializedName("taxId")
    val taxId: String,
    @SerializedName("mileageIntervalConfig")
    val mileageIntervalConfig: Int = 5000
)

data class BranchResourceDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("workshopId")
    val workshopId: String,
    @SerializedName("code")
    val code: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("address")
    val address: String,
    @SerializedName("phone")
    val phone: String
) {
    fun toDomain(): Branch = Branch(
        id = BranchId(id),
        workshopId = workshopId,
        code = code,
        name = name,
        address = address,
        phone = phone
    )
}

data class CreateBranchRequestDto(
    @SerializedName("workshopId")
    val workshopId: String,
    @SerializedName("code")
    val code: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("address")
    val address: String,
    @SerializedName("phone")
    val phone: String
)

data class WorkshopSpecialtyResourceDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("workshopId")
    val workshopId: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("code")
    val code: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("active")
    val active: Boolean? = true
) {
    fun toDomain(): WorkshopSpecialty = WorkshopSpecialty(
        id = id,
        workshopId = workshopId,
        name = name,
        code = code,
        description = description,
        active = active ?: true
    )
}

data class CreateWorkshopSpecialtyRequestDto(
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String? = null
)

data class OwnerResourceDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("userId")
    val userId: String,
    @SerializedName("firstName")
    val firstName: String,
    @SerializedName("lastName")
    val lastName: String,
    @SerializedName("documentType")
    val documentType: String,
    @SerializedName("documentNumber")
    val documentNumber: String,
    @SerializedName("phone")
    val phone: String
) {
    fun toDomain(): OwnerProfile = OwnerProfile(
        id = id,
        userId = UserId(userId),
        firstName = firstName,
        lastName = lastName,
        documentType = documentType,
        documentNumber = documentNumber,
        phone = phone
    )
}

data class CreateOwnerRequestDto(
    @SerializedName("userId")
    val userId: String,
    @SerializedName("firstName")
    val firstName: String,
    @SerializedName("lastName")
    val lastName: String,
    @SerializedName("documentType")
    val documentType: String,
    @SerializedName("documentNumber")
    val documentNumber: String,
    @SerializedName("phone")
    val phone: String
)
