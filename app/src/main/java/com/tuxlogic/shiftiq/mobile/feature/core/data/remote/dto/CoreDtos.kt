package com.tuxlogic.shiftiq.mobile.feature.core.data.remote.dto

import com.google.gson.annotations.SerializedName

// === Workshops ===

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
)

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

data class UpdateWorkshopRequestDto(
    @SerializedName("businessName")
    val businessName: String,
    @SerializedName("brandName")
    val brandName: String,
    @SerializedName("taxId")
    val taxId: String,
    @SerializedName("mileageIntervalConfig")
    val mileageIntervalConfig: Int
)

// === Specialties ===

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
    val active: Boolean? = true,
    @SerializedName("createdAt")
    val createdAt: String? = null,
    @SerializedName("updatedAt")
    val updatedAt: String? = null
)

data class SpecialtyRevisionResourceDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("specialtyId")
    val specialtyId: String,
    @SerializedName("fieldName")
    val fieldName: String,
    @SerializedName("oldValue")
    val oldValue: String? = null,
    @SerializedName("newValue")
    val newValue: String? = null,
    @SerializedName("changedBy")
    val changedBy: String? = null,
    @SerializedName("changedAt")
    val changedAt: String? = null
)

data class CreateWorkshopSpecialtyRequestDto(
    @SerializedName("name")
    val name: String,
    @SerializedName("code")
    val code: String,
    @SerializedName("description")
    val description: String? = null
)

data class UpdateWorkshopSpecialtyRequestDto(
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String? = null
)

// === Branches ===

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
)

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

data class UpdateBranchRequestDto(
    @SerializedName("code")
    val code: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("address")
    val address: String,
    @SerializedName("phone")
    val phone: String
)

// === Branch Subscriptions ===

data class BranchSubscriptionResourceDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("branchId")
    val branchId: String,
    @SerializedName("planId")
    val planId: String,
    @SerializedName("billingCycle")
    val billingCycle: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("startDate")
    val startDate: String,
    @SerializedName("endDate")
    val endDate: String
)

data class AssignBranchSubscriptionRequestDto(
    @SerializedName("planId")
    val planId: String,
    @SerializedName("billingCycle")
    val billingCycle: String,
    @SerializedName("cardNumber")
    val cardNumber: String,
    @SerializedName("cardHolderName")
    val cardHolderName: String,
    @SerializedName("expirationDate")
    val expirationDate: String,
    @SerializedName("cvv")
    val cvv: String
)

// === Owner Profiles ===

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
)

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

data class UpdateOwnerRequestDto(
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

// === Customer Profiles ===

data class CustomerResourceDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("userId")
    val userId: String,
    @SerializedName("isCorporate")
    val isCorporate: Boolean? = false,
    @SerializedName("firstName")
    val firstName: String? = null,
    @SerializedName("lastName")
    val lastName: String? = null,
    @SerializedName("businessName")
    val businessName: String? = null,
    @SerializedName("documentType")
    val documentType: String,
    @SerializedName("documentNumber")
    val documentNumber: String,
    @SerializedName("phone")
    val phone: String
)

data class CreateCustomerRequestDto(
    @SerializedName("userId")
    val userId: String,
    @SerializedName("firstName")
    val firstName: String? = null,
    @SerializedName("lastName")
    val lastName: String? = null,
    @SerializedName("businessName")
    val businessName: String? = null,
    @SerializedName("documentType")
    val documentType: String,
    @SerializedName("documentNumber")
    val documentNumber: String,
    @SerializedName("phone")
    val phone: String
)

data class UpdateCustomerRequestDto(
    @SerializedName("firstName")
    val firstName: String? = null,
    @SerializedName("lastName")
    val lastName: String? = null,
    @SerializedName("businessName")
    val businessName: String? = null,
    @SerializedName("documentType")
    val documentType: String,
    @SerializedName("documentNumber")
    val documentNumber: String,
    @SerializedName("phone")
    val phone: String
)

// === Employee Profiles ===

data class EmployeeResourceDto(
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
)

data class CreateEmployeeRequestDto(
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

data class UpdateEmployeeRequestDto(
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

// === Unified Profiles & Lookups ===

data class UnifiedProfileResourceDto(
    @SerializedName("userId")
    val userId: String,
    @SerializedName("roles")
    val roles: List<String> = emptyList(),
    @SerializedName("owner")
    val owner: OwnerResourceDto? = null,
    @SerializedName("employee")
    val employee: UnifiedEmployeeSummaryDto? = null,
    @SerializedName("customer")
    val customer: CustomerResourceDto? = null
)

data class UnifiedEmployeeSummaryDto(
    @SerializedName("employeeId")
    val employeeId: String,
    @SerializedName("workshopId")
    val workshopId: String? = null,
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

data class DocumentProfileLookupDto(
    @SerializedName("profileType")
    val profileType: String,
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
    val phone: String,
    @SerializedName("employeeId")
    val employeeId: String? = null,
    @SerializedName("roles")
    val roles: List<String>? = emptyList()
)
