package com.tuxlogic.shiftiq.mobile.feature.operations.presentation.workorder

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Navy900
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.PrimaryBlue
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQSuccess
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQTheme
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate100
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate200
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate500
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate700
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.spacing

@Composable
fun LiveWorkOrderRoute(
    workOrderId: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LiveWorkOrderViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(workOrderId) { viewModel.loadWorkOrder(workOrderId) }
    LiveWorkOrderScreen(uiState = uiState, onNavigateBack = onNavigateBack, modifier = modifier)
}

@Composable
fun LiveWorkOrderScreen(
    uiState: LiveWorkOrderUiState,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    Column(
        modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
    ) {
        Surface(color = Navy900, shadowElevation = spacing.elevationSmall) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = statusBarPadding, start = spacing.small, end = spacing.small, bottom = spacing.smallMedium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(Modifier.width(spacing.small))
                Column {
                    Text("Work order tracking", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Status and tasks from the workshop", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.75f))
                }
            }
        }

        when {
            uiState.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
            uiState.errorMessage != null -> EmptyOrErrorState(message = uiState.errorMessage)
            else -> Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(spacing.medium),
                verticalArrangement = Arrangement.spacedBy(spacing.medium)
            ) {
                WorkOrderSummaryCard(uiState)
                WorkOrderTasksCard(uiState.tasks)
                uiState.totalAmount?.let { amount ->
                    InfoCard(title = "Current total", value = amount, icon = { Icon(Icons.Default.Info, contentDescription = null, tint = PrimaryBlue) })
                }
                if (uiState.status != "COMPLETED" && uiState.status != "CANCELLED") {
                    InfoCard(
                        title = "Customer actions",
                        value = "Approvals and payments will appear here when the backend provides the customer workflow.",
                        icon = { Icon(Icons.Default.Info, contentDescription = null, tint = PrimaryBlue) }
                    )
                }
            }
        }
    }
}

@Composable
private fun WorkOrderSummaryCard(state: LiveWorkOrderUiState) {
    val spacing = MaterialTheme.spacing
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(spacing.radiusLarge),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(spacing.strokeThin, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(Modifier.fillMaxWidth().padding(spacing.medium), verticalArrangement = Arrangement.spacedBy(spacing.small)) {
            Text(state.orderNumber.ifBlank { "Work order" }, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(state.vehicleDescription, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Status: ${state.status.replace('_', ' ').lowercase().replaceFirstChar(Char::uppercase)}", style = MaterialTheme.typography.labelLarge, color = PrimaryBlue)
            state.diagnosticSummary.takeIf(String::isNotBlank)?.let {
                Text(it, style = MaterialTheme.typography.bodyMedium)
            }
            state.mileageIn?.let { Text("Mileage: $it km", style = MaterialTheme.typography.bodySmall, color = Slate700) }
        }
    }
}

@Composable
private fun WorkOrderTasksCard(tasks: List<LiveWorkOrderTaskItem>) {
    val spacing = MaterialTheme.spacing
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(spacing.radiusLarge),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(spacing.strokeThin, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(Modifier.fillMaxWidth().padding(spacing.medium), verticalArrangement = Arrangement.spacedBy(spacing.small)) {
            Text("Tasks and evidence", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            if (tasks.isEmpty()) {
                Text("No tasks have been registered for this work order yet.", style = MaterialTheme.typography.bodySmall, color = Slate500)
            }
            tasks.forEach { task ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(spacing.small), verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = if (task.status == "COMPLETED") Icons.Default.CheckCircle else Icons.Default.Build,
                        contentDescription = null,
                        tint = if (task.status == "COMPLETED") ShiftIQSuccess else PrimaryBlue,
                        modifier = Modifier.size(spacing.iconMedium)
                    )
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(spacing.micro)) {
                        Text(task.title.ifBlank { "Task" }, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        Text("${task.status.replace('_', ' ').lowercase().replaceFirstChar(Char::uppercase)} · ${task.price}", style = MaterialTheme.typography.bodySmall, color = Slate700)
                        task.mechanicName?.takeIf(String::isNotBlank)?.let {
                            Text("Assigned to $it", style = MaterialTheme.typography.labelSmall, color = Slate500)
                        }
                        if (task.evidenceCount > 0) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Image, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(spacing.iconSmall))
                                Spacer(Modifier.width(spacing.extraSmall))
                                Text("${task.evidenceCount} evidence file(s)", style = MaterialTheme.typography.labelSmall, color = Slate500)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoCard(title: String, value: String, icon: @Composable () -> Unit) {
    val spacing = MaterialTheme.spacing
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(spacing.radiusMedium)).background(Slate100).padding(spacing.medium),
        horizontalArrangement = Arrangement.spacedBy(spacing.small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon()
        Column {
            Text(title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Text(value, style = MaterialTheme.typography.bodySmall, color = Slate700)
        }
    }
}

@Composable
private fun EmptyOrErrorState(message: String) {
    val spacing = MaterialTheme.spacing
    Box(Modifier.fillMaxSize().padding(spacing.large), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(spacing.small)) {
            Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(spacing.iconLarge))
            Text(message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)
        }
    }
}

@PreviewLightDark
@PreviewFontScale
@Composable
private fun LiveWorkOrderScreenPreview() {
    ShiftIQTheme {
        LiveWorkOrderScreen(
            uiState = LiveWorkOrderUiState(
                orderNumber = "OT-2026-0418",
                vehicleDescription = "Kia Rio 2019 • ABC-123",
                status = "IN_PROGRESS",
                diagnosticSummary = "Preventive diagnosis requested by the customer.",
                mileageIn = 45102,
                totalAmount = "S/ 212.40",
                tasks = listOf(
                    LiveWorkOrderTaskItem("Inspection and O2 sensor diagnosis", "COMPLETED", "S/ 80.00", "Jorge Ramírez", 2),
                    LiveWorkOrderTaskItem("Sensor replacement", "DOING", "S/ 132.40", "Jorge Ramírez", 0)
                )
            ),
            onNavigateBack = {}
        )
    }
}
