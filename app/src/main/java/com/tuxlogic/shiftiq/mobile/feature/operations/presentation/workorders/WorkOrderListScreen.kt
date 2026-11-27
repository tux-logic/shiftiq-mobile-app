package com.tuxlogic.shiftiq.mobile.feature.operations.presentation.workorders

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tuxlogic.shiftiq.mobile.core.designsystem.components.ShiftIQPageHeader
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.PrimaryBlue
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQBorderSlate
import com.tuxlogic.shiftiq.mobile.core.localization.LocalizedText as Text
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.WorkOrderStatus

@Composable
fun WorkOrderListRoute(
    onOpenWorkOrder: (String) -> Unit,
    canCreateWorkOrder: Boolean = false,
    onCreateWorkOrder: () -> Unit = {},
    viewModel: WorkOrderListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { viewModel.refresh() }
    WorkOrderListScreen(
        uiState = uiState,
        onRefresh = viewModel::refresh,
        onOpenWorkOrder = onOpenWorkOrder,
        onSelectStatus = viewModel::selectStatus,
        canCreateWorkOrder = canCreateWorkOrder,
        onCreateWorkOrder = onCreateWorkOrder
    )
}

@Composable
fun WorkOrderListScreen(
    uiState: WorkOrderListUiState,
    onRefresh: () -> Unit,
    onOpenWorkOrder: (String) -> Unit,
    onSelectStatus: (WorkOrderStatus?) -> Unit = {},
    canCreateWorkOrder: Boolean = false,
    onCreateWorkOrder: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxSize()) {
        ShiftIQPageHeader(
            title = "Órdenes de trabajo",
            subtitle = "Órdenes de la sucursal activa",
            actions = {
                IconButton(onClick = onRefresh, enabled = !uiState.isLoading) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refrescar OTs", tint = Color.White)
                }
            }
        )
        PullToRefreshBox(
            modifier = Modifier.weight(1f),
            isRefreshing = uiState.isLoading,
            onRefresh = onRefresh
        ) {
            when {
                uiState.isLoading && uiState.filteredOrders.isEmpty() && uiState.errorMessage == null ->
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) { CircularProgressIndicator(color = PrimaryBlue) }
                uiState.errorMessage != null -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(24.dp)
                ) {
                    item { WorkOrderMessage(message = uiState.errorMessage) }
                }
                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                item {
                    val statuses = listOf(null, WorkOrderStatus.PENDING, WorkOrderStatus.IN_PROGRESS, WorkOrderStatus.COMPLETED)
                    val selectedTab = statuses.indexOf(uiState.selectedStatus).coerceAtLeast(0)
                    ScrollableTabRow(
                        selectedTabIndex = selectedTab,
                        edgePadding = 12.dp,
                        containerColor = Color.White,
                        contentColor = PrimaryBlue
                    ) {
                        statuses.forEachIndexed { index, status ->
                            val label = when (status) {
                                null -> "Todas"
                                WorkOrderStatus.PENDING -> "Pendientes"
                                WorkOrderStatus.IN_PROGRESS -> "En progreso"
                                WorkOrderStatus.COMPLETED -> "Completadas"
                                WorkOrderStatus.CANCELLED -> "Canceladas"
                            }
                            Tab(
                                selected = index == selectedTab,
                                onClick = { onSelectStatus(status) },
                                text = {
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Text(label)
                                        val count = uiState.countByStatus(status)
                                        if (count > 0) Text(count.toString(), style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                            )
                        }
                    }
                }
                if (uiState.filteredOrders.isEmpty()) {
                    item {
                        WorkOrderMessage(
                            message = if (canCreateWorkOrder && uiState.orders.isEmpty()) "No hay órdenes activas en esta sucursal." else "No hay órdenes para este filtro.",
                            actionLabel = if (canCreateWorkOrder && uiState.orders.isEmpty()) "Nueva OT" else null,
                            onAction = onCreateWorkOrder
                        )
                    }
                }
                items(uiState.filteredOrders, key = { it.id.toString() }) { order ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { onOpenWorkOrder(order.id.toString()) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, ShiftIQBorderSlate)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Build, contentDescription = null, tint = PrimaryBlue)
                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(order.formattedNumber.ifBlank { "Orden #${order.internalNumber}" }, fontWeight = FontWeight.Bold)
                                Text(order.diagnosticSummary.ifBlank { "Vehículo ${order.vehicleId}" }, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${order.status.name.replace('_', ' ')} · ${order.tasks.size} tareas", style = MaterialTheme.typography.labelMedium, color = PrimaryBlue)
                            }
                        }
                    }
                }
                }
            }
        }
    }
}

@Composable
private fun WorkOrderMessage(message: String, actionLabel: String? = null, onAction: () -> Unit = {}) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(message, color = MaterialTheme.colorScheme.onSurfaceVariant)
        if (actionLabel != null) androidx.compose.material3.Button(onClick = onAction) { Text(actionLabel) }
    }
}
