package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.appointments

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import com.tuxlogic.shiftiq.mobile.core.localization.LocalizedText as Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tuxlogic.shiftiq.mobile.core.designsystem.components.ShiftIQPageHeader
import com.tuxlogic.shiftiq.mobile.core.designsystem.components.ShiftIQEmptyStateIcon
import com.tuxlogic.shiftiq.mobile.core.designsystem.components.ShiftIQSkeletonBox
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.PrimaryBlue
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQBorderSlate
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQTheme
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.Appointment
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.AppointmentStatus
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import java.util.UUID

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AppointmentListScreen(
    uiState: AppointmentListUiState,
    onRefresh: () -> Unit,
    onFilterSelected: (AppointmentStatus?) -> Unit,
    onMarkStatus: (Appointment, AppointmentStatus) -> Unit,
    onNavigateToCreate: () -> Unit,
    onOpenWorkOrder: (Appointment) -> Unit,
    onNavigateToCustomerDirectory: () -> Unit = {},
    onSelectDate: (LocalDate) -> Unit = {},
    onClearMessages: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            onClearMessages()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            ShiftIQPageHeader(
                title = "Agenda de Citas",
                subtitle = "Gestión de Bahías · ${uiState.activeBranchName}",
                actions = {
                    IconButton(onClick = onNavigateToCustomerDirectory) {
                        Icon(
                            imageVector = Icons.Default.Groups,
                            contentDescription = "Directorio de clientes",
                            tint = Color.White
                        )
                    }
                }
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 32.dp)
            ) {
            AppointmentDateStrip(uiState.selectedDate, onSelectDate)
            val statusTabs = listOf(null, AppointmentStatus.PENDING, AppointmentStatus.CONFIRMED, AppointmentStatus.COMPLETED)
            val selectedTab = statusTabs.indexOf(uiState.selectedStatusFilter).coerceAtLeast(0)
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 8.dp,
                containerColor = Color.Transparent,
                contentColor = PrimaryBlue
            ) {
                statusTabs.forEachIndexed { index, status ->
                    val count = uiState.appointments.count { appointment ->
                        appointment.scheduledStart.take(10) == uiState.selectedDate.toString() &&
                            (status == null || appointment.status == status)
                    }
                    Tab(
                        selected = index == selectedTab,
                        onClick = { onFilterSelected(status) },
                        text = {
                            val label = when (status) {
                                null -> "Todas"
                                AppointmentStatus.PENDING -> "Pendientes"
                                AppointmentStatus.CONFIRMED -> "Confirmadas"
                                AppointmentStatus.COMPLETED -> "Completadas"
                                AppointmentStatus.CANCELED -> "Canceladas"
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(label)
                                if (count > 0) Text(count.toString(), style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    )
                }
            }

            uiState.errorMessage?.let { message ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(14.dp)).background(Color(0xFFFFE4E6)).padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(message, modifier = Modifier.weight(1f), color = Color(0xFFB91C1C), style = MaterialTheme.typography.bodySmall)
                    OutlinedButton(onClick = onRefresh, contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)) {
                        Text("Reintentar", color = Color(0xFFB91C1C))
                    }
                }
            }

            PullToRefreshBox(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                isRefreshing = uiState.isLoading,
                onRefresh = onRefresh
            ) {
                when {
                    uiState.isLoading && uiState.appointments.isEmpty() -> AppointmentSkeletonList()

                    uiState.filteredAppointments.isEmpty() -> LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 32.dp, top = 64.dp, end = 32.dp, bottom = 120.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        item {
                            ShiftIQEmptyStateIcon()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No hay citas agendadas para esta fecha",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Selecciona otro día o agenda una nueva cita para comenzar",
                                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(onClick = onNavigateToCreate) {
                                Icon(Icons.Default.Event, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Agendar en bahía")
                            }
                        }
                    }

                    else -> LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 120.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.filteredAppointments, key = { it.id }) { appointment ->
                            AppointmentCard(
                                appointment = appointment,
                                onConfirm = { onMarkStatus(appointment, AppointmentStatus.CONFIRMED) },
                                onCancel = { onMarkStatus(appointment, AppointmentStatus.CANCELED) },
                                onComplete = { onMarkStatus(appointment, AppointmentStatus.COMPLETED) },
                                onOpenWorkOrder = { onOpenWorkOrder(appointment) }
                            )
                        }
                    }
                }
            }
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun AppointmentSkeletonList() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(3) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, ShiftIQBorderSlate)
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    ShiftIQSkeletonBox(16.dp, widthFraction = 0.46f)
                    ShiftIQSkeletonBox(14.dp, widthFraction = 0.76f)
                    ShiftIQSkeletonBox(14.dp, widthFraction = 0.62f)
                }
            }
        }
    }
}

