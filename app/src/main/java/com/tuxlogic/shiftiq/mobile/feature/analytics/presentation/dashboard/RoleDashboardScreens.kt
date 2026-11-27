package com.tuxlogic.shiftiq.mobile.feature.analytics.presentation.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.PrimaryBlue
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQBorderSlate
import com.tuxlogic.shiftiq.mobile.core.localization.LocalizedText as Text
import com.tuxlogic.shiftiq.mobile.feature.analytics.domain.model.BranchDailySnapshot
import com.tuxlogic.shiftiq.mobile.feature.analytics.domain.model.BranchDailySummary
import com.tuxlogic.shiftiq.mobile.feature.analytics.domain.model.EmployeeDailyCompletion
import com.tuxlogic.shiftiq.mobile.feature.analytics.domain.model.EmployeeAssignedWorkOrderSummary
import com.tuxlogic.shiftiq.mobile.feature.analytics.domain.model.NetworkAnalyticsSummary
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.WorkOrderStatus
import java.time.LocalDate

@Composable
fun OwnerDashboardRoute(
    onNavigateToBilling: () -> Unit,
    onSwitchBranch: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToAppointments: () -> Unit,
    onNavigateToInventory: () -> Unit,
    onNavigateToTelemetry: () -> Unit,
    viewModel: OwnerDashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { viewModel.refresh() }
    OwnerDashboardScreen(
        uiState = uiState,
        onRefresh = viewModel::refresh,
        onNavigateToBilling = onNavigateToBilling,
        onSwitchBranch = onSwitchBranch,
        onNavigateToProfile = onNavigateToProfile,
        onNavigateToOrders = onNavigateToOrders,
        onNavigateToAppointments = onNavigateToAppointments,
        onNavigateToInventory = onNavigateToInventory,
        onNavigateToTelemetry = onNavigateToTelemetry,
        onRangeChanged = viewModel::setRangeDays,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerDashboardScreen(
    uiState: OwnerDashboardUiState,
    onRefresh: () -> Unit,
    onNavigateToBilling: () -> Unit,
    onSwitchBranch: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToAppointments: () -> Unit,
    onNavigateToInventory: () -> Unit,
    onNavigateToTelemetry: () -> Unit,
    onRangeChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxSize().background(DashboardBackground)) {
        RoleDashboardHeader(
            title = uiState.activeBranchName,
            subtitle = "Panel ejecutivo",
            badgeLabel = "PROPIETARIO · ADMIN",
            onTitleClick = onSwitchBranch,
            actions = {
                DashboardHeaderAction(Icons.AutoMirrored.Filled.ReceiptLong, "Facturación", onNavigateToBilling)
                DashboardHeaderAction(Icons.Default.Business, "Cambiar de sede", onSwitchBranch)
                DashboardHeaderAction(Icons.Default.Refresh, "Refrescar KPIs", onRefresh)
                DashboardHeaderAction(Icons.Default.Person, "Mi perfil", onNavigateToProfile)
            }
        )
        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = onRefresh,
            modifier = Modifier.weight(1f).fillMaxWidth()
        ) {
            when {
                uiState.isLoading && uiState.branchSummary == null -> LoadingState()
                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, top = 18.dp, end = 16.dp, bottom = 28.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                uiState.errorMessage?.let { message ->
                    item { MessageCard(message = message) }
                }
                uiState.branchSummary?.takeIf { it.dtcAlertsCount > 0 }?.let { summary ->
                    item {
                        DtcAlertBanner(summary.dtcAlertsCount, onNavigateToTelemetry)
                    }
                }
                uiState.branchSummary?.let { summary ->
                    item {
                        OwnerExecutiveOverviewCard(
                            summary = summary,
                            networkSummary = uiState.networkSummary,
                            onOpenBilling = onNavigateToBilling,
                            onNavigateToOrders = onNavigateToOrders,
                            onNavigateToAppointments = onNavigateToAppointments,
                            onNavigateToInventory = onNavigateToInventory,
                            onNavigateToTelemetry = onNavigateToTelemetry
                        )
                    }
                }
                if (uiState.branchSummary != null) {
                    item {
                        RevenueTrendChart(
                            snapshots = uiState.financialSnapshots,
                            selectedRangeDays = uiState.selectedRangeDays,
                            onRangeChanged = onRangeChanged
                        )
                    }
                    item { ActivityBarChart(snapshots = uiState.financialSnapshots) }
                }
                item {
                    QuickActionRow(
                        onNewOrder = onNavigateToOrders,
                        onNewAppointment = onNavigateToAppointments,
                        onNewBatch = onNavigateToInventory
                    )
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Órdenes activas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        TextButton(onClick = onNavigateToOrders) { Text("Ver todas") }
                    }
                }
                if (uiState.recentWorkOrders.isEmpty()) {
                    item { MessageCard("No hay órdenes activas en esta sucursal.") }
                } else {
                    item {
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, ShiftIQBorderSlate)
                        ) {
                            Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                uiState.recentWorkOrders.forEach { preview ->
                                    val order = preview.order
                                    WorkOrderPreviewCard(
                                        code = order.formattedNumber.ifBlank { "OT-${order.internalNumber}" },
                                        vehicle = preview.vehicleDisplay,
                                        service = order.diagnosticSummary.ifBlank { "Inspección general" },
                                        status = order.status.dashboardLabel(),
                                        statusColor = order.status.dashboardColor()
                                    )
                                }
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
fun EmployeeDashboardRoute(
    onNavigateToBilling: () -> Unit,
    onSwitchBranch: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToAppointments: () -> Unit,
    onNavigateToInventory: () -> Unit,
    onNavigateToTelemetry: () -> Unit,
    onOpenWorkOrder: (String) -> Unit,
    viewModel: EmployeeDashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { viewModel.refresh() }
    EmployeeDashboardScreen(
        uiState = uiState,
        onRefresh = viewModel::refresh,
        onNavigateToBilling = onNavigateToBilling,
        onSwitchBranch = onSwitchBranch,
        onNavigateToProfile = onNavigateToProfile,
        onNavigateToOrders = onNavigateToOrders,
        onNavigateToAppointments = onNavigateToAppointments,
        onNavigateToInventory = onNavigateToInventory,
        onNavigateToTelemetry = onNavigateToTelemetry,
        onOpenWorkOrder = onOpenWorkOrder,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeDashboardScreen(
    uiState: EmployeeDashboardUiState,
    onRefresh: () -> Unit,
    onNavigateToBilling: () -> Unit,
    onSwitchBranch: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToAppointments: () -> Unit,
    onNavigateToInventory: () -> Unit,
    onNavigateToTelemetry: () -> Unit,
    onOpenWorkOrder: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val summary = uiState.summary
    Column(modifier.fillMaxSize().background(DashboardBackground)) {
        RoleDashboardHeader(
            title = "Mi Resumen",
            subtitle = "Cola operativa · ${uiState.activeBranchName}",
            onTitleClick = onSwitchBranch,
            actions = {
                DashboardHeaderAction(Icons.AutoMirrored.Filled.ReceiptLong, "Facturación", onNavigateToBilling)
                DashboardHeaderAction(Icons.Default.Business, "Cambiar de sede", onSwitchBranch)
                DashboardHeaderAction(Icons.Default.Refresh, "Actualizar resumen", onRefresh)
                DashboardHeaderAction(Icons.Default.Person, "Mi perfil", onNavigateToProfile)
            }
        )
        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = onRefresh,
            modifier = Modifier.weight(1f).fillMaxWidth()
        ) {
            when {
                uiState.isLoading && summary == null -> LoadingState()
                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 28.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                uiState.errorMessage?.let { message -> item { MessageCard(message = message) } }
                summary?.takeIf { it.activeDtcAlertsCount > 0 }?.let { active ->
                    item { DtcAlertBanner(active.activeDtcAlertsCount, onNavigateToTelemetry) }
                }
                item {
                    TaskStatusRing(
                        completed = summary?.completedTasksCount ?: 0,
                        inProgress = summary?.inProgressTasksCount ?: 0,
                        pending = summary?.pendingTasksCount ?: 0
                    )
                }
                item {
                    EmployeeWeeklyActivityChart(
                        dailyCompletions = summary?.weeklyCompletedTasks.orEmpty(),
                        averageMinutes = summary?.avgCompletionMinutes
                    )
                }
                item {
                    QuickActionRow(
                        onNewOrder = onNavigateToOrders,
                        onNewAppointment = onNavigateToAppointments,
                        onNewBatch = onNavigateToInventory
                    )
                }
                if (!summary?.assignedWorkOrders.isNullOrEmpty()) {
                    item { EmployeeQueueWorkloadChart(summary!!.assignedWorkOrders) }
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Mi Cola de Trabajo", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        TextButton(onClick = onNavigateToOrders) { Text("Ver todas") }
                    }
                }
                if (summary?.assignedWorkOrders.isNullOrEmpty()) {
                    item { EmployeeEmptyQueueCard() }
                } else {
                    items(summary!!.assignedWorkOrders.take(6), key = { it.workOrderId }) { order ->
                        EmployeeAssignedOrderCard(
                            order = order,
                            onClick = { onOpenWorkOrder(order.workOrderId) }
                        )
                    }
                }
            }
        }
    }
}
}

@Composable
private fun MetricCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, ShiftIQBorderSlate)
    ) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = PrimaryBlue)
        }
    }
}

