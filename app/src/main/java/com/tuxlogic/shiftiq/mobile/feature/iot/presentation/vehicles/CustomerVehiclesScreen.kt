package com.tuxlogic.shiftiq.mobile.feature.iot.presentation.vehicles

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQTheme
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.DtcAlert
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.DtcSeverity
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.TelemetrySnapshot
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.Vehicle
import coil3.compose.AsyncImage
import java.text.NumberFormat
import java.util.Locale

private val GarageNavy = Color(0xFF1E3A8A)
private val GarageTeal = Color(0xFF0FB5BA)
private val GarageGreen = Color(0xFF3DBE6C)
private val GarageAmber = Color(0xFFF2A93B)
private val GarageOrange = Color(0xFFC94A0E)
private val GarageBackground = Color(0xFFF7F7F4)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerVehiclesScreen(
    viewModel: CustomerVehiclesViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    BackHandler(enabled = uiState.selectedVehicle != null || uiState.isAddingVehicle) {
        if (uiState.isAddingVehicle) {
            viewModel.cancelVehicleRegistration()
        } else {
            viewModel.clearSelectedVehicle()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = GarageBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when {
                            uiState.isAddingVehicle && uiState.isEditingVehicle -> "Editar vehículo"
                            uiState.isAddingVehicle -> "Agregar vehículo"
                            else -> uiState.selectedVehicle?.let { "${it.brand} ${it.model}" } ?: "Mi Garaje"
                        },
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (uiState.isAddingVehicle) {
                                viewModel.cancelVehicleRegistration()
                            } else if (uiState.selectedVehicle != null) {
                                viewModel.clearSelectedVehicle()
                            } else {
                                onNavigateBack()
                            }
                        }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (!uiState.isAddingVehicle) {
                        IconButton(
                            onClick = {
                                if (uiState.selectedVehicle != null) {
                                    viewModel.refreshSelectedVehicle()
                                } else {
                                    viewModel.loadVehicles()
                                }
                            }
                        ) {
                            Icon(Icons.Filled.Refresh, contentDescription = "Actualizar")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GarageNavy,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        val selectedVehicle = uiState.selectedVehicle
        if (uiState.isAddingVehicle) {
            VehicleRegistrationForm(
                uiState = uiState,
                onPlateChanged = viewModel::onVehiclePlateChanged,
                onBrandChanged = viewModel::onVehicleBrandChanged,
                onModelChanged = viewModel::onVehicleModelChanged,
                onYearChanged = viewModel::onVehicleYearChanged,
                onVinChanged = viewModel::onVehicleVinChanged,
                onSubmit = viewModel::submitVehicleRegistration,
                onCancel = viewModel::cancelVehicleRegistration,
                modifier = Modifier.padding(innerPadding)
            )
        } else if (selectedVehicle == null) {
            VehicleListContent(
                uiState = uiState,
                onVehicleClick = viewModel::selectVehicle,
                onRefresh = viewModel::loadVehicles,
                onAddVehicle = viewModel::startVehicleRegistration,
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            VehicleDetailContent(
                uiState = uiState,
                vehicle = selectedVehicle,
                onEditVehicle = { viewModel.startVehicleEditing(selectedVehicle) },
                onRequestDeleteVehicle = viewModel::requestDeleteVehicleConfirmation,
                onLoadMoreTelemetryHistory = viewModel::loadMoreTelemetryHistory,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }

    if (uiState.showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = viewModel::dismissDeleteVehicleConfirmation,
            title = { Text("Eliminar vehículo") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("¿Seguro que quieres eliminar este vehículo de tu garaje?")
                    uiState.vehicleDeleteErrorMessage?.let { message ->
                        Text(message, color = MaterialTheme.colorScheme.error)
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = viewModel::deleteSelectedVehicle,
                    enabled = !uiState.isDeletingVehicle
                ) {
                    if (uiState.isDeletingVehicle) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Eliminar", color = MaterialTheme.colorScheme.error)
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = viewModel::dismissDeleteVehicleConfirmation,
                    enabled = !uiState.isDeletingVehicle
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun VehicleListContent(
    uiState: CustomerVehiclesUiState,
    onVehicleClick: (Vehicle) -> Unit,
    onRefresh: () -> Unit,
    onAddVehicle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "FLOTA PERSONAL",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Vehículos registrados",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.size(8.dp))
            OutlinedButton(onClick = onAddVehicle) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(Modifier.size(4.dp))
                Text("Agregar")
            }
        }
        uiState.customerName?.takeIf { it.isNotBlank() }?.let { name ->
            Text(
                text = "Hola, $name. Revisa aquí el estado de tus autos.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(Modifier.height(20.dp))

        when {
            uiState.isLoading && uiState.vehicles.isEmpty() -> LoadingContent()
            uiState.errorMessage != null && uiState.vehicles.isEmpty() -> MessageContent(
                message = uiState.errorMessage,
                actionLabel = "Reintentar",
                onAction = onRefresh
            )
            uiState.vehicles.isEmpty() -> MessageContent(
                message = "Todavía no hay vehículos asociados a tu cuenta."
            )
            else -> {
                Text(
                    text = "${uiState.vehicles.size} ${if (uiState.vehicles.size == 1) "vehículo" else "vehículos"} · Toca uno para ver su diagnóstico",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(12.dp))
                if (uiState.errorMessage != null) {
                    Text(
                        text = uiState.errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(uiState.vehicles, key = { it.id.toString() }) { vehicle ->
                        VehicleCard(
                            vehicle = vehicle,
                            latestSnapshot = uiState.telemetryByVehicleId[vehicle.id.toString()],
                            isLoadingTelemetry = uiState.isLoadingVehicleTelemetry,
                            onClick = { onVehicleClick(vehicle) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VehicleRegistrationForm(
    uiState: CustomerVehiclesUiState,
    onPlateChanged: (String) -> Unit,
    onBrandChanged: (String) -> Unit,
    onModelChanged: (String) -> Unit,
    onYearChanged: (String) -> Unit,
    onVinChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = if (uiState.isEditingVehicle) "Edita los datos de tu auto" else "Registra tu auto",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = if (uiState.isEditingVehicle) {
                "Actualiza la información del vehículo."
            } else {
                "Ingresa los datos del vehículo para asociarlo a tu cuenta."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        OutlinedTextField(
            value = uiState.vehiclePlateInput,
            onValueChange = onPlateChanged,
            label = { Text("Placa") },
            singleLine = true,
            enabled = !uiState.isRegisteringVehicle,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = uiState.vehicleBrandInput,
            onValueChange = onBrandChanged,
            label = { Text("Marca") },
            singleLine = true,
            enabled = !uiState.isRegisteringVehicle,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = uiState.vehicleModelInput,
            onValueChange = onModelChanged,
            label = { Text("Modelo") },
            singleLine = true,
            enabled = !uiState.isRegisteringVehicle,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = uiState.vehicleYearInput,
            onValueChange = onYearChanged,
            label = { Text("Año") },
            singleLine = true,
            enabled = !uiState.isRegisteringVehicle,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = uiState.vehicleVinInput,
            onValueChange = onVinChanged,
            label = { Text("VIN (17 caracteres)") },
            singleLine = true,
            enabled = !uiState.isRegisteringVehicle,
            modifier = Modifier.fillMaxWidth()
        )

        uiState.vehicleRegistrationError?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Button(
            onClick = onSubmit,
            enabled = !uiState.isRegisteringVehicle,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (uiState.isRegisteringVehicle) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                Spacer(Modifier.size(8.dp))
                Text("Guardando…")
            } else {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(Modifier.size(6.dp))
                Text(if (uiState.isEditingVehicle) "Guardar cambios" else "Guardar vehículo")
            }
        }
        OutlinedButton(
            onClick = onCancel,
            enabled = !uiState.isRegisteringVehicle,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancelar")
        }
    }
}

@Composable
private fun VehicleCard(
    vehicle: Vehicle,
    latestSnapshot: TelemetrySnapshot?,
    isLoadingTelemetry: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${vehicle.brand} ${vehicle.model} ${vehicle.year}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = vehicle.plateNumber.value,
                        style = MaterialTheme.typography.labelLarge,
                        color = GarageNavy,
                        fontWeight = FontWeight.Bold
                    )
                }
                TelemetryStatusBadge(
                    hasTelemetry = latestSnapshot != null,
                    isLoading = isLoadingTelemetry
                )
            }

            if (!vehicle.photoUrl.isNullOrBlank()) {
                AsyncImage(
                    model = vehicle.photoUrl,
                    contentDescription = "${vehicle.brand} ${vehicle.model}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(138.dp)
                        .clip(RoundedCornerShape(14.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Surface(
                    modifier = Modifier.fillMaxWidth().height(112.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFF1F5F9)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.DirectionsCar,
                            contentDescription = null,
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(44.dp)
                        )
                    }
                }
            }

            if (latestSnapshot != null) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    VehicleMetric(
                        label = "Odómetro",
                        value = "${formatNumber(latestSnapshot.odometerKm)} km",
                        modifier = Modifier.weight(1f)
                    )
                    VehicleMetric(
                        label = "Combustible",
                        value = "${formatNumber(latestSnapshot.fuelLevelPercent)}%",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = latestSnapshot?.let { "Última lectura ${formatTimestamp(it.createdAt)}" }
                        ?: "Ver lecturas y alertas del vehículo",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = "Ver diagnóstico",
                    tint = GarageNavy
                )
            }
        }
    }
}

@Composable
private fun TelemetryStatusBadge(
    hasTelemetry: Boolean,
    isLoading: Boolean
) {
    val label = when {
        isLoading -> "Consultando"
        hasTelemetry -> "Datos recientes"
        else -> "Sin lecturas"
    }
    val tint = if (hasTelemetry) GarageTeal else GarageAmber

    Surface(
        shape = RoundedCornerShape(50),
        color = tint.copy(alpha = 0.12f)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = if (isLoading) MaterialTheme.colorScheme.onSurfaceVariant else tint,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun VehicleMetric(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF8FAFC)
    ) {
        Column(Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun VehicleDetailContent(
    uiState: CustomerVehiclesUiState,
    vehicle: Vehicle,
    onEditVehicle: () -> Unit,
    onRequestDeleteVehicle: () -> Unit,
    onLoadMoreTelemetryHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(Modifier.padding(20.dp)) {
                Text(
                    text = "DIAGNÓSTICO DEL VEHÍCULO",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = GarageNavy
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "${vehicle.brand} ${vehicle.model} ${vehicle.year}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Placa: ${vehicle.plateNumber.value}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onEditVehicle,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Filled.Edit, contentDescription = null)
                Spacer(Modifier.size(6.dp))
                Text("Editar")
            }
            OutlinedButton(
                onClick = onRequestDeleteVehicle,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(Icons.Filled.Delete, contentDescription = null)
                Spacer(Modifier.size(6.dp))
                Text("Eliminar")
            }
        }

        if (uiState.detailsErrorMessage != null) {
            Text(
                text = uiState.detailsErrorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }

        TelemetryCard(
            snapshot = uiState.latestTelemetry,
            isLoading = uiState.isLoadingDetails
        )

        TelemetryHistoryCard(
            history = uiState.telemetryHistory,
            alerts = uiState.dtcAlerts,
            isLoading = uiState.isLoadingDetails,
            hasMore = uiState.hasMoreTelemetryHistory,
            isLoadingMore = uiState.isLoadingMoreTelemetryHistory,
            errorMessage = uiState.telemetryHistoryErrorMessage,
            onLoadMore = onLoadMoreTelemetryHistory
        )

        DtcAlertsCard(
            alerts = uiState.dtcAlerts,
            isLoading = uiState.isLoadingDetails
        )
    }
}

@Composable
private fun TelemetryCard(
    snapshot: TelemetrySnapshot?,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Última lectura",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            when {
                isLoading -> CircularProgressIndicator(modifier = Modifier.size(28.dp))
                snapshot == null -> Text(
                    text = "Aún no hay lecturas de telemetría para este vehículo.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                else -> {
                    Text(
                        text = "Actualizado ${formatTimestamp(snapshot.createdAt)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        MetricTile("Velocidad", "${formatNumber(snapshot.speedKmh)} km/h", Modifier.weight(1f))
                        MetricTile("Temperatura", "${snapshot.temperature} °C", Modifier.weight(1f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        MetricTile("Combustible", "${formatNumber(snapshot.fuelLevelPercent)}%", Modifier.weight(1f))
                        MetricTile("Odómetro", "${formatNumber(snapshot.odometerKm)} km", Modifier.weight(1f))
                    }
                    MetricTile("Motor", "${snapshot.rpm} rpm", Modifier.fillMaxWidth())
                }
            }
        }
    }
}

@Composable
private fun MetricTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFFF8FAFC)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private enum class TelemetryChartMetric(
    val label: String,
    val unit: String,
    val color: Color
) {
    SPEED("Velocidad", "km/h", GarageTeal),
    TEMPERATURE("Temperatura", "°C", GarageOrange),
    RPM("RPM", "rpm", GarageNavy),
    FUEL("Combustible", "%", GarageGreen);

    fun value(snapshot: TelemetrySnapshot): Double = when (this) {
        SPEED -> snapshot.speedKmh
        TEMPERATURE -> snapshot.temperature.toDouble()
        RPM -> snapshot.rpm.toDouble()
        FUEL -> snapshot.fuelLevelPercent
    }
}

@Composable
private fun TelemetryHistoryCard(
    history: List<TelemetrySnapshot>,
    alerts: List<DtcAlert>,
    isLoading: Boolean,
    hasMore: Boolean,
    isLoadingMore: Boolean,
    errorMessage: String?,
    onLoadMore: () -> Unit
) {
    var selectedMetric by remember { mutableStateOf(TelemetryChartMetric.SPEED) }
    val orderedHistory = remember(history) { history.sortedBy { it.createdAt } }
    val alertsBySnapshot = remember(alerts) { alerts.groupBy { it.telemetrySnapshotId } }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Historial de telemetría",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Últimas ${history.size} lecturas recibidas",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            when {
                isLoading && history.isEmpty() -> CircularProgressIndicator(modifier = Modifier.size(28.dp))
                history.isEmpty() -> Text(
                    text = "Aún no hay lecturas anteriores para graficar.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                else -> {
                    val metricRows = TelemetryChartMetric.values().toList().chunked(2)
                    metricRows.forEach { rowMetrics ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowMetrics.forEach { metric ->
                                val isSelected = metric == selectedMetric
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedMetric = metric },
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) metric.color.copy(alpha = 0.12f) else Color(0xFFF8FAFC)
                                ) {
                                    Text(
                                        text = metric.label,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) metric.color else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp)
                                    )
                                }
                            }
                            if (rowMetrics.size == 1) Spacer(Modifier.weight(1f))
                        }
                    }

                    val metricValues = orderedHistory.map(selectedMetric::value)
                    val minimum = metricValues.minOrNull() ?: 0.0
                    val maximum = metricValues.maxOrNull() ?: 0.0
                    Text(
                        text = "Mín. ${formatNumber(minimum)} · Máx. ${formatNumber(maximum)} ${selectedMetric.unit}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    TelemetryTrendGraph(
                        history = orderedHistory,
                        metric = selectedMetric,
                        alertSnapshotIds = alertsBySnapshot.keys
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${formatTimestamp(orderedHistory.first().createdAt)} · antigua",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${formatTimestamp(orderedHistory.last().createdAt)} · reciente",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("●", color = selectedMetric.color)
                        Text("Lectura", style = MaterialTheme.typography.labelSmall)
                        Text("◎", color = GarageOrange)
                        Text("Lectura con alerta DTC", style = MaterialTheme.typography.labelSmall)
                    }

                    androidx.compose.material3.HorizontalDivider(color = Color(0xFFE2E8F0))
                    Text(
                        text = "Detalle de lecturas",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    orderedHistory.asReversed().forEachIndexed { index, snapshot ->
                        if (index > 0) androidx.compose.material3.HorizontalDivider(color = Color(0xFFF1F5F9))
                        TelemetryHistoryRow(
                            snapshot = snapshot,
                            alerts = alertsBySnapshot[snapshot.id].orEmpty()
                        )
                    }

                    errorMessage?.let { message ->
                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    if (isLoadingMore) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                            Spacer(Modifier.size(8.dp))
                            Text("Cargando lecturas…", style = MaterialTheme.typography.bodySmall)
                        }
                    } else if (hasMore) {
                        OutlinedButton(
                            onClick = onLoadMore,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Cargar más lecturas")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TelemetryTrendGraph(
    history: List<TelemetrySnapshot>,
    metric: TelemetryChartMetric,
    alertSnapshotIds: Set<com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.SnapshotId>
) {
    val values = history.map(metric::value)
    val minimum = values.minOrNull() ?: 0.0
    val maximum = values.maxOrNull() ?: 0.0
    val valueRange = maximum - minimum

    Canvas(modifier = Modifier.fillMaxWidth().height(150.dp)) {
        val left = 8.dp.toPx()
        val right = size.width - 8.dp.toPx()
        val top = 10.dp.toPx()
        val bottom = size.height - 10.dp.toPx()
        val chartHeight = bottom - top

        for (lineIndex in 0..3) {
            val y = top + chartHeight * lineIndex / 3f
            drawLine(
                color = Color(0xFFE2E8F0),
                start = Offset(left, y),
                end = Offset(right, y),
                strokeWidth = 1.dp.toPx()
            )
        }

        val points = history.mapIndexed { index, snapshot ->
            val xFraction = if (history.size == 1) 0.5f else index.toFloat() / (history.size - 1)
            val yFraction = if (valueRange == 0.0) 0.5f else ((metric.value(snapshot) - minimum) / valueRange).toFloat()
            Offset(
                x = left + (right - left) * xFraction,
                y = bottom - chartHeight * yFraction
            )
        }

        if (points.size > 1) {
            val path = Path().apply {
                moveTo(points.first().x, points.first().y)
                points.drop(1).forEach { lineTo(it.x, it.y) }
            }
            drawPath(path, color = metric.color, style = Stroke(width = 3.dp.toPx()))
        }

        points.forEachIndexed { index, point ->
            drawCircle(color = metric.color, radius = 4.dp.toPx(), center = point)
            if (history[index].id in alertSnapshotIds) {
                drawCircle(
                    color = GarageOrange,
                    radius = 8.dp.toPx(),
                    center = point,
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }
    }
}

@Composable
private fun TelemetryHistoryRow(
    snapshot: TelemetrySnapshot,
    alerts: List<DtcAlert>
) {
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Text(
            text = formatTimestamp(snapshot.createdAt),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "${snapshot.rpm} rpm · ${snapshot.temperature} °C · ${formatNumber(snapshot.speedKmh)} km/h · ${formatNumber(snapshot.fuelLevelPercent)}% combustible · ${formatNumber(snapshot.odometerKm)} km",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        alerts.forEach { alert ->
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = GarageOrange.copy(alpha = 0.10f)
            ) {
                Text(
                    text = "DTC ${alert.dtcCode} · ${alert.severity.spanishLabel()} · ${alert.description}",
                    style = MaterialTheme.typography.labelSmall,
                    color = GarageOrange,
                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun DtcAlertsCard(
    alerts: List<DtcAlert>,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.WarningAmber,
                    contentDescription = null,
                    tint = GarageAmber
                )
                Text(
                    text = "Alertas del vehículo",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            when {
                isLoading -> CircularProgressIndicator(modifier = Modifier.size(28.dp))
                alerts.isEmpty() -> Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(shape = RoundedCornerShape(50), color = GarageGreen.copy(alpha = 0.12f)) {
                        Text(
                            text = "Sin alertas reportadas",
                            style = MaterialTheme.typography.labelMedium,
                            color = GarageGreen,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
                else -> alerts.forEach { alert -> DtcAlertRow(alert) }
            }
        }
    }
}

@Composable
private fun DtcAlertRow(alert: DtcAlert) {
    val severityColor = when (alert.severity) {
        DtcSeverity.CRITICAL -> MaterialTheme.colorScheme.error
        DtcSeverity.HIGH -> GarageOrange
        DtcSeverity.MEDIUM -> GarageAmber
        DtcSeverity.LOW -> Color(0xFF60A5FA)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = alert.dtcCode,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Surface(
                shape = RoundedCornerShape(50),
                color = severityColor.copy(alpha = 0.12f)
            ) {
                Text(
                text = alert.severity.spanishLabel(),
                    style = MaterialTheme.typography.labelSmall,
                    color = severityColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }
        Text(text = alert.description, style = MaterialTheme.typography.bodyMedium)
        Text(
            text = formatTimestamp(alert.createdAt),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun LoadingContent() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun MessageContent(
    message: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (actionLabel != null && onAction != null) {
            OutlinedButton(onClick = onAction) {
                Text(actionLabel)
            }
        }
    }
}

private fun formatNumber(value: Double): String = NumberFormat
    .getNumberInstance(Locale.getDefault())
    .apply { maximumFractionDigits = 1 }
    .format(value)

private fun formatTimestamp(value: String): String = value.replace('T', ' ').take(16)

private fun DtcSeverity.spanishLabel(): String = when (this) {
    DtcSeverity.LOW -> "Baja"
    DtcSeverity.MEDIUM -> "Media"
    DtcSeverity.HIGH -> "Alta"
    DtcSeverity.CRITICAL -> "Crítica"
}

@Preview(showBackground = true)
@Composable
private fun CustomerVehiclesScreenPreview() {
    ShiftIQTheme {
        VehicleListContent(
            uiState = CustomerVehiclesUiState(isLoading = false),
            onVehicleClick = {},
            onRefresh = {},
            onAddVehicle = {}
        )
    }
}
