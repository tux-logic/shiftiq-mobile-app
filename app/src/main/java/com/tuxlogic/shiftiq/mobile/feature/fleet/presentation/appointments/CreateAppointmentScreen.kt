package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.appointments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.BuildCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.PrimaryBlue
import com.tuxlogic.shiftiq.mobile.core.localization.LocalizedText as Text
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private val AppointmentDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

@Composable
fun CreateAppointmentRoute(
    onNavigateBack: () -> Unit,
    onAppointmentCreated: () -> Unit,
    onNavigateToServices: () -> Unit = {},
    viewModel: CreateAppointmentViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CreateAppointmentScreen(
        uiState = uiState,
        onCustomerSelected = viewModel::selectCustomer,
        onVehicleSelected = viewModel::onVehicleIdChanged,
        onCustomerNameChanged = viewModel::onCustomerNameChanged,
        onCustomerPhoneChanged = viewModel::onCustomerPhoneChanged,
        onVehiclePlateChanged = viewModel::onVehiclePlateChanged,
        onVehicleModelChanged = viewModel::onVehicleModelChanged,
        onBaySelected = viewModel::onBaySelected,
        onServiceSelected = viewModel::onServiceSelected,
        onScheduledStartChanged = viewModel::onScheduledStartChanged,
        onNotesChanged = viewModel::onNotesChanged,
        onRefreshOptions = viewModel::refreshOptions,
        onNavigateToServices = onNavigateToServices,
        onSubmit = viewModel::createAppointment,
        onNavigateBack = onNavigateBack,
        onAppointmentCreated = onAppointmentCreated
    )
}

