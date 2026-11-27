package com.tuxlogic.shiftiq.mobile.feature.iot.presentation.telemetry

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.HistoryToggleOff
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tuxlogic.shiftiq.mobile.core.designsystem.components.ShiftIQPageHeader
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.PrimaryBlue
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQBorderSlate
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQSuccess
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate500
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate700
import com.tuxlogic.shiftiq.mobile.core.localization.LocalizedText as Text
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.DtcAlert
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.DtcSeverity
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.DtcStatus
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.TelemetrySnapshot
import java.time.Duration
import java.time.Instant

@Composable
fun TelemetryDashboardRoute(
    onNavigateBack: () -> Unit,
    onCreateWorkOrderFromAlert: (DtcAlert) -> Unit,
    viewModel: TelemetryDashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    TelemetryDashboardScreen(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onCreateWorkOrderFromAlert = onCreateWorkOrderFromAlert,
        onMarkResolved = viewModel::markAlertAsResolved,
        onRefresh = viewModel::loadAlerts,
        onSelectSeverity = viewModel::selectSeverity,
        onSelectAlert = viewModel::selectAlert,
        onLoadSensors = viewModel::loadSensors
    )
}

@Composable
fun TelemetryDashboardScreen(
    uiState: TelemetryDashboardUiState,
    onNavigateBack: () -> Unit,
    onCreateWorkOrderFromAlert: (DtcAlert) -> Unit,
    onMarkResolved: () -> Unit,
    onRefresh: () -> Unit,
    onSelectSeverity: (DtcSeverity?) -> Unit,
    onSelectAlert: (DtcAlert?) -> Unit,
    onLoadSensors: (DtcAlert) -> Unit,
    modifier: Modifier = Modifier
) {
    var showingSensors by remember { mutableStateOf(false) }
    val selectedAlert = uiState.selectedAlert
    Column(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        when {
            showingSensors && selectedAlert != null -> ShiftIQPageHeader(
                title = "Lectura de sensores",
                subtitle = selectedAlert.vehiclePlate,
                showBackButton = true,
                onBackPressed = { showingSensors = false; onSelectAlert(null) }
            )
            selectedAlert != null -> ShiftIQPageHeader(
                title = "Detalle del diagnóstico",
                subtitle = "${selectedAlert.vehiclePlate} · ${selectedAlert.vehicleModel}",
                showBackButton = true,
                onBackPressed = { onSelectAlert(null) }
            )
            else -> ShiftIQPageHeader(
                title = "Telemetría IoT",
                subtitle = "Alertas y sensores · ${uiState.activeBranchName}",
                showBackButton = false
            )
        }

        when {
            showingSensors && selectedAlert != null -> LiveSensorsContent(
                alert = selectedAlert,
                snapshot = uiState.sensorSnapshot,
                isLoading = uiState.isLoadingSensors,
                errorMessage = uiState.errorMessage,
                onRefresh = { onLoadSensors(selectedAlert) }
            )
            selectedAlert != null -> DtcDetailContent(
                alert = selectedAlert,
                onMarkResolved = onMarkResolved,
                onCreateWorkOrder = { onCreateWorkOrderFromAlert(selectedAlert) }
            )
            else -> TelemetryAlertList(
                uiState = uiState,
                onRefresh = onRefresh,
                onSelectSeverity = onSelectSeverity,
                onSelectAlert = onSelectAlert,
                onOpenWorkOrder = onCreateWorkOrderFromAlert,
                onOpenLiveSensors = { alert ->
                    onSelectAlert(alert)
                    onLoadSensors(alert)
                    showingSensors = true
                }
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TelemetryAlertList(
    uiState: TelemetryDashboardUiState,
    onRefresh: () -> Unit,
    onSelectSeverity: (DtcSeverity?) -> Unit,
    onSelectAlert: (DtcAlert?) -> Unit,
    onOpenWorkOrder: (DtcAlert) -> Unit,
    onOpenLiveSensors: (DtcAlert) -> Unit
) {
    PullToRefreshBox(
        isRefreshing = uiState.isLoading,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize()
    ) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, ShiftIQBorderSlate), shape = RoundedCornerShape(20.dp)) {
            Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = RoundedCornerShape(20.dp), color = PrimaryBlue.copy(alpha = .1f)) {
                    Row(Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.HistoryToggleOff, null, tint = PrimaryBlue, modifier = Modifier.size(15.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Lecturas OBD-II", style = MaterialTheme.typography.labelMedium, color = PrimaryBlue, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(Modifier.weight(1f))
                Button(
                    onClick = { uiState.visibleAlerts.firstOrNull()?.let(onOpenLiveSensors) },
                    enabled = uiState.visibleAlerts.isNotEmpty(),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 13.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Default.Analytics, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(5.dp))
                    Text("Telemetría en vivo", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf(
                Triple(null, "Todas", uiState.allAlerts.count { it.status == DtcStatus.ACTIVE }),
                Triple(DtcSeverity.CRITICAL, "Críticas", uiState.criticalCount),
                Triple(DtcSeverity.MEDIUM, "Moderadas", uiState.moderateCount)
            ).forEach { (severity, label, count) ->
                FilterChip(
                    selected = uiState.selectedSeverity == severity,
                    onClick = { onSelectSeverity(severity) },
                    label = { Text("$label $count") }
                )
            }
        }
        if (uiState.isLoading) {
            CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally), color = PrimaryBlue)
        }
        uiState.errorMessage?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        if (!uiState.isLoading && uiState.visibleAlerts.isEmpty()) {
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, ShiftIQBorderSlate)) {
                Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Sensors, null, tint = PrimaryBlue, modifier = Modifier.size(36.dp))
                    Text(if (uiState.allAlerts.isEmpty()) "No hay alertas de diagnóstico para esta sede." else "No hay alertas con esos filtros.", fontWeight = FontWeight.SemiBold)
                    Text("Las alertas se consultan desde los dispositivos OBD-II activos.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        uiState.visibleAlerts.forEach { alert ->
            DtcAlertCard(
                alert = alert,
                onClick = { onSelectAlert(alert) },
                onOpenWorkOrder = { onOpenWorkOrder(alert) }
            )
        }
        Spacer(Modifier.height(10.dp))
    }
    }
}

