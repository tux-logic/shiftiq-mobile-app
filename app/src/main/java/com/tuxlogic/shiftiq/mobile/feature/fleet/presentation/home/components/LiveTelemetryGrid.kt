package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQSuccess
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQTheme
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.spacing
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.DriverLiveTelemetry

@Composable
fun LiveTelemetryGrid(
    telemetry: DriverLiveTelemetry,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Column(modifier = modifier.fillMaxWidth()) {
        LiveTelemetryHeader()
        Spacer(modifier = Modifier.height(spacing.smallMedium))
        LiveTelemetryRow(
            firstTitle = "Régimen Motor",
            firstValue = telemetry.rpm.toString(),
            firstUnit = "RPM",
            firstIcon = Icons.Outlined.Settings,
            secondTitle = "Refrigerante",
            secondValue = telemetry.temperatureCelsius.toString(),
            secondUnit = "°C",
            secondIcon = Icons.Default.Thermostat
        )
        Spacer(modifier = Modifier.height(spacing.smallMedium))
        val formattedBattery = String.format(java.util.Locale.US, "%.1f", telemetry.batteryVoltage)
        val formattedKm = String.format(java.util.Locale.US, "%,d", telemetry.odometerKm)
        LiveTelemetryRow(
            firstTitle = "Batería",
            firstValue = formattedBattery,
            firstUnit = "V",
            firstIcon = Icons.Default.ElectricBolt,
            secondTitle = "Kilometraje",
            secondValue = formattedKm,
            secondUnit = "km",
            secondIcon = Icons.Default.Speed
        )
    }
}

@Composable
private fun LiveTelemetryHeader(modifier: Modifier = Modifier) {
    val spacing = MaterialTheme.spacing
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "TELEMETRÍA EN VIVO",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        LiveTelemetrySyncBadge()
    }
}

@Composable
private fun LiveTelemetrySyncBadge(modifier: Modifier = Modifier) {
    val spacing = MaterialTheme.spacing
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(spacing.radiusMedium))
            .background(ShiftIQSuccess.copy(alpha = 0.15f))
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
            text = "Sincronizado",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = ShiftIQSuccess
        )
    }
}

@Composable
private fun LiveTelemetryRow(
    firstTitle: String,
    firstValue: String,
    firstUnit: String,
    firstIcon: ImageVector,
    secondTitle: String,
    secondValue: String,
    secondUnit: String,
    secondIcon: ImageVector,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing.smallMedium)
    ) {
        TelemetryMetricCard(
            title = firstTitle,
            value = firstValue,
            unit = firstUnit,
            icon = firstIcon,
            modifier = Modifier.weight(1f)
        )
        TelemetryMetricCard(
            title = secondTitle,
            value = secondValue,
            unit = secondUnit,
            icon = secondIcon,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun TelemetryMetricCard(
    title: String,
    value: String,
    unit: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(spacing.radiusLarge),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(spacing.strokeThin, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = spacing.elevationSmall)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.medium)
        ) {
            TelemetryMetricTopRow(title = title, icon = icon)
            Spacer(modifier = Modifier.height(spacing.small))
            TelemetryMetricValueRow(value = value, unit = unit)
        }
    }
}

@Composable
private fun TelemetryMetricTopRow(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Box(
            modifier = Modifier
                .size(spacing.badgeHeight)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(spacing.iconMedium)
            )
        }
    }
}

@Composable
private fun TelemetryMetricValueRow(
    value: String,
    unit: String,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Bottom
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.width(spacing.extraSmall))
        Text(
            text = unit,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = spacing.micro)
        )
    }
}

@PreviewLightDark
@PreviewFontScale
@Composable
private fun LiveTelemetryGridPreview() {
    ShiftIQTheme {
        LiveTelemetryGrid(
            telemetry = DriverLiveTelemetry(
                rpm = 842,
                temperatureCelsius = 89,
                batteryVoltage = 13.8,
                odometerKm = 45210
            )
        )
    }
}
