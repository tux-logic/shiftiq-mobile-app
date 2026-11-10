package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.appointments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tuxlogic.shiftiq.mobile.core.designsystem.components.ShiftIQPrimaryButton
import com.tuxlogic.shiftiq.mobile.core.designsystem.components.ShiftIQTextField
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQTheme

import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAppointmentScreen(
    uiState: CreateAppointmentUiState,
    onBranchIdChanged: (String) -> Unit,
    onCustomerIdChanged: (String) -> Unit,
    onVehicleIdChanged: (String) -> Unit,
    onScheduledStartChanged: (String) -> Unit,
    onNotesChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onNavigateBack: () -> Unit,
    onAppointmentCreated: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { snackbarHostState.showSnackbar(it) }
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onAppointmentCreated()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Agendar Nueva Cita",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            Text(text = "Programa el servicio", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Agrega los datos del cliente, vehículo y horario para reservar su visita.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(22.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
            Text("Detalles de la cita", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(2.dp))
            ShiftIQTextField(
                value = uiState.branchId,
                onValueChange = onBranchIdChanged,
                label = "ID de Sede (UUID)",
                placeholder = "e.g. 550e8400-e29b-41d4-a716-446655440000",
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Place, contentDescription = null)
                }
            )

            ShiftIQTextField(
                value = uiState.customerId,
                onValueChange = onCustomerIdChanged,
                label = "ID de Cliente (UUID)",
                placeholder = "e.g. 123e4567-e89b-12d3-a456-426614174000",
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Person, contentDescription = null)
                }
            )

            ShiftIQTextField(
                value = uiState.vehicleId,
                onValueChange = onVehicleIdChanged,
                label = "ID de Vehículo (UUID)",
                placeholder = "e.g. 789e0123-e89b-12d3-a456-426614174000",
                leadingIcon = {
                    Icon(imageVector = Icons.Default.DirectionsCar, contentDescription = null)
                }
            )

            androidx.compose.material3.OutlinedTextField(
                value = uiState.scheduledStart,
                onValueChange = onScheduledStartChanged,
                label = { Text("Fecha y hora (ISO 8601)") },
                placeholder = { Text("Selecciona una fecha") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                leadingIcon = { Icon(imageVector = Icons.Default.Event, contentDescription = null) },
                trailingIcon = {
                    TextButton(onClick = { showDatePicker = true }) { Text("Elegir") }
                }
            )

            OutlinedTextField(
                value = uiState.notes,
                onValueChange = onNotesChanged,
                label = { Text("Notas o Motivo del Mantenimiento (Opcional)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                minLines = 3,
                maxLines = 5,
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Notes, contentDescription = null)
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            ShiftIQPrimaryButton(
                text = "Confirmar y Agendar Cita",
                onClick = onSubmit,
                isLoading = uiState.isLoading,
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = java.time.Instant.ofEpochMilli(millis)
                            .atZone(java.time.ZoneOffset.UTC).toLocalDate()
                        onScheduledStartChanged("${date}T10:00:00Z")
                    }
                    showDatePicker = false
                }) { Text("Continuar") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") } }
        ) { DatePicker(state = datePickerState) }
    }
}

@Preview(name = "Create Appointment Preview", showBackground = true)
@Composable
private fun CreateAppointmentPreview() {
    ShiftIQTheme {
        CreateAppointmentScreen(
            uiState = CreateAppointmentUiState(
                branchId = "550e8400-e29b-41d4-a716-446655440000",
                customerId = "123e4567-e89b-12d3-a456-426614174000",
                vehicleId = "789e0123-e89b-12d3-a456-426614174000",
                scheduledStart = "2026-10-15T10:00:00Z",
                notes = "Revisión completa de frenos y pastillas."
            ),
            onBranchIdChanged = {},
            onCustomerIdChanged = {},
            onVehicleIdChanged = {},
            onScheduledStartChanged = {},
            onNotesChanged = {},
            onSubmit = {},
            onNavigateBack = {},
            onAppointmentCreated = {}
        )
    }
}
