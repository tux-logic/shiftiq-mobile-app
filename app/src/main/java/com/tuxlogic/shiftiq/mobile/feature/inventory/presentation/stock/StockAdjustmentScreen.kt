package com.tuxlogic.shiftiq.mobile.feature.inventory.presentation.stock

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import com.tuxlogic.shiftiq.mobile.core.localization.LocalizedText as Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.tuxlogic.shiftiq.mobile.core.designsystem.components.ShiftIQPageHeader
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.PrimaryBlue
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.SeverityCrimson
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate200
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate50
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate500
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate700
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate900
import com.tuxlogic.shiftiq.mobile.feature.inventory.presentation.components.InventoryPrimaryButton
import com.tuxlogic.shiftiq.mobile.feature.inventory.presentation.components.InventorySectionCard
import com.tuxlogic.shiftiq.mobile.feature.inventory.presentation.components.NegativeStockDialog

@Composable
fun StockAdjustmentScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StockMovementViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.navigateBack) {
        if (state.navigateBack) {
            viewModel.onNavigatedBack()
            onBack()
        }
    }

    if (state.showNegativeStockDialog) {
        NegativeStockDialog(
            currentStock = state.currentStock,
            attemptedQty = state.quantity,
            onCorrectQuantity = viewModel::dismissNegativeStockDialog,
            onDismiss = viewModel::dismissNegativeStockDialog
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate50)
    ) {
        ShiftIQPageHeader(
            title = "Ajustar stock",
            subtitle = state.productName.takeIf { it.isNotBlank() } ?: "Ajuste de stock",
            showBackButton = true,
            onBackPressed = onBack
        )
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
            return@Column
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FilterChip(
                selected = true,
                onClick = {},
                enabled = false,
                label = { Text("Tipo: Descontar existencias", fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Slate900,
                    selectedLabelColor = Color.White,
                    disabledSelectedContainerColor = Slate900
                )
            )

            InventorySectionCard(title = "Cantidad a descontar") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = viewModel::decrementQuantity,
                        enabled = state.quantity > 1 && !state.isSaving,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Slate200)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Disminuir")
                    }
                    Text(
                        text = "${state.quantity} un.",
                        color = Slate900,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    OutlinedButton(
                        onClick = viewModel::incrementQuantity,
                        enabled = !state.isSaving,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Slate200)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Aumentar")
                    }
                }
            }

            InventorySectionCard(title = "Saldo resultante") {
                val resulting = state.adjustProjectedStock
                Text(
                    text = "$resulting un.",
                    color = if (resulting < 0) SeverityCrimson else Slate900,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Stock actual: ${state.currentStock} un.",
                    color = Slate500,
                    fontSize = 12.sp
                )
                if (resulting < 0) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "La cantidad supera las existencias disponibles.",
                        color = SeverityCrimson,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            InventorySectionCard(title = "Nota") {
                Text(
                    text = "El descuento aplica la política FIFO sobre los lotes más antiguos.",
                    color = Slate700,
                    fontSize = 13.sp
                )
            }

            state.errorMessage?.let {
                Text(text = it, color = SeverityCrimson, fontSize = 13.sp)
            }

            if (state.isSaving) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryBlue, modifier = Modifier.size(28.dp))
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 32.dp)
        ) {
            InventoryPrimaryButton(
                text = "Confirmar ajuste",
                onClick = viewModel::adjustStock,
                enabled = !state.isSaving && !state.isLoading
            )
        }
    }
}
