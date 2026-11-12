package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.home

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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Navy900
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQSuccess
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQTheme
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.spacing
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.B2CVehicleItem
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.DriverLiveTelemetry
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.DriverVehicleHealth
import com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.home.components.ActiveServiceCard
import com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.home.components.DtcAlertBanner
import com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.home.components.HealthRingCanvas
import com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.home.components.LiveTelemetryGrid

@Composable
fun DriverHomeRoute(
    onNavigateToWorkshops: () -> Unit,
    onNavigateToDiagnostic: () -> Unit,
    onNavigateToGarage: () -> Unit,
    onOpenDrawer: () -> Unit,
    onViewLiveOrder: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: DriverHomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    DriverHomeScreen(
        uiState = uiState,
        onNavigateToWorkshops = onNavigateToWorkshops,
        onNavigateToDiagnostic = onNavigateToDiagnostic,
        onNavigateToGarage = onNavigateToGarage,
        onOpenDrawer = onOpenDrawer,
        onViewLiveOrder = onViewLiveOrder,
        onToggleVehicleSelector = viewModel::toggleVehicleSelector,
        onSelectVehicle = viewModel::onSelectVehicle,
        onStartFullScan = viewModel::onStartFullScan,
        modifier = modifier
    )
}

@Composable
fun DriverHomeScreen(
    uiState: DriverHomeUiState,
    onNavigateToWorkshops: () -> Unit,
    onNavigateToDiagnostic: () -> Unit,
    onNavigateToGarage: () -> Unit,
    onOpenDrawer: () -> Unit,
    onToggleVehicleSelector: () -> Unit,
    onSelectVehicle: (B2CVehicleItem) -> Unit,
    onStartFullScan: () -> Unit,
    onViewLiveOrder: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        DriverHomeTopBar(
            avatarInitial = uiState.avatarInitial,
            isObdConnected = uiState.isObdConnected,
            onOpenDrawer = onOpenDrawer
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = spacing.medium, vertical = spacing.smallMedium),
            verticalArrangement = Arrangement.spacedBy(spacing.smallMedium)
        ) {
            DriverGreetingCard(
                driverName = uiState.driverName,
                driverRole = uiState.driverRole,
                avatarInitial = uiState.avatarInitial,
                isObdConnected = uiState.isObdConnected
            )

            ActiveServiceCard(
                serviceData = uiState.activeService,
                onViewLiveOrder = onViewLiveOrder,
                onNavigateToWorkshops = onNavigateToWorkshops
            )

            VehicleHealthCard(
                selectedVehicle = uiState.selectedVehicle,
                availableVehicles = uiState.availableVehicles,
                vehicleHealth = uiState.vehicleHealth,
                isSelectorOpen = uiState.isVehicleSelectorOpen,
                onToggleSelector = onToggleVehicleSelector,
                onSelectVehicle = onSelectVehicle
            )

            LiveTelemetryGrid(telemetry = uiState.liveTelemetry)

            DtcAlertBanner(
                alert = uiState.activeDtcAlert,
                onViewDiagnosticDetail = onNavigateToDiagnostic
            )

            FullScanButton(onClick = onStartFullScan)

            Spacer(modifier = Modifier.height(spacing.medium))
        }
    }

    if (uiState.isScanning) {
        ScanProgressDialog(message = uiState.scanProgressMessage)
    }
}

@Composable
private fun DriverHomeTopBar(
    avatarInitial: String,
    isObdConnected: Boolean,
    onOpenDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(
                top = statusBarPadding,
                start = spacing.medium,
                end = spacing.medium,
                bottom = spacing.smallMedium
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ObdStatusChip(isConnected = isObdConnected)
            Text(
                text = "Home Dashboard",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
            IconButton(
                onClick = onOpenDrawer,
                modifier = Modifier
                    .size(spacing.badgeHeight + spacing.smallMedium)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.20f))
            ) {
                Text(
                    text = avatarInitial,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
private fun ObdStatusChip(
    isConnected: Boolean,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(spacing.radiusFull))
            .background(Navy900)
            .padding(horizontal = spacing.smallMedium, vertical = spacing.compact),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(spacing.indicatorHeight)
                .clip(CircleShape)
                .background(if (isConnected) ShiftIQSuccess else MaterialTheme.colorScheme.error)
        )
        Spacer(modifier = Modifier.width(spacing.compact))
        Text(
            text = if (isConnected) "OBD2 CONECTADO" else "OBD2 DESCONECTADO",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
private fun DriverGreetingCard(
    driverName: String,
    driverRole: String,
    avatarInitial: String,
    isObdConnected: Boolean,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(spacing.radiusLarge),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(spacing.strokeThin, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = spacing.elevationSmall)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.medium),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DriverGreetingInfo(
                driverName = driverName,
                driverRole = driverRole,
                avatarInitial = avatarInitial
            )
            DriverObdBadge(isConnected = isObdConnected)
        }
    }
}

