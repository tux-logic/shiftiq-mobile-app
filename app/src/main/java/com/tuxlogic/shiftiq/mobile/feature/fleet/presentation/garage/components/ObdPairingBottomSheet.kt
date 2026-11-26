package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.garage.components

import android.Manifest
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import com.tuxlogic.shiftiq.mobile.core.localization.LocalizedText as Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.tuxlogic.shiftiq.mobile.core.common.ble.ObdBluetoothScanner
import com.tuxlogic.shiftiq.mobile.core.common.ble.ScannedObdDevice
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.CanBusCyan
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Navy900
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQSuccess
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQTheme
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.spacing
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

data class DiscoveredObdDevice(
    val id: String,
    val name: String,
    val macAddress: String,
    val signalStrength: String,
    val isRecommended: Boolean = false
)

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ObdBluetoothScannerEntryPoint {
    fun obdBluetoothScanner(): ObdBluetoothScanner
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ObdPairingBottomSheet(
    onDismissRequest: () -> Unit,
    onDeviceConnected: (DiscoveredObdDevice) -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scanner = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            ObdBluetoothScannerEntryPoint::class.java
        ).obdBluetoothScanner()
    }

    var devices by remember { mutableStateOf<List<DiscoveredObdDevice>>(emptyList()) }
    var isScanning by remember { mutableStateOf(false) }
    var isConnecting by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var selectedDeviceId by rememberSaveable { mutableStateOf("") }
    var scanNonce by remember { mutableStateOf(0) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        val allGranted = grants.values.all { it }
        if (allGranted) {
            scanNonce += 1
        } else {
            statusMessage = "Bluetooth permissions are required to discover OBD-II adapters."
            isScanning = false
        }
    }

    fun requiredPermissions(): Array<String> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT
        )
    } else {
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN
        )
    }

    fun startScan() {
        if (!scanner.hasBluetoothHardware()) {
            statusMessage = "This device does not support Bluetooth LE scanning."
            return
        }
        if (!scanner.isBluetoothEnabled()) {
            statusMessage = "Turn on Bluetooth to scan for OBD-II adapters."
            context.startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
            return
        }
        val missing = scanner.missingPermissions()
        if (missing.isNotEmpty()) {
            permissionLauncher.launch(requiredPermissions())
            return
        }
        scanNonce += 1
    }

    LaunchedEffect(scanNonce) {
        if (scanNonce == 0) return@LaunchedEffect
        isScanning = true
        statusMessage = null
        devices = emptyList()
        selectedDeviceId = ""
        scanner.scan()
            .catch { error ->
                statusMessage = error.message ?: "Bluetooth scan failed."
                isScanning = false
            }
            .collect { scanned ->
                devices = scanned.map { it.toUiModel() }
                if (selectedDeviceId.isBlank()) {
                    selectedDeviceId = devices.firstOrNull { it.isRecommended }?.id
                        ?: devices.firstOrNull()?.id.orEmpty()
                }
            }
        isScanning = false
        if (devices.isEmpty() && statusMessage == null) {
            statusMessage = "No OBD-II adapters found nearby. Keep the dongle powered and within range."
        }
    }

    DisposableEffect(Unit) {
        startScan()
        onDispose { }
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier
    ) {
        ObdPairingContent(
            devices = devices,
            selectedDeviceId = selectedDeviceId,
            isScanning = isScanning,
            isConnecting = isConnecting,
            statusMessage = statusMessage,
            onSelectDevice = { selectedDeviceId = it },
            onConnect = {
                val selected = devices.firstOrNull { it.id == selectedDeviceId } ?: return@ObdPairingContent
                scope.launch {
                    isConnecting = true
                    statusMessage = null
                    var reachable = false
                    scanner.connectProbe(selected.macAddress)
                        .catch { reachable = false }
                        .collect { reachable = it }
                    isConnecting = false
                    if (reachable) {
                        Toast.makeText(context, "OBD-II adapter connected", Toast.LENGTH_SHORT).show()
                        onDeviceConnected(selected)
                    } else {
                        // Still allow pairing selection when GATT probe fails (classic SPP dongles).
                        Toast.makeText(
                            context,
                            "Adapter selected. Full live telemetry links through workshop IoT registration.",
                            Toast.LENGTH_LONG
                        ).show()
                        onDeviceConnected(selected)
                    }
                }
            },
            onRescan = { startScan() }
        )
    }
}

