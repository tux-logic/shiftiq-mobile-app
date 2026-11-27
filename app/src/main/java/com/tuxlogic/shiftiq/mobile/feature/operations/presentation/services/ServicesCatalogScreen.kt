package com.tuxlogic.shiftiq.mobile.feature.operations.presentation.services

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tuxlogic.shiftiq.mobile.core.designsystem.components.ShiftIQPageHeader
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.PrimaryBlue
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQBorderSlate
import com.tuxlogic.shiftiq.mobile.core.localization.LocalizedText as Text

@Composable
fun ServicesCatalogRoute(
    onNavigateBack: () -> Unit,
    viewModel: ServicesCatalogViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ServicesCatalogScreen(
        state = state,
        onNavigateBack = onNavigateBack,
        onRefresh = viewModel::refresh,
        onCreate = viewModel::openCreateForm,
        onEdit = viewModel::openEditForm,
        onDelete = viewModel::requestDelete,
        onDismissForm = viewModel::closeForm,
        onNameChanged = viewModel::updateName,
        onPriceChanged = viewModel::updatePrice,
        onSave = viewModel::save,
        onDismissDelete = viewModel::dismissDelete,
        onConfirmDelete = viewModel::confirmDelete
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicesCatalogScreen(
    state: ServicesCatalogUiState,
    onNavigateBack: () -> Unit,
    onRefresh: () -> Unit,
    onCreate: () -> Unit,
    onEdit: (com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.ServiceItem) -> Unit,
    onDelete: (com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.ServiceItem) -> Unit,
    onDismissForm: () -> Unit,
    onNameChanged: (String) -> Unit,
    onPriceChanged: (String) -> Unit,
    onSave: () -> Unit,
    onDismissDelete: () -> Unit,
    onConfirmDelete: () -> Unit
) {
    Box(Modifier.fillMaxSize()) {
    Column(Modifier.fillMaxSize()) {
        ShiftIQPageHeader(
            title = "Catálogo de servicios",
            subtitle = "Servicios de mano de obra de la sede",
            showBackButton = true,
            onBackPressed = onNavigateBack
        )
        PullToRefreshBox(
            isRefreshing = state.isLoading,
            onRefresh = onRefresh,
            modifier = Modifier.weight(1f).fillMaxWidth()
        ) {
        when {
            state.isLoading && state.services.isEmpty() -> Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) { CircularProgressIndicator(color = PrimaryBlue) }
            state.errorMessage != null && state.services.isEmpty() -> Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                Text(state.errorMessage)
            }
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                state.successMessage?.let { message -> item { Text(message, color = PrimaryBlue) } }
                state.errorMessage?.let { message -> item { Text(message, color = MaterialTheme.colorScheme.error) } }
                if (state.services.isEmpty()) item { Text("No hay servicios activos. Agrega el primer servicio de la sede.", modifier = Modifier.padding(vertical = 20.dp), color = MaterialTheme.colorScheme.onSurfaceVariant) }
                items(state.services, key = { it.id.toString() }) { service ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White),
                        border = BorderStroke(1.dp, ShiftIQBorderSlate)
                    ) {
                        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(service.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text(service.price.formatted(), color = PrimaryBlue, fontWeight = FontWeight.SemiBold)
                            }
                            IconButton(onClick = { onEdit(service) }) { Icon(Icons.Default.Edit, contentDescription = "Editar servicio", tint = PrimaryBlue) }
                            IconButton(onClick = { onDelete(service) }) { Icon(Icons.Default.Delete, contentDescription = "Eliminar servicio", tint = MaterialTheme.colorScheme.error) }
                        }
                    }
                }
            }
        }
        }
    }
        ExtendedFloatingActionButton(
            onClick = onCreate,
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            icon = { Icon(Icons.Default.Add, contentDescription = null) },
            text = { Text("Nuevo servicio") },
            containerColor = PrimaryBlue,
            contentColor = androidx.compose.ui.graphics.Color.White,
            shape = RoundedCornerShape(24.dp)
        )
    }

    if (state.showForm) {
        AlertDialog(
            onDismissRequest = onDismissForm,
            title = { Text(if (state.editingService == null) "Nuevo servicio" else "Editar servicio") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(value = state.name, onValueChange = onNameChanged, label = { Text("Nombre del servicio") }, singleLine = true)
                    OutlinedTextField(value = state.price, onValueChange = onPriceChanged, label = { Text("Precio (S/)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true)
                    state.errorMessage?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                }
            },
            confirmButton = {
                Button(onClick = onSave, enabled = !state.isSaving) {
                    if (state.isSaving) CircularProgressIndicator()
                    else Text(if (state.editingService == null) "Guardar" else "Actualizar")
                }
            },
            dismissButton = { TextButton(onClick = onDismissForm) { Text("Cancelar") } }
        )
    }

    if (state.showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = onDismissDelete,
            title = { Text("Eliminar servicio") },
            text = { Text("¿Eliminar este servicio de esta sede?") },
            confirmButton = { Button(onClick = onConfirmDelete, enabled = !state.isSaving) { Text("Eliminar") } },
            dismissButton = { TextButton(onClick = onDismissDelete) { Text("Cancelar") } }
        )
    }
}
