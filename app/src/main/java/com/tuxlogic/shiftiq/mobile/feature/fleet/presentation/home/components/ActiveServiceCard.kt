package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQTheme
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.spacing
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.ActiveServiceCardData

@Composable
fun ActiveServiceCard(
    serviceData: ActiveServiceCardData?,
    onViewLiveOrder: (String) -> Unit,
    modifier: Modifier = Modifier,
    onNavigateToWorkshops: () -> Unit = {},
    isLoading: Boolean = false,
) {
    when {
        isLoading -> {
            ActiveServiceCardSkeleton(modifier = modifier)
        }
        serviceData != null -> {
            ActiveServiceCardContent(
                service = serviceData,
                onViewLiveOrder = { onViewLiveOrder(serviceData.workOrderId) },
                modifier = modifier,
            )
        }
        else -> {
            EmptyServiceCardContent(
                onNavigateToWorkshops = onNavigateToWorkshops,
                modifier = modifier,
            )
        }
    }
}

@Composable
fun ActiveServiceCard(
    service: ActiveServiceCardData,
    onViewLiveOrder: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ActiveServiceCardContent(
        service = service,
        onViewLiveOrder = onViewLiveOrder,
        modifier = modifier,
    )
}

@Composable
fun ActiveServiceCardContent(
    service: ActiveServiceCardData,
    onViewLiveOrder: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
        border = BorderStroke(
            width = MaterialTheme.spacing.divider,
            color = MaterialTheme.colorScheme.outlineVariant,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.smallMedium),
        ) {
            ActiveServiceBadgeHeader(startedAt = service.startedAt)
            ActiveServiceTitleSection(
                workshopName = service.workshopName,
                phaseText = service.phaseText,
            )
            ActiveServiceJobDetail(currentJob = service.currentJob)
            ActiveServiceProgressBar(
                currentPhaseIndex = service.currentPhaseIndex,
                totalPhases = service.totalPhases,
            )
            ActiveServiceActionButton(onClick = onViewLiveOrder)
        }
    }
}

@Composable
private fun ActiveServiceBadgeHeader(
    startedAt: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.extraSmall)
                .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.8f))
                .border(
                    width = MaterialTheme.spacing.divider,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = MaterialTheme.shapes.extraSmall,
                )
                .padding(
                    horizontal = MaterialTheme.spacing.small,
                    vertical = MaterialTheme.spacing.extraSmall,
                ),
        ) {
            Text(
                text = "[ EN SERVICIO ACTIVO ]",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        Text(
            text = "Iniciado: $startedAt",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ActiveServiceTitleSection(
    workshopName: String,
    phaseText: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.compact),
    ) {
        Text(
            text = "Tu auto está en reparación en $workshopName",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.compact),
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(MaterialTheme.spacing.iconSmall),
            )
            Text(
                text = phaseText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun ActiveServiceJobDetail(
    currentJob: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Normal,
                ),
            ) {
                append("Trabajo en ejecución: ")
            }
            withStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium,
                ),
            ) {
                append(currentJob)
            }
        },
        style = MaterialTheme.typography.bodySmall,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier.fillMaxWidth(),
    )
}

@Composable
private fun ActiveServiceProgressBar(
    currentPhaseIndex: Int,
    totalPhases: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.compact),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        for (phase in 1..totalPhases) {
            val segmentColor = when {
                phase < currentPhaseIndex -> MaterialTheme.colorScheme.tertiary
                phase == currentPhaseIndex -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.surfaceContainerHighest
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(MaterialTheme.spacing.indicatorHeight)
                    .clip(CircleShape)
                    .background(segmentColor),
            )
        }
    }
}

@Composable
private fun ActiveServiceActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(MaterialTheme.spacing.buttonHeight),
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.inverseSurface,
            contentColor = MaterialTheme.colorScheme.inverseOnSurface,
        ),
    ) {
        Text(
            text = "Ver Orden de Trabajo en Vivo",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.width(MaterialTheme.spacing.compact))
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            modifier = Modifier.size(MaterialTheme.spacing.iconMedium),
        )
    }
}

