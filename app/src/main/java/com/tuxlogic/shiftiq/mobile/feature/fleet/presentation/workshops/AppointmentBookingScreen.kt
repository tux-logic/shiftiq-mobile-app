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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Navy900
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQSuccess
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQTheme
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQWarning
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.spacing

@Composable
fun AppointmentBookingRoute(
    workshopId: String,
    onNavigateBack: () -> Unit,
    onBookingConfirmed: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    AppointmentBookingScreen(
        workshopId = workshopId,
        onNavigateBack = onNavigateBack,
        onBookingConfirmed = onBookingConfirmed,
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
    var selectedDayIndex by rememberSaveable { mutableIntStateOf(0) }
    var selectedSlotIndex by rememberSaveable { mutableIntStateOf(1) }
    var additionalNotes by rememberSaveable { mutableStateOf("Noté consumo elevado en frío y luz Check Engine.") }
    var showConfirmationDialog by rememberSaveable { mutableStateOf(false) }

    val days = listOf("Hoy 15 Feb", "Mañana 16 Feb", "Jue 17 Feb", "Vie 18 Feb")
    val slots = listOf("08:30 AM", "10:00 AM", "11:30 AM", "02:00 PM", "03:30 PM", "05:00 PM")

    val spacing = MaterialTheme.spacing
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        BookingTopBar(onNavigateBack = onNavigateBack, statusBarPadding = statusBarPadding)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = spacing.medium, vertical = spacing.smallMedium),
            verticalArrangement = Arrangement.spacedBy(spacing.medium)
        ) {
            WorkshopBookingSummaryCard(workshopId = workshopId)
            DaySelectorSection(days = days, selectedIndex = selectedDayIndex, onSelectDay = { selectedDayIndex = it })
            BaySlotGridSection(slots = slots, selectedIndex = selectedSlotIndex, onSelectSlot = { selectedSlotIndex = it })
            BookingNotesField(notes = additionalNotes, onNotesChange = { additionalNotes = it })
            PaymentGuaranteeBadge()
            ConfirmBookingButton(onClick = { showConfirmationDialog = true })
            Spacer(modifier = Modifier.height(spacing.medium))
        }
    }

    if (showConfirmationDialog) {
        BookingConfirmationDialog(
            workshopName = "Taller Automotriz Silva",
            date = days[selectedDayIndex],
            timeSlot = slots[selectedSlotIndex],
            onDismiss = { showConfirmationDialog = false },
            onConfirm = {
                showConfirmationDialog = false
                onBookingConfirmed("OT-2026-0418")
            }
        )
    }
}

@Composable
private fun BookingTopBar(
    onNavigateBack: () -> Unit,
    statusBarPadding: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Navy900)
            .padding(
                top = statusBarPadding,
                start = spacing.small,
                end = spacing.medium,
                bottom = spacing.smallMedium
            ),
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
                text = "Reserva garantizada en taller certificado",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun WorkshopBookingSummaryCard(
    workshopId: String,
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
                .padding(spacing.medium)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Taller Automotriz Silva",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "San Miguel • 1.8 km",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
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
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            Spacer(modifier = Modifier.height(spacing.small))
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(spacing.radiusSmall))
                    .background(ShiftIQWarning.copy(alpha = 0.12f))
                    .padding(horizontal = spacing.small, vertical = spacing.extraSmall),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = ShiftIQWarning,
                    modifier = Modifier.size(spacing.iconSmall)
                )
                Spacer(modifier = Modifier.width(spacing.extraSmall))
                Text(
                    text = "Alineado para resolver DTC P0135 (Sensor O2)",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = ShiftIQWarning
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
            text = "Selecciona el Día",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
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
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(
                        spacing.strokeThin,
                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
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
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
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
            text = "Horario Disponible en Bahía 02",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
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
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            spacing.strokeThin,
            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
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
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
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
            text = "Síntomas o Notas Adicionales",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(spacing.small))
        OutlinedTextField(
            value = notes,
            onValueChange = onNotesChange,
            placeholder = { Text("Describe anomalías observadas...") },
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
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
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
            text = "Garantía ShiftIQ: Sin cobros por adelantado. Pago al retirar el vehículo tras tu aprobación de tareas.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
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
        colors = ButtonDefaults.buttonColors(containerColor = Navy900, contentColor = Color.White)
    ) {
        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(spacing.iconMedium))
        Spacer(modifier = Modifier.width(spacing.small))
        Text("Confirmar Reserva de Bahía", fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun BookingConfirmationDialog(
    workshopName: String,
    date: String,
    timeSlot: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val spacing = MaterialTheme.spacing
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Confirmar Reserva",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = "Se reservará la Bahía 02 en $workshopName para el $date a las $timeSlot.",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancelar")
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
            workshopId = "w1",
            onNavigateBack = {},
            onBookingConfirmed = {}
        )
    }
}
