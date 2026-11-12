package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.home.components

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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQSuccess
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQTheme
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQWarning
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.spacing
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.DriverDtcAlert

@Composable
fun DtcAlertBanner(
    alert: DriverDtcAlert?,
    onViewDiagnosticDetail: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (alert != null) {
        ActiveDtcAlertCard(
            alert = alert,
            onViewDiagnosticDetail = onViewDiagnosticDetail,
            modifier = modifier
        )
    } else {
        CleanHealthBanner(modifier = modifier)
    }
}

@Composable
private fun ActiveDtcAlertCard(
    alert: DriverDtcAlert,
    onViewDiagnosticDetail: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(spacing.radiusLarge),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(spacing.strokeMedium, ShiftIQWarning),
        elevation = CardDefaults.cardElevation(defaultElevation = spacing.elevationSmall)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.medium)
        ) {
            DtcAlertTopHeader(title = alert.title, code = alert.code)
            Spacer(modifier = Modifier.height(spacing.small))
            DtcSeverityPill(severity = alert.severity, km = alert.suggestedReviewKm)
            Spacer(modifier = Modifier.height(spacing.small))
            Text(
                text = alert.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(spacing.smallMedium))
            DtcViewDetailCta(onClick = onViewDiagnosticDetail)
        }
    }
}

@Composable
private fun DtcAlertTopHeader(
    title: String,
    code: String,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
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
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(spacing.radiusSmall))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .padding(horizontal = spacing.small, vertical = spacing.extraSmall)
        ) {
            Text(
                text = code,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun DtcSeverityPill(
    severity: String,
    km: Int,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(spacing.radiusSmall))
            .background(ShiftIQWarning.copy(alpha = 0.12f))
            .padding(horizontal = spacing.smallMedium, vertical = spacing.extraSmall)
    ) {
        Text(
            text = "Severidad: $severity • Revisión sugerida en $km km",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = ShiftIQWarning
        )
    }
}

@Composable
private fun DtcViewDetailCta(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Ver Diagnóstico Completo",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(spacing.extraSmall))
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(spacing.iconSmall)
        )
    }
}

@Composable
private fun CleanHealthBanner(modifier: Modifier = Modifier) {
    val spacing = MaterialTheme.spacing
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(spacing.radiusLarge),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(spacing.strokeThin, ShiftIQSuccess.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(defaultElevation = spacing.elevationSmall)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = ShiftIQSuccess,
                modifier = Modifier.size(spacing.iconExtraLarge)
            )
            Spacer(modifier = Modifier.width(spacing.smallMedium))
            Column {
                Text(
                    text = "Sin códigos de falla detectados",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(spacing.micro))
                Text(
                    text = "Todos los sistemas ECU operan dentro de los parámetros OEM normales.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@PreviewLightDark
@PreviewFontScale
@Composable
private fun DtcAlertBannerPreview() {
    ShiftIQTheme {
        DtcAlertBanner(
            alert = DriverDtcAlert(
                code = "P0135",
                title = "Circuito Calefactor Sensor O2",
                severity = "Moderada",
                suggestedReviewKm = 500,
                description = "Se detectó resistencia anómala en el circuito del calefactor de la sonda lambda."
            ),
            onViewDiagnosticDetail = {}
        )
    }
}
