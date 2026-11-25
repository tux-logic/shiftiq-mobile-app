package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.workshops.appointments

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tuxlogic.shiftiq.mobile.core.common.geo.openWorkshopDirections
import com.tuxlogic.shiftiq.mobile.core.designsystem.components.ShiftIQEmptyState
import com.tuxlogic.shiftiq.mobile.core.designsystem.components.ShiftIQPageHeader
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Navy900
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.PrimaryBlue
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.SeverityCrimson
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQBorderSlate
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQSeverityOrange
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQSuccess
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQTheme
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate100
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
                onRefresh = onRefresh
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
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            } else if (currentList.isEmpty()) {
                EmptyAppointmentsView(isUpcoming = uiState.selectedTab == AppointmentTabFilter.UPCOMING)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
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
    modifier: Modifier = Modifier
) {
    ShiftIQPageHeader(
        title = "Mis Citas",
        subtitle = "Agenda y seguimiento de atención en sede",
        showBackButton = true,
        onBackPressed = onNavigateBack,
        modifier = modifier,
        actions = {
            IconButton(onClick = onRefresh) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refrescar citas",
                    tint = Color.White
                )
            }
        }
    )
}

@Composable
private fun AppointmentTabsHeader(
    selectedTab: AppointmentTabFilter,
    upcomingCount: Int,
    historyCount: Int,
    onTabSelected: (AppointmentTabFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth().background(Color.White)) {
        TabRow(
            selectedTabIndex = selectedTab.ordinal,
            modifier = Modifier.fillMaxWidth(),
            containerColor = Color.White,
            contentColor = PrimaryBlue,
            divider = {},
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab.ordinal]),
                    height = 2.5.dp,
                    color = PrimaryBlue
                )
            }
        ) {
            Tab(
                selected = selectedTab == AppointmentTabFilter.UPCOMING,
                onClick = { onTabSelected(AppointmentTabFilter.UPCOMING) },
                selectedContentColor = PrimaryBlue,
                unselectedContentColor = Slate500,
                text = {
                    Text(
                        text = "Próximas  $upcomingCount",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (selectedTab == AppointmentTabFilter.UPCOMING) {
                            FontWeight.Bold
                        } else {
                            FontWeight.SemiBold
                        }
                    )
                }
            )
            Tab(
                selected = selectedTab == AppointmentTabFilter.HISTORY,
                onClick = { onTabSelected(AppointmentTabFilter.HISTORY) },
                selectedContentColor = PrimaryBlue,
                unselectedContentColor = Slate500,
                text = {
                    Text(
                        text = "Historial  $historyCount",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (selectedTab == AppointmentTabFilter.HISTORY) {
                            FontWeight.Bold
                        } else {
                            FontWeight.SemiBold
                        }
                    )
                }
            )
        }
        HorizontalDivider(thickness = 1.dp, color = ShiftIQBorderSlate.copy(alpha = 0.6f))
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
    val context = LocalContext.current
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(spacing.strokeThin, ShiftIQBorderSlate),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(spacing.small)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (appointment.bayNumber.isNotBlank()) {
                    AppointmentBayBadge(bayNumber = appointment.bayNumber)
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = Slate500,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = appointment.scheduledTime,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Slate900
                )
                Spacer(modifier = Modifier.weight(1f))
                AppointmentStatusBadge(status = appointment.status)
            }

            HorizontalDivider(thickness = 1.dp, color = ShiftIQBorderSlate)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = appointment.workshopName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Slate500,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = appointment.address,
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate500
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Navy900)
                        .padding(horizontal = 9.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = appointment.vehiclePlate,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Slate100)
                    .border(BorderStroke(spacing.strokeThin, ShiftIQBorderSlate.copy(alpha = 0.55f)), RoundedCornerShape(14.dp))
                    .padding(12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = null,
                            tint = Slate500,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = appointment.vehicleDescription,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Slate900
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = appointment.reasonOrService,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryBlue
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = Slate500,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${appointment.scheduledDate} · ${appointment.appointmentCode}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Slate500
                        )
                    }
                }
            }

            if (isUpcoming && appointment.status == "PENDING") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.small)
                ) {
                    Button(
                        onClick = {
                            openWorkshopDirections(
                                context = context,
                                address = appointment.address,
                                label = appointment.workshopName
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(spacing.buttonHeight),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Navigation,
                            contentDescription = null,
                            modifier = Modifier.size(spacing.iconMedium)
                        )
                        Spacer(modifier = Modifier.width(spacing.extraSmall))
                        Text(
                            text = "Cómo llegar",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = onRequestCancel,
                        modifier = Modifier
                            .weight(1f)
                            .height(spacing.buttonHeight),
                        border = BorderStroke(spacing.strokeThin, SeverityCrimson),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cancel,
                            contentDescription = null,
                            tint = SeverityCrimson,
                            modifier = Modifier.size(spacing.iconMedium)
                        )
                        Spacer(modifier = Modifier.width(spacing.extraSmall))
                        Text(
                            text = "Cancelar",
                            color = SeverityCrimson,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AppointmentBayBadge(
    bayNumber: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(PrimaryBlue.copy(alpha = 0.07f))
            .border(BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.22f)), RoundedCornerShape(10.dp))
            .padding(horizontal = 9.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Build,
            contentDescription = null,
            tint = PrimaryBlue,
            modifier = Modifier.size(13.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = bayNumber,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = PrimaryBlue
        )
    }
}

@Composable
private fun AppointmentStatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (label, bg, fg) = when (status) {
        "COMPLETED" -> Triple("Atendida", ShiftIQSuccess.copy(alpha = 0.10f), ShiftIQSuccess)
        "CANCELED" -> Triple("Cancelada", SeverityCrimson.copy(alpha = 0.10f), SeverityCrimson)
        "CONFIRMED" -> Triple("Confirmada", ShiftIQSuccess.copy(alpha = 0.10f), ShiftIQSuccess)
        else -> Triple("Pendiente", ShiftIQSeverityOrange.copy(alpha = 0.10f), ShiftIQSeverityOrange)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = fg
        )
    }
}

@Composable
private fun EmptyAppointmentsView(
    isUpcoming: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        ShiftIQEmptyState(
            title = if (isUpcoming) {
                "No tienes citas activas próximas"
            } else {
                "No hay historial de citas anteriores"
            },
            message = "Reserva una bahía técnica en cualquier taller certificado ShiftIQ desde el buscador.",
            icon = Icons.Default.CalendarMonth
        )
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
        shape = RoundedCornerShape(18.dp),
        containerColor = Color.White
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