private val DashboardBackground = Color(0xFFF8FAFC)

@Composable
private fun RoleDashboardHeader(
    title: String,
    subtitle: String,
    badgeLabel: String? = null,
    onTitleClick: (() -> Unit)? = null,
    actions: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit
) {
    val bottomShape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(bottomShape)
            .background(Brush.linearGradient(listOf(Color(0xFF0F172A), Color(0xFF2563EB))))
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(start = 16.dp, top = 10.dp, end = 14.dp, bottom = 18.dp)
    ) {
        if (badgeLabel != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    badgeLabel,
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.09f))
                        .border(1.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                )
                androidx.compose.foundation.layout.Spacer(Modifier.weight(1f))
                actions()
            }
            androidx.compose.foundation.layout.Spacer(Modifier.height(12.dp))
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .then(if (onTitleClick != null) Modifier.clickable(onClick = onTitleClick) else Modifier),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        title,
                        maxLines = 1,
                        color = Color.White,
                        fontSize = if (badgeLabel == null) 18.5.sp else 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.3).sp
                    )
                    if (onTitleClick != null) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Cambiar sede", tint = Color.White.copy(alpha = 0.75f))
                    }
                }
                Text(subtitle, maxLines = 1, color = Color.White.copy(alpha = 0.7f), fontSize = 12.5.sp)
            }
            if (badgeLabel == null) actions()
        }
    }
}