@Composable
fun CreateAppointmentScreen(
    uiState: CreateAppointmentUiState,
    onCustomerSelected: (String) -> Unit,
    onVehicleSelected: (String) -> Unit,
    onCustomerNameChanged: (String) -> Unit,
    onCustomerPhoneChanged: (String) -> Unit,
    onVehiclePlateChanged: (String) -> Unit,
    onVehicleModelChanged: (String) -> Unit,
    onBaySelected: (Int) -> Unit,
    onServiceSelected: (String) -> Unit,
    onScheduledStartChanged: (String) -> Unit,
    onNotesChanged: (String) -> Unit,
    onRefreshOptions: () -> Unit,
    onNavigateToServices: () -> Unit,
    onSubmit: () -> Unit,
    onNavigateBack: () -> Unit,
    onAppointmentCreated: () -> Unit,
    modifier: Modifier = Modifier,
    modalPresentation: Boolean = false
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    var customerMenuExpanded by remember { mutableStateOf(false) }
    var vehicleMenuExpanded by remember { mutableStateOf(false) }
    var serviceMenuExpanded by remember { mutableStateOf(false) }
    var showValidationErrors by remember { mutableStateOf(false) }
    val selectedCustomer = uiState.customers.firstOrNull { it.customerId == uiState.selectedCustomerId }
    val scheduledDateTime = remember(uiState.scheduledStart) { parseScheduledStart(uiState.scheduledStart) }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.takeUnless {
            it == "Ingrese el cliente." || it == "Ingrese la placa del vehículo." ||
                it == "Seleccione un servicio."
        }?.let { snackbarHostState.showSnackbar(it) }
    }
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) onAppointmentCreated()
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        if (modalPresentation) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 8.dp, top = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Agendar Nueva Cita", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar")
                }
            }
        } else {
            androidx.compose.material3.Surface(color = MaterialTheme.colorScheme.background) {
                com.tuxlogic.shiftiq.mobile.core.designsystem.components.ShiftIQPageHeader(
                    title = "Agendar Nueva Cita",
                    subtitle = "Gestión de Bahías · ${selectedCustomer?.name ?: "Nueva cita"}",
                    showBackButton = true,
                    onBackPressed = onNavigateBack
                )
            }
        }

        when {
            uiState.isLoadingOptions -> Box(
                Modifier.fillMaxWidth().height(260.dp),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator(color = PrimaryBlue) }

            else -> Box(Modifier.weight(1f, fill = false).fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (uiState.customers.isNotEmpty()) {
                        Box {
                            OutlinedButton(onClick = { customerMenuExpanded = true }, modifier = Modifier.fillMaxWidth()) {
                                Icon(Icons.Default.Person, contentDescription = null)
                                Text(
                                    selectedCustomer?.let { "${it.name} (${it.vehicles.firstOrNull()?.plateNumber?.value.orEmpty()})" }
                                    ?: "Seleccionar Cliente Registrado (Opcional)",
                                    modifier = Modifier.weight(1f).padding(start = 8.dp),
                                    textAlign = TextAlign.Start,
                                    maxLines = 1
                                )
                            }
                            DropdownMenu(expanded = customerMenuExpanded, onDismissRequest = { customerMenuExpanded = false }) {
                                DropdownMenuItem(
                                    text = { Text("Cliente Nuevo / Ingresar Manualmente") },
                                    onClick = { customerMenuExpanded = false; onCustomerSelected("") }
                                )
                                uiState.customers.forEach { customer ->
                                    DropdownMenuItem(
                                        text = { Text("${customer.name} (${customer.vehicles.firstOrNull()?.plateNumber?.value.orEmpty()})") },
                                        onClick = { customerMenuExpanded = false; onCustomerSelected(customer.customerId) }
                                    )
                                }
                            }
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = uiState.customerName,
                            onValueChange = onCustomerNameChanged,
                            label = { Text("Nombre Completo *") },
                            modifier = Modifier.weight(3f),
                            singleLine = true,
                            isError = showValidationErrors && uiState.customerName.isBlank(),
                            supportingText = if (showValidationErrors && uiState.customerName.isBlank()) {
                                { Text("Ingrese el cliente", color = MaterialTheme.colorScheme.error) }
                            } else null,
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
                        )
                        OutlinedTextField(
                            value = uiState.customerPhone,
                            onValueChange = onCustomerPhoneChanged,
                            label = { Text("Teléfono") },
                            modifier = Modifier.weight(2f),
                            singleLine = true,
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) }
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = uiState.vehiclePlate,
                            onValueChange = onVehiclePlateChanged,
                            label = { Text("Placa *") },
                            placeholder = { Text("ABC-123") },
                            modifier = Modifier.weight(2f),
                            singleLine = true,
                            isError = showValidationErrors && uiState.vehiclePlate.isBlank(),
                            supportingText = if (showValidationErrors && uiState.vehiclePlate.isBlank()) {
                                { Text("Ingrese placa", color = MaterialTheme.colorScheme.error) }
                            } else null,
                            leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) }
                        )
                        OutlinedTextField(
                            value = uiState.vehicleModel,
                            onValueChange = onVehicleModelChanged,
                            label = { Text("Modelo / Marca") },
                            placeholder = { Text("Toyota Corolla") },
                            modifier = Modifier.weight(3f),
                            singleLine = true,
                            leadingIcon = { Icon(Icons.Default.DirectionsCar, contentDescription = null) }
                        )
                    }

                    if (selectedCustomer?.vehicles.orEmpty().size > 1) {
                        Box {
                            OutlinedButton(onClick = { vehicleMenuExpanded = true }, modifier = Modifier.fillMaxWidth()) {
                                Icon(Icons.Default.DirectionsCar, contentDescription = null)
                                Text("Seleccionar vehículo registrado", modifier = Modifier.weight(1f).padding(start = 8.dp), textAlign = TextAlign.Start)
                            }
                            DropdownMenu(expanded = vehicleMenuExpanded, onDismissRequest = { vehicleMenuExpanded = false }) {
                                selectedCustomer?.vehicles.orEmpty().forEach { vehicle ->
                                    DropdownMenuItem(
                                        text = { Text("${vehicle.plateNumber.value} · ${vehicle.brand} ${vehicle.model}".trim()) },
                                        onClick = { vehicleMenuExpanded = false; onVehicleSelected(vehicle.id.toString()) }
                                    )
                                }
                            }
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedButton(
                            onClick = {
                                val current = scheduledDateTime ?: LocalDate.now().atTime(9, 0)
                                val minDate = LocalDate.now().minusDays(1)
                                val maxDate = LocalDate.now().plusDays(90)
                                DatePickerDialog(
                                    context,
                                    { _, year, month, day ->
                                        val next = LocalDate.of(year, month + 1, day).atTime(current.toLocalTime())
                                        onScheduledStartChanged(formatScheduledStart(next))
                                    },
                                    current.year,
                                    current.monthValue - 1,
                                    current.dayOfMonth
                                ).apply {
                                    datePicker.minDate = minDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
                                    datePicker.maxDate = maxDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
                                }.show()
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = null)
                            Text(
                                scheduledDateTime?.format(DateTimeFormatter.ofPattern("d/M/yyyy", Locale.forLanguageTag("es-PE"))) ?: "Elegir fecha",
                                modifier = Modifier.padding(start = 8.dp), maxLines = 1
                            )
                        }
                        OutlinedButton(
                            onClick = {
                                val current = scheduledDateTime ?: LocalDate.now().atTime(9, 0)
                                TimePickerDialog(
                                    context,
                                    { _, hour, minute -> onScheduledStartChanged(formatScheduledStart(current.withHour(hour).withMinute(minute))) },
                                    current.hour,
                                    current.minute,
                                    false
                                ).show()
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Schedule, contentDescription = null)
                            Text(scheduledDateTime?.format(DateTimeFormatter.ofPattern("h:mm a", Locale.forLanguageTag("es-PE"))) ?: "Elegir hora", modifier = Modifier.padding(start = 8.dp))
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Bahía Asignada:", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            (1..4).forEach { bay ->
                                FilterChip(
                                    selected = uiState.selectedBayNumber == bay,
                                    onClick = { onBaySelected(bay) },
                                    label = { Text("Bahía $bay") },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    when {
                        uiState.isLoadingServices && uiState.services.isEmpty() -> Row(
                            modifier = Modifier.padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            CircularProgressIndicator(Modifier.height(18.dp), strokeWidth = 2.dp)
                            Text("Cargando servicios de la sede...", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        uiState.services.isEmpty() -> Row(
                            modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium).padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.BuildCircle, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("No hay servicios creados en esta sede.", modifier = Modifier.weight(1f).padding(horizontal = 10.dp), style = MaterialTheme.typography.bodySmall)
                            TextButton(onClick = onNavigateToServices) { Text("Crear") }
                        }
                        else -> Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Tipo de Servicio / Trabajo", style = MaterialTheme.typography.labelLarge)
                            Box {
                                OutlinedButton(onClick = { serviceMenuExpanded = true }, modifier = Modifier.fillMaxWidth()) {
                                    Icon(Icons.Default.BuildCircle, contentDescription = null)
                                    val chosen = uiState.services.firstOrNull { it.name == uiState.selectedServiceName }
                                    Text(
                                        chosen?.let { "${it.name} (S/ ${it.price.amount.toPlainString()})" }.orEmpty(),
                                        modifier = Modifier.weight(1f).padding(start = 8.dp), textAlign = TextAlign.Start, maxLines = 1
                                    )
                                }
                                DropdownMenu(expanded = serviceMenuExpanded, onDismissRequest = { serviceMenuExpanded = false }) {
                                    uiState.services.forEach { service ->
                                        DropdownMenuItem(
                                            text = { Text("${service.name} (S/ ${service.price.amount.toPlainString()})") },
                                            onClick = { serviceMenuExpanded = false; onServiceSelected(service.name) }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = uiState.notes,
                        onValueChange = onNotesChanged,
                        label = { Text("Notas / Síntomas reportados") },
                        placeholder = { Text("Detalles del cliente o sospecha técnica...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 4,
                        leadingIcon = { Icon(Icons.AutoMirrored.Filled.Notes, contentDescription = null) }
                    )

                    Button(
                        onClick = { showValidationErrors = true; onSubmit() },
                        enabled = !uiState.isLoading,
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        if (uiState.isLoading) CircularProgressIndicator(Modifier.height(20.dp), strokeWidth = 2.dp)
                        else Icon(Icons.Default.Event, contentDescription = null)
                        Text("Confirmar y Agendar", modifier = Modifier.padding(start = 8.dp), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.CenterHorizontally))
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun CreateAppointmentBottomSheet(
    viewModel: CreateAppointmentViewModel,
    onDismiss: () -> Unit,
    onAppointmentCreated: () -> Unit,
    onNavigateToServices: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        CreateAppointmentScreen(
            uiState = uiState,
            onCustomerSelected = viewModel::selectCustomer,
            onVehicleSelected = viewModel::onVehicleIdChanged,
            onCustomerNameChanged = viewModel::onCustomerNameChanged,
            onCustomerPhoneChanged = viewModel::onCustomerPhoneChanged,
            onVehiclePlateChanged = viewModel::onVehiclePlateChanged,
            onVehicleModelChanged = viewModel::onVehicleModelChanged,
            onBaySelected = viewModel::onBaySelected,
            onServiceSelected = viewModel::onServiceSelected,
            onScheduledStartChanged = viewModel::onScheduledStartChanged,
            onNotesChanged = viewModel::onNotesChanged,
            onRefreshOptions = viewModel::refreshOptions,
            onNavigateToServices = onNavigateToServices,
            onSubmit = viewModel::createAppointment,
            onNavigateBack = onDismiss,
            onAppointmentCreated = onAppointmentCreated,
            modalPresentation = true
        )
    }
}

private fun parseScheduledStart(value: String): LocalDateTime? = runCatching {
    LocalDateTime.parse(value.removeSuffix("Z"))
}.getOrNull()

private fun formatScheduledStart(value: LocalDateTime): String = AppointmentDateTimeFormatter.format(value)
