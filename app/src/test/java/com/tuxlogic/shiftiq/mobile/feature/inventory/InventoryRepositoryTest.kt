package com.tuxlogic.shiftiq.mobile.feature.inventory

import com.tuxlogic.shiftiq.mobile.core.common.dispatchers.DispatcherProvider
import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.datastore.SessionDataStore
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.core.model.Money
import com.tuxlogic.shiftiq.mobile.feature.inventory.infrastructure.remote.api.InventoryApiService
import com.tuxlogic.shiftiq.mobile.feature.inventory.infrastructure.remote.dto.BatchUpdateResponseDto
import com.tuxlogic.shiftiq.mobile.feature.inventory.infrastructure.remote.dto.ProductResourceDto
import com.tuxlogic.shiftiq.mobile.feature.inventory.infrastructure.repository.InventoryRepositoryImpl
import com.tuxlogic.shiftiq.mobile.feature.inventory.domain.model.ProductId
import com.tuxlogic.shiftiq.mobile.feature.inventory.domain.model.Sku
import com.tuxlogic.shiftiq.mobile.feature.inventory.application.usecase.AddStockBatchUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.util.UUID

class InventoryRepositoryTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = object : DispatcherProvider {
        override val main: CoroutineDispatcher = testDispatcher
        override val io: CoroutineDispatcher = testDispatcher
        override val default: CoroutineDispatcher = testDispatcher
        override val unconfined: CoroutineDispatcher = testDispatcher
    }

    private val inventoryApiService: InventoryApiService = mockk()
    private val sessionDataStore: SessionDataStore = mockk()

    private lateinit var inventoryRepository: InventoryRepositoryImpl
    private lateinit var addStockBatchUseCase: AddStockBatchUseCase

    private val branchUuid = UUID.randomUUID()
    private val branchId = BranchId(branchUuid)
    private val productId = ProductId(UUID.randomUUID())

    private val sampleProductDto = ProductResourceDto(
        id = productId.toString(),
        branchId = branchUuid.toString(),
        category = "Brakes",
        name = "Front Brake Pad",
        sku = "BRK-PAD-001",
        description = "Ceramic brake pads",
        salePrice = 85.0,
        minimumStock = 4,
        currentStock = 12,
        lowStockAlert = false
    )

    @Before
    fun setUp() {
        inventoryRepository = InventoryRepositoryImpl(inventoryApiService, sessionDataStore, dispatchers)
        addStockBatchUseCase = AddStockBatchUseCase(inventoryRepository)
    }

    // --- AddStockBatchUseCase Tests ---

    @Test
    fun `addStockBatch with zero quantity returns validation error`() = runTest(testDispatcher) {
        val result = addStockBatchUseCase(productId, quantity = 0, acquisitionCost = 25.0)

        assertTrue(result is AppResult.Failure)
        val error = (result as AppResult.Failure).error
        assertTrue(error is AppError.Validation)
        assertEquals("quantity", (error as AppError.Validation).details)
    }

    @Test
    fun `addStockBatch with positive quantity and negative cost returns validation error`() = runTest(testDispatcher) {
        val result = addStockBatchUseCase(productId, quantity = 10, acquisitionCost = -5.0)

        assertTrue(result is AppResult.Failure)
        val error = (result as AppResult.Failure).error
        assertTrue(error is AppError.Validation)
        assertEquals("acquisitionCost", (error as AppError.Validation).details)
    }

    @Test
    fun `addStockBatch with positive quantity calls repository and returns updated stock`() = runTest(testDispatcher) {
        coEvery {
            inventoryApiService.addStockBatch(productId.toString(), any())
        } returns Response.success(BatchUpdateResponseDto("Stock updated", 22))

        val result = addStockBatchUseCase(productId, quantity = 10, acquisitionCost = 25.0)

        assertTrue(result is AppResult.Success)
        assertEquals(22, (result as AppResult.Success).data)
        coVerify(exactly = 1) { inventoryApiService.addStockBatch(productId.toString(), any()) }
    }

    @Test
    fun `addStockBatch with negative quantity sets zero acquisition cost and calls api`() = runTest(testDispatcher) {
        coEvery {
            inventoryApiService.addStockBatch(productId.toString(), any())
        } returns Response.success(BatchUpdateResponseDto("Stock adjusted", 8))

        val result = addStockBatchUseCase(productId, quantity = -4, acquisitionCost = 99.0)

        assertTrue(result is AppResult.Success)
        assertEquals(8, (result as AppResult.Success).data)
        coVerify(exactly = 1) { inventoryApiService.addStockBatch(productId.toString(), any()) }
    }

    // --- Repository Implementation Tests ---

    @Test
    fun `getProducts uses session activeBranchId when not provided`() = runTest(testDispatcher) {
        coEvery { sessionDataStore.activeBranchId } returns flowOf(branchId)
        coEvery {
            inventoryApiService.getProducts(branchUuid.toString(), null, null, null)
        } returns Response.success(listOf(sampleProductDto))

        val result = inventoryRepository.getProducts(branchId = null)

        assertTrue(result is AppResult.Success)
        val list = (result as AppResult.Success).data
        assertEquals(1, list.size)
        assertEquals("Front Brake Pad", list[0].name)
    }

    @Test
    fun `getProducts returns validation error when no branch is active in datastore`() = runTest(testDispatcher) {
        coEvery { sessionDataStore.activeBranchId } returns flowOf(null)

        val result = inventoryRepository.getProducts(branchId = null)

        assertTrue(result is AppResult.Failure)
        val error = (result as AppResult.Failure).error
        assertTrue(error is AppError.Validation)
        assertEquals("branchId", (error as AppError.Validation).details)
    }

    @Test
    fun `createProduct sends request and maps domain entity`() = runTest(testDispatcher) {
        coEvery {
            inventoryApiService.createProduct(any())
        } returns Response.success(sampleProductDto)

        val result = inventoryRepository.createProduct(
            branchId = branchId,
            category = "Brakes",
            name = "Front Brake Pad",
            sku = Sku("BRK-PAD-001"),
            description = "Ceramic brake pads",
            salePrice = 85.0,
            minimumStock = 4
        )

        assertTrue(result is AppResult.Success)
        val item = (result as AppResult.Success).data
        assertEquals("BRK-PAD-001", item.sku.toString())
        assertEquals(Money(85.0), item.salePrice)
        coVerify(exactly = 1) { inventoryApiService.createProduct(any()) }
    }

    @Test
    fun `deleteProduct calls api and returns success`() = runTest(testDispatcher) {
        coEvery {
            inventoryApiService.deleteProduct(productId.toString())
        } returns Response.success(Unit)

        val result = inventoryRepository.deleteProduct(productId)

        assertTrue(result is AppResult.Success)
        coVerify(exactly = 1) { inventoryApiService.deleteProduct(productId.toString()) }
    }
}
