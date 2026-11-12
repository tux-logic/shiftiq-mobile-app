package com.tuxlogic.shiftiq.mobile.feature.inventory.presentation.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.arrowBack
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
import com.tuxlogic.shiftiq.mobile.feature.inventory.domain.model.ProductBatch
import com.tuxlogic.shiftiq.mobile.feature.inventory.domain.model.ProductDetail
import com.tuxlogic.shiftiq.mobile.feature.inventory.presentation.components.InventoryPrimaryButton
import com.tuxlogic.shiftiq.mobile.feature.inventory.presentation.components.InventorySectionCard
import com.tuxlogic.shiftiq.mobile.feature.inventory.presentation.components.ProductInUseDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    onBack: () -> Unit,
    onEdit: (String) -> Unit,
    onBatchIn: (String) -> Unit,
    onAdjust: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProductDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val detail = state.detail
    val productId = detail?.id?.toString().orEmpty()

    LaunchedEffect(state.navigateBack) {
        if (state.navigateBack) {
            viewModel.onNavigatedBack()
            onBack()
        }
    }

    if (state.showInUseDialog && detail != null) {
        ProductInUseDialog(
            productName = detail.name,
            onDismiss = viewModel::dismissInUseDialog
        )
    }

    if (state.showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = viewModel::dismissDeleteConfirm,
            title = { Text("¿Eliminar repuesto?") },
            text = {
                Text(
                    "Esta acción eliminará el producto y sus lotes. " +
                        "Si está asociado a órdenes de trabajo, el sistema bloqueará la baja."
                )
            },
            confirmButton = {
                Button(
                    onClick = viewModel::confirmDelete,
                    colors = ButtonDefaults.buttonColors(containerColor = SeverityCrimson)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissDeleteConfirm) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Slate50,
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = detail?.name ?: "Detalle del producto",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Slate900,
                        maxLines = 1
                    )
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
        when {
            state.isLoading && detail == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            }
            detail == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.errorMessage ?: "No se pudo cargar el producto.",
                        color = SeverityCrimson
                    )
                }
            }
            else -> {
                ProductDetailContent(
                    detail = detail,
                    isDeleting = state.isDeleting,
                    errorMessage = state.errorMessage,
                    paddingTop = padding.calculateTopPadding(),
                    onEdit = { onEdit(productId) },
                    onBatchIn = { onBatchIn(productId) },
                    onAdjust = { onAdjust(productId) },
                    onDelete = viewModel::requestDelete
                )
            }
        }
    }
}

@Composable
private fun ProductDetailContent(
    detail: ProductDetail,
    isDeleting: Boolean,
    errorMessage: String?,
    paddingTop: androidx.compose.ui.unit.Dp,
    onEdit: () -> Unit,
    onBatchIn: () -> Unit,
    onAdjust: () -> Unit,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = paddingTop)
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Slate100)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "SKU: ${detail.sku.value}",
                    color = Slate700,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Slate100)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = detail.category,
                    color = Slate600,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(16.dp)
        ) {
            Text("EXISTENCIAS ACTUALES", color = Slate500, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(
                text = "${detail.currentStock} un.",
                color = if (detail.lowStockAlert) SeverityCrimson else Slate900,
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "${detail.batches.size} lotes FIFO activos",
                color = Slate500,
                fontSize = 12.sp
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            InfoMiniCard(
                label = "Mín. alerta",
                value = "${detail.minimumStock} un.",
                modifier = Modifier.weight(1f)
            )
            InfoMiniCard(
                label = "Precio venta",
                value = detail.salePrice.formatted(),
                modifier = Modifier.weight(1f),
                valueColor = PrimaryBlue
            )
        }

        if (!detail.description.isNullOrBlank()) {
            InventorySectionCard(title = "Descripción") {
                Text(detail.description, color = Slate700, fontSize = 13.sp, lineHeight = 18.sp)
            }
        }

        InventorySectionCard(title = "Lotes FIFO", badge = "${detail.batches.size} lotes") {
            val sorted = detail.batches.sortedBy { it.createdAt }
            if (sorted.isEmpty()) {
                Text("No hay lotes con existencias disponibles.", color = Slate500, fontSize = 12.sp)
            } else {
                sorted.forEachIndexed { index, batch ->
                    if (index > 0) {
                        HorizontalDivider(color = Slate200, modifier = Modifier.padding(vertical = 8.dp))
                    }
                    FifoBatchRow(batch = batch, priorityIndex = index + 1)
                }
            }
        }

        InventoryPrimaryButton(
            text = "Ingresar Nuevo Lote",
            onClick = onBatchIn,
            enabled = !isDeleting,
            leadingIcon = Icons.Default.Add
        )

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = onAdjust,
                enabled = !isDeleting,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Slate200)
            ) {
                Icon(Icons.Default.Remove, contentDescription = null, tint = Slate700, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.size(6.dp))
                Text("Ajustar Stock", color = Slate800, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            }
            OutlinedButton(
                onClick = onEdit,
                enabled = !isDeleting,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Slate200)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, tint = Slate700, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.size(6.dp))
                Text("Editar Información", color = Slate800, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
            }
        }

        InventorySectionCard(title = "Zona de peligro", badge = "Eliminar") {
            Text(
                text = "Elimina el repuesto del catálogo. Solo es posible si no está vinculado a órdenes de trabajo.",
                color = Slate600,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = onDelete,
                enabled = !isDeleting,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE2E2), contentColor = SeverityCrimson)
            ) {
                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.size(6.dp))
                Text("Eliminar repuesto", fontWeight = FontWeight.Bold)
            }
        }

        errorMessage?.let {
            Text(text = it, color = SeverityCrimson, fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun InfoMiniCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = Slate900
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .padding(14.dp)
    ) {
        Text(label.uppercase(), color = Slate500, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, color = valueColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun FifoBatchRow(batch: ProductBatch, priorityIndex: Int) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Prioridad FIFO #$priorityIndex",
                color = Slate800,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = batch.acquisitionCost.formatted(),
                color = Slate600,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Inicial: ${batch.initialQuantity} • Disponible: ${batch.availableQuantity}",
            color = Slate700,
            fontSize = 12.sp
        )
        if (batch.createdAt.isNotBlank()) {
            Text(text = "Ingreso: ${batch.createdAt}", color = Slate400, fontSize = 11.sp)
        }
    }
}
