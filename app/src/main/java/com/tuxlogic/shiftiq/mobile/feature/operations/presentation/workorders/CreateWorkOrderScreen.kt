package com.tuxlogic.shiftiq.mobile.feature.operations.presentation.workorders

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tuxlogic.shiftiq.mobile.core.designsystem.components.ShiftIQPageHeader
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.PrimaryBlue
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQBorderSlate
import com.tuxlogic.shiftiq.mobile.core.localization.LocalizedText as Text
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.Appointment

@Composable
fun CreateWorkOrderRoute(
    onNavigateBack: () -> Unit,
    onWorkOrderCreated: () -> Unit,
    initialDtcPrefill: DtcWorkOrderPrefill? = null,
    initialAppointmentId: String? = null,
    viewModel: CreateWorkOrderViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(state.created) { if (state.created) onWorkOrderCreated() }
    LaunchedEffect(initialDtcPrefill) {
        initialDtcPrefill?.let(viewModel::applyDtcPrefill)
    }
    LaunchedEffect(initialAppointmentId) {
        initialAppointmentId?.let(viewModel::applyAppointmentPrefill)
    }
    CreateWorkOrderScreen(
        state = state,
        onNavigateBack = onNavigateBack,
        onSelectAppointment = viewModel::selectAppointment,
        onMileageChanged = viewModel::updateMileage,
        onDiagnosticChanged = viewModel::updateDiagnostic,
        onSubmit = viewModel::submit
    )
}

@Composable
fun CreateWorkOrderScreen(
    state: CreateWorkOrderUiState,
    onNavigateBack: () -> Unit,
    onSelectAppointment: (Appointment) -> Unit,
    onMileageChanged: (String) -> Unit,
    onDiagnosticChanged: (String) -> Unit,
    onSubmit: () -> Unit
) {
    var showAppointmentPicker by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize()) {
        ShiftIQPageHeader(
            title = "Nueva orden de trabajo",
            subtitle = "Registra el ingreso desde una cita pendiente",
            showBackButton = true,
            onBackPressed = onNavigateBack
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White),
                    border = BorderStroke(1.dp, ShiftIQBorderSlate),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    val prefill = state.dtcPrefill
                    Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        if (prefill == null) {
                            Text("CITA PENDIENTE", style = MaterialTheme.typography.labelMedium, color = PrimaryBlue, fontWeight = FontWeight.Bold)
                            if (state.isLoadingAppointments) {
                                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally), color = PrimaryBlue)
                            } else if (state.appointments.isEmpty()) {
                                Text("No hay citas pendientes en esta sede. Agenda una cita antes de abrir una orden.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            } else if (state.selectedAppointment != null) {
                                val appointment = state.selectedAppointment
                                Text(appointment.customerName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                appointment.customerPhone.takeIf(String::isNotBlank)?.let { Text(it, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                                Text("${appointment.vehiclePlate} · ${appointment.vehicleModel}", style = MaterialTheme.typography.bodyMedium)
                                Text(appointment.serviceType, style = MaterialTheme.typography.bodyMedium, color = PrimaryBlue, fontWeight = FontWeight.SemiBold)
                                appointment.notes?.takeIf(String::isNotBlank)?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                            } else {
                                OutlinedButton(onClick = { showAppointmentPicker = true }, modifier = Modifier.fillMaxWidth()) {
                                    Text("Seleccionar cita")
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                }
                            }
                        } else {
                            Text("DATOS DEL VEHÍCULO Y CLIENTE", style = MaterialTheme.typography.labelMedium, color = PrimaryBlue, fontWeight = FontWeight.Bold)
                            Text("${prefill.plate} · ${prefill.model}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            Text(prefill.customerName, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Código DTC ${prefill.dtcCode}", style = MaterialTheme.typography.bodySmall, color = PrimaryBlue)
                            when {
                                state.isLoadingAppointments -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally), color = PrimaryBlue)
                                state.selectedAppointment != null -> Text("Cita pendiente: ${appointmentLabel(state.selectedAppointment)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                state.appointments.isEmpty() -> Text("Se necesita una cita pendiente para este vehículo.", color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White),
                    border = BorderStroke(1.dp, ShiftIQBorderSlate),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(if (state.dtcPrefill == null) "DATOS DE INGRESO" else "DATOS DE INGRESO Y DIAGNÓSTICO", style = MaterialTheme.typography.labelMedium, color = PrimaryBlue, fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = state.mileage,
                            onValueChange = onMileageChanged,
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Kilometraje de entrada") },
                            suffix = { Text("km") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = state.diagnosticSummary,
                            onValueChange = onDiagnosticChanged,
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Diagnóstico inicial") },
                            minLines = 3
                        )
                    }
                }
            }
            state.errorMessage?.let { message ->
                item { Text(message, color = MaterialTheme.colorScheme.error) }
            }
            item {
                Button(
                    onClick = onSubmit,
                    enabled = !state.isSubmitting && !state.isLoadingAppointments && state.selectedAppointment != null,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 50.dp)
                ) {
                    if (state.isSubmitting) CircularProgressIndicator()
                    else Text(if (state.dtcPrefill == null) "Abrir orden de trabajo" else "ABRIR ORDEN DE TRABAJO")
                }
            }
        }
    }

    if (showAppointmentPicker) {
        AlertDialog(
            onDismissRequest = { showAppointmentPicker = false },
            title = { Text("Seleccionar cita") },
            text = {
                LazyColumn(Modifier.heightIn(max = 420.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.appointments, key = { it.id.toString() }) { appointment ->
                        Card(
                            modifier = Modifier.fillMaxWidth().clickable {
                                onSelectAppointment(appointment)
                                showAppointmentPicker = false
                            },
                            colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White),
                            border = BorderStroke(1.dp, ShiftIQBorderSlate)
                        ) {
                            Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(appointmentLabel(appointment), fontWeight = FontWeight.SemiBold)
                                Text("Cliente ${appointment.customerId.toString().take(8)} · Vehículo ${appointment.vehicleId.toString().take(8)}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showAppointmentPicker = false }) { Text("Cerrar") } }
        )
    }
}

private fun appointmentLabel(appointment: Appointment): String =
    "${appointment.scheduledStart.replace('T', ' ').take(16)} · ${appointment.id.toString().take(8)}"
