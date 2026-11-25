package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.diagnostic

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SettingsInputAntenna
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tuxlogic.shiftiq.mobile.core.designsystem.components.ShiftIQPageHeader
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.PrimaryBlue
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQBorderSlate
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQTheme
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQWarning
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.WalBufferAmber
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.spacing

data class BufferedFrameItem(
    val pid: String,
    val description: String,
    val value: String,
    val timestamp: String
)

@Composable
fun OfflineTelemetryRoute(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OfflineTelemetryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    OfflineTelemetryScreen(
        onNavigateBack = onNavigateBack,
        frames = uiState.frames,
        frameCount = uiState.frameCount,
        memoryKb = uiState.memoryKb,
        message = uiState.message,
        onSync = viewModel::loadBufferedFrames,
        modifier = modifier
    )
}

@Composable
fun OfflineTelemetryScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    frames: List<BufferedFrameItem> = emptyList(),
    frameCount: Int = 0,
    memoryKb: Double = 0.0,
    message: String? = null,
    onSync: () -> Unit = {}
) {
    val spacing = MaterialTheme.spacing
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        OfflineTopBar(
            onNavigateBack = onNavigateBack,
            statusBarPadding = statusBarPadding
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(spacing.medium)
        ) {
            OfflineWarningBanner()
            LocalBufferDiagramCard()
            QueueStatsCard(frameCount = frameCount, memoryKb = memoryKb)
            if (!message.isNullOrBlank()) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            BufferedFramesSection(frames = frames)
            SyncActionButton(onSync = onSync)
        }
    }
}

@Composable
private fun OfflineTopBar(
    onNavigateBack: () -> Unit,
    statusBarPadding: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    ShiftIQPageHeader(
        title = "Modo Offline",
        subtitle = "Buffer local de telemetría",
        badgeLabel = "Sin Conexión",
        showBackButton = true,
        onBackPressed = onNavigateBack,
        modifier = modifier
    )
}

@Composable
private fun OfflineWarningBanner(modifier: Modifier = Modifier) {
    val spacing = MaterialTheme.spacing
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(spacing.strokeThin, ShiftIQWarning),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(spacing.badgeHeight + spacing.small)
                    .clip(CircleShape)
                    .background(ShiftIQWarning.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = ShiftIQWarning,
                    modifier = Modifier.size(spacing.iconLarge)
                )
            }
            Spacer(modifier = Modifier.width(spacing.smallMedium))
            Column {
                Text(
                    text = "Almacenamiento Local Activo",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "La telemetría se resguarda en el buffer SQLite (Room) local del teléfono sin pérdida de datos.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun LocalBufferDiagramCard(modifier: Modifier = Modifier) {
    val spacing = MaterialTheme.spacing
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(spacing.strokeThin, ShiftIQBorderSlate),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.medium)
        ) {
            Text(
                text = "Flujo de Contingencia de Datos",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(spacing.smallMedium))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DiagramNode(icon = Icons.Default.SettingsInputAntenna, label = "Dongle OBD", isOnline = true)
                Text(text = "→", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                DiagramNode(icon = Icons.Default.PhoneAndroid, label = "Buffer Room", isOnline = true)
                Text(text = "⇸", style = MaterialTheme.typography.titleMedium, color = WalBufferAmber)
                DiagramNode(icon = Icons.Default.CloudOff, label = "Nube Central", isOnline = false)
            }
        }
    }
}

@Composable
private fun DiagramNode(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isOnline: Boolean,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(spacing.avatarMedium)
                .clip(CircleShape)
                .background(
                    if (isOnline) MaterialTheme.colorScheme.primaryContainer
                    else WalBufferAmber.copy(alpha = 0.2f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isOnline) MaterialTheme.colorScheme.primary else WalBufferAmber,
                modifier = Modifier.size(spacing.iconLarge)
            )
        }
        Spacer(modifier = Modifier.height(spacing.extraSmall))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun QueueStatsCard(
    frameCount: Int,
    memoryKb: Double,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(spacing.strokeThin, ShiftIQBorderSlate),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
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
                    imageVector = Icons.Default.Storage,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(spacing.iconLarge)
                )
                Spacer(modifier = Modifier.width(spacing.small))
                Column {
                    Text(
                        text = "$frameCount tramas en cola local",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Memoria ocupada: ${memoryKb} KB",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun BufferedFramesSection(
    frames: List<BufferedFrameItem>,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Tramas Recientes en Cola",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(spacing.small))
        Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
            frames.forEach { frame ->
                BufferedFrameCard(frame = frame)
            }
        }
    }
}

@Composable
private fun BufferedFrameCard(
    frame: BufferedFrameItem,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(spacing.strokeThin, ShiftIQBorderSlate),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.medium),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "${frame.pid} • ${frame.description}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Registrado: ${frame.timestamp}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = frame.value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun SyncActionButton(
    onSync: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Button(
        onClick = onSync,
        modifier = modifier
            .fillMaxWidth()
            .height(spacing.buttonHeight),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue, contentColor = Color.White)
    ) {
        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(spacing.iconMedium))
        Spacer(modifier = Modifier.width(spacing.small))
        Text("Forzar Sincronización con la Nube", fontWeight = FontWeight.Bold)
    }
}

@PreviewLightDark
@PreviewFontScale
@Composable
private fun OfflineTelemetryScreenPreview() {
    ShiftIQTheme {
        OfflineTelemetryScreen(onNavigateBack = {})
    }
}
