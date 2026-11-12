package com.tuxlogic.shiftiq.mobile.feature.inventory.infrastructure.remote.dto

import com.google.gson.annotations.SerializedName

data class CreateProductRequestDto(
    @SerializedName("branchId") val branchId: String,
    @SerializedName("category") val category: String,
    @SerializedName("name") val name: String,
    @SerializedName("sku") val sku: String,
    @SerializedName("description") val description: String? = null,
    @SerializedName("salePrice") val salePrice: Double,
    @SerializedName("minimumStock") val minimumStock: Int
)

data class UpdateProductRequestDto(
    @SerializedName("name") val name: String,
    @SerializedName("category") val category: String,
    @SerializedName("sku") val sku: String,
    @SerializedName("description") val description: String? = null,
    @SerializedName("salePrice") val salePrice: Double,
    @SerializedName("minimumStock") val minimumStock: Int
)

data class AddBatchRequestDto(
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("acquisitionCost") val acquisitionCost: Double
)

data class ProductBatchDto(
    @SerializedName("batchId") val batchId: String,
    @SerializedName("initialQuantity") val initialQuantity: Int,
    @SerializedName("availableQuantity") val availableQuantity: Int,
    @SerializedName("acquisitionCost") val acquisitionCost: Double,
    @SerializedName("createdAt") val createdAt: String? = null
)

data class ProductResourceDto(
    @SerializedName("id") val id: String,
    @SerializedName("branchId") val branchId: String,
    @SerializedName("category") val category: String,
    @SerializedName("name") val name: String,
    @SerializedName("sku") val sku: String,
    @SerializedName("description") val description: String? = null,
    @SerializedName("salePrice") val salePrice: Double,
    @SerializedName("minimumStock") val minimumStock: Int,
    @SerializedName("currentStock") val currentStock: Int? = null,
    @SerializedName("lowStockAlert") val lowStockAlert: Boolean? = null
)

data class ProductDetailResourceDto(
    @SerializedName("id") val id: String,
    @SerializedName("branchId") val branchId: String,
    @SerializedName("category") val category: String,
    @SerializedName("name") val name: String,
    @SerializedName("sku") val sku: String,
    @SerializedName("description") val description: String? = null,
    @SerializedName("salePrice") val salePrice: Double,
    @SerializedName("minimumStock") val minimumStock: Int,
    @SerializedName("currentStock") val currentStock: Int? = null,
    @SerializedName("lowStockAlert") val lowStockAlert: Boolean? = null,
    @SerializedName("batches") val batches: List<ProductBatchDto>? = null
)

data class BatchUpdateResponseDto(
    @SerializedName("message") val message: String? = null,
    @SerializedName("currentStock") val currentStock: Int
)
