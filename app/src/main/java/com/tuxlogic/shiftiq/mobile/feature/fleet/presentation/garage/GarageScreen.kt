package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.garage

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.hilt.navigation.compose.hiltViewModel
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.CanBusCyan
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Navy900
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ObdTeal
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQSuccess
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQTheme
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQWarning
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate50
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate100
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate200
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate300
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate400
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate500
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate700
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.spacing
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.B2CVehicleItem
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.MaintenanceHistoryItem
import com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.garage.components.ObdPairingBottomSheet

@Composable
fun MyGarageRoute(
    onAddVehicle: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToExternalServiceRegistration: () -> Unit = {},
    onNavigateToInterventionDetail: (String) -> Unit = {},
    onNavigateToVoucherPdf: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: GarageViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    MyGarageScreen(
        uiState = uiState,
        onAddVehicle = onAddVehicle,
        onNavigateToProfile = onNavigateToProfile,
        onNavigateToExternalServiceRegistration = onNavigateToExternalServiceRegistration,
        onNavigateToInterventionDetail = onNavigateToInterventionDetail,
        onNavigateToVoucherPdf = onNavigateToVoucherPdf,
        modifier = modifier
    )
}

@Composable
fun GarageScreen(
    onAddVehicle: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToExternalServiceRegistration: () -> Unit = {},
    onNavigateToInterventionDetail: (String) -> Unit = {},
    onNavigateToVoucherPdf: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: GarageViewModel = hiltViewModel()
) {
    MyGarageRoute(
        onAddVehicle = onAddVehicle,
        onNavigateToProfile = onNavigateToProfile,
        onNavigateToExternalServiceRegistration = onNavigateToExternalServiceRegistration,
        onNavigateToInterventionDetail = onNavigateToInterventionDetail,
        onNavigateToVoucherPdf = onNavigateToVoucherPdf,
        modifier = modifier,
        viewModel = viewModel
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyGarageScreen(
    uiState: GarageUiState,
    onAddVehicle: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToExternalServiceRegistration: () -> Unit,
    onNavigateToInterventionDetail: (String) -> Unit,
    onNavigateToVoucherPdf: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showPairingSheet by rememberSaveable { mutableStateOf(false) }
    val spacing = MaterialTheme.spacing
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        GarageTopBar(
            onNavigateToProfile = onNavigateToProfile,
            statusBarPadding = statusBarPadding
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = spacing.medium, vertical = spacing.smallMedium),
            verticalArrangement = Arrangement.spacedBy(spacing.medium)
        ) {
            PersonalFleetHeader(onAddVehicle = onAddVehicle)

            uiState.vehicles.forEach { vehicle ->
                VehicleFleetCard(
                    vehicle = vehicle,
                    onPairObd = { showPairingSheet = true }
                )
            }

            RealTimeTelemetryHeader()
            GarageTelemetryKpiGrid(
                odometerKm = uiState.currentOdometerKm,
                nextServiceKm = uiState.nextServiceKm,
                batteryVoltage = uiState.batteryHealthVoltage,
                oilLifePercent = uiState.engineOilLifePercent
            )

            MaintenanceHistorySection(
                items = uiState.maintenanceHistory,
                onNavigateToInterventionDetail = onNavigateToInterventionDetail,
                onNavigateToVoucherPdf = onNavigateToVoucherPdf
            )

            OutsideNetworkServiceBanner(
                onAddExternalService = onNavigateToExternalServiceRegistration
            )
            Spacer(modifier = Modifier.height(spacing.medium))
        }
    }

    if (showPairingSheet) {
        ObdPairingBottomSheet(
            onDismissRequest = { showPairingSheet = false },
            onDeviceConnected = { showPairingSheet = false }
        )
    }
}

@Composable
private fun GarageTopBar(
    onNavigateToProfile: () -> Unit,
    statusBarPadding: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Navy900)
            .padding(
                horizontal = spacing.medium,
                vertical = spacing.medium
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Mi Garaje - Historial",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        IconButton(
            onClick = onNavigateToProfile,
            modifier = Modifier
                .size(spacing.badgeHeight + spacing.smallMedium)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.15f))
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Mi Perfil",
                tint = Color.White,
                modifier = Modifier.size(spacing.iconLarge)
            )
        }
    }
}

