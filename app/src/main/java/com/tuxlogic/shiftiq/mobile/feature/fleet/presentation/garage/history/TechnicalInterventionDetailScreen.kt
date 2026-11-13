package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.garage.history

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.hilt.navigation.compose.hiltViewModel
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Navy900
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ObdTeal
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.PrimaryBlue
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQSuccess
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQTheme
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate100
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate200
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate300
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate400
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate500
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate700
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.spacing

@Composable
fun TechnicalInterventionDetailRoute(
    interventionId: String,
    onNavigateBack: () -> Unit,
    onNavigateToVoucherPdf: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TechnicalInterventionDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    TechnicalInterventionDetailScreen(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onNavigateToVoucherPdf = { onNavigateToVoucherPdf(uiState.voucherId) },
        modifier = modifier
    )
}

@Composable
fun TechnicalInterventionDetailScreen(
    uiState: TechnicalInterventionDetailUiState,
    onNavigateBack: () -> Unit,
    onNavigateToVoucherPdf: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        InterventionTopBar(
            onNavigateBack = onNavigateBack,
            statusBarPadding = statusBarPadding
        )

        when {
            uiState.isLoading -> Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
            uiState.errorMessage != null -> Box(Modifier.weight(1f).fillMaxWidth().padding(spacing.large), contentAlignment = Alignment.Center) {
                Text(uiState.errorMessage, color = MaterialTheme.colorScheme.error)
            }
            uiState.isLoaded -> Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(spacing.medium),
                verticalArrangement = Arrangement.spacedBy(spacing.medium)
            ) {
                MetadataSubBar(expNumber = uiState.expNumber)
                InterventionSummaryCard(uiState = uiState)
                if (uiState.laserCalibrations.isNotEmpty()) LaserCalibrationSection(calibrations = uiState.laserCalibrations)
                if (uiState.protocolOperations.isNotEmpty()) ExecutedOperationsSection(operations = uiState.protocolOperations)
                if (uiState.usedSupplies.isNotEmpty()) SuppliesUsedSection(supplies = uiState.usedSupplies)
                if (uiState.technicianObservation.isNotBlank() || uiState.warrantyPolicy.isNotBlank()) {
                    TechnicianObservationSection(
                        observation = uiState.technicianObservation,
                        warrantyPolicy = uiState.warrantyPolicy
                    )
                }
                Spacer(modifier = Modifier.height(spacing.medium))
            }
        }

        if (!uiState.isLoading && uiState.errorMessage == null && uiState.voucherId.isNotBlank()) {
            FixedBottomActionFooter(onNavigateToVoucherPdf = onNavigateToVoucherPdf)
        }
    }
}

@Composable
private fun InterventionTopBar(
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
                top = statusBarPadding + spacing.small,
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
                    contentDescription = "Volver",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(spacing.extraSmall))
            Text(
                text = "Detalle De Intervención",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        Box(
            modifier = Modifier
                .size(spacing.badgeHeight + spacing.smallMedium)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(spacing.iconLarge)
            )
        }
    }
}

@Composable
private fun MetadataSubBar(expNumber: String, modifier: Modifier = Modifier) {
    val spacing = MaterialTheme.spacing
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "EXP-TEC $expNumber",
            style = MaterialTheme.typography.labelMedium,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = Slate700
        )
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(spacing.radiusExtraSmall))
                .background(Color.White)
                .border(spacing.strokeThin, Slate300, RoundedCornerShape(spacing.radiusExtraSmall))
                .padding(horizontal = spacing.small, vertical = spacing.extraSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = Navy900,
                modifier = Modifier.size(spacing.iconSmall)
            )
            Spacer(modifier = Modifier.width(spacing.extraSmall))
            Text(
                text = "REGISTRO SELLADO",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Navy900
            )
        }
    }
}