private fun ScannedObdDevice.toUiModel(): DiscoveredObdDevice = DiscoveredObdDevice(
    id = id,
    name = name,
    macAddress = address,
    signalStrength = signalLabel,
    isRecommended = isRecommended
)

@Composable
private fun ObdPairingContent(
    devices: List<DiscoveredObdDevice>,
    selectedDeviceId: String,
    onSelectDevice: (String) -> Unit,
    onConnect: () -> Unit,
    onRescan: () -> Unit,
    modifier: Modifier = Modifier,
    isScanning: Boolean = false,
    isConnecting: Boolean = false,
    statusMessage: String? = null
) {
    val spacing = MaterialTheme.spacing
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.medium, vertical = spacing.smallMedium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ObdScannerPulseHeader(isScanning = isScanning)
        Spacer(modifier = Modifier.height(spacing.smallMedium))
        ObdProtocolTag()
        Spacer(modifier = Modifier.height(spacing.medium))
        when {
            isScanning && devices.isEmpty() -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(spacing.iconExtraLarge),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(spacing.small))
                Text(
                    text = "Scanning for nearby BLE / bonded OBD adapters…",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            devices.isEmpty() -> {
                Text(
                    text = statusMessage ?: "No OBD-II devices are available.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            else -> {
                ObdDevicesList(
                    devices = devices,
                    selectedDeviceId = selectedDeviceId,
                    onSelectDevice = onSelectDevice
                )
                statusMessage?.let { message ->
                    Spacer(modifier = Modifier.height(spacing.small))
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(spacing.medium))
        ObdSafetyNotice()
        Spacer(modifier = Modifier.height(spacing.medium))
        ObdActionButtons(
            onConnect = onConnect,
            onRescan = onRescan,
            connectEnabled = !isScanning && !isConnecting && devices.any { it.id == selectedDeviceId },
            isConnecting = isConnecting,
            isScanning = isScanning
        )
        Spacer(modifier = Modifier.height(spacing.large))
    }
}

@Composable
private fun ObdScannerPulseHeader(
    isScanning: Boolean,
    modifier: Modifier = Modifier
) {
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
            text = if (isScanning) "Buscando Dispositivos OBD-II" else "Dispositivos OBD-II",
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
    modifier: Modifier = Modifier,
    connectEnabled: Boolean = true,
    isConnecting: Boolean = false,
    isScanning: Boolean = false
) {
    val spacing = MaterialTheme.spacing
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        Button(
            onClick = onConnect,
            enabled = connectEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(spacing.buttonHeight),
            shape = RoundedCornerShape(spacing.radiusMedium),
            colors = ButtonDefaults.buttonColors(
                containerColor = Navy900,
                contentColor = Color.White
            )
        ) {
            if (isConnecting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(spacing.iconMedium),
                    color = Color.White,
                    strokeWidth = spacing.strokeThick
                )
                Spacer(modifier = Modifier.width(spacing.small))
                Text(
                    text = "Conectando…",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            } else {
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
        }

        OutlinedButton(
            onClick = onRescan,
            enabled = !isScanning && !isConnecting,
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
                DiscoveredObdDevice("1", "OBD2-BT-9821", "00:1D:A5:68:98:21", "Excellent (-52 dBm)", true),
                DiscoveredObdDevice("2", "OBDII-Generic-ELM327", "11:22:33:44:55:66", "Fair (-74 dBm)", false)
            ),
            selectedDeviceId = "1",
            onSelectDevice = {},
            onConnect = {},
            onRescan = {}
        )
    }
}
