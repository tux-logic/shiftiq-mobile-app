package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.workshops

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.SeverityCrimson
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQSuccess
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQTheme
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQWarning
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate100
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate200
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate500
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate700
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate900
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.spacing
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.B2CVehicleItem
import com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.workshops.booking.AlternativeBranch
import com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.workshops.booking.AppointmentBookingUiState
import com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.workshops.booking.AppointmentBookingViewModel
import com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.workshops.booking.BayOption
import com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.workshops.booking.ConfirmedAppointmentVoucher

@Composable
fun AppointmentBookingRoute(
    workshopId: String,
    onNavigateBack: () -> Unit,
    onBookingConfirmed: (String) -> Unit,
    onNavigateToMyAppointments: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: AppointmentBookingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AppointmentBookingScreen(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onSelectDay = viewModel::onSelectDay,
        onSelectBay = viewModel::onSelectBay,
        onSelectSlot = viewModel::onSelectSlot,
        onNotesChange = viewModel::onNotesChanged,
        onOpenConfirmation = viewModel::onOpenConfirmationModal,
        onDismissConfirmation = viewModel::onDismissConfirmationModal,
        onConfirmBooking = viewModel::confirmBooking,
        onDismissSuccessVoucher = {
            viewModel.onDismissSuccessVoucher()
            onBookingConfirmed(uiState.successVoucher?.reservationCode ?: "RES-CONFIRMED")
        },
        onNavigateToMyAppointments = {
            viewModel.onDismissSuccessVoucher()
            onNavigateToMyAppointments()
        },
        onDismissConflictDialog = viewModel::onDismissConflictDialog,
        onDismissBranchUnavailableDialog = viewModel::onDismissBranchUnavailableDialog,
        onSelectAlternativeBranch = viewModel::onSelectAlternativeBranch,
        modifier = modifier
    )
}

@Composable
fun AppointmentBookingScreen(
    workshopId: String,
    onNavigateBack: () -> Unit,
    onBookingConfirmed: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    AppointmentBookingRoute(
        workshopId = workshopId,
        onNavigateBack = onNavigateBack,
        onBookingConfirmed = onBookingConfirmed,
        modifier = modifier
    )
}

@Composable
fun AppointmentBookingScreen(
    uiState: AppointmentBookingUiState,
    onNavigateBack: () -> Unit,
    onSelectDay: (Int) -> Unit,
    onSelectBay: (String) -> Unit,
    onSelectSlot: (Int) -> Unit,
    onNotesChange: (String) -> Unit,
    onOpenConfirmation: () -> Unit,
    onDismissConfirmation: () -> Unit,
    onConfirmBooking: () -> Unit,
    onDismissSuccessVoucher: () -> Unit,
    onNavigateToMyAppointments: () -> Unit,
    onDismissConflictDialog: () -> Unit,
    onDismissBranchUnavailableDialog: () -> Unit,
    onSelectAlternativeBranch: (AlternativeBranch) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            BookingTopBar(onNavigateBack = onNavigateBack, statusBarPadding = statusBarPadding)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = spacing.medium, vertical = spacing.smallMedium),
                verticalArrangement = Arrangement.spacedBy(spacing.medium)
            ) {
                WorkshopBookingSummaryCard(
                    workshopName = uiState.workshopName,
                    address = uiState.workshopAddress
                )
                VehicleRegistrationCard(vehicle = uiState.selectedVehicle)
                DaySelectorSection(
                    days = uiState.availableDays,
                    selectedIndex = uiState.selectedDayIndex,
                    onSelectDay = onSelectDay
                )
                BaySelectorSection(
                    bays = uiState.availableBays,
                    selectedBayId = uiState.selectedBayId,
                    onSelectBay = onSelectBay
                )
                BaySlotGridSection(
                    slots = uiState.availableSlots,
                    selectedIndex = uiState.selectedSlotIndex,
                    onSelectSlot = onSelectSlot
                )
                BookingNotesField(
                    notes = uiState.reasonNotes,
                    onNotesChange = onNotesChange
                )
                PaymentGuaranteeBadge()
                ConfirmBookingButton(onClick = onOpenConfirmation)
                Spacer(modifier = Modifier.height(spacing.medium))
            }
        }

        // Modal 1: Confirmación Previa de Reserva (Figma 234:21046)
        if (uiState.showConfirmationModal) {
            BookingConfirmationModal(
                uiState = uiState,
                isLoading = uiState.isBookingLoading,
                onDismiss = onDismissConfirmation,
                onConfirm = onConfirmBooking
            )
        }

        // Modal 2: Cita Confirmada con Éxito (Figma 234:21158)
        uiState.successVoucher?.let { voucher ->
            BookingSuccessModal(
                voucher = voucher,
                onViewMyAppointments = onNavigateToMyAppointments,
                onDismiss = onDismissSuccessVoucher
            )
        }

        // Modal 3: Error de Conflicto de Turno / 409 Conflict (Figma 234:18116)
        if (uiState.showConflictErrorDialog) {
            TurnConflictErrorDialog(
                onSelectSlot = {
                    onSelectSlot(it)
                    onDismissConflictDialog()
                },
                onDismiss = onDismissConflictDialog
            )
        }

        // Modal 4: Sucursal No Disponible (Figma 234:18202)
        if (uiState.showBranchUnavailableDialog) {
            BranchUnavailableDialog(
                branches = uiState.alternativeBranches,
                onSelectBranch = onSelectAlternativeBranch,
                onDismiss = onDismissBranchUnavailableDialog
            )
        }
    }
}

