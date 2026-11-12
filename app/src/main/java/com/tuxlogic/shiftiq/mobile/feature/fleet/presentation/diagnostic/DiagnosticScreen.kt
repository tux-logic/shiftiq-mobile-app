package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.diagnostic

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.CanBusCyan
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Navy900
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQSuccess
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQTheme
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQWarning
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.spacing
import com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.home.components.HealthRingCanvas

data class EcuInspectionItem(
    val ecuId: String,
    val moduleName: String,
    val hasDtc: Boolean,
    val dtcCode: String? = null,
    val statusText: String
)

@Composable
fun DiagnosticsRoute(
    onNavigateToWorkshops: () -> Unit,
    onNavigateToDtcDetail: (String) -> Unit = {},
    onNavigateToOfflineMode: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: DiagnosticViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    DiagnosticsScreen(
        uiState = uiState,
        onNavigateToWorkshops = onNavigateToWorkshops,
        onNavigateToDtcDetail = onNavigateToDtcDetail,
        onNavigateToOfflineMode = onNavigateToOfflineMode,
        onStartScan = viewModel::startDiagnosticScan,
        onClearCodes = viewModel::clearDtcCodes,
        modifier = modifier
    )
}

@Composable
fun DiagnosticScreen(
    onNavigateToWorkshops: () -> Unit,
    onNavigateToDtcDetail: (String) -> Unit = {},
    onNavigateToOfflineMode: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: DiagnosticViewModel = hiltViewModel()
) {
    DiagnosticsRoute(
        onNavigateToWorkshops = onNavigateToWorkshops,
        onNavigateToDtcDetail = onNavigateToDtcDetail,
        onNavigateToOfflineMode = onNavigateToOfflineMode,
        modifier = modifier,
        viewModel = viewModel
    )
}

@Composable
fun DiagnosticsScreen(
    uiState: DiagnosticUiState,
    onNavigateToWorkshops: () -> Unit,
    onNavigateToDtcDetail: (String) -> Unit,
    onNavigateToOfflineMode: () -> Unit,
    onStartScan: () -> Unit,
    onClearCodes: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sampleEcus = listOf(
        EcuInspectionItem("ECU #7E0", "Motor / Inyección", true, "P0135", "Alerta DTC Activa"),
        EcuInspectionItem("ECU #7E1", "Transmisión Automática", false, null, "Sin Códigos de Falla"),
        EcuInspectionItem("ECU #7E2", "Módulo Frenos ABS / ESP", false, null, "Sin Códigos de Falla"),
        EcuInspectionItem("ECU #7E3", "Sistema de Airbags / SRS", false, null, "Sin Códigos de Falla")
    )

    val spacing = MaterialTheme.spacing
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        DiagnosticsTopBar(statusBarPadding = statusBarPadding)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = spacing.medium, vertical = spacing.smallMedium),
            verticalArrangement = Arrangement.spacedBy(spacing.medium)
        ) {
            ProtocolBusHeaderCard()
            CircularProgressSection(progressPercent = 68, ecuCount = "05 / 08 ECUS")
            ModuleInterrogationStatusPill()
            CanBusTrafficMonitor()
            InspectedEcusSection(
                ecus = sampleEcus,
                onDtcClick = { code -> onNavigateToDtcDetail(code) }
            )
            DiagnosticsActionButtons(
                onStopScan = onStartScan,
                onOfflineClick = onNavigateToOfflineMode,
                onDownloadReport = {}
            )
            Spacer(modifier = Modifier.height(spacing.medium))
        }
    }
}

