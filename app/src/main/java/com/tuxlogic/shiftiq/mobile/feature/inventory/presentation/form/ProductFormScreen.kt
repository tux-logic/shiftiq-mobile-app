package com.tuxlogic.shiftiq.mobile.feature.inventory.presentation.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.tuxlogic.shiftiq.mobile.core.designsystem.components.ShiftIQPageHeader
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.PrimaryBlue
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.SeverityCrimson
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate200
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate400
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate50
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate500
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate900
import com.tuxlogic.shiftiq.mobile.feature.inventory.presentation.components.DuplicateSkuDialog
import com.tuxlogic.shiftiq.mobile.feature.inventory.presentation.components.InventoryPrimaryButton
import com.tuxlogic.shiftiq.mobile.feature.inventory.presentation.components.InventorySectionCard
import com.tuxlogic.shiftiq.mobile.feature.inventory.presentation.components.InventorySuggestedCategories

@Composable
fun ProductFormScreen(
    onBack: () -> Unit,
    onViewCatalog: () -> Unit = onBack,
    modifier: Modifier = Modifier,
    viewModel: ProductFormViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.navigateBack) {
        if (state.navigateBack) {
            viewModel.onNavigatedBack()
            onBack()
        }
    }

    if (state.showDuplicateSkuDialog) {
        DuplicateSkuDialog(
            sku = state.sku,
            onViewExisting = {
                viewModel.dismissDuplicateSkuDialog()
                onViewCatalog()
            },
            onEditSku = viewModel::dismissDuplicateSkuDialog,
            onDismiss = viewModel::dismissDuplicateSkuDialog
        )
    }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Slate900,
        unfocusedTextColor = Slate900,
        cursorColor = PrimaryBlue,
        focusedBorderColor = PrimaryBlue,
        unfocusedBorderColor = Slate200,
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate50)
    ) {
        ShiftIQPageHeader(
            title = if (state.isEditMode) "Editar repuesto" else "Registrar repuesto",
            subtitle = "Formulario de producto",
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
                .verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            InventorySectionCard(title = "1. Identificación", badge = "SKU") {
                OutlinedTextField(
                    value = state.sku,
                    onValueChange = viewModel::onSkuChanged,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Código SKU *") },
                    placeholder = { Text("Ej. FLT-OIL-001", color = Slate400) },
                    singleLine = true,
                    enabled = !state.isSaving && !state.isEditMode,
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors
                )
            }

            InventorySectionCard(title = "2. Comercial", badge = "Catálogo") {
                OutlinedTextField(
                    value = state.name,
                    onValueChange = viewModel::onNameChanged,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Nombre del repuesto *") },
                    singleLine = true,
                    enabled = !state.isSaving,
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = state.category,
                    onValueChange = viewModel::onCategoryChanged,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Categoría *") },
                    placeholder = { Text(InventorySuggestedCategories.joinToString(", "), color = Slate400) },
                    singleLine = true,
                    enabled = !state.isSaving,
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = state.description,
                    onValueChange = viewModel::onDescriptionChanged,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Descripción") },
                    minLines = 2,
                    enabled = !state.isSaving,
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors
                )
            }

            InventorySectionCard(title = "3. Parámetros de stock", badge = "Inventario") {
                OutlinedTextField(
                    value = state.minimumStockText,
                    onValueChange = viewModel::onMinimumStockChanged,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Stock mínimo de alerta *") },
                    singleLine = true,
                    enabled = !state.isSaving,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = state.salePriceText,
                    onValueChange = viewModel::onSalePriceChanged,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Precio de venta (S/) *") },
                    singleLine = true,
                    enabled = !state.isSaving,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors
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

            Spacer(modifier = Modifier.height(8.dp))
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 32.dp)
        ) {
            InventoryPrimaryButton(
                text = if (state.isEditMode) "Guardar cambios" else "Registrar repuesto",
                onClick = viewModel::submit,
                enabled = !state.isSaving && !state.isLoading
            )
        }
    }
}