@Composable
private fun BookingTopBar(
    onNavigateBack: () -> Unit,
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Atrás",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(spacing.small))
            Column {
                Text(
                    text = "Agendar Cita y Bahía",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Reserva técnica en taller certificado",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun WorkshopBookingSummaryCard(
    workshopName: String,
    address: String,
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
            verticalArrangement = Arrangement.spacedBy(spacing.extraSmall)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = workshopName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Slate900
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = ShiftIQWarning,
                        modifier = Modifier.size(spacing.iconMedium)
                    )
                    Spacer(modifier = Modifier.width(spacing.micro))
                    Text(
                        text = "4.8",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                }
            }

            Text(
                text = address,
                style = MaterialTheme.typography.bodySmall,
                color = Slate500
            )

            Spacer(modifier = Modifier.height(spacing.micro))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(spacing.radiusSmall))
                    .background(PrimaryBlue.copy(alpha = 0.1f))
                    .padding(horizontal = spacing.small, vertical = spacing.micro)
            ) {
                Text(
                    text = "Alineado para resolver falla DTC P0135 (Sensor O2)",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )
            }
        }
    }
}

@Composable
private fun VehicleRegistrationCard(
    vehicle: B2CVehicleItem?,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(spacing.radiusMedium),
        colors = CardDefaults.cardColors(containerColor = Slate100),
        border = BorderStroke(spacing.strokeThin, Slate200)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.DirectionsCar,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier.size(spacing.iconLarge)
            )
            Spacer(modifier = Modifier.width(spacing.smallMedium))
            Column {
                Text(
                    text = "VEHÍCULO ASIGNADO",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Slate500
                )
                Text(
                    text = vehicle?.let { "${it.brand} ${it.model} ${it.year} • Placa ${it.plateNumber}" } ?: "Kia Rio 2019 • ABC-123",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Slate900
                )
            }
        }
    }
}

