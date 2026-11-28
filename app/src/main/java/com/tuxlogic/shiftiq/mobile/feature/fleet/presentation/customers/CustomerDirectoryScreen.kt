package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.customers

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import android.widget.Toast
import com.tuxlogic.shiftiq.mobile.core.designsystem.components.ShiftIQPageHeader
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.PrimaryBlue
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQBorderSlate
import com.tuxlogic.shiftiq.mobile.core.localization.LocalizedText as Text
import com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.appointments.CreateAppointmentViewModel
import java.time.LocalDate

@Composable
fun CustomerDirectoryRoute(
    onNavigateBack: () -> Unit,
    initialAppointmentDate: String? = null,
    onNavigateToServices: () -> Unit = {},
    viewModel: CustomerDirectoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val appointmentViewModel: CreateAppointmentViewModel = hiltViewModel()
    var showCreateAppointmentSheet by remember { mutableStateOf(false) }
    var reopenCreateAppointmentSheet by remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, reopenCreateAppointmentSheet) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && reopenCreateAppointmentSheet) {
                reopenCreateAppointmentSheet = false
                appointmentViewModel.refreshOptions()
                showCreateAppointmentSheet = true
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    LaunchedEffect(Unit) { viewModel.refresh() }
    CustomerDirectoryScreen(
        uiState = uiState,
        onRefresh = viewModel::refresh,
        onNavigateBack = onNavigateBack,
        onCreateAppointment = { branchId, customerId ->
            appointmentViewModel.prepareNewAppointment(
                branchId = branchId,
                customerId = customerId,
                scheduledDate = initialAppointmentDate?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
            )
            showCreateAppointmentSheet = true
        }
    )
    if (showCreateAppointmentSheet) {
        com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.appointments.CreateAppointmentBottomSheet(
            viewModel = appointmentViewModel,
            onDismiss = { showCreateAppointmentSheet = false },
            onAppointmentCreated = {
                showCreateAppointmentSheet = false
                viewModel.refresh()
            },
            onNavigateToServices = {
                showCreateAppointmentSheet = false
                reopenCreateAppointmentSheet = true
                onNavigateToServices()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDirectoryScreen(
    uiState: CustomerDirectoryUiState,
    onRefresh: () -> Unit,
    onNavigateBack: () -> Unit,
    onCreateAppointment: (branchId: String, customerId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var query by remember { mutableStateOf("") }
    val context = LocalContext.current
    val filteredCustomers = remember(uiState.customers, query) {
        val normalizedQuery = query.trim().lowercase()
        if (normalizedQuery.isEmpty()) uiState.customers else uiState.customers.filter {
            it.name.lowercase().contains(normalizedQuery) ||
                it.phone.lowercase().contains(normalizedQuery) ||
                it.document.lowercase().contains(normalizedQuery) ||
                it.vehiclePlate.lowercase().contains(normalizedQuery)
        }
    }

    Column(modifier.fillMaxSize()) {
        ShiftIQPageHeader(
            title = "Directorio de clientes",
            subtitle = "Clientes registrados en esta sede",
            showBackButton = true,
            onBackPressed = onNavigateBack
        )
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            label = { Text("Buscar cliente") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            singleLine = true
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("${filteredCustomers.size} clientes registrados", color = MaterialTheme.colorScheme.onSurfaceVariant)
            OutlinedButton(
                onClick = { onCreateAppointment(uiState.branchId, "") },
                enabled = uiState.branchId.isNotBlank()
            ) {
                Icon(Icons.Default.Event, contentDescription = null)
                Text("Nueva cita", modifier = Modifier.padding(start = 6.dp))
            }
        }

        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = onRefresh,
            modifier = Modifier.weight(1f).fillMaxWidth()
        ) {
        when {
            uiState.isLoading && uiState.customers.isEmpty() -> Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) { CircularProgressIndicator(color = PrimaryBlue) }
            uiState.errorMessage != null && uiState.customers.isEmpty() -> Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(uiState.errorMessage, color = MaterialTheme.colorScheme.error, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                OutlinedButton(onClick = onRefresh) {
                    Icon(Icons.Default.Event, contentDescription = null)
                    Text("Reintentar", modifier = Modifier.padding(start = 8.dp))
                }
            }
            filteredCustomers.isEmpty() -> Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("No se encontraron clientes", fontWeight = FontWeight.Bold)
                Text("Prueba con otro término o revisa los registros de esta sede.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredCustomers, key = { it.customerId }) { customer ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White),
                        border = BorderStroke(1.dp, ShiftIQBorderSlate)
                    ) {
                        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(shape = CircleShape, color = PrimaryBlue.copy(alpha = 0.1f), modifier = Modifier.size(40.dp)) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(customer.name.firstOrNull()?.uppercaseChar()?.toString() ?: "C", color = PrimaryBlue, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Column(Modifier.weight(1f).padding(start = 12.dp)) {
                                    Text(customer.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    if (customer.phone.isNotBlank()) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Phone, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                                            Text(customer.phone, modifier = Modifier.padding(start = 5.dp), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            TextButton(
                                                onClick = {
                                                    Toast.makeText(
                                                        context,
                                                        "Contactando a ${customer.name} (${customer.phone})",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                },
                                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                                            ) {
                                                Icon(Icons.Default.Phone, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(13.dp))
                                                Text("Llamar", modifier = Modifier.padding(start = 3.dp), style = MaterialTheme.typography.labelSmall, color = PrimaryBlue)
                                            }
                                        }
                                    }
                                }
                                if (customer.vehiclePlate.isNotBlank()) {
                                    Surface(shape = RoundedCornerShape(8.dp), color = androidx.compose.ui.graphics.Color(0xFF0F172A)) {
                                        Text(customer.vehiclePlate, modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp), color = androidx.compose.ui.graphics.Color.White, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            if (customer.vehiclePlate.isNotBlank() || customer.vehicleModel.isNotBlank()) {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                                ) {
                                    Row(Modifier.padding(horizontal = 10.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                                        Text(customer.vehicleModel.ifBlank { "Vehículo" }, modifier = Modifier.padding(start = 7.dp), color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                OutlinedButton(onClick = { onCreateAppointment(uiState.branchId, customer.customerId) }) {
                                    Icon(Icons.Default.Event, contentDescription = null)
                                    Text("Agendar cita", modifier = Modifier.padding(start = 6.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
        }
    }
}