@Composable
private fun InterventionSummaryCard(
    uiState: TechnicalInterventionDetailUiState,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(spacing.radiusMedium),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(spacing.strokeThin, Slate200)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.medium),
            verticalArrangement = Arrangement.spacedBy(spacing.smallMedium)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(spacing.radiusExtraSmall))
                        .background(ShiftIQSuccess.copy(alpha = 0.12f))
                        .padding(horizontal = spacing.small, vertical = spacing.extraSmall)
                ) {
                    Text(
                        text = uiState.statusText,
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = ShiftIQSuccess
                    )
                }
                if (uiState.odometerKm > 0) Text(
                    text = "ODÓM: ${uiState.odometerKm} km",
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = Slate700
                )
            }
            if (uiState.serviceTitle.isNotBlank()) Text(
                text = uiState.serviceTitle,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (uiState.executionDate.isNotBlank()) Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = Slate400,
                    modifier = Modifier.size(spacing.iconSmall)
                )
                Spacer(modifier = Modifier.width(spacing.compact))
                Text(
                    text = uiState.executionDate,
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    color = Slate500
                )
            }
            if (uiState.workshopName.isNotBlank() || uiState.workshopAddress.isNotBlank()) {
                InterventionEntityRow(Icons.Default.Place, uiState.workshopName, uiState.workshopAddress)
            }
            if (uiState.technicianName.isNotBlank() || uiState.technicianCert.isNotBlank()) {
                InterventionEntityRow(Icons.Default.Engineering, "TÉCNICO ESPECIALISTA", listOf(uiState.technicianName, uiState.technicianCert).filter(String::isNotBlank).joinToString(" · "))
            }
            if (uiState.totalCostPen != null) {
                HorizontalDivider(color = Slate100, thickness = spacing.strokeThin)
                CostLiquidationFooter(
                    totalCost = uiState.totalCostPen,
                    paymentMethod = uiState.paymentMethod
                )
            }
        }
    }
}

@Composable
private fun InterventionEntityRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(spacing.radiusSmall))
            .background(Slate100.copy(alpha = 0.5f))
            .border(spacing.strokeThin, Slate200.copy(alpha = 0.5f), RoundedCornerShape(spacing.radiusSmall))
            .padding(spacing.smallMedium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(spacing.badgeHeight + spacing.small)
                .clip(RoundedCornerShape(spacing.radiusSmall))
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Navy900,
                modifier = Modifier.size(spacing.iconMedium)
            )
        }
        Spacer(modifier = Modifier.width(spacing.smallMedium))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Slate700
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Slate500
            )
        }
    }
}

@Composable
private fun CostLiquidationFooter(
    totalCost: Double,
    paymentMethod: String,
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
                text = "Costo Total Liquidado",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (paymentMethod.isNotBlank()) Text(
                text = paymentMethod,
                style = MaterialTheme.typography.bodySmall,
                color = Slate400
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "S/ ${String.format(java.util.Locale.US, "%.2f", totalCost)}",
                style = MaterialTheme.typography.titleLarge,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.ExtraBold,
                color = Navy900
            )
            Text(
                text = "PEN (INC. IGV)",
                style = MaterialTheme.typography.labelSmall,
                fontFamily = FontFamily.Monospace,
                color = Slate400
            )
        }
    }
}

@Composable
private fun LaserCalibrationSection(
    calibrations: List<CalibrationValueItem>,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Valores Técnicos y Calibración",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(spacing.radiusExtraSmall))
                    .background(ObdTeal.copy(alpha = 0.12f))
                    .padding(horizontal = spacing.small, vertical = spacing.micro)
            ) {
                Text(
                    text = "((•)) LÁSER 3D",
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = ObdTeal
                )
            }
        }

        calibrations.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.smallMedium)
            ) {
                rowItems.forEach { item ->
                    CalibrationCard(item = item, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun CalibrationCard(
    item: CalibrationValueItem,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(spacing.radiusSmall),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(spacing.strokeThin, Slate200)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.smallMedium),
            verticalArrangement = Arrangement.spacedBy(spacing.extraSmall)
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.labelSmall,
                color = Slate500
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${item.initialValue} → ${item.finalValue}",
                    style = MaterialTheme.typography.labelMedium,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = Slate700
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(spacing.radiusExtraSmall))
                    .background(ShiftIQSuccess.copy(alpha = 0.12f))
                    .padding(horizontal = spacing.compact, vertical = spacing.micro)
            ) {
                Text(
                    text = item.statusText,
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = ShiftIQSuccess
                )
            }
        }
    }
}

