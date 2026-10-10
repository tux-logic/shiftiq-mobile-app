package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.appointments

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQTheme
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.Appointment
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.AppointmentStatus
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentListScreen(
    uiState: AppointmentListUiState,
    onRefresh: () -> Unit,
    onFilterSelected: (AppointmentStatus?) -> Unit,
    onMarkStatus: (Appointment, AppointmentStatus) -> Unit,
    onDeleteAppointment: (UUID) -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateBack: () -> Unit,
    onClearMessages: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage, uiState.successMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            onClearMessages()
        }
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            onClearMessages()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Agenda de Citas",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = uiState.activeBranchId?.let { "Sede: ${it.toString().take(8)}..." } ?: "Todas las sedes",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                actions = {
                    IconButton(onClick = onRefresh) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Recargar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreate,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Agendar Cita")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Filtros por estado
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = uiState.selectedStatusFilter == null,
                        onClick = { onFilterSelected(null) },
                        label = { Text("Todas (${uiState.appointments.size})") },
                        colors = FilterChipDefaults.filterChipColors()
                    )
                }
                item {
                    FilterChip(
                        selected = uiState.selectedStatusFilter == AppointmentStatus.PENDING,
                        onClick = { onFilterSelected(AppointmentStatus.PENDING) },
                        label = {
                            Text("Pendientes (${uiState.appointments.count { it.status == AppointmentStatus.PENDING }})")
                        }
                    )
                }
                item {
                    FilterChip(
                        selected = uiState.selectedStatusFilter == AppointmentStatus.COMPLETED,
                        onClick = { onFilterSelected(AppointmentStatus.COMPLETED) },
                        label = {
                            Text("Completadas (${uiState.appointments.count { it.status == AppointmentStatus.COMPLETED }})")
                        }
                    )
                }
                item {
                    FilterChip(
                        selected = uiState.selectedStatusFilter == AppointmentStatus.CANCELED,
                        onClick = { onFilterSelected(AppointmentStatus.CANCELED) },
                        label = {
                            Text("Canceladas (${uiState.appointments.count { it.status == AppointmentStatus.CANCELED }})")
                        }
                    )
                }
            }

            if (uiState.isLoading && uiState.appointments.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else if (uiState.filteredAppointments.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Event,
                                contentDescription = null,
                                modifier = Modifier.size(36.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No hay citas registradas",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Agenda una nueva cita para tu sede usando el botón '+' inferior.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.filteredAppointments, key = { it.id }) { appointment ->
                        AppointmentCard(
                            appointment = appointment,
                            onMarkCompleted = { onMarkStatus(appointment, AppointmentStatus.COMPLETED) },
                            onMarkCanceled = { onMarkStatus(appointment, AppointmentStatus.CANCELED) },
                            onDelete = { onDeleteAppointment(appointment.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AppointmentCard(
    appointment: Appointment,
    onMarkCompleted: () -> Unit,
    onMarkCanceled: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Event,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = appointment.scheduledStart.replace("T", " ").take(16),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                StatusPill(status = appointment.status)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Cliente",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Text(
                        text = appointment.customerId.toString().take(8) + "...",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Vehículo",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Text(
                        text = appointment.vehicleId.toString().take(8) + "...",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                    )
                }
            }

            appointment.notes?.takeIf { it.isNotBlank() }?.let { notes ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Notas: $notes",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
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
                        onClick = onMarkCompleted,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color(0xFF2E7D32)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Completar", style = MaterialTheme.typography.labelMedium)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedButton(
                        onClick = onMarkCanceled,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Cancelar", style = MaterialTheme.typography.labelMedium)
                    }

                    Spacer(modifier = Modifier.width(8.dp))
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun StatusPill(
    status: AppointmentStatus,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (status) {
        AppointmentStatus.PENDING -> Pair(Color(0xFFFFF3E0), Color(0xFFE65100))
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
            onDeleteAppointment = {},
            onNavigateToCreate = {},
            onNavigateBack = {},
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
            onDeleteAppointment = {},
            onNavigateToCreate = {},
            onNavigateBack = {},
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
            onMarkCompleted = {},
            onMarkCanceled = {},
            onDelete = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
