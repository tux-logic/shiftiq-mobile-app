package com.tuxlogic.shiftiq.mobile.feature.operations.presentation.workorders

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tuxlogic.shiftiq.mobile.core.designsystem.components.ShiftIQEmptyStateIcon
import com.tuxlogic.shiftiq.mobile.core.designsystem.components.ShiftIQPageHeader
import com.tuxlogic.shiftiq.mobile.core.designsystem.components.ShiftIQSkeletonBox
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.PrimaryBlue
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQBorderSlate
import com.tuxlogic.shiftiq.mobile.core.localization.LocalizedText as Text
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.TaskStatus
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.WorkOrder
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.WorkOrderStatus

private val Graphite = Color(0xFF0F172A)
private val Slate = Color(0xFF64748B)
private val InProgressColor = Color(0xFF0EA5A8)
private val PendingColor = Color(0xFFF59E0B)
private val CompletedColor = Color(0xFF16A34A)
private val CancelledColor = Color(0xFF64748B)
private val WarmNeutral = Color(0xFFF8FAFC)

@Composable
fun WorkOrderListRoute(
    onOpenWorkOrder: (String) -> Unit,
    onNavigateToProfile: () -> Unit = {},
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
        onNavigateToProfile = onNavigateToProfile,
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
    onNavigateToProfile: () -> Unit = {},
    onSelectStatus: (WorkOrderStatus?) -> Unit = {},
    canCreateWorkOrder: Boolean = false,
    onCreateWorkOrder: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val statuses = listOf(null, WorkOrderStatus.PENDING, WorkOrderStatus.IN_PROGRESS, WorkOrderStatus.COMPLETED)

    Column(modifier.fillMaxSize().background(WarmNeutral)) {
        ShiftIQPageHeader(
            title = "Órdenes de Trabajo",
            subtitle = uiState.activeBranchName,
            actions = {
                IconButton(onClick = onNavigateToProfile) {
                    Icon(Icons.Default.Person, contentDescription = "Mi perfil", tint = Color.White)
                }
            }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            statuses.forEach { status ->
                val selected = uiState.selectedStatus == status
                val label = status?.listTabLabel() ?: "Todas"
                val count = uiState.countByStatus(status)
                Column(
                    modifier = Modifier
                        .clickable { onSelectStatus(status) }
                        .padding(horizontal = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.height(44.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            label,
                            color = if (selected) PrimaryBlue else Slate,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
                            style = MaterialTheme.typography.labelLarge
                        )
                        if (count > 0) {
                            Text(
                                count.toString(),
                                color = if (selected) PrimaryBlue else Slate,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.5.dp)
                            .background(if (selected) PrimaryBlue else Color.Transparent)
                    )
                }
            }
        }
        androidx.compose.material3.HorizontalDivider(color = ShiftIQBorderSlate.copy(alpha = 0.6f))

        PullToRefreshBox(
            modifier = Modifier.weight(1f),
            isRefreshing = uiState.isLoading,
            onRefresh = onRefresh
        ) {
            when {
                uiState.isLoading && uiState.filteredOrders.isEmpty() && uiState.errorMessage == null ->
                    WorkOrderSkeletonList()
                uiState.errorMessage != null ->
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        item {
                            WorkOrderEmptyState(
                                title = "No se pudieron cargar las órdenes",
                                message = uiState.errorMessage,
                                actionLabel = "Reintentar",
                                onAction = onRefresh
                            )
                        }
                    }
                uiState.filteredOrders.isEmpty() ->
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 24.dp, top = 24.dp, end = 24.dp, bottom = 116.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        item {
                            Spacer(Modifier.height(56.dp))
                            WorkOrderEmptyState(
                                title = "No hay órdenes en esta categoría",
                                message = if (canCreateWorkOrder) {
                                    "Registra una nueva orden para comenzar."
                                } else {
                                    "No hay órdenes de trabajo disponibles en esta sede."
                                },
                                actionLabel = if (canCreateWorkOrder) "Nueva OT" else null,
                                onAction = onCreateWorkOrder
                            )
                        }
                    }
                else ->
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, top = 10.dp, end = 16.dp, bottom = 116.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.filteredOrders, key = { it.id.toString() }) { order ->
                            WorkOrderCard(order = order, onClick = { onOpenWorkOrder(order.id.toString()) })
                        }
                    }
            }
        }
    }
}

