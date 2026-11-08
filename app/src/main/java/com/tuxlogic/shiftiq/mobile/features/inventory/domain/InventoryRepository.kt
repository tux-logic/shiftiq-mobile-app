package com.tuxlogic.shiftiq.mobile.features.inventory.domain

import com.tuxlogic.shiftiq.mobile.features.inventory.domain.model.*

interface InventoryRepository {
 suspend fun getAuthorizedBranches():Result<List<Pair<InventoryWorkshop,InventoryBranch>>>
 suspend fun getProducts(branchId:String,name:String?,category:String?,lowStockOnly:Boolean):Result<List<InventoryProduct>>
 suspend fun getProduct(productId:String):Result<InventoryProductDetails>
 suspend fun createProduct(branchId:String,name:String,category:String,sku:String,description:String,salePrice:Double,minimumStock:Int):Result<Unit>
 suspend fun updateProduct(productId:String,name:String,category:String,sku:String,description:String,salePrice:Double,minimumStock:Int):Result<Unit>
 suspend fun addStock(productId:String,quantity:Int,acquisitionCost:Double):Result<Unit>
 suspend fun deleteProduct(productId:String):Result<Unit>
}
