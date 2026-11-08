package com.tuxlogic.shiftiq.mobile.features.inventory.infrastructure.remote

import com.google.gson.annotations.SerializedName
import com.tuxlogic.shiftiq.mobile.features.inventory.domain.model.*
import retrofit2.Response
import retrofit2.http.*

data class ProductDto(@SerializedName("id") val id:String,@SerializedName("branchId") val branchId:String,@SerializedName("category") val category:String,@SerializedName("name") val name:String,@SerializedName("sku") val sku:String,@SerializedName("description") val description:String?,@SerializedName("salePrice") val salePrice:Double,@SerializedName("minimumStock") val minimumStock:Int,@SerializedName("currentStock") val currentStock:Int,@SerializedName("lowStockAlert") val lowStockAlert:Boolean) {
 fun toDomain()=InventoryProduct(id,branchId,category,name,sku,description,salePrice,minimumStock,currentStock,lowStockAlert)
}
data class BatchDto(@SerializedName("batchId") val batchId:String,@SerializedName("initialQuantity") val initialQuantity:Int,@SerializedName("availableQuantity") val availableQuantity:Int,@SerializedName("acquisitionCost") val acquisitionCost:Double,@SerializedName("createdAt") val createdAt:String?) {
 fun toDomain()=InventoryBatch(batchId,initialQuantity,availableQuantity,acquisitionCost,createdAt)
}
data class ProductDetailsDto(@SerializedName("id") val id:String,@SerializedName("branchId") val branchId:String,@SerializedName("category") val category:String,@SerializedName("name") val name:String,@SerializedName("sku") val sku:String,@SerializedName("description") val description:String?,@SerializedName("salePrice") val salePrice:Double,@SerializedName("minimumStock") val minimumStock:Int,@SerializedName("currentStock") val currentStock:Int,@SerializedName("lowStockAlert") val lowStockAlert:Boolean,@SerializedName("batches") val batches:List<BatchDto> = emptyList()) {
 fun toDomain()=InventoryProductDetails(ProductDto(id,branchId,category,name,sku,description,salePrice,minimumStock,currentStock,lowStockAlert).toDomain(),batches.map{it.toDomain()})
}
data class CreateProductRequestDto(@SerializedName("branchId") val branchId:String,@SerializedName("category") val category:String,@SerializedName("name") val name:String,@SerializedName("sku") val sku:String,@SerializedName("description") val description:String?,@SerializedName("salePrice") val salePrice:Double,@SerializedName("minimumStock") val minimumStock:Int)
data class UpdateProductRequestDto(@SerializedName("name") val name:String,@SerializedName("category") val category:String,@SerializedName("sku") val sku:String,@SerializedName("description") val description:String?,@SerializedName("salePrice") val salePrice:Double,@SerializedName("minimumStock") val minimumStock:Int)
data class AddBatchRequestDto(@SerializedName("quantity") val quantity:Int,@SerializedName("acquisitionCost") val acquisitionCost:Double)
data class WorkshopDto(@SerializedName("id") val id:String,@SerializedName("businessName") val businessName:String,@SerializedName("brandName") val brandName:String?)
data class BranchDto(@SerializedName("id") val id:String,@SerializedName("workshopId") val workshopId:String,@SerializedName("name") val name:String,@SerializedName("address") val address:String?,@SerializedName("code") val code:String?)
data class ProfileContextDto(@SerializedName("roles") val roles:List<String> = emptyList(),@SerializedName("owner") val owner:ProfileDto?,@SerializedName("employee") val employee:ProfileDto?)
data class ProfileDto(@SerializedName("id") val id:String)
fun ProfileContextDto.toDomain()=InventoryProfile(roles,owner?.id,employee?.id)
fun WorkshopDto.toDomain()=InventoryWorkshop(id,businessName,brandName)
fun BranchDto.toDomain()=InventoryBranch(id,name,address,workshopId)

interface InventoryApiService {
 @GET("profiles/me") suspend fun getMyProfile(@Header("Authorization") authorization:String):Response<ProfileContextDto>
 @GET("workshops") suspend fun getWorkshopsByOwner(@Header("Authorization") authorization:String,@Query("ownerId") ownerId:String):Response<List<WorkshopDto>>
 @GET("branches") suspend fun getBranchesByWorkshop(@Header("Authorization") authorization:String,@Query("workshopId") workshopId:String):Response<List<BranchDto>>
 @GET("inventory/products") suspend fun getProducts(@Header("Authorization") authorization:String,@Query("branchId") branchId:String,@Query("name") name:String?=null,@Query("category") category:String?=null,@Query("lowStockOnly") lowStockOnly:Boolean?=null):Response<List<ProductDto>>
 @GET("inventory/products/{productId}") suspend fun getProduct(@Header("Authorization") authorization:String,@Path("productId") productId:String):Response<ProductDetailsDto>
 @POST("inventory/products") suspend fun createProduct(@Header("Authorization") authorization:String,@Body request:CreateProductRequestDto):Response<ProductDto>
 @PUT("inventory/products/{productId}") suspend fun updateProduct(@Header("Authorization") authorization:String,@Path("productId") productId:String,@Body request:UpdateProductRequestDto):Response<ProductDto>
 @POST("inventory/products/{productId}/batches") suspend fun addBatch(@Header("Authorization") authorization:String,@Path("productId") productId:String,@Body request:AddBatchRequestDto):Response<BatchDto>
 @DELETE("inventory/products/{productId}") suspend fun deleteProduct(@Header("Authorization") authorization:String,@Path("productId") productId:String):Response<Unit>
}