@Composable
private fun WorkOrderCard(order: WorkOrder, onClick: () -> Unit) {
    val statusColor = order.status.listColor()
    val completedTasks = order.tasks.count { it.status == TaskStatus.COMPLETED }
    val progress = if (order.tasks.isEmpty()) 0f else completedTasks.toFloat() / order.tasks.size

    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, ShiftIQBorderSlate),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.fillMaxWidth().padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(8.dp).clip(CircleShape).background(statusColor))
                Spacer(Modifier.width(8.dp))
                Text(
                    order.formattedNumber.ifBlank { "OT-${order.internalNumber.toString().padStart(6, '0')}" },
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Graphite,
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.titleSmall
                )
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = statusColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        order.status.listLabel(),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    order.vehicleModel,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Graphite,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = WarmNeutral,
                    border = BorderStroke(1.dp, ShiftIQBorderSlate)
                ) {
                    Text(
                        order.vehiclePlate,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        color = Graphite,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
            Spacer(Modifier.height(6.dp))
            Text(
                if (order.customerPhone.isNotBlank()) "${order.customerName} · ${order.customerPhone}" else order.customerName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color(0xFF475569),
                style = MaterialTheme.typography.bodySmall
            )
            if (order.diagnosticSummary.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    order.diagnosticSummary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Slate,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(Modifier.height(14.dp))
            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(4.dp)),
                color = if (order.tasks.isNotEmpty() && completedTasks == order.tasks.size) CompletedColor else PrimaryBlue,
                trackColor = Color(0xFFF1F5F9)
            )
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "S/ ${order.totalAmount.amount.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString()}",
                    color = PrimaryBlue,
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.titleSmall
                )
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
private fun WorkOrderSkeletonList() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, top = 10.dp, end = 16.dp, bottom = 116.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(3) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(20.dp))
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ShiftIQSkeletonBox(16.dp, widthFraction = 0.62f)
                ShiftIQSkeletonBox(14.dp, widthFraction = 0.86f)
                ShiftIQSkeletonBox(12.dp, widthFraction = 0.72f)
                ShiftIQSkeletonBox(4.dp)
                ShiftIQSkeletonBox(16.dp, widthFraction = 0.34f)
            }
        }
    }
}

@Composable
private fun WorkOrderEmptyState(
    title: String,
    message: String?,
    actionLabel: String?,
    onAction: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ShiftIQEmptyStateIcon()
        Spacer(Modifier.height(16.dp))
        Text(
            title,
            textAlign = TextAlign.Center,
            color = Graphite,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium
        )
        if (!message.isNullOrBlank()) {
            Spacer(Modifier.height(6.dp))
            Text(message, textAlign = TextAlign.Center, color = Slate, style = MaterialTheme.typography.bodySmall)
        }
        if (actionLabel != null) {
            Spacer(Modifier.height(20.dp))
            Button(onClick = onAction) { Text(actionLabel) }
        }
    }
}

private fun WorkOrderStatus.listTabLabel(): String = when (this) {
    WorkOrderStatus.PENDING -> "Pendientes"
    WorkOrderStatus.IN_PROGRESS -> "En progreso"
    WorkOrderStatus.COMPLETED -> "Completadas"
    WorkOrderStatus.PAID -> "Pagadas"
    WorkOrderStatus.DELETED, WorkOrderStatus.CANCELLED -> "Canceladas"
}

private fun WorkOrderStatus.listLabel(): String = when (this) {
    WorkOrderStatus.PENDING -> "Pendiente"
    WorkOrderStatus.IN_PROGRESS -> "En progreso"
    WorkOrderStatus.COMPLETED -> "Completada"
    WorkOrderStatus.PAID -> "Pagada"
    WorkOrderStatus.DELETED, WorkOrderStatus.CANCELLED -> "Cancelada"
}

private fun WorkOrderStatus.listColor(): Color = when (this) {
    WorkOrderStatus.PENDING -> PendingColor
    WorkOrderStatus.IN_PROGRESS -> InProgressColor
    WorkOrderStatus.COMPLETED -> CompletedColor
    WorkOrderStatus.PAID -> CompletedColor
    WorkOrderStatus.DELETED, WorkOrderStatus.CANCELLED -> CancelledColor
}