@Composable
private fun PersonalFleetHeader(
    onAddVehicle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "FLOTA PERSONAL",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Vehículos Registrados",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        OutlinedButton(
            onClick = onAddVehicle,
            shape = RoundedCornerShape(spacing.radiusFull),
            border = BorderStroke(spacing.strokeThin, MaterialTheme.colorScheme.primary)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(spacing.iconMedium)
            )
            Spacer(modifier = Modifier.width(spacing.extraSmall))
            Text(
                text = "Agregar",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun VehicleFleetCard(
    vehicle: B2CVehicleItem,
    onPairObd: () -> Unit,
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
                .padding(spacing.medium)
        ) {
            VehicleCardTopRow(vehicle = vehicle)
            Spacer(modifier = Modifier.height(spacing.small))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${vehicle.brand} ${vehicle.model} ${vehicle.year}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                PlateChip(plate = vehicle.plateNumber)
            }
            Spacer(modifier = Modifier.height(spacing.small))
            VehicleSpecBox(vehicle = vehicle)
            Spacer(modifier = Modifier.height(spacing.smallMedium))
            VehicleCardFooterRow(vehicle = vehicle, onPairObd = onPairObd)
        }
    }
}

@Composable
private fun PlateChip(plate: String, modifier: Modifier = Modifier) {
    val spacing = MaterialTheme.spacing
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(spacing.radiusExtraSmall))
            .background(Slate100)
            .border(spacing.strokeThin, Slate200, RoundedCornerShape(spacing.radiusExtraSmall))
            .padding(horizontal = spacing.small, vertical = spacing.extraSmall)
    ) {
        Text(
            text = plate,
            style = MaterialTheme.typography.labelSmall,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = Slate700
        )
    }
}

@Composable
private fun VehicleSpecBox(vehicle: B2CVehicleItem, modifier: Modifier = Modifier) {
    val spacing = MaterialTheme.spacing
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(spacing.radiusMedium))
            .background(Slate100.copy(alpha = 0.5f))
            .border(spacing.strokeThin, Slate200.copy(alpha = 0.6f), RoundedCornerShape(spacing.radiusMedium))
            .padding(horizontal = spacing.medium, vertical = spacing.smallMedium)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "IDENTIFICADOR VIN",
                    style = MaterialTheme.typography.labelSmall,
                    color = Slate400,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = vehicle.vin,
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold,
                    color = Slate700
                )
            }
            Icon(
                imageVector = Icons.Default.DirectionsCar,
                contentDescription = null,
                tint = Slate400,
                modifier = Modifier.size(spacing.iconExtraLarge)
            )
        }
    }
}

@Composable
private fun VehicleCardTopRow(
    vehicle: B2CVehicleItem,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (vehicle.isMonitored) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(spacing.radiusFull))
                    .background(ShiftIQSuccess.copy(alpha = 0.12f))
                    .border(spacing.strokeThin, ShiftIQSuccess.copy(alpha = 0.3f), RoundedCornerShape(spacing.radiusFull))
                    .padding(horizontal = spacing.small, vertical = spacing.extraSmall),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(spacing.indicatorHeight)
                        .clip(CircleShape)
                        .background(ShiftIQSuccess)
                )
                Spacer(modifier = Modifier.width(spacing.extraSmall))
                Text(
                    text = "Monitoreo Activo",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = ShiftIQSuccess
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(spacing.radiusFull))
                    .background(ShiftIQWarning.copy(alpha = 0.12f))
                    .border(spacing.strokeThin, ShiftIQWarning.copy(alpha = 0.3f), RoundedCornerShape(spacing.radiusFull))
                    .padding(horizontal = spacing.small, vertical = spacing.extraSmall),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = ShiftIQWarning,
                    modifier = Modifier.size(spacing.iconSmall)
                )
                Spacer(modifier = Modifier.width(spacing.extraSmall))
                Text(
                    text = "Sin Dispositivo",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = ShiftIQWarning
                )
            }
        }

        if (vehicle.isPrimary) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(spacing.radiusExtraSmall))
                    .background(Slate100)
                    .padding(horizontal = spacing.small, vertical = spacing.extraSmall)
            ) {
                Text(
                    text = "Ppal",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    color = Slate500
                )
            }
        }
    }
}

@Composable
private fun VehicleCardFooterRow(
    vehicle: B2CVehicleItem,
    onPairObd: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    if (vehicle.isMonitored) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(spacing.radiusSmall))
                .background(Slate50)
                .padding(horizontal = spacing.medium, vertical = spacing.small),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Wifi,
                    contentDescription = null,
                    tint = ObdTeal,
                    modifier = Modifier.size(spacing.iconMedium)
                )
                Spacer(modifier = Modifier.width(spacing.compact))
                Text(
                    text = "OBD2 Enlazado",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = ObdTeal
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(spacing.radiusExtraSmall))
                    .background(Color.White)
                    .border(spacing.strokeThin, Slate200, RoundedCornerShape(spacing.radiusExtraSmall))
                    .padding(horizontal = spacing.small, vertical = spacing.micro)
            ) {
                Text(
                    text = "${vehicle.batteryVoltage}V",
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = Slate700
                )
            }
        }
    } else {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = onPairObd,
                shape = RoundedCornerShape(spacing.radiusMedium),
                colors = ButtonDefaults.buttonColors(containerColor = Slate100, contentColor = Slate700),
                border = BorderStroke(spacing.strokeThin, Slate300),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Bluetooth,
                    contentDescription = null,
                    modifier = Modifier.size(spacing.iconMedium),
                    tint = Slate700
                )
                Spacer(modifier = Modifier.width(spacing.small))
                Text(
                    text = "Vincular OBD2",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Slate700
                )
            }
        }
    }
}