@Composable
private fun AppointmentDateStrip(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val dates = (-2L..4L).map(selectedDate::plusDays)
    LazyRow(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(dates) { date ->
            val selected = date == selectedDate
            Surface(
                modifier = Modifier.clickable { onDateSelected(date) },
                shape = RoundedCornerShape(14.dp),
                color = if (selected) PrimaryBlue else Color.White,
                border = if (selected) null else BorderStroke(1.dp, ShiftIQBorderSlate)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()).uppercase(),
                        color = if (selected) Color.White.copy(alpha = 0.8f) else Color(0xFF64748B),
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        date.dayOfMonth.toString(),
                        color = if (selected) Color.White else Color(0xFF1E293B),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun AppointmentCard(
    appointment: Appointment,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    onComplete: () -> Unit,
    onOpenWorkOrder: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, ShiftIQBorderSlate),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = PrimaryBlue.copy(alpha = 0.08f),
                    border = BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.24f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Event, contentDescription = null, modifier = Modifier.size(14.dp), tint = PrimaryBlue)
                        Spacer(Modifier.width(4.dp))
                        Text("Bahía ${appointment.bayNumber}", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = PrimaryBlue))
                    }
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = formatAppointmentTime(appointment.scheduledStart),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = Color(0xFF475569)
                )
                Spacer(Modifier.weight(1f))
                StatusPill(status = appointment.status)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val initials = appointment.customerName.split(" ").filter(String::isNotBlank)
                    .take(2).mapNotNull { it.firstOrNull()?.uppercaseChar() }.joinToString("")
                Surface(shape = CircleShape, color = PrimaryBlue.copy(alpha = 0.1f), modifier = Modifier.size(40.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(initials.ifBlank { "C" }, color = PrimaryBlue, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(appointment.customerName, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                    if (appointment.customerPhone.isNotBlank()) {
                        Text(appointment.customerPhone, style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
                    }
                }
                Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFF0F172A)) {
                    Text(
                        appointment.vehiclePlate,
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFF8FAFC),
                border = BorderStroke(1.dp, ShiftIQBorderSlate.copy(alpha = 0.8f))
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(appointment.vehicleModel, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                    Text(appointment.serviceType, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = PrimaryBlue))
                    appointment.notes?.takeIf { it.isNotBlank() }?.let { notes ->
                        Text(notes, style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B), fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Botones de acción rápida
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (appointment.status == AppointmentStatus.PENDING) {
                    OutlinedButton(
                        onClick = onCancel,
                        shape = RoundedCornerShape(18.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                        colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Cancelar", style = MaterialTheme.typography.labelMedium)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = onConfirm,
                        shape = RoundedCornerShape(18.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF15803D))
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Confirmar", style = MaterialTheme.typography.labelMedium)
                    }

                    Spacer(modifier = Modifier.width(8.dp))
                }

                if (appointment.status == AppointmentStatus.CONFIRMED) {
                    OutlinedButton(
                        onClick = onComplete,
                        shape = RoundedCornerShape(18.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Completar", style = MaterialTheme.typography.labelMedium)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Button(
                    onClick = onOpenWorkOrder,
                    shape = RoundedCornerShape(18.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 7.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A))
                ) {
                    Icon(Icons.Default.Event, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(5.dp))
                    Text("Abrir OT", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

private fun formatAppointmentTime(raw: String): String = runCatching {
    val dateTime = java.time.OffsetDateTime.parse(raw).atZoneSameInstant(java.time.ZoneId.systemDefault())
    dateTime.format(java.time.format.DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault()))
}.getOrElse {
    runCatching {
        java.time.LocalDateTime.parse(raw).format(
            java.time.format.DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault())
        )
    }.getOrDefault(raw.substringAfter('T').take(5))
}

@Composable
fun StatusPill(
    status: AppointmentStatus,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (status) {
        AppointmentStatus.PENDING -> Pair(Color(0xFFFFF3E0), Color(0xFFE65100))
        AppointmentStatus.CONFIRMED -> Pair(Color(0xFFEFF6FF), PrimaryBlue)
        AppointmentStatus.COMPLETED -> Pair(Color(0xFFE8F5E9), Color(0xFF2E7D32))
        AppointmentStatus.CANCELED -> Pair(Color(0xFFFFEBEE), Color(0xFFC62828))
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = bgColor
    ) {
        Text(
            text = status.label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        )
    }
}

@Preview(name = "Appointment List - With Items", showBackground = true)
@Composable
private fun AppointmentListPreview() {
    ShiftIQTheme {
        AppointmentListScreen(
            uiState = AppointmentListUiState(
                appointments = listOf(
                    Appointment(
                        id = UUID.randomUUID(),
                        branchId = UUID.randomUUID(),
                        customerId = UUID.randomUUID(),
                        vehicleId = UUID.randomUUID(),
                        scheduledStart = "2026-10-15T10:00:00Z",
                        status = AppointmentStatus.PENDING,
                        notes = "Mantenimiento preventivo de frenos y cambio de aceite."
                    ),
                    Appointment(
                        id = UUID.randomUUID(),
                        branchId = UUID.randomUUID(),
                        customerId = UUID.randomUUID(),
                        vehicleId = UUID.randomUUID(),
                        scheduledStart = "2026-10-14T15:30:00Z",
                        status = AppointmentStatus.COMPLETED,
                        notes = "Diagnóstico con escáner OBD2."
                    )
                ),
                filteredAppointments = listOf(
                    Appointment(
                        id = UUID.randomUUID(),
                        branchId = UUID.randomUUID(),
                        customerId = UUID.randomUUID(),
                        vehicleId = UUID.randomUUID(),
                        scheduledStart = "2026-10-15T10:00:00Z",
                        status = AppointmentStatus.PENDING,
                        notes = "Mantenimiento preventivo de frenos y cambio de aceite."
                    )
                ),
                activeBranchId = UUID.randomUUID()
            ),
            onRefresh = {},
            onFilterSelected = {},
            onMarkStatus = { _, _ -> },
            onNavigateToCreate = {},
            onOpenWorkOrder = {},
            onClearMessages = {}
        )
    }
}

@Preview(name = "Appointment List - Empty State", showBackground = true)
@Composable
private fun EmptyAppointmentListPreview() {
    ShiftIQTheme {
        AppointmentListScreen(
            uiState = AppointmentListUiState(
                appointments = emptyList(),
                filteredAppointments = emptyList()
            ),
            onRefresh = {},
            onFilterSelected = {},
            onMarkStatus = { _, _ -> },
            onNavigateToCreate = {},
            onOpenWorkOrder = {},
            onClearMessages = {}
        )
    }
}

@Preview(name = "Appointment Card Preview", showBackground = true)
@Composable
private fun AppointmentCardPreview() {
    ShiftIQTheme {
        AppointmentCard(
            appointment = Appointment(
                id = UUID.randomUUID(),
                branchId = UUID.randomUUID(),
                customerId = UUID.randomUUID(),
                vehicleId = UUID.randomUUID(),
                scheduledStart = "2026-10-15T10:00:00Z",
                status = AppointmentStatus.PENDING,
                notes = "Revisión técnica de motor."
            ),
            onConfirm = {},
            onCancel = {},
            onComplete = {},
            onOpenWorkOrder = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
