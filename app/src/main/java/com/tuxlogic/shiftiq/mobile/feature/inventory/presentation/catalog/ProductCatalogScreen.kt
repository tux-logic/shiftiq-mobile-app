package com.tuxlogic.shiftiq.mobile.feature.inventory.presentation.catalog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.tuxlogic.shiftiq.mobile.core.designsystem.components.ShiftIQEmptyStateIcon
import com.tuxlogic.shiftiq.mobile.core.designsystem.components.ShiftIQPageHeader
import com.tuxlogic.shiftiq.mobile.core.designsystem.components.ShiftIQSkeletonBox
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.PrimaryBlue
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQBorderSlate
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate500
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate900
import com.tuxlogic.shiftiq.mobile.core.localization.LocalizedText as Text
import com.tuxlogic.shiftiq.mobile.feature.inventory.presentation.InventoryStockFilter
import com.tuxlogic.shiftiq.mobile.feature.inventory.presentation.InventoryViewModel
import com.tuxlogic.shiftiq.mobile.feature.inventory.presentation.components.InventorySearchField
import com.tuxlogic.shiftiq.mobile.feature.inventory.presentation.components.ProductCatalogCard
import kotlinx.coroutines.delay

private val InventoryCategories = listOf(
    "TODOS", "LUBRICANTES", "FILTROS", "FRENOS", "SUSPENSION", "MOTOR"
)

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ProductCatalogScreen(
    onOpenDetail: (String) -> Unit,
    onCreateProduct: (branchId: String?) -> Unit,
    onBatchIn: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: InventoryViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val lifecycleOwner = LocalLifecycleOwner.current
    var showCategoryFilter by remember { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.loadProducts()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(state.infoMessage) {
        val message = state.infoMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message)
        delay(2500)
        viewModel.clearMessages()
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().background(Color(0xFFF8FAFC))) {
            ShiftIQPageHeader(
                title = "Inventario & Lotes B2B",
                subtitle = "Catálogo de Repuestos · ${state.activeBranchName}"
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 14.dp, end = 16.dp, bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                InventorySearchField(
                    value = state.searchQuery,
                    onValueChange = viewModel::updateSearch,
                    modifier = Modifier.weight(1f),
                    onClear = { viewModel.updateSearch("") }
                )
                IconButton(
                    onClick = { showCategoryFilter = true },
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            if (state.selectedCategory != "TODOS") Slate900 else Color.White,
                            RoundedCornerShape(16.dp)
                        )
                ) {
                    Icon(
                        Icons.Default.Tune,
                        contentDescription = "Filtrar por categoría",
                        tint = if (state.selectedCategory != "TODOS") Color.White else Color(0xFF475569)
                    )
                }
            }

            val filters = InventoryStockFilter.values()
            val selectedFilterIndex = filters.indexOf(state.stockFilter).coerceAtLeast(0)
            ScrollableTabRow(
                selectedTabIndex = selectedFilterIndex,
                edgePadding = 12.dp,
                containerColor = Color.White,
                contentColor = PrimaryBlue
            ) {
                filters.forEachIndexed { index, filter ->
                    val tabLabel = when (filter) {
                        InventoryStockFilter.ALL -> "Todos" to state.products.size
                        InventoryStockFilter.LOW_STOCK -> "Stock crítico" to state.criticalCount
                        InventoryStockFilter.OUT_OF_STOCK -> "Agotado" to state.outOfStockCount
                    }
                    Tab(
                        selected = selectedFilterIndex == index,
                        onClick = { viewModel.setStockFilter(filter) },
                        text = { Text("${tabLabel.first}  ${tabLabel.second}", fontSize = 12.sp) }
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 18.dp, top = 8.dp, end = 18.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (state.selectedCategory != "TODOS") {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(PrimaryBlue.copy(alpha = 0.1f))
                            .clickable { viewModel.selectCategory("TODOS") }
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(state.selectedCategory, color = PrimaryBlue, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(4.dp))
                        Icon(Icons.Default.Close, contentDescription = "Quitar categoría", tint = PrimaryBlue, modifier = Modifier.size(13.dp))
                    }
                }
                Spacer(Modifier.weight(1f))
                Text("Valuación total: ${state.warehouseValue.formatted()}", color = Slate500, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }

            state.errorMessage?.let { message ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp)
                        .background(Color(0xFFFFE4E6), RoundedCornerShape(14.dp)).padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(message, modifier = Modifier.weight(1f), color = Color(0xFFB91C1C), fontSize = 12.sp)
                    IconButton(onClick = viewModel::loadProducts, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reintentar", tint = Color(0xFFB91C1C))
                    }
                }
            }

            PullToRefreshBox(
                modifier = Modifier.weight(1f),
                isRefreshing = state.isLoading,
                onRefresh = viewModel::loadProducts
            ) {
                when {
                    state.isLoading && state.products.isEmpty() -> ProductSkeletonList()
                    state.visibleProducts.isEmpty() -> LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 32.dp, vertical = 64.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        item {
                            ShiftIQEmptyStateIcon()
                            Spacer(Modifier.height(16.dp))
                            Text(
                                "No se encontraron repuestos",
                                textAlign = TextAlign.Center,
                                color = Slate900,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.5.sp
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                "Prueba con otra categoría o añade uno nuevo",
                                textAlign = TextAlign.Center,
                                color = Slate500,
                                fontSize = 13.sp
                            )
                            Spacer(Modifier.height(20.dp))
                            Button(onClick = { onCreateProduct(state.selectedBranchId) }) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Registrar nuevo producto")
                            }
                        }
                    }
                    else -> LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 120.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(state.visibleProducts, key = { it.id.toString() }) { product ->
                            ProductCatalogCard(
                                product = product,
                                onOpenDetail = { onOpenDetail(product.id.toString()) },
                                onBatchIn = { onBatchIn(product.id.toString()) }
                            )
                        }
                    }
                }
            }
        }

        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))

        if (showCategoryFilter) {
            ModalBottomSheet(onDismissRequest = { showCategoryFilter = false }, containerColor = Color.White) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Filtrar por categoría", color = Slate900, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                    InventoryCategories.chunked(2).forEach { rowCategories ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            rowCategories.forEach { category ->
                                FilterChip(
                                    selected = state.selectedCategory == category,
                                    onClick = {
                                        showCategoryFilter = false
                                        viewModel.selectCategory(category)
                                    },
                                    label = { Text(category) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductSkeletonList() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(4) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, ShiftIQBorderSlate)
            ) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    ShiftIQSkeletonBox(16.dp, widthFraction = 0.55f)
                    ShiftIQSkeletonBox(14.dp, widthFraction = 0.82f)
                    ShiftIQSkeletonBox(12.dp, widthFraction = 0.62f)
                }
            }
        }
    }
}
