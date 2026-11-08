package com.tuxlogic.shiftiq.mobile.features.inventory.infrastructure.repositories

import com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.local.UserDao
import com.tuxlogic.shiftiq.mobile.features.inventory.domain.InventoryRepository
import com.tuxlogic.shiftiq.mobile.features.inventory.domain.model.*
import com.tuxlogic.shiftiq.mobile.features.inventory.infrastructure.remote.*
import javax.inject.Inject

class InventoryRepositoryImpl @Inject constructor(private val api:InventoryApiService,private val userDao:UserDao):InventoryRepository {
 private suspend fun authHeader():String="Bearer "+(userDao.getActiveUser()?:error("Inicia sesión para consultar el inventario.")).token
 override suspend fun getAuthorizedBranches():Result<List<Pair<InventoryWorkshop,InventoryBranch>>> = runCatching {
  val auth=authHeader();val profileResponse=api.getMyProfile(auth);check(profileResponse.isSuccessful){profileResponse.message()}
  val owner=profileResponse.body()?.owner?:error("Tu cuenta no tiene perfil de propietario de taller.")
  val workshopsResponse=api.getWorkshopsByOwner(auth,owner.id);check(workshopsResponse.isSuccessful){workshopsResponse.message()}
  workshopsResponse.body().orEmpty().flatMap{workshop->
   val branchResponse=api.getBranchesByWorkshop(auth,workshop.id);check(branchResponse.isSuccessful){branchResponse.message()}
   branchResponse.body().orEmpty().map{workshop.toDomain() to it.toDomain()}
  }
 }
 override suspend fun getProducts(branchId:String,name:String?,category:String?,lowStockOnly:Boolean):Result<List<InventoryProduct>> = runCatching {
  val response=api.getProducts(authHeader(),branchId,name?.takeIf{it.isNotBlank()},category?.takeIf{it.isNotBlank()},lowStockOnly.takeIf{it});check(response.isSuccessful){response.message()};response.body().orEmpty().map{it.toDomain()}
 }
 override suspend fun getProduct(productId:String):Result<InventoryProductDetails> = runCatching {val response=api.getProduct(authHeader(),productId);check(response.isSuccessful){response.message()};response.body()?.toDomain()?:error("El producto llegó vacío.")}
 override suspend fun createProduct(branchId:String,name:String,category:String,sku:String,description:String,salePrice:Double,minimumStock:Int):Result<Unit> = runCatching {val response=api.createProduct(authHeader(),CreateProductRequestDto(branchId,category,name,sku,description.ifBlank{null},salePrice,minimumStock));check(response.isSuccessful){response.message()}}
 override suspend fun updateProduct(productId:String,name:String,category:String,sku:String,description:String,salePrice:Double,minimumStock:Int):Result<Unit> = runCatching {val response=api.updateProduct(authHeader(),productId,UpdateProductRequestDto(name,category,sku,description.ifBlank{null},salePrice,minimumStock));check(response.isSuccessful){response.message()}}
 override suspend fun addStock(productId:String,quantity:Int,acquisitionCost:Double):Result<Unit> = runCatching {val response=api.addBatch(authHeader(),productId,AddBatchRequestDto(quantity,acquisitionCost));check(response.isSuccessful){response.message()}}
 override suspend fun deleteProduct(productId:String):Result<Unit> = runCatching {val response=api.deleteProduct(authHeader(),productId);check(response.isSuccessful){response.message()}}
}