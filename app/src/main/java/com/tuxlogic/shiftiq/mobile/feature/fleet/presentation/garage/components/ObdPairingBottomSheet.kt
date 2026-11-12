package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.garage.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BluetoothSearching
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.CanBusCyan
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Navy900
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQSuccess
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQTheme
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.spacing

data class DiscoveredObdDevice(
    val id: String,
    val name: String,
    val macAddress: String,
    val signalStrength: String,
    val isRecommended: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ObdPairingBottomSheet(
    onDismissRequest: () -> Unit,
    onDeviceConnected: (DiscoveredObdDevice) -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    val sampleDevices = listOf(
        DiscoveredObdDevice(
            id = "obd-1",
            name = "OBD2-BT-9821",
            macAddress = "00:1D:A5:68:98:21",
            signalStrength = "Excelente (-52 dBm)",
            isRecommended = true
        ),
        DiscoveredObdDevice(
            id = "obd-2",
            name = "OBDII-Generic-ELM327",
            macAddress = "11:22:33:44:55:66",
            signalStrength = "Media (-74 dBm)",
            isRecommended = false
        )
    )

    var selectedDeviceId by rememberSaveable { mutableStateOf(sampleDevices.first().id) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier
    ) {
        ObdPairingContent(
            devices = sampleDevices,
            selectedDeviceId = selectedDeviceId,
            onSelectDevice = { selectedDeviceId = it },
            onConnect = {
                val selected = sampleDevices.firstOrNull { it.id == selectedDeviceId }
                if (selected != null) onDeviceConnected(selected)
            },
            onRescan = {}
        )
    }
}

@Composable
private fun ObdPairingContent(
    devices: List<DiscoveredObdDevice>,
    selectedDeviceId: String,
    onSelectDevice: (String) -> Unit,
    onConnect: () -> Unit,
    onRescan: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.medium, vertical = spacing.smallMedium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ObdScannerPulseHeader()
        Spacer(modifier = Modifier.height(spacing.smallMedium))
        ObdProtocolTag()
        Spacer(modifier = Modifier.height(spacing.medium))
        ObdDevicesList(
            devices = devices,
            selectedDeviceId = selectedDeviceId,
            onSelectDevice = onSelectDevice
        )
        Spacer(modifier = Modifier.height(spacing.medium))
        ObdSafetyNotice()
        Spacer(modifier = Modifier.height(spacing.medium))
        ObdActionButtons(onConnect = onConnect, onRescan = onRescan)
        Spacer(modifier = Modifier.height(spacing.large))
    }
}

@Composable
private fun ObdScannerPulseHeader(modifier: Modifier = Modifier) {
    val spacing = MaterialTheme.spacing
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(spacing.gaugeSmall / 2)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.BluetoothSearching,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(spacing.iconExtraLarge)
            )
        }
        Spacer(modifier = Modifier.height(spacing.small))
        Text(
            text = "Buscando Dispositivos OBD-II",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(spacing.micro))
        Text(
            text = "Asegúrate de que el adaptador BLE esté encendido",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ObdProtocolTag(modifier: Modifier = Modifier) {
    val spacing = MaterialTheme.spacing
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(spacing.radiusFull))
            .background(CanBusCyan.copy(alpha = 0.12f))
            .padding(horizontal = spacing.smallMedium, vertical = spacing.extraSmall)
    ) {
        Text(
            text = "Filtro activo: ISO 15765-4 (CAN-BUS)",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = CanBusCyan
        )
    }
}

@Composable
private fun ObdDevicesList(
    devices: List<DiscoveredObdDevice>,
    selectedDeviceId: String,
    onSelectDevice: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        devices.forEach { device ->
            ObdDeviceCard(
                device = device,
                isSelected = device.id == selectedDeviceId,
                onSelect = { onSelectDevice(device.id) }
            )
        }
    }
}

@Composable
private fun ObdDeviceCard(
    device: DiscoveredObdDevice,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
    val containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surface

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect),
        shape = RoundedCornerShape(spacing.radiusLarge),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(spacing.strokeThin, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.medium),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isSelected) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(spacing.iconLarge)
                )
                Spacer(modifier = Modifier.width(spacing.smallMedium))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = device.name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (device.isRecommended) {
                            Spacer(modifier = Modifier.width(spacing.small))
                            RecommendedBadge()
                        }
                    }
                    Spacer(modifier = Modifier.height(spacing.micro))
                    Text(
                        text = "MAC: ${device.macAddress} • ${device.signalStrength}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun RecommendedBadge(modifier: Modifier = Modifier) {
    val spacing = MaterialTheme.spacing
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(spacing.radiusSmall))
            .background(ShiftIQSuccess.copy(alpha = 0.15f))
            .padding(horizontal = spacing.compact, vertical = spacing.micro)
    ) {
        Text(
            text = "Recomendado",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = ShiftIQSuccess
        )
    }
}

@Composable
private fun ObdSafetyNotice(modifier: Modifier = Modifier) {
    val spacing = MaterialTheme.spacing
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(spacing.radiusMedium))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(spacing.smallMedium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(spacing.iconMedium)
        )
        Spacer(modifier = Modifier.width(spacing.small))
        Text(
            text = "El switch de encendido debe estar en posición ON sin arrancar el motor.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ObdActionButtons(
    onConnect: () -> Unit,
    onRescan: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        Button(
            onClick = onConnect,
            modifier = Modifier
                .fillMaxWidth()
                .height(spacing.buttonHeight),
            shape = RoundedCornerShape(spacing.radiusMedium),
            colors = ButtonDefaults.buttonColors(
                containerColor = Navy900,
                contentColor = Color.White
            )
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(spacing.iconMedium)
            )
            Spacer(modifier = Modifier.width(spacing.small))
            Text(
                text = "Conectar Dispositivo",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }

        OutlinedButton(
            onClick = onRescan,
            modifier = Modifier
                .fillMaxWidth()
                .height(spacing.buttonHeight),
            shape = RoundedCornerShape(spacing.radiusMedium)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(spacing.iconMedium)
            )
            Spacer(modifier = Modifier.width(spacing.small))
            Text(
                text = "Volver a Escanear",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@PreviewLightDark
@PreviewFontScale
@Composable
private fun ObdPairingContentPreview() {
    ShiftIQTheme {
        ObdPairingContent(
            devices = listOf(
                DiscoveredObdDevice("1", "OBD2-BT-9821", "00:1D:A5:68:98:21", "Excelente (-52 dBm)", true),
                DiscoveredObdDevice("2", "OBDII-Generic-ELM327", "11:22:33:44:55:66", "Media (-74 dBm)", false)
            ),
            selectedDeviceId = "1",
            onSelectDevice = {},
            onConnect = {},
            onRescan = {}
        )
    }
}