@Composable
private fun DriverGreetingInfo(
    driverName: String,
    driverRole: String,
    avatarInitial: String,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(spacing.avatarMedium)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = avatarInitial,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.width(spacing.smallMedium))
        Column {
            Text(
                text = "¡Hola, $driverName!",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(spacing.micro))
            Text(
                text = driverRole,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DriverObdBadge(
    isConnected: Boolean,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(spacing.radiusMedium))
            .background(ShiftIQSuccess.copy(alpha = 0.12f))
            .padding(horizontal = spacing.smallMedium, vertical = spacing.extraSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(spacing.indicatorHeight)
                .clip(CircleShape)
                .background(if (isConnected) ShiftIQSuccess else MaterialTheme.colorScheme.error)
        )
        Spacer(modifier = Modifier.width(spacing.compact))
        Text(
            text = if (isConnected) "OBD-II Conectado" else "Desconectado",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = if (isConnected) ShiftIQSuccess else MaterialTheme.colorScheme.error
        )
    }
}

@Composable
private fun VehicleHealthCard(
    selectedVehicle: B2CVehicleItem?,
    availableVehicles: List<B2CVehicleItem>,
    vehicleHealth: DriverVehicleHealth,
    isSelectorOpen: Boolean,
    onToggleSelector: () -> Unit,
    onSelectVehicle: (B2CVehicleItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(spacing.radiusLarge),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(spacing.strokeThin, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = spacing.elevationSmall)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.medium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            VehicleSelectorDropdown(
                selectedVehicle = selectedVehicle,
                availableVehicles = availableVehicles,
                isOpen = isSelectorOpen,
                onToggle = onToggleSelector,
                onSelect = onSelectVehicle
            )
            Spacer(modifier = Modifier.height(spacing.smallMedium))
            HealthRingCanvas(score = vehicleHealth.score)
            Spacer(modifier = Modifier.height(spacing.smallMedium))
            Text(
                text = vehicleHealth.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(spacing.micro))
            Text(
                text = vehicleHealth.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun VehicleSelectorDropdown(
    selectedVehicle: B2CVehicleItem?,
    availableVehicles: List<B2CVehicleItem>,
    isOpen: Boolean,
    onToggle: () -> Unit,
    onSelect: (B2CVehicleItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    val vehicleTitle = selectedVehicle?.let {
        "${it.brand} ${it.model} ${it.year} • Placa ${it.plateNumber}"
    } ?: "Kia Rio 2019 • Placa ABC-123"

    Box(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggle)
                .padding(vertical = spacing.extraSmall),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.DirectionsCar,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(spacing.iconLarge)
                )
                Spacer(modifier = Modifier.width(spacing.small))
                Text(
                    text = vehicleTitle,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Cambiar",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(spacing.iconLarge)
                )
            }
        }

        DropdownMenu(expanded = isOpen, onDismissRequest = onToggle) {
            availableVehicles.forEach { vehicle ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "${vehicle.brand} ${vehicle.model} (${vehicle.plateNumber})",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (vehicle.id == selectedVehicle?.id) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    onClick = { onSelect(vehicle) },
                    trailingIcon = {
                        if (vehicle.id == selectedVehicle?.id) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun FullScanButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(spacing.buttonHeight + spacing.small),
        shape = RoundedCornerShape(spacing.radiusLarge),
        colors = ButtonDefaults.buttonColors(
            containerColor = Navy900,
            contentColor = Color.White
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = spacing.elevationSmall)
    ) {
        Icon(
            imageVector = Icons.Default.QrCodeScanner,
            contentDescription = null,
            modifier = Modifier.size(spacing.iconLarge)
        )
        Spacer(modifier = Modifier.width(spacing.smallMedium))
        Text(
            text = "Iniciar Escaneo Completo",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ScanProgressDialog(
    message: String?,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Dialog(onDismissRequest = {}) {
        Card(
            modifier = modifier,
            shape = RoundedCornerShape(spacing.radiusLarge),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = spacing.elevationMedium)
        ) {
            Column(
                modifier = Modifier.padding(spacing.large),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(spacing.extraExtraLarge)
                )
                Spacer(modifier = Modifier.height(spacing.medium))
                Text(
                    text = "Escaneo OBD-II en Curso",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(spacing.small))
                Text(
                    text = message ?: "Consultando sensores ECU...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@PreviewLightDark
@PreviewFontScale
@Composable
private fun DriverHomeScreenPreview() {
    ShiftIQTheme {
        DriverHomeScreen(
            uiState = DriverHomeUiState(
                driverName = "Carlos Mendoza",
                selectedVehicle = B2CVehicleItem(
                    id = "v-1",
                    brand = "Kia",
                    model = "Rio",
                    year = 2019,
                    plateNumber = "ABC-123",
                    vin = "KNADE1234567890",
                    isMonitored = true
                ),
                vehicleHealth = DriverVehicleHealth(
                    score = 94,
                    title = "Vehículo en Óptimas Condiciones",
                    description = "Motor, frenos y transmisión operando con métricas normales."
                ),
                liveTelemetry = DriverLiveTelemetry(
                    rpm = 840,
                    temperatureCelsius = 89,
                    batteryVoltage = 13.8,
                    odometerKm = 45210
                )
            ),
            onNavigateToWorkshops = {},
            onNavigateToDiagnostic = {},
            onNavigateToGarage = {},
            onOpenDrawer = {},
            onToggleVehicleSelector = {},
            onSelectVehicle = {},
            onStartFullScan = {}
        )
    }
}