@Composable
private fun DiagnosticsTopBar(
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
        Column {
            Text(
                text = "Diagnóstico OBD-II",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Escaneo y lectura en tiempo real",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(spacing.radiusSmall))
                .background(Color.White.copy(alpha = 0.15f))
                .padding(horizontal = spacing.small, vertical = spacing.extraSmall)
        ) {
            Text(
                text = "EN VIVO",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun ProtocolBusHeaderCard(modifier: Modifier = Modifier) {
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(spacing.indicatorHeight + spacing.extraSmall)
                        .clip(CircleShape)
                        .background(CanBusCyan)
                )
                Spacer(modifier = Modifier.width(spacing.small))
                Column {
                    Text(
                        text = "BUS CAN-H / CAN-L LINK ESTABLISHED",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Velocidad de bus: 500 kbps (11-bit identifier)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun CircularProgressSection(
    progressPercent: Int,
    ecuCount: String,
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HealthRingCanvas(score = progressPercent, ringColor = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(spacing.small))
            Text(
                text = "Escaneo Progresivo de Módulos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "$ecuCount módulos inspeccionados",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ModuleInterrogationStatusPill(modifier: Modifier = Modifier) {
    val spacing = MaterialTheme.spacing
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(spacing.radiusMedium))
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f))
            .padding(horizontal = spacing.medium, vertical = spacing.small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(spacing.iconMedium)
        )
        Spacer(modifier = Modifier.width(spacing.small))
        Text(
            text = "Consultando DTC en Banco 1 Emisiones...",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun CanBusTrafficMonitor(modifier: Modifier = Modifier) {
    val spacing = MaterialTheme.spacing
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(spacing.radiusLarge),
        colors = CardDefaults.cardColors(containerColor = Navy900),
        border = BorderStroke(spacing.strokeThin, CanBusCyan.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.medium)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "CAN-BUS TX / RX TELEMETRY",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = CanBusCyan
                )
                Text(
                    text = "PID: \$010C",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.LightGray
                )
            }
            Spacer(modifier = Modifier.height(spacing.extraSmall))
            Text(
                text = "TX: 7DF 02 01 05 CC CC CC CC\nRX: 7E8 03 41 05 59 00 00 00",
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.Monospace,
                color = Color.White
            )
        }
    }
}

@Composable
private fun InspectedEcusSection(
    ecus: List<EcuInspectionItem>,
    onDtcClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Módulos Inspeccionados",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(spacing.small))
        Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
            ecus.forEach { ecu ->
                EcuInspectionCard(ecu = ecu, onDtcClick = onDtcClick)
            }
        }
    }
}

@Composable
private fun EcuInspectionCard(
    ecu: EcuInspectionItem,
    onDtcClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = ecu.hasDtc && ecu.dtcCode != null) {
                ecu.dtcCode?.let { onDtcClick(it) }
            },
        shape = RoundedCornerShape(spacing.radiusMedium),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            spacing.strokeThin,
            if (ecu.hasDtc) ShiftIQWarning else MaterialTheme.colorScheme.outlineVariant
        )
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
                    imageVector = if (ecu.hasDtc) Icons.Default.Warning else Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = if (ecu.hasDtc) ShiftIQWarning else ShiftIQSuccess,
                    modifier = Modifier.size(spacing.iconLarge)
                )
                Spacer(modifier = Modifier.width(spacing.smallMedium))
                Column {
                    Text(
                        text = "${ecu.ecuId} • ${ecu.moduleName}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = ecu.statusText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (ecu.hasDtc && ecu.dtcCode != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(spacing.radiusSmall))
                        .background(ShiftIQWarning.copy(alpha = 0.15f))
                        .padding(horizontal = spacing.small, vertical = spacing.extraSmall)
                ) {
                    Text(
                        text = ecu.dtcCode,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = ShiftIQWarning
                    )
                }
            }
        }
    }
}

@Composable
private fun DiagnosticsActionButtons(
    onStopScan: () -> Unit,
    onOfflineClick: () -> Unit,
    onDownloadReport: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        Button(
            onClick = onStopScan,
            modifier = Modifier
                .fillMaxWidth()
                .height(spacing.buttonHeight),
            shape = RoundedCornerShape(spacing.radiusMedium),
            colors = ButtonDefaults.buttonColors(containerColor = Navy900, contentColor = Color.White)
        ) {
            Icon(Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(spacing.iconMedium))
            Spacer(modifier = Modifier.width(spacing.small))
            Text("Detener Escaneo", fontWeight = FontWeight.Bold)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.small)
        ) {
            OutlinedButton(
                onClick = onOfflineClick,
                modifier = Modifier
                    .weight(1f)
                    .height(spacing.buttonHeight),
                shape = RoundedCornerShape(spacing.radiusMedium)
            ) {
                Icon(Icons.Default.CloudQueue, contentDescription = null, modifier = Modifier.size(spacing.iconSmall))
                Spacer(modifier = Modifier.width(spacing.extraSmall))
                Text("Modo Offline")
            }
            OutlinedButton(
                onClick = onDownloadReport,
                modifier = Modifier
                    .weight(1f)
                    .height(spacing.buttonHeight),
                shape = RoundedCornerShape(spacing.radiusMedium)
            ) {
                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(spacing.iconSmall))
                Spacer(modifier = Modifier.width(spacing.extraSmall))
                Text("Reporte PDF")
            }
        }
    }
}

@PreviewLightDark
@PreviewFontScale
@Composable
private fun DiagnosticsScreenPreview() {
    ShiftIQTheme {
        DiagnosticsScreen(
            uiState = DiagnosticUiState(),
            onNavigateToWorkshops = {},
            onNavigateToDtcDetail = {},
            onNavigateToOfflineMode = {},
            onStartScan = {},
            onClearCodes = {}
        )
    }
}