@Composable
private fun DashboardHeaderAction(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    description: String,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .padding(start = 3.dp)
            .size(34.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.08f))
            .border(1.dp, Color.White.copy(alpha = 0.18f), CircleShape)
    ) {
        Icon(icon, contentDescription = description, tint = Color.White, modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun DtcAlertBanner(alertCount: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
        border = BorderStroke(1.dp, Color(0xFFE11D48).copy(alpha = 0.22f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(Modifier.size(9.dp).background(Color(0xFFE11D48), CircleShape))
            Text(
                if (alertCount == 1) "1 vehículo con alerta DTC activa" else "$alertCount vehículos con alertas DTC activas",
                modifier = Modifier.weight(1f),
                color = Color(0xFF991B1B),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold
            )
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color(0xFFE11D48), modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun WorkOrderPreviewCard(
    code: String,
    vehicle: String,
    service: String,
    status: String,
    statusColor: Color
) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(Modifier.size(8.dp).background(statusColor, CircleShape))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text("$code · $vehicle", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, maxLines = 1)
            Text(service, style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B), maxLines = 1)
        }
        Surface(shape = RoundedCornerShape(20.dp), color = statusColor.copy(alpha = 0.12f)) {
            Text(status, modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp), color = statusColor, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
        }
    }
}

private fun WorkOrderStatus.dashboardLabel(): String = when (this) {
    WorkOrderStatus.PENDING -> "Pendiente"
    WorkOrderStatus.IN_PROGRESS -> "En progreso"
    WorkOrderStatus.COMPLETED -> "Completada"
    WorkOrderStatus.CANCELLED -> "Cancelada"
}

private fun WorkOrderStatus.dashboardColor(): Color = when (this) {
    WorkOrderStatus.PENDING -> Color(0xFFF59E0B)
    WorkOrderStatus.IN_PROGRESS -> PrimaryBlue
    WorkOrderStatus.COMPLETED -> Color(0xFF16A34A)
    WorkOrderStatus.CANCELLED -> Color(0xFFE11D48)
}

@Composable
private fun EmployeeAssignedOrderCard(
    order: EmployeeAssignedWorkOrderSummary,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, ShiftIQBorderSlate)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                Modifier.size(42.dp).clip(RoundedCornerShape(13.dp)).background(Color(0xFFEFF6FF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Build, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(21.dp))
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("OT-${order.internalNumber}", fontWeight = FontWeight.Bold, color = Color(0xFF1E293B), modifier = Modifier.weight(1f))
                    Text(
                        order.status.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.labelSmall,
                        color = PrimaryBlue,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Text("Vehículo ${order.vehicleId}", style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B), maxLines = 1)
                Text(
                    "${order.assignedTasksCount} tareas · ${order.pendingTasksCount} pendientes",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF64748B)
                )
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun EmployeeEmptyQueueCard() {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, ShiftIQBorderSlate)
    ) {
        Column(
            Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(Modifier.size(48.dp).clip(CircleShape).background(Color(0xFFEFF6FF)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Build, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(23.dp))
            }
            Text("Sin órdenes asignadas en tu cola.", fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
            Text(
                "Cuando el taller te asigne tareas aparecerán aquí.",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF64748B)
            )
        }
    }
}

