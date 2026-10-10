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
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Datos de la Cita",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            Text(
                text = "Ingresa los identificadores de la sede, cliente y vehículo para registrar la cita en el sistema.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

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

            ShiftIQTextField(
                value = uiState.scheduledStart,
                onValueChange = onScheduledStartChanged,
                label = "Fecha y Hora Programada (ISO 8601)",
                placeholder = "2026-10-15T10:00:00Z",
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Event, contentDescription = null)
                }
            )

            OutlinedTextField(
                value = uiState.notes,
                onValueChange = onNotesChanged,
                label = { Text("Notas o Motivo del Mantenimiento (Opcional)") },
                modifier = Modifier.fillMaxWidth(),
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

            Spacer(modifier = Modifier.height(8.dp))

            ShiftIQPrimaryButton(
                text = "Confirmar y Agendar Cita",
                onClick = onSubmit,
                isLoading = uiState.isLoading,
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            )
        }
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