@Composable
private fun DtcAlertCard(alert: DtcAlert, onClick: () -> Unit, onOpenWorkOrder: () -> Unit) {
    val severityColor = severityColor(alert.severity)
    val severityBackground = when (alert.severity) {
        DtcSeverity.CRITICAL -> Color(0x14DC2626)
        DtcSeverity.MEDIUM -> Color(0x19F59E0B)
        DtcSeverity.LOW -> Color(0x1938BDF8)
        DtcSeverity.HIGH -> Color(0x19F97316)
    }
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(if (alert.severity == DtcSeverity.CRITICAL) 1.5.dp else 1.dp, if (alert.severity == DtcSeverity.CRITICAL) Color(0x80DC2626) else ShiftIQBorderSlate),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFF1E293B)) {
                    Text(alert.dtcCode, Modifier.padding(horizontal = 10.dp, vertical = 4.dp), color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                }
                Surface(shape = RoundedCornerShape(12.dp), color = severityBackground) {
                    Text(alert.severity.label(), Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = severityColor, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.weight(1f))
                Text(alert.timeAgo(), style = MaterialTheme.typography.labelSmall, color = Slate500, maxLines = 1)
            }
            Text(alert.description, fontWeight = FontWeight.Bold, color = Slate700, style = MaterialTheme.typography.titleSmall)
            Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFF1F5F9)) {
                Row(Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DirectionsCar, null, modifier = Modifier.size(15.dp), tint = Slate500)
                    Spacer(Modifier.width(6.dp))
                    Text("${alert.vehiclePlate} · ${alert.vehicleModel}", Modifier.weight(1f), maxLines = 1, color = Slate700, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    if (alert.customerName.isNotBlank()) Text(alert.customerName, maxLines = 1, color = Slate500, style = MaterialTheme.typography.labelSmall)
                }
            }
            if (alert.aiDiagnosticRecommendation.isNotBlank()) {
                Surface(shape = RoundedCornerShape(10.dp), color = PrimaryBlue.copy(alpha = .07f), border = BorderStroke(1.dp, PrimaryBlue.copy(alpha = .18f))) {
                    Row(Modifier.fillMaxWidth().padding(10.dp), verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.AutoAwesome, null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(alert.aiDiagnosticRecommendation, color = PrimaryBlue, style = MaterialTheme.typography.bodySmall, maxLines = 2)
                    }
                }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                OutlinedButton(onClick = onClick, shape = RoundedCornerShape(18.dp), contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)) {
                    Icon(Icons.Default.Analytics, null, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(5.dp))
                    Text("Freeze frame", style = MaterialTheme.typography.labelMedium)
                }
                Spacer(Modifier.width(8.dp))
                Button(onClick = onOpenWorkOrder, shape = RoundedCornerShape(18.dp), colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)), contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 13.dp, vertical = 6.dp)) {
                    Icon(Icons.Default.PostAdd, null, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(5.dp))
                    Text("Abrir OT", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
private fun DtcDetailContent(
    alert: DtcAlert,
    onMarkResolved: () -> Unit,
    onCreateWorkOrder: () -> Unit
) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, ShiftIQBorderSlate), shape = RoundedCornerShape(18.dp)) {
            Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(alert.dtcCode, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = severityColor(alert.severity))
                Text("${alert.vehiclePlate} · ${alert.vehicleModel.ifBlank { "Vehículo" }}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text("${alert.customerName.ifBlank { "Cliente" }} · ${alert.systemCategory.ifBlank { "Sistema del vehículo" }}")
                Text(alert.description, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("${alert.severity.label()} · ${alert.status.label()} · ${alert.timeAgo()}", style = MaterialTheme.typography.bodySmall)
            }
        }
        if (alert.aiDiagnosticRecommendation.isNotBlank()) {
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF0FF)), shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Recomendación de diagnóstico", fontWeight = FontWeight.Bold, color = PrimaryBlue)
                    Text(alert.aiDiagnosticRecommendation)
                }
            }
        }
        if (alert.freezeFrame.isNotEmpty()) {
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, ShiftIQBorderSlate), shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Datos al detectar la alerta", fontWeight = FontWeight.Bold)
                    alert.freezeFrame.forEach { (label, value) ->
                        Row(Modifier.fillMaxWidth()) {
                            Text(label, Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(value, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(onClick = onMarkResolved, modifier = Modifier.weight(1f), shape = RoundedCornerShape(18.dp)) {
                Icon(Icons.Default.CheckCircleOutline, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Marcar como resuelta")
            }
            Button(onClick = onCreateWorkOrder, modifier = Modifier.weight(1f), shape = RoundedCornerShape(18.dp), colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A))) {
                Icon(Icons.Default.PostAdd, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Abrir OT")
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun LiveSensorsContent(
    alert: DtcAlert,
    snapshot: TelemetrySnapshot?,
    isLoading: Boolean,
    errorMessage: String?,
    onRefresh: () -> Unit
) {
    PullToRefreshBox(
        isRefreshing = isLoading,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize()
    ) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, ShiftIQBorderSlate), shape = RoundedCornerShape(16.dp)) {
            Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 11.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.HistoryToggleOff, null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text("Lectura más reciente · ${alert.vehiclePlate}", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text("Desliza para actualizar", style = MaterialTheme.typography.labelSmall, color = Slate500)
            }
        }
        Text("ÚLTIMAS LECTURAS DE SENSORES", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Slate500)
        when {
            isLoading -> CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally), color = PrimaryBlue)
            snapshot != null -> {
                val metrics = listOf(
                    SensorMetric("Engine RPM", snapshot.rpm.toDouble(), "rpm", 650.0, 3500.0),
                    SensorMetric("Coolant Temperature", snapshot.temperature.toDouble(), "°C", 80.0, 102.0),
                    SensorMetric("Vehicle Speed (VSS)", snapshot.speedKmh, "km/h", 0.0, 140.0),
                    SensorMetric("Odometer", snapshot.odometerKm, "km", 0.0, 500000.0),
                    SensorMetric("Fuel Tank Level", snapshot.fuelLevelPercent, "%", 15.0, 100.0)
                )
                metrics.chunked(2).forEach { rowMetrics ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        rowMetrics.forEach { metric ->
                            SensorGaugeCard(metric, Modifier.weight(1f))
                        }
                        if (rowMetrics.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
                Text("Lectura: ${snapshot.createdAt}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            else -> {
                Text(errorMessage ?: "No hay lecturas de telemetría para este vehículo.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
    }
}

@Composable
private fun SensorGaugeCard(metric: SensorMetric, modifier: Modifier = Modifier) {
    val isWarning = metric.value < metric.normalMin || metric.value > metric.normalMax
    val isDanger = metric.value > metric.normalMax * 1.15 || (metric.normalMin > 0 && metric.value < metric.normalMin * .85)
    val statusColor = when {
        isDanger -> Color(0xFFDC2626)
        isWarning -> Color(0xFFF59E0B)
        else -> ShiftIQSuccess
    }
    val fraction = if (metric.normalMax > 0) (metric.value / metric.normalMax).toFloat().coerceIn(0f, 1f) else .5f
    Card(
        modifier = modifier.height(144.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(if (isWarning) 1.5.dp else 1.dp, if (isWarning) statusColor.copy(alpha = .5f) else ShiftIQBorderSlate),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.fillMaxSize().padding(14.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = RoundedCornerShape(8.dp), color = statusColor.copy(alpha = .12f)) {
                    Icon(Icons.Default.Sensors, null, tint = statusColor, modifier = Modifier.padding(6.dp).size(18.dp))
                }
                Spacer(Modifier.width(8.dp))
                Text(metric.label, maxLines = 1, style = MaterialTheme.typography.labelMedium, color = Slate500, fontWeight = FontWeight.SemiBold)
            }
            Text("${metric.value.formatSensorValue()} ${metric.unit}", color = statusColor, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            LinearProgressIndicator(
                progress = { fraction },
                modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(4.dp)),
                color = statusColor,
                trackColor = Color(0xFFF1F5F9)
            )
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Min: ${metric.normalMin.toInt()} ${metric.unit}", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                Text("Max: ${metric.normalMax.toInt()} ${metric.unit}", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
            }
        }
    }
}

private data class SensorMetric(
    val label: String,
    val value: Double,
    val unit: String,
    val normalMin: Double,
    val normalMax: Double
)

private fun Double.formatSensorValue(): String =
    if (this % 1.0 == 0.0) toInt().toString() else "%.1f".format(this)

private fun severityColor(severity: DtcSeverity): Color = when (severity) {
    DtcSeverity.CRITICAL -> Color(0xFFD32F2F)
    DtcSeverity.HIGH -> Color(0xFFEF6C00)
    DtcSeverity.MEDIUM -> Color(0xFFED9D00)
    DtcSeverity.LOW -> Color(0xFF388E3C)
}

private fun DtcSeverity.label(): String = when (this) {
    DtcSeverity.CRITICAL -> "Crítica"
    DtcSeverity.HIGH -> "Alta"
    DtcSeverity.MEDIUM -> "Moderada"
    DtcSeverity.LOW -> "Baja"
}

private fun DtcStatus.label(): String = when (this) {
    DtcStatus.ACTIVE -> "Activa"
    DtcStatus.IN_INSPECTION -> "En revisión"
    DtcStatus.RESOLVED -> "Resuelta"
}

private fun DtcAlert.timeAgo(): String {
    val instant = runCatching { Instant.parse(createdAt) }.getOrNull() ?: return createdAt
    val elapsed = Duration.between(instant, Instant.now()).seconds.coerceAtLeast(0)
    return when {
        elapsed < 60 -> "Ahora"
        elapsed < 3600 -> "Hace ${elapsed / 60} min"
        elapsed < 86400 -> "Hace ${elapsed / 3600} h"
        else -> "Hace ${elapsed / 86400} días"
    }
}
