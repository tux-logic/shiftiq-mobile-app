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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Navy900
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQSuccess
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQTheme
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQWarning
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.spacing

@Composable
fun DtcDetailRoute(
    dtcCode: String,
    onNavigateBack: () -> Unit,
    onNavigateToWorkshops: () -> Unit,
    modifier: Modifier = Modifier
) {
    DtcDetailScreen(
        dtcCode = dtcCode,
        onNavigateBack = onNavigateBack,
        onNavigateToWorkshops = onNavigateToWorkshops,
        modifier = modifier
    )
}

@Composable
fun DtcDetailScreen(
    dtcCode: String,
    onNavigateBack: () -> Unit,
    onNavigateToWorkshops: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        DtcDetailTopBar(
            dtcCode = dtcCode,
            onNavigateBack = onNavigateBack,
            statusBarPadding = statusBarPadding
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = spacing.medium, vertical = spacing.smallMedium),
            verticalArrangement = Arrangement.spacedBy(spacing.medium)
        ) {
            DtcAlertOverviewCard(dtcCode = dtcCode)
            PlainLanguageTranslationCard()
            DrivingImpactSection()
            TechnicalSpecCard()
            EstimateTwinCards()
            DtcActionButtons(
                onFindWorkshop = onNavigateToWorkshops,
                onDownloadReport = {}
            )
            Spacer(modifier = Modifier.height(spacing.medium))
        }
    }
}

@Composable
private fun DtcDetailTopBar(
    dtcCode: String,
    onNavigateBack: () -> Unit,
    statusBarPadding: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Navy900)
            .padding(
                top = spacing.medium,
                start = spacing.small,
                end = spacing.medium,
                bottom = spacing.smallMedium
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Atrás",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(spacing.small))
            Text(
                text = "Detalle de Diagnóstico",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(spacing.radiusSmall))
                .background(Color.White.copy(alpha = 0.15f))
                .padding(horizontal = spacing.small, vertical = spacing.extraSmall)
        ) {
            Text(
                text = dtcCode,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun DtcAlertOverviewCard(
    dtcCode: String,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(spacing.radiusLarge),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(spacing.strokeMedium, ShiftIQWarning),
        elevation = CardDefaults.cardElevation(defaultElevation = spacing.elevationSmall)
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
                Row(verticalAlignment = Alignment.CenterVertically) {
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
                            text = "Código $dtcCode",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Circuito Calefactor Sensor O2",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                DtcUrgencyPill()
            }
            Spacer(modifier = Modifier.height(spacing.smallMedium))
            Text(
                text = "Ventana sugerida de intervención: Próximos 500 km",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = ShiftIQWarning
            )
        }
    }
}

@Composable
private fun DtcUrgencyPill(modifier: Modifier = Modifier) {
    val spacing = MaterialTheme.spacing
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(spacing.radiusSmall))
            .background(ShiftIQWarning.copy(alpha = 0.15f))
            .padding(horizontal = spacing.small, vertical = spacing.extraSmall)
    ) {
        Text(
            text = "MODERADO / PREVENTIVO",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = ShiftIQWarning
        )
    }
}

@Composable
private fun PlainLanguageTranslationCard(modifier: Modifier = Modifier) {
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
                .padding(spacing.medium)
        ) {
            Text(
                text = "¿Qué significa en lenguaje sencillo?",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(spacing.small))
            Text(
                text = "La resistencia interna que precalienta la sonda de oxígeno (sensor lambda) tarda más de lo normal en alcanzar la temperatura óptima. El motor sigue funcionando de forma segura, pero consume más combustible al arrancar y emite mayores emisiones durante los primeros 5 minutos.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DrivingImpactSection(modifier: Modifier = Modifier) {
    val spacing = MaterialTheme.spacing
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Impacto en la Conducción",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(spacing.small))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.small)
        ) {
            ImpactCard(
                icon = Icons.Default.LocalGasStation,
                title = "Consumo",
                desc = "+15% en frío",
                modifier = Modifier.weight(1f)
            )
            ImpactCard(
                icon = Icons.Default.Warning,
                title = "Check Engine",
                desc = "Luz activa",
                modifier = Modifier.weight(1f)
            )
            ImpactCard(
                icon = Icons.Default.CheckCircle,
                title = "Seguridad",
                desc = "Seguro conducir",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ImpactCard(
    icon: ImageVector,
    title: String,
    desc: String,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(spacing.radiusMedium),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(spacing.strokeThin, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.smallMedium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(spacing.iconLarge)
            )
            Spacer(modifier = Modifier.height(spacing.extraSmall))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TechnicalSpecCard(modifier: Modifier = Modifier) {
    val spacing = MaterialTheme.spacing
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(spacing.radiusLarge),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        border = BorderStroke(spacing.strokeThin, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.medium)
        ) {
            Text(
                text = "Especificación Técnica OEM",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(spacing.small))
            Text(
                text = "Ubicación: Colector de escape antes del catalizador (Banco 1, Sensor 1)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Número de Parte OEM: 39210-2B100 (Bosch / Mobis)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun EstimateTwinCards(modifier: Modifier = Modifier) {
    val spacing = MaterialTheme.spacing
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing.smallMedium)
    ) {
        EstimateMetricCard(
            title = "Presupuesto Estimado",
            value = "S/ 140 - S/ 190",
            icon = Icons.Default.Build,
            modifier = Modifier.weight(1f)
        )
        EstimateMetricCard(
            title = "Tiempo en Bahía",
            value = "45 min",
            icon = Icons.Default.Schedule,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun EstimateMetricCard(
    title: String,
    value: String,
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
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun DtcActionButtons(
    onFindWorkshop: () -> Unit,
    onDownloadReport: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        Button(
            onClick = onFindWorkshop,
            modifier = Modifier
                .fillMaxWidth()
                .height(spacing.buttonHeight),
            shape = RoundedCornerShape(spacing.radiusMedium),
            colors = ButtonDefaults.buttonColors(containerColor = Navy900, contentColor = Color.White)
        ) {
            Icon(Icons.Default.Place, contentDescription = null, modifier = Modifier.size(spacing.iconMedium))
            Spacer(modifier = Modifier.width(spacing.small))
            Text("Buscar Taller Cercano para Reparación", fontWeight = FontWeight.Bold)
        }
        OutlinedButton(
            onClick = onDownloadReport,
            modifier = Modifier
                .fillMaxWidth()
                .height(spacing.buttonHeight),
            shape = RoundedCornerShape(spacing.radiusMedium)
        ) {
            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(spacing.iconMedium))
            Spacer(modifier = Modifier.width(spacing.small))
            Text("Descargar Informe PDF")
        }
    }
}

@PreviewLightDark
@PreviewFontScale
@Composable
private fun DtcDetailScreenPreview() {
    ShiftIQTheme {
        DtcDetailScreen(
            dtcCode = "P0135",
            onNavigateBack = {},
            onNavigateToWorkshops = {}
        )
    }
}
