package com.tuxlogic.shiftiq.mobile.feature.inventory

import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.core.model.Money
import com.tuxlogic.shiftiq.mobile.feature.inventory.domain.model.BatchId
import com.tuxlogic.shiftiq.mobile.feature.inventory.domain.model.ProductBatch
import com.tuxlogic.shiftiq.mobile.feature.inventory.domain.model.ProductDetail
import com.tuxlogic.shiftiq.mobile.feature.inventory.domain.model.ProductId
import com.tuxlogic.shiftiq.mobile.feature.inventory.domain.model.ProductItem
import com.tuxlogic.shiftiq.mobile.feature.inventory.domain.model.Sku
import com.tuxlogic.shiftiq.mobile.feature.inventory.domain.repository.InventoryRepository
import com.tuxlogic.shiftiq.mobile.feature.inventory.application.usecase.CreateProductUseCase
import com.tuxlogic.shiftiq.mobile.feature.inventory.application.usecase.DeleteProductUseCase
import com.tuxlogic.shiftiq.mobile.feature.inventory.application.usecase.GetProductByIdUseCase
import com.tuxlogic.shiftiq.mobile.feature.inventory.application.usecase.GetProductsUseCase
import com.tuxlogic.shiftiq.mobile.feature.inventory.application.usecase.UpdateProductUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.UUID

class ProductUseCasesTest {

    private val inventoryRepository: InventoryRepository = mockk()

    private lateinit var getProductsUseCase: GetProductsUseCase
    private lateinit var getProductByIdUseCase: GetProductByIdUseCase
    private lateinit var createProductUseCase: CreateProductUseCase
    private lateinit var updateProductUseCase: UpdateProductUseCase
    private lateinit var deleteProductUseCase: DeleteProductUseCase

    private val branchId = BranchId(UUID.randomUUID())
    private val productId = ProductId(UUID.randomUUID())
    private val sampleSku = Sku("FLT-OIL-001")

    private val dummyProduct = ProductItem(
        id = productId,
        branchId = branchId,
        category = "Filters",
        name = "Synthetic Oil Filter",
        sku = sampleSku,
        description = "High quality synthetic oil filter",
        salePrice = Money(45.0),
        minimumStock = 5,
        currentStock = 20,
        lowStockAlert = false
    )

    private val dummyDetail = ProductDetail(
        id = productId,
        branchId = branchId,
        category = "Filters",
        name = "Synthetic Oil Filter",
        sku = sampleSku,
        description = "High quality synthetic oil filter",
        salePrice = Money(45.0),
        minimumStock = 5,
        currentStock = 20,
        lowStockAlert = false,
        batches = listOf(
            ProductBatch(
                batchId = BatchId("BATCH-001"),
                initialQuantity = 20,
                availableQuantity = 20,
                acquisitionCost = Money(22.5),
                createdAt = "2026-11-07T12:00:00Z"
            )
        )
    )

    @Before
    fun setUp() {
        getProductsUseCase = GetProductsUseCase(inventoryRepository)
        getProductByIdUseCase = GetProductByIdUseCase(inventoryRepository)
        createProductUseCase = CreateProductUseCase(inventoryRepository)
        updateProductUseCase = UpdateProductUseCase(inventoryRepository)
        deleteProductUseCase = DeleteProductUseCase(inventoryRepository)
    }

    // --- Query Use Cases ---

    @Test
    fun `getProducts delegates to repository and returns list`() = runTest {
        val expected = listOf(dummyProduct)
        coEvery {
            inventoryRepository.getProducts(branchId, "Oil", "Filters", false)
        } returns AppResult.Success(expected)

        val result = getProductsUseCase(branchId, name = "  Oil  ", category = " Filters ", lowStockOnly = false)

        assertTrue(result is AppResult.Success)
        assertEquals(expected, (result as AppResult.Success).data)
        coVerify(exactly = 1) { inventoryRepository.getProducts(branchId, "Oil", "Filters", false) }
    }

    @Test
    fun `getProductById delegates to repository and returns detail`() = runTest {
        coEvery { inventoryRepository.getProductById(productId) } returns AppResult.Success(dummyDetail)

        val result = getProductByIdUseCase(productId)

        assertTrue(result is AppResult.Success)
        assertEquals(dummyDetail, (result as AppResult.Success).data)
        coVerify(exactly = 1) { inventoryRepository.getProductById(productId) }
    }

    // --- CreateProductUseCase Validations ---