@Composable
fun EmptyServiceCardContent(
    onNavigateToWorkshops: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
        border = BorderStroke(
            width = MaterialTheme.spacing.divider,
            color = MaterialTheme.colorScheme.outlineVariant,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            EmptyServiceInfo(modifier = Modifier.weight(1f))
            OutlinedButton(
                onClick = onNavigateToWorkshops,
                shape = MaterialTheme.shapes.medium,
                border = BorderStroke(
                    width = MaterialTheme.spacing.divider,
                    color = MaterialTheme.colorScheme.primary,
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary,
                ),
            ) {
                Icon(
                    imageVector = Icons.Default.Storefront,
                    contentDescription = null,
                    modifier = Modifier.size(MaterialTheme.spacing.iconSmall),
                )
                Spacer(modifier = Modifier.width(MaterialTheme.spacing.extraSmall))
                Text(
                    text = "Buscar Taller",
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }
}

@Composable
private fun EmptyServiceInfo(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.smallMedium),
    ) {
        Box(
            modifier = Modifier
                .size(MaterialTheme.spacing.extraExtraLarge)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainerHighest),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Build,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(MaterialTheme.spacing.iconLarge),
            )
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.micro),
        ) {
            Text(
                text = "Sin servicios activos",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "No tienes órdenes en taller ahora.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
fun ActiveServiceCardSkeleton(
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
        border = BorderStroke(
            width = MaterialTheme.spacing.divider,
            color = MaterialTheme.colorScheme.outlineVariant,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.smallMedium),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.35f)
                    .height(MaterialTheme.spacing.medium)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest),
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(MaterialTheme.spacing.large)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest),
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(MaterialTheme.spacing.smallMedium)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest),
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(MaterialTheme.spacing.indicatorHeight)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest),
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(MaterialTheme.spacing.buttonHeight)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest),
            )
        }
    }
}

class ActiveServiceCardPreviewParameterProvider : PreviewParameterProvider<ActiveServiceCardState> {
    override val values: Sequence<ActiveServiceCardState> = sequenceOf(
        ActiveServiceCardState.Success(
            data = ActiveServiceCardData(
                workOrderId = "wo-001",
                workshopName = "Taller Silva",
                phaseText = "Fase 3 de 4: En Rampa (Bahía 2) • Téc. Roberto Silva",
                mechanicName = "Roberto Silva",
                currentJob = "Reemplazo de Sensor O2 Bosch (45 min restantes)",
                remainingMinutes = 45,
                currentPhaseIndex = 3,
                totalPhases = 4,
                startedAt = "08:30 AM",
            ),
        ),
        ActiveServiceCardState.Success(
            data = ActiveServiceCardData(
                workOrderId = "wo-002",
                workshopName = "Taller Mecánico Especializado Automotriz Silva Hermanos & Asociados S.A.C.",
                phaseText = "Fase 3 de 4: En Rampa de Alineamiento y Balanceo Computarizado (Bahía 2) • Téc. Roberto Silva Velásquez",
                mechanicName = "Roberto Silva Velásquez",
                currentJob = "Diagnóstico avanzado y reemplazo integral de convertidor catalítico con sensor O2 Bosch original con recalibración de ECU",
                remainingMinutes = 90,
                currentPhaseIndex = 3,
                totalPhases = 4,
                startedAt = "08:30 AM",
            ),
        ),
        ActiveServiceCardState.Loading,
        ActiveServiceCardState.Empty,
    )
}

sealed interface ActiveServiceCardState {
    data class Success(val data: ActiveServiceCardData) : ActiveServiceCardState
    data object Loading : ActiveServiceCardState
    data object Empty : ActiveServiceCardState
}

@PreviewLightDark
@PreviewFontScale
@Composable
private fun ActiveServiceCardPreview(
    @PreviewParameter(ActiveServiceCardPreviewParameterProvider::class) state: ActiveServiceCardState,
) {
    ShiftIQTheme {
        when (state) {
            is ActiveServiceCardState.Success -> {
                ActiveServiceCard(
                    serviceData = state.data,
                    onViewLiveOrder = {},
                    onNavigateToWorkshops = {},
                )
            }
            is ActiveServiceCardState.Loading -> {
                ActiveServiceCard(
                    serviceData = null,
                    isLoading = true,
                    onViewLiveOrder = {},
                    onNavigateToWorkshops = {},
                )
            }
            is ActiveServiceCardState.Empty -> {
                ActiveServiceCard(
                    serviceData = null,
                    isLoading = false,
                    onViewLiveOrder = {},
                    onNavigateToWorkshops = {},
                )
            }
        }
    }
}
