package com.tuxlogic.shiftiq.mobile.feature.inventory.presentation.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import com.tuxlogic.shiftiq.mobile.core.localization.LocalizedText as Text
import androidx.compose.runtime.Composable
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
import com.tuxlogic.shiftiq.mobile.core.designsystem.components.ShiftIQPageHeader
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.PrimaryBlue
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQBorderSlate
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

@Composable
fun ProductDetailScreen(
    onBack: () -> Unit,
    onBatchIn: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProductDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val detail = state.detail
    val productId = detail?.id?.toString().orEmpty()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate50)
    ) {
        ShiftIQPageHeader(
            title = detail?.name ?: "Detalle del producto",
            subtitle = "Detalle de producto",
            showBackButton = true,
            onBackPressed = onBack
        )
        when {
            state.isLoading && detail == null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            }
            detail == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
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
                    errorMessage = state.errorMessage,
                    onBatchIn = { onBatchIn(productId) }
                )
            }
        }
    }
}

@Composable
private fun ProductDetailContent(
    detail: ProductDetail,
    errorMessage: String?,
    onBatchIn: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 32.dp),
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
                .clip(RoundedCornerShape(18.dp))
                .background(Color.White)
                .border(BorderStroke(1.dp, ShiftIQBorderSlate), RoundedCornerShape(18.dp))
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
            leadingIcon = Icons.Default.Add
        )

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
