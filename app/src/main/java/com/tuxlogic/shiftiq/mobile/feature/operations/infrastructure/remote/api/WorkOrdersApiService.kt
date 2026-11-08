package com.tuxlogic.shiftiq.mobile.feature.operations.infrastructure.remote.api

import com.tuxlogic.shiftiq.mobile.feature.operations.infrastructure.remote.dto.AddProductToTaskRequestDto
import com.tuxlogic.shiftiq.mobile.feature.operations.infrastructure.remote.dto.AddTaskRequestDto
import com.tuxlogic.shiftiq.mobile.feature.operations.infrastructure.remote.dto.CreateWorkOrderRequestDto
import com.tuxlogic.shiftiq.mobile.feature.operations.infrastructure.remote.dto.UpdateProductQuantityRequestDto
import com.tuxlogic.shiftiq.mobile.feature.operations.infrastructure.remote.dto.UpdateTaskRequestDto
import com.tuxlogic.shiftiq.mobile.feature.operations.infrastructure.remote.dto.UpdateWorkOrderRequestDto
import com.tuxlogic.shiftiq.mobile.feature.operations.infrastructure.remote.dto.WorkOrderResourceDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface WorkOrdersApiService {

    @GET("api/v1/work-orders")
    suspend fun getWorkOrders(
        @Query("branchId") branchId: String? = null,
        @Query("vehicleId") vehicleId: String? = null
    ): Response<List<WorkOrderResourceDto>>

    @GET("api/v1/work-orders/{id}")
    suspend fun getWorkOrderById(
        @Path("id") id: String
    ): Response<WorkOrderResourceDto>

    @POST("api/v1/work-orders")
    suspend fun createWorkOrder(
        @Body request: CreateWorkOrderRequestDto
    ): Response<WorkOrderResourceDto>

    @PUT("api/v1/work-orders/{id}")
    suspend fun updateWorkOrder(
        @Path("id") id: String,
        @Body request: UpdateWorkOrderRequestDto
    ): Response<WorkOrderResourceDto>

    @POST("api/v1/work-orders/{id}/complete")
    suspend fun completeWorkOrder(
        @Path("id") id: String
    ): Response<WorkOrderResourceDto>

    @DELETE("api/v1/work-orders/{id}")
    suspend fun deleteWorkOrder(
        @Path("id") id: String
    ): Response<Unit>

    // Tasks endpoints
    @POST("api/v1/work-orders/{id}/tasks")
    suspend fun addTask(
        @Path("id") id: String,
        @Body request: AddTaskRequestDto
    ): Response<WorkOrderResourceDto>

    @PUT("api/v1/work-orders/{id}/tasks/{taskId}")
    suspend fun updateTask(
        @Path("id") id: String,
        @Path("taskId") taskId: String,
        @Body request: UpdateTaskRequestDto
    ): Response<WorkOrderResourceDto>

    @DELETE("api/v1/work-orders/{id}/tasks/{taskId}")
    suspend fun deleteTask(
        @Path("id") id: String,
        @Path("taskId") taskId: String
    ): Response<Unit>

    @POST("api/v1/work-order-tasks/{taskId}/start")
    suspend fun startTask(
        @Path("taskId") taskId: String
    ): Response<WorkOrderResourceDto>

    @POST("api/v1/work-order-tasks/{taskId}/complete")
    suspend fun completeTask(
        @Path("taskId") taskId: String
    ): Response<WorkOrderResourceDto>

    @POST("api/v1/work-order-tasks/{taskId}/reopen")
    suspend fun reopenTask(
        @Path("taskId") taskId: String
    ): Response<WorkOrderResourceDto>

    @POST("api/v1/work-order-tasks/{taskId}/assign-mechanic")
    suspend fun assignMechanic(
        @Path("taskId") taskId: String,
        @Query("mechanicId") mechanicId: String
    ): Response<WorkOrderResourceDto>

    // Products on task endpoints
    @POST("api/v1/work-order-tasks/{taskId}/products")
    suspend fun reserveProduct(
        @Path("taskId") taskId: String,
        @Body request: AddProductToTaskRequestDto
    ): Response<WorkOrderResourceDto>

    @PUT("api/v1/work-order-tasks/{taskId}/products/{productId}")
    suspend fun updateProductQuantity(
        @Path("taskId") taskId: String,
        @Path("productId") productId: String,
        @Body request: UpdateProductQuantityRequestDto
    ): Response<WorkOrderResourceDto>

    @DELETE("api/v1/work-order-tasks/{taskId}/products/{productId}")
    suspend fun removeProduct(
        @Path("taskId") taskId: String,
        @Path("productId") productId: String
    ): Response<Unit>
}