    @Test
    fun `createProduct with blank category returns validation error`() = runTest {
        val result = createProductUseCase(
            branchId = branchId,
            category = "   ",
            name = "Oil Filter",
            sku = "SKU-001",
            salePrice = 40.0,
            minimumStock = 5
        )

        assertTrue(result is AppResult.Failure)
        val error = (result as AppResult.Failure).error
        assertTrue(error is AppError.Validation)
        assertEquals("category", (error as AppError.Validation).details)
        coVerify(exactly = 0) { inventoryRepository.createProduct(any(), any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `createProduct with blank name returns validation error`() = runTest {
        val result = createProductUseCase(
            branchId = branchId,
            category = "Filters",
            name = "   ",
            sku = "SKU-001",
            salePrice = 40.0,
            minimumStock = 5
        )

        assertTrue(result is AppResult.Failure)
        val error = (result as AppResult.Failure).error
        assertTrue(error is AppError.Validation)
        assertEquals("name", (error as AppError.Validation).details)
        coVerify(exactly = 0) { inventoryRepository.createProduct(any(), any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `createProduct with blank sku returns validation error`() = runTest {
        val result = createProductUseCase(
            branchId = branchId,
            category = "Filters",
            name = "Oil Filter",
            sku = "   ",
            salePrice = 40.0,
            minimumStock = 5
        )

        assertTrue(result is AppResult.Failure)
        val error = (result as AppResult.Failure).error
        assertTrue(error is AppError.Validation)
        assertEquals("sku", (error as AppError.Validation).details)
        coVerify(exactly = 0) { inventoryRepository.createProduct(any(), any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `createProduct with sku over 50 chars returns validation error`() = runTest {
        val longSku = "A".repeat(51)
        val result = createProductUseCase(
            branchId = branchId,
            category = "Filters",
            name = "Oil Filter",
            sku = longSku,
            salePrice = 40.0,
            minimumStock = 5
        )

        assertTrue(result is AppResult.Failure)
        val error = (result as AppResult.Failure).error
        assertTrue(error is AppError.Validation)
        assertEquals("sku", (error as AppError.Validation).details)
        coVerify(exactly = 0) { inventoryRepository.createProduct(any(), any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `createProduct with non-positive price returns validation error`() = runTest {
        val result = createProductUseCase(
            branchId = branchId,
            category = "Filters",
            name = "Oil Filter",
            sku = "SKU-001",
            salePrice = 0.0,
            minimumStock = 5
        )

        assertTrue(result is AppResult.Failure)
        val error = (result as AppResult.Failure).error
        assertTrue(error is AppError.Validation)
        assertEquals("salePrice", (error as AppError.Validation).details)
        coVerify(exactly = 0) { inventoryRepository.createProduct(any(), any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `createProduct with negative minimum stock returns validation error`() = runTest {
        val result = createProductUseCase(
            branchId = branchId,
            category = "Filters",
            name = "Oil Filter",
            sku = "SKU-001",
            salePrice = 35.0,
            minimumStock = -1
        )

        assertTrue(result is AppResult.Failure)
        val error = (result as AppResult.Failure).error
        assertTrue(error is AppError.Validation)
        assertEquals("minimumStock", (error as AppError.Validation).details)
        coVerify(exactly = 0) { inventoryRepository.createProduct(any(), any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `createProduct with valid inputs delegates to repository`() = runTest {
        coEvery {
            inventoryRepository.createProduct(
                branchId = branchId,
                category = "Filters",
                name = "Oil Filter",
                sku = Sku("SKU-001"),
                description = "High flow",
                salePrice = 45.0,
                minimumStock = 5
            )
        } returns AppResult.Success(dummyProduct)

        val result = createProductUseCase(
            branchId = branchId,
            category = "Filters",
            name = "Oil Filter",
            sku = "SKU-001",
            description = "High flow",
            salePrice = 45.0,
            minimumStock = 5
        )

        assertTrue(result is AppResult.Success)
        assertEquals(dummyProduct, (result as AppResult.Success).data)
        coVerify(exactly = 1) {
            inventoryRepository.createProduct(
                branchId = branchId,
                category = "Filters",
                name = "Oil Filter",
                sku = Sku("SKU-001"),
                description = "High flow",
                salePrice = 45.0,
                minimumStock = 5
            )
        }
    }

    // --- UpdateProductUseCase Validations ---

    @Test
    fun `updateProduct with blank name returns validation error`() = runTest {
        val result = updateProductUseCase(
            id = productId,
            category = "Filters",
            name = "  ",
            sku = "SKU-001",
            salePrice = 50.0,
            minimumStock = 2
        )

        assertTrue(result is AppResult.Failure)
        val error = (result as AppResult.Failure).error
        assertTrue(error is AppError.Validation)
        assertEquals("name", (error as AppError.Validation).details)
        coVerify(exactly = 0) { inventoryRepository.updateProduct(any(), any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `updateProduct with valid inputs delegates to repository`() = runTest {
        val updatedProduct = dummyProduct.copy(name = "Updated Filter")
        coEvery {
            inventoryRepository.updateProduct(
                id = productId,
                category = "Filters",
                name = "Updated Filter",
                sku = Sku("SKU-001"),
                description = null,
                salePrice = 55.0,
                minimumStock = 10
            )
        } returns AppResult.Success(updatedProduct)

        val result = updateProductUseCase(
            id = productId,
            category = "Filters",
            name = "Updated Filter",
            sku = "SKU-001",
            description = null,
            salePrice = 55.0,
            minimumStock = 10
        )

        assertTrue(result is AppResult.Success)
        assertEquals(updatedProduct, (result as AppResult.Success).data)
        coVerify(exactly = 1) {
            inventoryRepository.updateProduct(
                id = productId,
                category = "Filters",
                name = "Updated Filter",
                sku = Sku("SKU-001"),
                description = null,
                salePrice = 55.0,
                minimumStock = 10
            )
        }
    }

    // --- DeleteProductUseCase ---

    @Test
    fun `deleteProduct delegates to repository`() = runTest {
        coEvery { inventoryRepository.deleteProduct(productId) } returns AppResult.Success(Unit)

        val result = deleteProductUseCase(productId)

        assertTrue(result is AppResult.Success)
        coVerify(exactly = 1) { inventoryRepository.deleteProduct(productId) }
    }
}
