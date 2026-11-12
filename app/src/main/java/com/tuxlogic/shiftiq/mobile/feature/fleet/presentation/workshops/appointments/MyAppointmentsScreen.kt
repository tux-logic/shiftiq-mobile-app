package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.workshops.appointments

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.SeverityCrimson
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQSuccess
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQTheme
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQWarning
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate200
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate500
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate700
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate900
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.spacing
import java.util.UUID

@Composable
fun MyAppointmentsRoute(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MyAppointmentsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MyAppointmentsScreen(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onRefresh = viewModel::loadAppointments,
        onTabSelected = viewModel::onTabSelected,
        onRequestCancel = viewModel::onRequestCancelAppointment,
        onDismissCancelDialog = viewModel::onDismissCancelDialog,
        onConfirmCancel = viewModel::confirmCancellation,
        onClearMessage = viewModel::clearUserMessage,
        modifier = modifier
    )
}

@Composable
fun MyAppointmentsScreen(
    uiState: MyAppointmentsUiState,
    onNavigateBack: () -> Unit,
    onRefresh: () -> Unit,
    onTabSelected: (AppointmentTabFilter) -> Unit,
    onRequestCancel: (AppointmentDisplayItem) -> Unit,
    onDismissCancelDialog: () -> Unit,
    onConfirmCancel: () -> Unit,
    onClearMessage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let {
            snackbarHostState.showSnackbar(it)
            onClearMessage()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            MyAppointmentsTopBar(
                onNavigateBack = onNavigateBack,
                onRefresh = onRefresh,
                statusBarPadding = statusBarPadding
            )

            AppointmentTabsHeader(
                selectedTab = uiState.selectedTab,
                upcomingCount = uiState.upcomingAppointments.size,
                historyCount = uiState.historyAppointments.size,
                onTabSelected = onTabSelected
            )

            val currentList = if (uiState.selectedTab == AppointmentTabFilter.UPCOMING) {
                uiState.upcomingAppointments
            } else {
                uiState.historyAppointments
            }

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else if (currentList.isEmpty()) {
                EmptyAppointmentsView(isUpcoming = uiState.selectedTab == AppointmentTabFilter.UPCOMING)
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = spacing.medium, vertical = spacing.smallMedium),
                    verticalArrangement = Arrangement.spacedBy(spacing.medium)
                ) {
                    items(items = currentList, key = { it.id.toString() }) { appointment ->
                        AppointmentItemCard(
                            appointment = appointment,
                            isUpcoming = uiState.selectedTab == AppointmentTabFilter.UPCOMING,
                            onRequestCancel = { onRequestCancel(appointment) }
                        )
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(spacing.medium)
        )

        uiState.appointmentToCancel?.let { item ->
            CancelAppointmentDialog(
                appointment = item,
                isCanceling = uiState.isCanceling,
                onConfirm = onConfirmCancel,
                onDismiss = onDismissCancelDialog
            )
        }
    }
}

@Composable
private fun MyAppointmentsTopBar(
    onNavigateBack: () -> Unit,
    onRefresh: () -> Unit,
    statusBarPadding: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Navy900,
        shadowElevation = spacing.elevationSmall
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = statusBarPadding)
                .height(spacing.topBarHeight)
                .padding(horizontal = spacing.small),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.White
                )
            }
            Text(
                text = "Mis Citas de Taller",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            IconButton(onClick = onRefresh) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refrescar citas",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
private fun AppointmentTabsHeader(
    selectedTab: AppointmentTabFilter,
    upcomingCount: Int,
    historyCount: Int,
    onTabSelected: (AppointmentTabFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    TabRow(
        selectedTabIndex = selectedTab.ordinal,
        modifier = modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.surface,
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab.ordinal]),
                height = spacing.indicatorHeight,
                color = PrimaryBlue
            )
        }
    ) {
        Tab(
            selected = selectedTab == AppointmentTabFilter.UPCOMING,
            onClick = { onTabSelected(AppointmentTabFilter.UPCOMING) },
            text = {
                Text(
                    text = "Próximas ($upcomingCount)",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (selectedTab == AppointmentTabFilter.UPCOMING) FontWeight.Bold else FontWeight.Medium
                )
            }
        )
        Tab(
            selected = selectedTab == AppointmentTabFilter.HISTORY,
            onClick = { onTabSelected(AppointmentTabFilter.HISTORY) },
            text = {
                Text(
                    text = "Historial ($historyCount)",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (selectedTab == AppointmentTabFilter.HISTORY) FontWeight.Bold else FontWeight.Medium
                )
            }
        )
    }
}

