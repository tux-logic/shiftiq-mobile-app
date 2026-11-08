package com.tuxlogic.shiftiq.mobile.features.inventory.domain.model

data class InventoryProduct(val id: String, val branchId: String, val category: String, val name: String, val sku: String, val description: String?, val salePrice: Double, val minimumStock: Int, val currentStock: Int, val lowStockAlert: Boolean)
data class InventoryBatch(val batchId: String, val initialQuantity: Int, val availableQuantity: Int, val acquisitionCost: Double, val createdAt: String?)
data class InventoryProductDetails(val product: InventoryProduct, val batches: List<InventoryBatch>)
data class InventoryBranch(val id: String, val name: String, val address: String?, val workshopId: String)
data class InventoryWorkshop(val id: String, val businessName: String, val brandName: String?)
data class InventoryProfile(val roles: List<String>, val ownerId: String?, val employeeId: String?)