@Composable
private fun ExecutedOperationsSection(
    operations: List<ProtocolOperationItem>,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        Text(
            text = "Protocolo de Operaciones Ejecutadas",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(spacing.radiusMedium),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(spacing.strokeThin, Slate200)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(spacing.medium),
                verticalArrangement = Arrangement.spacedBy(spacing.smallMedium)
            ) {
                operations.forEachIndexed { index, op ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing.smallMedium)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(spacing.iconExtraLarge)
                                .clip(RoundedCornerShape(spacing.radiusExtraSmall))
                                .background(Navy900),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(spacing.iconSmall)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = op.title,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Slate700
                            )
                            Text(
                                text = op.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = Slate400
                            )
                        }
                    }
                    if (index < operations.size - 1) {
                        HorizontalDivider(color = Slate100, thickness = spacing.strokeThin)
                    }
                }
            }
        }
    }
}

@Composable
private fun SuppliesUsedSection(
    supplies: List<UsedSupplyItem>,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        Text(
            text = "Insumos y Repuestos Utilizados",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        supplies.forEach { supply ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(spacing.radiusMedium),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(spacing.strokeThin, Slate200)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(spacing.medium),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(spacing.smallMedium),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(spacing.stepperNodeSize)
                                .clip(RoundedCornerShape(spacing.radiusSmall))
                                .background(Slate100),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Build,
                                contentDescription = null,
                                tint = Slate700,
                                modifier = Modifier.size(spacing.iconMedium)
                            )
                        }
                        Column {
                            Text(
                                text = supply.name,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Slate700
                            )
                            Text(
                                text = supply.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = Slate400
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(spacing.radiusExtraSmall))
                            .background(ShiftIQSuccess.copy(alpha = 0.12f))
                            .padding(horizontal = spacing.small, vertical = spacing.extraSmall)
                    ) {
                        Text(
                            text = supply.badge,
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = ShiftIQSuccess
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TechnicianObservationSection(
    observation: String,
    warrantyPolicy: String,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(spacing.radiusMedium),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(spacing.strokeThin, Slate200)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.medium),
            verticalArrangement = Arrangement.spacedBy(spacing.smallMedium)
        ) {
            Text(
                text = "Observación del Técnico",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = observation,
                style = MaterialTheme.typography.bodySmall,
                color = Slate500
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(spacing.radiusSmall))
                    .background(ShiftIQSuccess.copy(alpha = 0.08f))
                    .border(spacing.strokeThin, ShiftIQSuccess.copy(alpha = 0.2f), RoundedCornerShape(spacing.radiusSmall))
                    .padding(spacing.smallMedium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = ShiftIQSuccess,
                    modifier = Modifier.size(spacing.iconLarge)
                )
                Spacer(modifier = Modifier.width(spacing.smallMedium))
                Column {
                    Text(
                        text = "PÓLIZA DE COBERTURA",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = ShiftIQSuccess
                    )
                    Text(
                        text = warrantyPolicy,
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate700
                    )
                }
            }
        }
    }
}

@Composable
private fun FixedBottomActionFooter(
    onNavigateToVoucherPdf: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .border(spacing.strokeThin, Slate200)
            .padding(spacing.medium)
    ) {
        Button(
            onClick = onNavigateToVoucherPdf,
            shape = RoundedCornerShape(spacing.radiusSmall),
            colors = ButtonDefaults.buttonColors(containerColor = Navy900, contentColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .height(spacing.buttonHeight)
        ) {
            Icon(
                imageVector = Icons.Default.PictureAsPdf,
                contentDescription = null,
                modifier = Modifier.size(spacing.iconMedium)
            )
            Spacer(modifier = Modifier.width(spacing.small))
            Text(
                text = "Ver Factura / Boleta Digital (PDF)",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@PreviewLightDark
@PreviewFontScale
@Composable
private fun TechnicalInterventionDetailScreenPreview() {
    ShiftIQTheme {
        TechnicalInterventionDetailScreen(
            uiState = TechnicalInterventionDetailUiState(
                expNumber = "OT-2026-0418",
                isSealed = true,
                statusText = "COMPLETED",
                odometerKm = 45102,
                serviceTitle = "Preventive maintenance",
                executionDate = "12/11/2026",
                workshopName = "Taller asignado",
                totalCostPen = 280.0,
                protocolOperations = listOf(ProtocolOperationItem("Inspection", "COMPLETED · S/ 80.00")),
                usedSupplies = listOf(UsedSupplyItem("Oil filter", "1 × S/ 35.00")),
                voucherId = "example-voucher-id",
                isLoading = false,
                isLoaded = true
            ),
            onNavigateBack = {},
            onNavigateToVoucherPdf = {}
        )
    }
}