@Composable
private fun DaySelectorSection(
    days: List<String>,
    selectedIndex: Int,
    onSelectDay: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Selecciona la Fecha",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = Slate900
        )
        Spacer(modifier = Modifier.height(spacing.small))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.small)
        ) {
            days.forEachIndexed { index, day ->
                val isSelected = index == selectedIndex
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onSelectDay(index) },
                    shape = RoundedCornerShape(spacing.radiusMedium),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) PrimaryBlue else MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(
                        spacing.strokeThin,
                        if (isSelected) PrimaryBlue else Slate200
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = spacing.smallMedium),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = day,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else Slate900
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BaySelectorSection(
    bays: List<BayOption>,
    selectedBayId: String,
    onSelectBay: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        Text(
            text = "Selecciona la Bahía Técnica",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = Slate900
        )

        bays.forEach { bay ->
            val isSelected = bay.id == selectedBayId
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectBay(bay.id) },
                shape = RoundedCornerShape(spacing.radiusMedium),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) PrimaryBlue.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(
                    spacing.strokeThin,
                    if (isSelected) PrimaryBlue else Slate200
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(spacing.medium),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = bay.name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                        Text(
                            text = bay.type,
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate500
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(spacing.radiusSmall))
                            .background(ShiftIQSuccess.copy(alpha = 0.15f))
                            .padding(horizontal = spacing.small, vertical = spacing.micro)
                    ) {
                        Text(
                            text = "Disponible",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = ShiftIQSuccess
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BaySlotGridSection(
    slots: List<String>,
    selectedIndex: Int,
    onSelectSlot: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Horarios Libres de Turno",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = Slate900
        )
        Spacer(modifier = Modifier.height(spacing.small))
        Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.small)
            ) {
                for (i in 0..2) {
                    BaySlotCard(
                        slot = slots[i],
                        isSelected = i == selectedIndex,
                        onClick = { onSelectSlot(i) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.small)
            ) {
                for (i in 3..5) {
                    BaySlotCard(
                        slot = slots[i],
                        isSelected = i == selectedIndex,
                        onClick = { onSelectSlot(i) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun BaySlotCard(
    slot: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(spacing.radiusMedium),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) PrimaryBlue else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            spacing.strokeThin,
            if (isSelected) PrimaryBlue else Slate200
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = spacing.smallMedium),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = slot,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else Slate900
            )
        }
    }
}

@Composable
private fun BookingNotesField(
    notes: String,
    onNotesChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Comentarios o Síntomas para el Mecánico",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = Slate900
        )
        Spacer(modifier = Modifier.height(spacing.small))
        OutlinedTextField(
            value = notes,
            onValueChange = onNotesChange,
            placeholder = { Text("Ej. El testigo Check Engine parpadea al superar 60 km/h o en ralentí prolongado...", style = MaterialTheme.typography.bodySmall) },
            shape = RoundedCornerShape(spacing.radiusMedium),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun PaymentGuaranteeBadge(modifier: Modifier = Modifier) {
    val spacing = MaterialTheme.spacing
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(spacing.radiusMedium))
            .background(Slate100)
            .padding(spacing.smallMedium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Shield,
            contentDescription = null,
            tint = ShiftIQSuccess,
            modifier = Modifier.size(spacing.iconLarge)
        )
        Spacer(modifier = Modifier.width(spacing.small))
        Text(
            text = "Garantía ShiftIQ: Sin cobros por adelantado. Pago presencial en caja del taller al retirar el vehículo.",
            style = MaterialTheme.typography.bodySmall,
            color = Slate700
        )
    }
}

@Composable
private fun ConfirmBookingButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(spacing.buttonHeight),
        shape = RoundedCornerShape(spacing.radiusMedium),
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue, contentColor = Color.White)
    ) {
        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(spacing.iconMedium))
        Spacer(modifier = Modifier.width(spacing.small))
        Text("Continuar a Confirmación", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun BookingConfirmationModal(
    uiState: AppointmentBookingUiState,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val spacing = MaterialTheme.spacing
    val selectedDay = uiState.availableDays[uiState.selectedDayIndex]
    val selectedSlot = uiState.availableSlots[uiState.selectedSlotIndex]
    val selectedBay = uiState.availableBays.firstOrNull { it.id == uiState.selectedBayId }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Resumen de Cita Técnica",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Slate900
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(spacing.small)
            ) {
                ConfirmationDetailRow(label = "TALLER TÉCNICO", value = uiState.workshopName)
                ConfirmationDetailRow(label = "FECHA Y TURNO", value = "$selectedDay • $selectedSlot")
                ConfirmationDetailRow(label = "BAHÍA ASIGNADA", value = selectedBay?.name ?: "Bahía 2")
                ConfirmationDetailRow(label = "VEHÍCULO", value = uiState.selectedVehicle?.let { "${it.brand} ${it.model} • ${it.plateNumber}" } ?: "ABC-123")
                ConfirmationDetailRow(label = "PRESUPUESTO ESTIMADO", value = uiState.estimatedPricePen)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(spacing.radiusSmall))
                        .background(Slate100)
                        .padding(spacing.small)
                ) {
                    Text(
                        text = "Política de agenda: Tolerancia máxima de 10 minutos para recepción en bahía.",
                        style = MaterialTheme.typography.labelSmall,
                        color = Slate500
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(spacing.radiusMedium)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(spacing.iconMedium))
                } else {
                    Text("Confirmar Reserva de Cita", fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(spacing.radiusMedium)
            ) {
                Text("Modificar", color = Slate700)
            }
        },
        shape = RoundedCornerShape(spacing.radiusLarge)
    )
}

@Composable
private fun ConfirmationDetailRow(label: String, value: String) {
    val spacing = MaterialTheme.spacing
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Slate500)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = Slate900)
    }
}