@Composable
private fun AppointmentItemCard(
    appointment: AppointmentDisplayItem,
    isUpcoming: Boolean,
    onRequestCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(spacing.radiusLarge),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(spacing.strokeThin, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.medium),
            verticalArrangement = Arrangement.spacedBy(spacing.small)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppointmentStatusBadge(status = appointment.status)
                Text(
                    text = appointment.appointmentCode,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Slate500
                )
            }

            Text(
                text = appointment.workshopName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Slate900
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = Slate500, modifier = Modifier.size(spacing.iconSmall))
                Spacer(modifier = Modifier.width(spacing.extraSmall))
                Text(text = appointment.address, style = MaterialTheme.typography.bodySmall, color = Slate700)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(spacing.iconSmall))
                Spacer(modifier = Modifier.width(spacing.extraSmall))
                Text(text = "${appointment.scheduledDate} • ${appointment.scheduledTime}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = Slate900)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.DirectionsCar, contentDescription = null, tint = Slate500, modifier = Modifier.size(spacing.iconSmall))
                Spacer(modifier = Modifier.width(spacing.extraSmall))
                Text(text = "${appointment.vehicleDescription} • Placa ${appointment.vehiclePlate}", style = MaterialTheme.typography.bodySmall, color = Slate700)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Schedule, contentDescription = null, tint = ShiftIQWarning, modifier = Modifier.size(spacing.iconSmall))
                Spacer(modifier = Modifier.width(spacing.extraSmall))
                Text(text = appointment.bayNumber, style = MaterialTheme.typography.bodySmall, color = Slate700)
            }

            Text(
                text = "Motivo: ${appointment.reasonOrService}",
                style = MaterialTheme.typography.bodySmall,
                color = Slate500
            )

            if (isUpcoming && appointment.status == "PENDING") {
                Spacer(modifier = Modifier.height(spacing.extraSmall))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.small)
                ) {
                    Button(
                        onClick = { /* Navigation intent */ },
                        modifier = Modifier.weight(1f).height(spacing.buttonHeight),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        shape = RoundedCornerShape(spacing.radiusMedium)
                    ) {
                        Icon(imageVector = Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(spacing.iconMedium))
                        Spacer(modifier = Modifier.width(spacing.extraSmall))
                        Text(text = "Cómo llegar", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onRequestCancel,
                        modifier = Modifier.weight(1f).height(spacing.buttonHeight),
                        border = BorderStroke(spacing.strokeThin, SeverityCrimson),
                        shape = RoundedCornerShape(spacing.radiusMedium)
                    ) {
                        Icon(imageVector = Icons.Default.Cancel, contentDescription = null, tint = SeverityCrimson, modifier = Modifier.size(spacing.iconMedium))
                        Spacer(modifier = Modifier.width(spacing.extraSmall))
                        Text(text = "Cancelar", color = SeverityCrimson, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun AppointmentStatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    val (label, bg, fg) = when (status) {
        "COMPLETED" -> Triple("Atendida con Éxito", ShiftIQSuccess.copy(alpha = 0.15f), ShiftIQSuccess)
        "CANCELED" -> Triple("Cancelada", SeverityCrimson.copy(alpha = 0.15f), SeverityCrimson)
        else -> Triple("Cita Confirmada", PrimaryBlue.copy(alpha = 0.15f), PrimaryBlue)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(spacing.radiusSmall))
            .background(bg)
            .padding(horizontal = spacing.small, vertical = spacing.micro)
    ) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = fg)
    }
}

@Composable
private fun EmptyAppointmentsView(
    isUpcoming: Boolean,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Box(
        modifier = modifier.fillMaxSize().padding(spacing.large),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing.small)
        ) {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = null,
                tint = Slate500,
                modifier = Modifier.size(spacing.gaugeSmall)
            )
            Text(
                text = if (isUpcoming) "No tienes citas activas próximas" else "No hay historial de citas anteriores",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Slate700
            )
            Text(
                text = "Reserva una bahía técnica en cualquier taller certificado ShiftIQ desde el buscador.",
                style = MaterialTheme.typography.bodySmall,
                color = Slate500
            )
        }
    }
}

@Composable
private fun CancelAppointmentDialog(
    appointment: AppointmentDisplayItem,
    isCanceling: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val spacing = MaterialTheme.spacing
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "¿Cancelar esta Cita?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Slate900
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
                Text(
                    text = "Estás a punto de cancelar la cita ${appointment.appointmentCode} en ${appointment.workshopName} programada para el ${appointment.scheduledDate} a las ${appointment.scheduledTime}.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate700
                )
                Text(
                    text = "La bahía técnica asignada quedará liberada para otros conductores.",
                    style = MaterialTheme.typography.labelSmall,
                    color = Slate500
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !isCanceling,
                colors = ButtonDefaults.buttonColors(containerColor = SeverityCrimson),
                shape = RoundedCornerShape(spacing.radiusMedium)
            ) {
                if (isCanceling) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(spacing.iconMedium))
                } else {
                    Text(text = "Sí, Cancelar Cita", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                }
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(spacing.radiusMedium)
            ) {
                Text(text = "Mantener Cita", color = Slate700, style = MaterialTheme.typography.bodyMedium)
            }
        },
        shape = RoundedCornerShape(spacing.radiusLarge)
    )
}

@PreviewLightDark
@PreviewFontScale
@Composable
private fun MyAppointmentsScreenPreview() {
    ShiftIQTheme {
        MyAppointmentsScreen(
            uiState = MyAppointmentsUiState(
                upcomingAppointments = listOf(
                    AppointmentDisplayItem(
                        id = UUID.randomUUID(),
                        appointmentCode = "SQ-APPT-8429",
                        workshopName = "Taller Automotriz Silva - San Miguel",
                        address = "Av. La Marina 2450 • San Miguel",
                        scheduledDate = "Mar 23 Mayo",
                        scheduledTime = "10:00 AM",
                        bayNumber = "Bahía 2 (Elevador)",
                        vehiclePlate = "ABC-123",
                        vehicleDescription = "Kia Rio 2019",
                        reasonOrService = "Diagnóstico preventivo DTC P0135",
                        estimatedCostPen = "S/ 140 - S/ 180 PEN",
                        status = "PENDING"
                    )
                )
            ),
            onNavigateBack = {},
            onRefresh = {},
            onTabSelected = {},
            onRequestCancel = {},
            onDismissCancelDialog = {},
            onConfirmCancel = {},
            onClearMessage = {}
        )
    }
}
