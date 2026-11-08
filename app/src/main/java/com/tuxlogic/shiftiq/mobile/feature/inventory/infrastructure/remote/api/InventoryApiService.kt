package com.tuxlogic.shiftiq.mobile.feature.inventory.infrastructure.remote.api

import com.tuxlogic.shiftiq.mobile.feature.inventory.infrastructure.remote.dto.AddBatchRequestDto
import com.tuxlogic.shiftiq.mobile.feature.inventory.infrastructure.remote.dto.BatchUpdateResponseDto
import com.tuxlogic.shiftiq.mobile.feature.inventory.infrastructure.remote.dto.CreateProductRequestDto
import com.tuxlogic.shiftiq.mobile.feature.inventory.infrastructure.remote.dto.ProductDetailResourceDto
import com.tuxlogic.shiftiq.mobile.feature.inventory.infrastructure.remote.dto.ProductResourceDto
import com.tuxlogic.shiftiq.mobile.feature.inventory.infrastructure.remote.dto.UpdateProductRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface InventoryApiService {

    @POST("api/v1/inventory/products")
    suspend fun createProduct(
        @Body request: CreateProductRequestDto
    ): Response<ProductResourceDto>

    @GET("api/v1/inventory/products")
    suspend fun getProducts(
        @Query("branchId") branchId: String,
        @Query("name") name: String? = null,
        @Query("category") category: String? = null,
        @Query("lowStockOnly") lowStockOnly: Boolean? = null
    ): Response<List<ProductResourceDto>>

    @GET("api/v1/inventory/products/branch/{branchId}")
    suspend fun getProductsByBranch(
        @Path("branchId") branchId: String,
        @Query("name") name: String? = null,
        @Query("category") category: String? = null,
        @Query("lowStockOnly") lowStockOnly: Boolean? = null
    ): Response<List<ProductResourceDto>>

    @GET("api/v1/inventory/products/{productId}")
    suspend fun getProductById(
        @Path("productId") productId: String
    ): Response<ProductDetailResourceDto>

    @PUT("api/v1/inventory/products/{productId}")
    suspend fun updateProduct(
        @Path("productId") productId: String,
        @Body request: UpdateProductRequestDto
    ): Response<ProductResourceDto>

    @POST("api/v1/inventory/products/{productId}/batches")
    suspend fun addStockBatch(
        @Path("productId") productId: String,
        @Body request: AddBatchRequestDto
    ): Response<BatchUpdateResponseDto>

    @DELETE("api/v1/inventory/products/{productId}")
    suspend fun deleteProduct(
        @Path("productId") productId: String
    ): Response<Unit>
}
