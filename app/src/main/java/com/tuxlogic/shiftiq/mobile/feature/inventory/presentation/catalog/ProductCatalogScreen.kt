package com.tuxlogic.shiftiq.mobile.feature.inventory.presentation.catalog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.arrowBack
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.PrimaryBlue
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.SeverityCrimson
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate200
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate400
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate50
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate500
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate900
import com.tuxlogic.shiftiq.mobile.feature.inventory.presentation.InventoryBranchUi
import com.tuxlogic.shiftiq.mobile.feature.inventory.presentation.InventoryViewModel
import com.tuxlogic.shiftiq.mobile.feature.inventory.presentation.components.InventoryCategoryChips
import com.tuxlogic.shiftiq.mobile.feature.inventory.presentation.components.InventoryPrimaryButton
import com.tuxlogic.shiftiq.mobile.feature.inventory.presentation.components.InventorySearchField
import com.tuxlogic.shiftiq.mobile.feature.inventory.presentation.components.InventorySummaryRow
import com.tuxlogic.shiftiq.mobile.feature.inventory.presentation.components.ProductCatalogCard
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductCatalogScreen(
    onBack: () -> Unit,
    onOpenDetail: (String) -> Unit,
    onCreateProduct: (branchId: String?) -> Unit,
    onBatchIn: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: InventoryViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadProducts()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(state.errorMessage) {
        val message = state.errorMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message)
        viewModel.clearMessages()
    }

    LaunchedEffect(state.infoMessage) {
        val message = state.infoMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message)
        delay(2500)
        viewModel.clearMessages()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Slate50,
        contentWindowInsets = WindowInsets.statusBars,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Inventario",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Slate900
                        )
                        Text(
                            text = "Catálogo, lotes FIFO y existencias",
                            fontSize = 12.sp,
                            color = Slate500
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = arrowBack,
                            contentDescription = "Atrás",
                            tint = Slate900,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                InventoryPrimaryButton(
                    text = "+ Registrar Nuevo Repuesto",
                    onClick = { onCreateProduct(state.selectedBranchId) },
                    leadingIcon = Icons.Default.Add
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            CatalogBranchPicker(
                branches = state.branches,
                selectedId = state.selectedBranchId,
                onSelect = viewModel::selectBranch
            )

            Spacer(modifier = Modifier.height(12.dp))

            InventorySummaryRow(
                totalProducts = state.products.size,
                criticalCount = state.criticalCount,
                warehouseValueLabel = state.warehouseValue.formatted()
            )

            Spacer(modifier = Modifier.height(12.dp))

            InventorySearchField(
                value = state.searchQuery,
                onValueChange = viewModel::updateSearch
            )

            Spacer(modifier = Modifier.height(8.dp))

            InventoryCategoryChips(
                categories = state.availableCategories,
                selected = state.categoryFilter,
                totalCount = state.products.size,
                onSelectAll = viewModel::clearCategoryFilter,
                onSelectCategory = viewModel::setCategoryFilter
            )

            Spacer(modifier = Modifier.height(8.dp))

            FilterChip(
                selected = state.lowStockOnly,
                onClick = { viewModel.setLowStockOnly(!state.lowStockOnly) },
                label = { Text("Stock crítico", fontSize = 12.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (state.lowStockOnly) SeverityCrimson else Slate500,
                        modifier = Modifier.size(16.dp)
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFFFEE2E2),
                    selectedLabelColor = SeverityCrimson
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            when {
                state.isLoading && state.products.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryBlue)
                    }
                }
                state.products.isEmpty() -> {
                    CatalogEmptyBanner()
                }
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 12.dp)
                    ) {
                        items(state.products, key = { it.id.toString() }) { product ->
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
    }
}

@Composable
private fun CatalogEmptyBanner() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Slate200)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Inventario vacío",
                color = Slate900,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "No se encontraron productos para esta sucursal o con el filtro seleccionado.",
                color = Slate500,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun CatalogBranchPicker(
    branches: List<InventoryBranchUi>,
    selectedId: String?,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val current = branches.firstOrNull { it.id == selectedId } ?: branches.firstOrNull()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = true },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Slate200)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "SEDE DEL TALLER",
                    color = Slate500,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = current?.name ?: "Seleccionar sede",
                    color = Slate900,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Text(text = "Cambiar ▼", color = PrimaryBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            branches.forEach { branch ->
                DropdownMenuItem(
                    text = {
                        Text(
                            branch.name,
                            color = Slate900,
                            fontWeight = if (branch.id == selectedId) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    onClick = {
                        onSelect(branch.id)
                        expanded = false
                    }
                )
            }
        }
    }
}
