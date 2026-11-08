package com.tuxlogic.shiftiq.mobile.feature.operations.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CreateWorkOrderRequestDto(
    @SerializedName("appointmentId") val appointmentId: String,
    @SerializedName("branchId") val branchId: String,
    @SerializedName("vehicleId") val vehicleId: String,
    @SerializedName("customerId") val customerId: String,
    @SerializedName("diagnosticSummary") val diagnosticSummary: String,
    @SerializedName("mileageIn") val mileageIn: Int,
    @SerializedName("entryInspectionImages") val entryInspectionImages: List<String> = emptyList()
)

data class UpdateWorkOrderRequestDto(
    @SerializedName("diagnosticSummary") val diagnosticSummary: String,
    @SerializedName("mileageIn") val mileageIn: Int
)

data class AddTaskRequestDto(
    @SerializedName("serviceId") val serviceId: String,
    @SerializedName("assignedMechanicId") val assignedMechanicId: String,
    @SerializedName("description") val description: String,
    @SerializedName("evidenceImages") val evidenceImages: List<String> = emptyList()
)

data class UpdateTaskRequestDto(
    @SerializedName("serviceId") val serviceId: String? = null,
    @SerializedName("assignedMechanicId") val assignedMechanicId: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("evidenceImages") val evidenceImages: List<String>? = null
)

data class AddProductToTaskRequestDto(
    @SerializedName("productId") val productId: String,
    @SerializedName("quantity") val quantity: Int
)

data class UpdateProductQuantityRequestDto(
    @SerializedName("quantity") val quantity: Int
)

data class TaskProductResourceDto(
    @SerializedName("productId") val productId: String,
    @SerializedName("productName") val productName: String? = null,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("unitPrice") val unitPrice: Double,
    @SerializedName("totalPrice") val totalPrice: Double
)

data class WorkOrderTaskResourceDto(
    @SerializedName("id") val id: String,
    @SerializedName("serviceId") val serviceId: String,
    @SerializedName("serviceName") val serviceName: String? = null,
    @SerializedName("branchId") val branchId: String,
    @SerializedName("assignedMechanicId") val assignedMechanicId: String? = null,
    @SerializedName("assignedMechanicName") val assignedMechanicName: String? = null,
    @SerializedName("status") val status: String,
    @SerializedName("description") val description: String,
    @SerializedName("price") val price: Double,
    @SerializedName("evidenceImages") val evidenceImages: List<String>? = null,
    @SerializedName("products") val products: List<TaskProductResourceDto>? = null,
    @SerializedName("startedAt") val startedAt: String? = null,
    @SerializedName("completedAt") val completedAt: String? = null
)

data class WorkOrderResourceDto(
    @SerializedName("id") val id: String,
    @SerializedName("appointmentId") val appointmentId: String,
    @SerializedName("branchId") val branchId: String,
    @SerializedName("vehicleId") val vehicleId: String,
    @SerializedName("customerId") val customerId: String,
    @SerializedName("internalNumber") val internalNumber: Int? = null,
    @SerializedName("formattedNumber") val formattedNumber: String? = null,
    @SerializedName("status") val status: String,
    @SerializedName("diagnosticSummary") val diagnosticSummary: String,
    @SerializedName("mileageIn") val mileageIn: Int,
    @SerializedName("totalAmount") val totalAmount: Double? = null,
    @SerializedName("tasks") val tasks: List<WorkOrderTaskResourceDto>? = null,
    @SerializedName("entryInspectionImages") val entryInspectionImages: List<String>? = null,
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("updatedAt") val updatedAt: String? = null,
    @SerializedName("completedAt") val completedAt: String? = null
)