@Composable
private fun BookingSuccessModal(
    voucher: ConfirmedAppointmentVoucher,
    onViewMyAppointments: () -> Unit,
    onDismiss: () -> Unit
) {
    val spacing = MaterialTheme.spacing
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(spacing.avatarLarge)
                        .clip(CircleShape)
                        .background(ShiftIQSuccess.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = ShiftIQSuccess,
                        modifier = Modifier.size(spacing.iconExtraLarge)
                    )
                }
                Spacer(modifier = Modifier.height(spacing.small))
                Text(
                    text = "¡Cita Confirmada con Éxito!",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Slate900
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(spacing.small)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(spacing.radiusMedium))
                        .background(Navy900)
                        .padding(spacing.smallMedium),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = voucher.reservationCode,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                ConfirmationDetailRow(label = "Taller", value = voucher.workshopName)
                ConfirmationDetailRow(label = "Fecha y Turno", value = "${voucher.scheduledDate} • ${voucher.scheduledTime}")
                ConfirmationDetailRow(label = "Bahía", value = voucher.bayName)
                ConfirmationDetailRow(label = "Estimado a Pagar", value = "${voucher.estimatedPen} (Pago presencial)")
            }
        },
        confirmButton = {
            Button(
                onClick = onViewMyAppointments,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(spacing.radiusMedium)
            ) {
                Text("Ver en Mis Citas", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(spacing.radiusMedium)
            ) {
                Text("Aceptar y Salir", color = Slate700)
            }
        },
        shape = RoundedCornerShape(spacing.radiusLarge)
    )
}

@Composable
private fun TurnConflictErrorDialog(
    onSelectSlot: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val spacing = MaterialTheme.spacing
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = SeverityCrimson, modifier = Modifier.size(spacing.iconLarge))
                Spacer(modifier = Modifier.width(spacing.small))
                Text(text = "Turno No Disponible", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Slate900)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
                Text(
                    text = "El horario y bahía seleccionados acaban de ser reservados por otro conductor (409 Conflict).",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate700
                )
                Text(
                    text = "Horarios alternativos inmediatos disponibles hoy:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Slate900
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.extraSmall)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(spacing.radiusSmall))
                            .background(PrimaryBlue.copy(alpha = 0.15f))
                            .clickable { onSelectSlot(2) }
                            .padding(vertical = spacing.small),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("11:30 AM", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(spacing.radiusSmall))
                            .background(PrimaryBlue.copy(alpha = 0.15f))
                            .clickable { onSelectSlot(3) }
                            .padding(vertical = spacing.small),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("02:00 PM", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(spacing.radiusMedium)
            ) {
                Text("Elegir Horario")
            }
        },
        shape = RoundedCornerShape(spacing.radiusLarge)
    )
}

@Composable
private fun BranchUnavailableDialog(
    branches: List<AlternativeBranch>,
    onSelectBranch: (AlternativeBranch) -> Unit,
    onDismiss: () -> Unit
) {
    val spacing = MaterialTheme.spacing
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Warning, contentDescription = null, tint = ShiftIQWarning, modifier = Modifier.size(spacing.iconLarge))
                Spacer(modifier = Modifier.width(spacing.small))
                Text(text = "Sede No Disponible", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Slate900)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
                Text(
                    text = "La sede seleccionada se encuentra en mantenimiento. Puedes agendar en estas sedes alternativas:",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate700
                )
                branches.forEach { branch ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectBranch(branch) },
                        shape = RoundedCornerShape(spacing.radiusSmall),
                        colors = CardDefaults.cardColors(containerColor = Slate100),
                        border = BorderStroke(spacing.strokeThin, Slate200)
                    ) {
                        Column(modifier = Modifier.padding(spacing.small)) {
                            Text(text = branch.name, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Slate900)
                            Text(text = "${branch.address} • ${branch.activeBaysCount} Bahías activas", style = MaterialTheme.typography.labelSmall, color = Slate500)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, shape = RoundedCornerShape(spacing.radiusMedium)) {
                Text("Cerrar")
            }
        },
        shape = RoundedCornerShape(spacing.radiusLarge)
    )
}

@PreviewLightDark
@PreviewFontScale
@Composable
private fun AppointmentBookingScreenPreview() {
    ShiftIQTheme {
        AppointmentBookingScreen(
            uiState = AppointmentBookingUiState(),
            onNavigateBack = {},
            onSelectDay = {},
            onSelectBay = {},
            onSelectSlot = {},
            onNotesChange = {},
            onOpenConfirmation = {},
            onDismissConfirmation = {},
            onConfirmBooking = {},
            onDismissSuccessVoucher = {},
            onNavigateToMyAppointments = {},
            onDismissConflictDialog = {},
            onDismissBranchUnavailableDialog = {},
            onSelectAlternativeBranch = {}
        )
    }
}