@Composable
private fun OwnerExecutiveOverviewCard(
    summary: BranchDailySummary,
    networkSummary: NetworkAnalyticsSummary?,
    onOpenBilling: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToAppointments: () -> Unit,
    onNavigateToInventory: () -> Unit,
    onNavigateToTelemetry: () -> Unit
) {
    val totalFlow = summary.completedWorkOrdersCount + summary.totalAppointmentsCount
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, ShiftIQBorderSlate)
    ) {
        Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Column(
                Modifier.fillMaxWidth().clickable(onClick = onOpenBilling),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Ingresos del mes", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    networkSummary?.let {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Red", style = MaterialTheme.typography.labelMedium, color = PrimaryBlue, fontWeight = FontWeight.SemiBold)
                            Text(it.totalNetworkRevenue.formatted(), style = MaterialTheme.typography.labelMedium, color = PrimaryBlue, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
                Text(summary.totalRevenue.formatted(), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row(Modifier.clickable(onClick = onNavigateToOrders), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    Box(Modifier.size(8.dp).background(PrimaryBlue, RoundedCornerShape(50)))
                    Text("Órdenes", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                    Text("· ${summary.completedWorkOrdersCount}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                }
                Row(Modifier.clickable(onClick = onNavigateToAppointments), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    Box(Modifier.size(8.dp).background(Color(0xFF14B8A6), RoundedCornerShape(50)))
                    Text("Citas", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                    Text("· ${summary.totalAppointmentsCount}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                }
            }
            Row(Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(8.dp))) {
                if (totalFlow == 0) {
                    Box(Modifier.fillMaxWidth().height(8.dp).background(ShiftIQBorderSlate))
                } else {
                    Box(Modifier.weight(summary.completedWorkOrdersCount.coerceAtLeast(1).toFloat()).fillMaxSize().background(PrimaryBlue))
                    Box(Modifier.weight(summary.totalAppointmentsCount.coerceAtLeast(1).toFloat()).fillMaxSize().background(Color(0xFF14B8A6)))
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                HealthStatusCard(
                    label = if (summary.lowStockAlertsCount > 0) "Reponer stock" else "Stock saludable",
                    count = summary.lowStockAlertsCount.takeIf { it > 0 },
                    icon = Icons.Default.Inventory2,
                    alert = summary.lowStockAlertsCount > 0,
                    onClick = onNavigateToInventory,
                    modifier = Modifier.weight(1f)
                )
                HealthStatusCard(
                    label = if (summary.dtcAlertsCount > 0) "Revisión OBD2 necesaria" else "Telemetría estable",
                    count = null,
                    icon = Icons.Default.Sensors,
                    alert = summary.dtcAlertsCount > 0,
                    onClick = onNavigateToTelemetry,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun HealthStatusCard(
    label: String,
    count: Int?,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    alert: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val color = if (alert) Color(0xFFB45309) else Color(0xFF15803D)
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f)),
        border = BorderStroke(1.dp, color.copy(alpha = 0.16f))
    ) {
        Row(Modifier.fillMaxWidth().padding(10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold, color = color, maxLines = 2)
            count?.let { Text(it.toString(), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = color) }
        }
    }
}

@Composable
private fun ActionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, ShiftIQBorderSlate)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(icon, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(24.dp))
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.SemiBold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun MessageCard(message: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, ShiftIQBorderSlate)
    ) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(message, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun LoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = PrimaryBlue)
    }
}

@Composable
private fun QuickActionRow(
    onNewOrder: () -> Unit,
    onNewAppointment: () -> Unit,
    onNewBatch: () -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        QuickAction("Orden", Icons.Default.Build, onNewOrder, Modifier.weight(1f))
        QuickAction("Cita", Icons.Default.CalendarMonth, onNewAppointment, Modifier.weight(1f))
        QuickAction("Lote", Icons.Default.Inventory2, onNewBatch, Modifier.weight(1f))
    }
}

@Composable
private fun QuickAction(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(onClick = onClick, modifier = modifier) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Icon(icon, contentDescription = null, tint = PrimaryBlue)
            Text(label, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun RevenueTrendChart(
    snapshots: List<BranchDailySnapshot>,
    selectedRangeDays: Int,
    onRangeChanged: (Int) -> Unit
) {
    val total = snapshots.sumOf { it.totalRevenue.amount.toDouble() }
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, ShiftIQBorderSlate)
    ) {
        Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Tendencia de ingresos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("S/ ${"%,.2f".format(total)}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = PrimaryBlue)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(7, 14, 30).forEach { days ->
                    FilterChip(
                        selected = selectedRangeDays == days,
                        onClick = { onRangeChanged(days) },
                        label = { Text("${days}d") }
                    )
                }
            }
            if (snapshots.isEmpty()) {
                Text("No hay movimientos de ingresos para este periodo.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                Canvas(Modifier.fillMaxWidth().height(128.dp)) {
                    val values = snapshots.map { it.totalRevenue.amount.toFloat() }
                    val max = values.maxOrNull()?.coerceAtLeast(1f) ?: 1f
                    val path = Path()
                    values.forEachIndexed { index, value ->
                        val x = if (values.size == 1) size.width / 2 else size.width * index / (values.size - 1)
                        val y = size.height - (value / max * (size.height - 12f)) - 6f
                        if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
                        drawCircle(PrimaryBlue, radius = 4f, center = Offset(x, y))
                    }
                    drawPath(path, PrimaryBlue, style = Stroke(width = 4f, cap = StrokeCap.Round))
                }
            }
            Text("${snapshots.firstOrNull()?.snapshotDate.orEmpty()}   —   ${snapshots.lastOrNull()?.snapshotDate.orEmpty()}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun ActivityBarChart(snapshots: List<BranchDailySnapshot>) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, ShiftIQBorderSlate)
    ) {
        Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Actividad diaria", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                ChartLegend(Color(0xFF2563EB), "Órdenes")
                ChartLegend(Color(0xFF14B8A6), "Citas")
            }
            if (snapshots.isEmpty()) {
                Text("No hay actividad registrada para este periodo.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                Canvas(Modifier.fillMaxWidth().height(120.dp)) {
                    val max = snapshots.maxOf { maxOf(it.completedWorkOrdersCount, it.totalAppointmentsCount) }.coerceAtLeast(1)
                    val groupWidth = size.width / snapshots.size
                    val barWidth = (groupWidth * 0.24f).coerceAtLeast(3f)
                    snapshots.forEachIndexed { index, snapshot ->
                        val x = groupWidth * index + groupWidth / 2f
                        val orderHeight = size.height * snapshot.completedWorkOrdersCount / max
                        val appointmentHeight = size.height * snapshot.totalAppointmentsCount / max
                        drawRoundRect(Color(0xFF2563EB), Offset(x - barWidth - 2f, size.height - orderHeight), Size(barWidth, orderHeight), cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f))
                        drawRoundRect(Color(0xFF14B8A6), Offset(x + 2f, size.height - appointmentHeight), Size(barWidth, appointmentHeight), cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f))
                    }
                }
            }
        }
    }
}

