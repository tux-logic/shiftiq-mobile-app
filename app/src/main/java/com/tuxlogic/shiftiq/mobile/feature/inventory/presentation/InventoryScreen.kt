package com.tuxlogic.shiftiq.mobile.feature.inventory.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.tuxlogic.shiftiq.mobile.core.designsystem.components.scaleOnPress
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.arrowBack
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.warningAmber
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Primary
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.PrimaryBlue
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.SeverityCrimson
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate100
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate200
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate400
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate50
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate500
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate600
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate700
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate800
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate900
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.TelematicsGreen
import com.tuxlogic.shiftiq.mobile.feature.inventory.domain.model.ProductDetail
import com.tuxlogic.shiftiq.mobile.feature.inventory.domain.model.ProductItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: InventoryViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var productDialog by remember { mutableStateOf(false) }
    var stockDialog by remember { mutableStateOf(false) }
    var editingProduct by remember { mutableStateOf<ProductItem?>(null) }
    var activeProductId by remember { mutableStateOf<String?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Slate50,
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Inventario de Taller",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Slate900
                        )
                        Text(
                            text = "Productos, lotes y existencias",
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
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            // Branch Picker Dropdown
            BranchPicker(
                branches = state.branches,
                selectedId = state.selectedBranchId,
                onSelect = viewModel::selectBranch
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = viewModel::updateSearch,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar producto por nombre o SKU...", color = Slate400, fontSize = 14.sp) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                textStyle = LocalTextStyle.current.copy(color = Slate900, fontSize = 14.sp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Slate900,
                    unfocusedTextColor = Slate900,
                    cursorColor = PrimaryBlue,
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = Slate200,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Action row: Filter chip + Refresh + New Product button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = state.lowStockOnly,
                    onClick = { viewModel.setLowStockOnly(!state.lowStockOnly) },
                    label = { Text("Stock crítico") },
                    leadingIcon = {
                        Icon(
                            imageVector = warningAmber,
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

                TextButton(onClick = viewModel::loadProducts) {
                    Text("Actualizar", color = PrimaryBlue, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Primary)
                        .scaleOnPress(onClick = {
                            editingProduct = null
                            productDialog = true
                        })
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("+ Nuevo", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Main Product List or Loading/Empty
            when {
                state.isLoading && state.products.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PrimaryBlue)
                    }
                }
                state.products.isEmpty() -> {
                    InfoCard(
                        title = "Inventario vacío",
                        message = "No se encontraron productos registrados para esta sucursal o con el filtro seleccionado."
                    )
                }
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(vertical = 6.dp)
                    ) {
                        items(state.products, key = { it.id.toString() }) { product ->
                            ProductCard(
                                product = product,
                                onOpen = { viewModel.loadProduct(product.id.toString()) },
                                onStock = {
                                    activeProductId = product.id.toString()
                                    stockDialog = true
                                }
                            )
                        }
                    }
                }
            }

            // Message toasts / alerts
            state.errorMessage?.let {
                Spacer(modifier = Modifier.height(6.dp))
                InfoCard(title = "Atención", message = it, isError = true)
            }
            state.infoMessage?.let {
                Spacer(modifier = Modifier.height(6.dp))
                InfoCard(title = "Éxito", message = it, isError = false)
            }
        }
    }

    // Modal: Formulario Crear / Editar Producto
    if (productDialog) {
        ProductFormDialog(
            initial = editingProduct,
            isSaving = state.isSaving,
            onDismiss = { productDialog = false },
            onSave = { name, cat, sku, desc, price, min ->
                viewModel.saveProduct(
                    id = editingProduct?.id?.toString(),
                    name = name,
                    category = cat,
                    sku = sku,
                    description = desc,
                    price = price,
                    minimumStock = min
                )
                productDialog = false
            }
        )
    }

    // Modal: Ajuste / Ingreso de Lote de Stock
    if (stockDialog) {
        StockFormDialog(
            isSaving = state.isSaving,
            onDismiss = { stockDialog = false },
            onSave = { qty, cost ->
                activeProductId?.let { id ->
                    viewModel.addStock(id, qty, cost)
                }
                stockDialog = false
            }
        )
    }

    // Modal: Detalle de Producto con Lotes
    state.selectedProduct?.let { detail ->
        ProductDetailDialog(
            detail = detail,
            onClose = viewModel::clearSelectedProduct,
            onStock = {
                activeProductId = detail.id.toString()
                stockDialog = true
            },
            onDelete = {
                viewModel.deleteProduct(detail.id.toString())
            }
        )
    }
}

