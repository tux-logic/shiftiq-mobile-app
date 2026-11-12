package com.tuxlogic.shiftiq.mobile.feature.inventory.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.tuxlogic.shiftiq.mobile.feature.inventory.presentation.catalog.ProductCatalogScreen

@Composable
fun InventoryScreen(
    onBack: () -> Unit = {},
    onOpenDetail: (String) -> Unit = {},
    onCreateProduct: (branchId: String?) -> Unit = {},
    onBatchIn: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: InventoryViewModel = hiltViewModel()
) {
    ProductCatalogScreen(
        onBack = onBack,
        onOpenDetail = onOpenDetail,
        onCreateProduct = onCreateProduct,
        onBatchIn = onBatchIn,
        modifier = modifier,
        viewModel = viewModel
    )
}