@Composable
private fun ChartLegend(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        Box(Modifier.size(9.dp).background(color, RoundedCornerShape(3.dp)))
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun TaskStatusRing(completed: Int, inProgress: Int, pending: Int) {
    val total = completed + inProgress + pending
    val percent = if (total == 0) 0 else completed * 100 / total
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, ShiftIQBorderSlate)
    ) {
        Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Estado de tareas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(if (total == 0) "Sin tareas registradas" else "$total tareas en tu cola", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                Box(Modifier.size(124.dp), contentAlignment = Alignment.Center) {
                    Canvas(Modifier.fillMaxSize().padding(12.dp)) {
                        val stroke = 16.dp.toPx()
                        drawArc(Color(0xFFE2E8F0), -90f, 360f, false, style = Stroke(stroke))
                        if (total > 0) {
                            var start = -90f
                            listOf(
                                completed to Color(0xFF16A34A),
                                inProgress to Color(0xFF14B8A6),
                                pending to Color(0xFFF59E0B)
                            ).forEach { (count, color) ->
                                val sweep = 360f * count / total
                                if (sweep > 0) drawArc(color, start, sweep, false, style = Stroke(stroke))
                                start += sweep
                            }
                        }
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("$percent%", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("Completado", style = MaterialTheme.typography.labelSmall)
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                    ChartLegend(Color(0xFF16A34A), "Completadas: $completed")
                    ChartLegend(Color(0xFF14B8A6), "En progreso: $inProgress")
                    ChartLegend(Color(0xFFF59E0B), "Pendientes: $pending")
                }
            }
        }
    }
}