@Composable
private fun RealTimeTelemetryHeader(modifier: Modifier = Modifier) {
    val spacing = MaterialTheme.spacing
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Tune,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(spacing.iconMedium)
            )
            Spacer(modifier = Modifier.width(spacing.compact))
            Text(
                text = "Telemetría en Tiempo Real",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text = "Sync: Hace 3m",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun GarageTelemetryKpiGrid(
    odometerKm: Int?,
    nextServiceKm: Int?,
    batteryVoltage: Double?,
    oilLifePercent: Int?,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.smallMedium)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.smallMedium)
        ) {
            val formattedOdo = odometerKm?.let { String.format(java.util.Locale.US, "%,d", it) } ?: "—"
            val formattedNext = nextServiceKm?.let { String.format(java.util.Locale.US, "%,d", it) } ?: "—"
            GarageMetricCard(
                title = "Odómetro Actual",
                value = formattedOdo,
                unit = "km",
                subtitle = if (odometerKm == null) "Sin lectura disponible" else "Última lectura recibida",
                subtitleColor = ShiftIQSuccess,
                icon = Icons.Default.Speed,
                modifier = Modifier.weight(1f)
            )
            GarageMetricCard(
                title = "Próx. Servicio",
                value = formattedNext,
                unit = "km",
                subtitle = if (nextServiceKm == null) "Sin dato disponible" else "Intervalo informado",
                subtitleColor = MaterialTheme.colorScheme.onSurfaceVariant,
                icon = Icons.Default.CalendarToday,
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.smallMedium)
        ) {
            val formattedBat = batteryVoltage?.let { String.format(java.util.Locale.US, "%.1f", it) } ?: "—"
            GarageMetricCard(
                title = "Salud Batería",
                value = formattedBat,
                unit = "V",
                subtitle = if (batteryVoltage == null) "Sin lectura disponible" else "Última lectura recibida",
                subtitleColor = ShiftIQSuccess,
                icon = Icons.Default.ElectricBolt,
                modifier = Modifier.weight(1f)
            )
            GarageMetricCard(
                title = "Aceite Motor",
                value = oilLifePercent?.let { "$it%" } ?: "—",
                unit = "vida",
                subtitle = if (oilLifePercent == null) "Sin dato disponible" else "Dato del vehículo",
                subtitleColor = MaterialTheme.colorScheme.onSurfaceVariant,
                icon = Icons.Default.Opacity,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun GarageMetricCard(
    title: String,
    value: String,
    unit: String,
    subtitle: String,
    subtitleColor: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Card(
        modifier = modifier,
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
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(spacing.iconMedium)
                )
            }
            Spacer(modifier = Modifier.height(spacing.small))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(spacing.extraSmall))
                Text(
                    text = unit,
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(spacing.extraSmall))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = subtitleColor
            )
        }
    }
}

@Composable
private fun MaintenanceHistorySection(
    items: List<MaintenanceHistoryItem>,
    onNavigateToInterventionDetail: (String) -> Unit,
    onNavigateToVoucherPdf: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Historial de Intervenciones",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Registros certificados y facturas",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            YearFilterDropdown()
        }

        items.forEachIndexed { index, item ->
            TimelineEventItem(
                item = item,
                isPrimaryPin = index == 0,
                onNavigateToInterventionDetail = onNavigateToInterventionDetail,
                onNavigateToVoucherPdf = onNavigateToVoucherPdf
            )
        }
    }
}

@Composable
private fun YearFilterDropdown(modifier: Modifier = Modifier) {
    val spacing = MaterialTheme.spacing
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(spacing.radiusSmall))
            .background(Color.White)
            .border(spacing.strokeThin, Slate300, RoundedCornerShape(spacing.radiusSmall))
            .padding(horizontal = spacing.smallMedium, vertical = spacing.compact),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Todos los años",
            style = MaterialTheme.typography.labelMedium,
            color = Slate700
        )
        Spacer(modifier = Modifier.width(spacing.extraSmall))
        Icon(
            imageVector = Icons.Default.ExpandMore,
            contentDescription = null,
            tint = Slate500,
            modifier = Modifier.size(spacing.iconMedium)
        )
    }
}