@Composable
private fun BranchPicker(
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
        shape = RoundedCornerShape(12.dp),
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
                Text(text = "SEDE DEL TALLER", color = Slate500, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text(text = current?.name ?: "Seleccionar sede", color = Slate900, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
            Text(text = "Cambiar ▼", color = PrimaryBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            branches.forEach { branch ->
                DropdownMenuItem(
                    text = { Text(branch.name, color = Slate900, fontWeight = if (branch.id == selectedId) FontWeight.Bold else FontWeight.Normal) },
                    onClick = {
                        onSelect(branch.id)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun ProductCard(
    product: ProductItem,
    onOpen: () -> Unit,
    onStock: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, if (product.lowStockAlert) Color(0xFFFCA5A5) else Slate200)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = product.name,
                    color = Slate900,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = product.salePrice.formatted(),
                    color = PrimaryBlue,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${product.category} • SKU: ${product.sku.value}",
                color = Slate500,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (product.lowStockAlert) SeverityCrimson else TelematicsGreen)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Stock: ${product.currentStock} un. (Mín: ${product.minimumStock})",
                        color = if (product.lowStockAlert) SeverityCrimson else Slate700,
                        fontSize = 12.sp,
                        fontWeight = if (product.lowStockAlert) FontWeight.Bold else FontWeight.Medium
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Slate100)
                        .clickable(onClick = onStock)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(text = "+ Stock", color = Slate800, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ProductDetailDialog(
    detail: ProductDetail,
    onClose: () -> Unit,
    onStock: () -> Unit,
    onDelete: () -> Unit
) {
    var confirmDelete by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onClose,
        title = {
            Column {
                Text(text = detail.name, fontWeight = FontWeight.Bold, color = Slate900, fontSize = 18.sp)
                Text(text = "${detail.category} • SKU ${detail.sku.value}", color = Slate500, fontSize = 12.sp)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Precio de Venta:", color = Slate600, fontSize = 13.sp)
                    Text(text = detail.salePrice.formatted(), color = PrimaryBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Stock Actual / Mínimo:", color = Slate600, fontSize = 13.sp)
                    Text(text = "${detail.currentStock} / ${detail.minimumStock} unidades", color = Slate900, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
                if (!detail.description.isNullOrBlank()) {
                    Text(text = detail.description, color = Slate700, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Lotes de Inventario Registrados", fontWeight = FontWeight.Bold, color = Slate800, fontSize = 13.sp)

                if (detail.batches.isEmpty()) {
                    Text(text = "No hay lotes con existencias disponibles.", color = Slate500, fontSize = 12.sp)
                } else {
                    detail.batches.forEach { batch ->
                        HorizontalDivider(color = Slate200)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Inicial: ${batch.initialQuantity} | Disp: ${batch.availableQuantity}", color = Slate800, fontSize = 12.sp)
                            Text(text = batch.acquisitionCost.formatted(), color = Slate600, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onStock,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("+ Añadir Lote")
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                TextButton(onClick = { confirmDelete = true }) {
                    Text("Eliminar", color = SeverityCrimson)
                }
                TextButton(onClick = onClose) {
                    Text("Cerrar", color = Slate700)
                }
            }
        }
    )

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("¿Eliminar producto?") },
            text = { Text("Esta acción eliminará el producto y todos sus lotes asociados permanentemente.") },
            confirmButton = {
                Button(
                    onClick = {
                        confirmDelete = false
                        onDelete()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SeverityCrimson)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmDelete = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun ProductFormDialog(
    initial: ProductItem?,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String?, Double, Int) -> Unit
) {
    var name by remember(initial?.id) { mutableStateOf(initial?.name.orEmpty()) }
    var category by remember(initial?.id) { mutableStateOf(initial?.category.orEmpty()) }
    var sku by remember(initial?.id) { mutableStateOf(initial?.sku?.value.orEmpty()) }
    var description by remember(initial?.id) { mutableStateOf(initial?.description.orEmpty()) }
    var priceText by remember(initial?.id) { mutableStateOf(initial?.salePrice?.amount?.toString().orEmpty()) }
    var minText by remember(initial?.id) { mutableStateOf(initial?.minimumStock?.toString().orEmpty()) }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "Nuevo Producto" else "Editar Producto", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre del Producto *") },
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(color = Slate900)
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Categoría (ej: Frenos, Aceites) *") },
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(color = Slate900)
                )
                OutlinedTextField(
                    value = sku,
                    onValueChange = { sku = it },
                    label = { Text("Código SKU *") },
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(color = Slate900)
                )
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = { Text("Precio de Venta (S/) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(color = Slate900)
                )
                OutlinedTextField(
                    value = minText,
                    onValueChange = { minText = it },
                    label = { Text("Stock Mínimo de Alerta *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(color = Slate900)
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción (Opcional)") },
                    textStyle = LocalTextStyle.current.copy(color = Slate900)
                )
                error?.let {
                    Text(text = it, color = SeverityCrimson, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(
                enabled = !isSaving,
                onClick = {
                    val price = priceText.toDoubleOrNull()
                    val minimum = minText.toIntOrNull()
                    error = when {
                        name.isBlank() || category.isBlank() || sku.isBlank() -> "Completa todos los campos obligatorios."
                        price == null || price <= 0.0 -> "El precio debe ser un número mayor a 0."
                        minimum == null || minimum < 0 -> "El stock mínimo no puede ser negativo."
                        else -> null
                    }
                    if (error == null) {
                        onSave(
                            name.trim(),
                            category.trim(),
                            sku.trim().uppercase(),
                            description.trim().takeIf { it.isNotBlank() },
                            price!!,
                            minimum!!
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text(if (isSaving) "Guardando..." else "Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Slate700)
            }
        }
    )
}

@Composable
private fun StockFormDialog(
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSave: (Int, Double) -> Unit
) {
    var qty by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Registrar Movimiento de Stock", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Ingresa la cantidad a añadir al lote y el costo unitario de adquisición.",
                    color = Slate600,
                    fontSize = 12.sp
                )
                OutlinedTextField(
                    value = qty,
                    onValueChange = { qty = it },
                    label = { Text("Cantidad a Ingresar *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(color = Slate900)
                )
                OutlinedTextField(
                    value = cost,
                    onValueChange = { cost = it },
                    label = { Text("Costo Unitario de Adquisición (S/) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(color = Slate900)
                )
                error?.let {
                    Text(text = it, color = SeverityCrimson, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(
                enabled = !isSaving,
                onClick = {
                    val q = qty.toIntOrNull()
                    val c = cost.toDoubleOrNull()
                    error = when {
                        q == null || q <= 0 -> "Ingresa una cantidad entera mayor a cero."
                        c == null || c < 0.0 -> "El costo no puede ser negativo."
                        else -> null
                    }
                    if (error == null) {
                        onSave(q!!, c!!)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text(if (isSaving) "Registrando..." else "Registrar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Slate700)
            }
        }
    )
}

@Composable
private fun InfoCard(title: String, message: String, isError: Boolean = false) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = if (isError) Color(0xFFFEF2F2) else Color.White),
        border = BorderStroke(1.dp, if (isError) Color(0xFFFECACA) else Slate200)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                color = if (isError) SeverityCrimson else Slate900,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = message,
                color = if (isError) Color(0xFFB91C1C) else Slate600,
                fontSize = 12.sp
            )
        }
    }
}