@Composable
private fun EmployeeWeeklyActivityChart(
    dailyCompletions: List<EmployeeDailyCompletion>,
    averageMinutes: Double?
) {
    val days = if (dailyCompletions.isNotEmpty()) dailyCompletions else (6 downTo 0).map {
        EmployeeDailyCompletion(LocalDate.now().minusDays(it.toLong()).toString(), 0)
    }
    val total = days.sumOf { it.count }
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, ShiftIQBorderSlate)
    ) {
        Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Productividad semanal", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("$total tareas completadas en 7 días", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                averageMinutes?.let {
                    Text(formatDuration(it), color = PrimaryBlue, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                }
            }
            val max = days.maxOfOrNull { it.count }.orEmptyMax()
            Canvas(Modifier.fillMaxWidth().height(124.dp)) {
                val groupWidth = size.width / days.size
                val barWidth = groupWidth * 0.46f
                days.forEachIndexed { index, day ->
                    val barHeight = size.height * day.count / max
                    val left = index * groupWidth + (groupWidth - barWidth) / 2f
                    drawRoundRect(Color(0xFF2563EB), Offset(left, size.height - barHeight), Size(barWidth, barHeight.coerceAtLeast(2f)), cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f))
                }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                days.forEach { day -> Text(day.date.takeLast(2), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            }
        }
    }
}

@Composable
private fun EmployeeQueueWorkloadChart(orders: List<EmployeeAssignedWorkOrderSummary>) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, ShiftIQBorderSlate)
    ) {
        Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Carga por orden", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            orders.take(5).forEach { order ->
                val total = order.assignedTasksCount.coerceAtLeast(order.pendingTasksCount)
                val doneRatio = if (total == 0) 0f else (total - order.pendingTasksCount).toFloat() / total
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Orden #${order.internalNumber}", fontWeight = FontWeight.SemiBold)
                        Text("${order.pendingTasksCount} pendientes", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    LinearProgressIndicator(
                        progress = { doneRatio.coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth().height(7.dp),
                        color = PrimaryBlue,
                        trackColor = Color(0xFFE2E8F0)
                    )
                }
            }
        }
    }
}

private fun Int?.orEmptyMax(): Float = (this ?: 1).coerceAtLeast(1).toFloat()

private fun formatDuration(minutes: Double): String {
    val rounded = minutes.toInt()
    return if (rounded < 60) "$rounded min/tarea" else {
        val hours = rounded / 60
        val remainder = rounded % 60
        if (remainder == 0) "${hours}h/tarea" else "${hours}h ${remainder}m"
    }
}