@Composable
private fun TimelineEventItem(
    item: MaintenanceHistoryItem,
    isPrimaryPin: Boolean,
    onNavigateToInterventionDetail: (String) -> Unit,
    onNavigateToVoucherPdf: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing.smallMedium)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = spacing.small)
        ) {
            Box(
                modifier = Modifier
                    .size(spacing.timelinePinSize)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(
                        spacing.strokeMedium,
                        if (isPrimaryPin) Navy900 else ObdTeal,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(spacing.small)
                        .clip(CircleShape)
                        .background(if (isPrimaryPin) Navy900 else ObdTeal)
                )
            }
            Box(
                modifier = Modifier
                    .width(spacing.strokeMedium)
                    .height(spacing.vehicleCardHeight)
                    .background(Slate300)
            )
        }

        MaintenanceTimelineCard(
            item = item,
            onNavigateToInterventionDetail = onNavigateToInterventionDetail,
            onNavigateToVoucherPdf = onNavigateToVoucherPdf,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun MaintenanceTimelineCard(
    item: MaintenanceHistoryItem,
    onNavigateToInterventionDetail: (String) -> Unit,
    onNavigateToVoucherPdf: (String) -> Unit,
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
                .padding(spacing.medium),
            verticalArrangement = Arrangement.spacedBy(spacing.small)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.date,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "${item.mileage} km",
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${item.workshopName} • ${item.description}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            HorizontalDivider(color = Slate100, thickness = spacing.strokeThin)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Total Invertido",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "S/ ${String.format(java.util.Locale.US, "%.2f", item.totalCostPen)} PEN",
                        style = MaterialTheme.typography.labelMedium,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (item.hasPdfVoucher) {
                    Button(
                        onClick = { onNavigateToVoucherPdf(item.voucherId ?: item.id) },
                        shape = RoundedCornerShape(spacing.radiusSmall),
                        colors = ButtonDefaults.buttonColors(containerColor = Slate100, contentColor = Slate700)
                    ) {
                        Text("PDF Comprobante", style = MaterialTheme.typography.labelSmall)
                        Spacer(modifier = Modifier.width(spacing.extraSmall))
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = null,
                            modifier = Modifier.size(spacing.iconSmall)
                        )
                    }
                } else {
                    Button(
                        onClick = { onNavigateToInterventionDetail(item.id) },
                        shape = RoundedCornerShape(spacing.radiusSmall),
                        colors = ButtonDefaults.buttonColors(containerColor = Slate100, contentColor = Navy900)
                    ) {
                        Text("Ver Detalle", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(spacing.extraSmall))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(spacing.iconSmall)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OutsideNetworkServiceBanner(
    onAddExternalService: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(spacing.radiusLarge),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(spacing.strokeThin, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.medium),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "¿Realizaste un servicio fuera de red?",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Registra factura y kilometraje manual",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(spacing.small))
            Button(
                onClick = onAddExternalService,
                shape = RoundedCornerShape(spacing.radiusMedium),
                colors = ButtonDefaults.buttonColors(containerColor = Navy900, contentColor = Color.White)
            ) {
                Text("Añadir", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@PreviewLightDark
@PreviewFontScale
@Composable
private fun MyGarageScreenPreview() {
    ShiftIQTheme {
        MyGarageScreen(
            uiState = GarageUiState(
                vehicles = listOf(
                    B2CVehicleItem(
                        id = "v-1",
                        brand = "Kia",
                        model = "Rio",
                        year = 2019,
                        plateNumber = "ABC-123",
                        vin = "KNADE1234567890",
                        isMonitored = true,
                        batteryVoltage = 13.8
                    )
                ),
                currentOdometerKm = 48120,
                nextServiceKm = 50000,
                batteryHealthVoltage = 13.8,
                engineOilLifePercent = 78,
                maintenanceHistory = listOf(
                    MaintenanceHistoryItem(
                        id = "h-1",
                        date = "15 FEB 2026",
                        title = "Mantenimiento Preventivo 45,000 km",
                        mileage = 45102,
                        workshopName = "Taller Automotriz Silva",
                        description = "Cambio de aceite sintético 5W-30 y filtros.",
                        totalCostPen = 280.0,
                        hasPdfVoucher = true
                    )
                )
            ),
            onAddVehicle = {},
            onNavigateToProfile = {},
            onNavigateToExternalServiceRegistration = {},
            onNavigateToInterventionDetail = {},
            onNavigateToVoucherPdf = {}
        )
    }
}
