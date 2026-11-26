package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.diagnostic

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import com.tuxlogic.shiftiq.mobile.core.localization.LocalizedText as Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import android.widget.Toast
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tuxlogic.shiftiq.mobile.core.presentation.pdf.PdfDocumentExporter
import com.tuxlogic.shiftiq.mobile.core.designsystem.components.ShiftIQPageHeader
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.PrimaryBlue
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQBorderSlate
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQSuccess
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQTheme
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQWarning
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.spacing
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.DtcAlert

private data class DtcReferenceDetails(
    val title: String,
    val severity: String,
    val suggestedReviewKm: Int,
    val plainLanguage: String,
    val consumptionImpact: String,
    val checkEngineImpact: String,
    val safetyImpact: String,
    val location: String,
    val part: String,
    val recommendation: String
)

/**
 * Static SAE-style reference text for known DTC codes.
 * The code itself comes from live telemetry navigation; this block only explains the code.
 */
private fun referenceDetailsForDtc(code: String): DtcReferenceDetails = when (code.uppercase()) {
    "P0135" -> DtcReferenceDetails(
        title = "O2 sensor heater circuit (Bank 1 Sensor 1)",
        severity = "Medium",
        suggestedReviewKm = 500,
        plainLanguage = "The oxygen sensor heater circuit may not be warming the sensor as expected.",
        consumptionImpact = "May increase during cold starts",
        checkEngineImpact = "Check-engine light typically on",
        safetyImpact = "Usually no immediate driving risk",
        location = "Exhaust · Bank 1 · Sensor 1",
        part = "Oxygen sensor / heater circuit",
        recommendation = "Have a workshop verify the sensor and wiring with a live OBD-II scan."
    )
    "P0420" -> DtcReferenceDetails(
        title = "Catalyst system efficiency below threshold",
        severity = "Medium",
        suggestedReviewKm = 250,
        plainLanguage = "The catalytic converter may be operating below the expected efficiency threshold.",
        consumptionImpact = "Emissions may rise",
        checkEngineImpact = "Check-engine light typically on",
        safetyImpact = "Workshop inspection recommended",
        location = "Exhaust · catalytic converter",
        part = "Catalyst and oxygen sensors",
        recommendation = "Confirm sensors and exhaust health before replacing parts."
    )
    "P0301" -> DtcReferenceDetails(
        title = "Cylinder 1 misfire detected",
        severity = "High",
        suggestedReviewKm = 100,
        plainLanguage = "Cylinder 1 reported irregular combustion.",
        consumptionImpact = "Uneven fuel use possible",
        checkEngineImpact = "Check-engine light typically on",
        safetyImpact = "Avoid heavy acceleration until inspected",
        location = "Engine · cylinder 1",
        part = "Spark plug, coil, or injector for cylinder 1",
        recommendation = "Request a technical inspection promptly."
    )
    "P0171" -> DtcReferenceDetails(
        title = "System too lean (Bank 1)",
        severity = "Medium",
        suggestedReviewKm = 300,
        plainLanguage = "The air-fuel mixture on bank 1 may contain more air than expected.",
        consumptionImpact = "Performance can feel irregular",
        checkEngineImpact = "Check-engine light typically on",
        safetyImpact = "Preventive inspection recommended",
        location = "Intake · Bank 1",
        part = "Air intake and mixture sensors",
        recommendation = "Inspect intake leaks and mixture sensors."
    )
    else -> DtcReferenceDetails(
        title = "Diagnostic trouble code $code",
        severity = "Review",
        suggestedReviewKm = 500,
        plainLanguage = "This code was reported by vehicle telemetry. Use the workshop scan for a definitive interpretation.",
        consumptionImpact = "Not assessed from this code alone",
        checkEngineImpact = "Depends on the vehicle ECU",
        safetyImpact = "Not assessed from this code alone",
        location = "Not available for this code",
        part = "Not assigned automatically",
        recommendation = "Confirm the definition with workshop diagnostic equipment."
    )
}

@Composable
fun DtcDetailRoute(
    dtcCode: String,
    onNavigateBack: () -> Unit,
    onNavigateToWorkshops: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DtcDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val resolvedCode = uiState.dtcCode.ifBlank { dtcCode }
    val details = mergeLiveDtcDetails(resolvedCode, uiState.alert)
    val reportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { destination ->
        if (destination != null) {
            val reportLines = listOf(
                "Fecha: ${java.time.LocalDateTime.now()}",
                "Origen: telemetría OBD-II del vehículo reportada por ShiftIQ.",
                "Código DTC: $resolvedCode",
                "Descripción: ${details.title}",
                "Severidad: ${details.severity}.",
                "Ventana sugerida de revisión: próximos ${details.suggestedReviewKm} km.",
                "Interpretación: ${details.plainLanguage}",
                "Recomendación: ${details.recommendation}",
                uiState.alert?.createdAt?.let { "Detectado: $it" }.orEmpty()
            ).filter { it.isNotBlank() }
            PdfDocumentExporter.write(context, destination, "Informe DTC $resolvedCode", reportLines)
                .onSuccess {
                    Toast.makeText(context, "Informe PDF guardado", Toast.LENGTH_LONG).show()
                }
                .onFailure {
                    Toast.makeText(context, "No se pudo guardar el PDF: ${it.localizedMessage.orEmpty()}", Toast.LENGTH_LONG).show()
                }
        }
    }
    DtcDetailScreen(
        uiState = uiState,
        details = details,
        onNavigateBack = onNavigateBack,
        onNavigateToWorkshops = onNavigateToWorkshops,
        onDownloadReport = {
            reportLauncher.launch("ShiftIQ_DTC_${resolvedCode}_${System.currentTimeMillis()}.pdf")
        },
        modifier = modifier
    )
}

private fun mergeLiveDtcDetails(code: String, alert: DtcAlert?): DtcReferenceDetails {
    val reference = referenceDetailsForDtc(code)
    if (alert == null) return reference
    return reference.copy(
        title = alert.description.ifBlank { reference.title },
        severity = alert.severity.toDisplayLabel(),
        plainLanguage = alert.description.ifBlank { reference.plainLanguage }
    )
}

@Composable
private fun DtcDetailScreen(
    uiState: DtcDetailUiState,
    details: DtcReferenceDetails,
    onNavigateBack: () -> Unit,
    onNavigateToWorkshops: () -> Unit,
    onDownloadReport: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    val dtcCode = uiState.dtcCode

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        DtcDetailTopBar(
            dtcCode = dtcCode,
            onNavigateBack = onNavigateBack,
            statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
        )

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            }
            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(spacing.medium)
            ) {
                uiState.errorMessage?.let { message ->
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                DtcAlertOverviewCard(
                    dtcCode = dtcCode,
                    details = details,
                    sourceLabel = if (uiState.alert != null) "Alerta OBD-II en vivo" else "Referencia SAE"
                )
                PlainLanguageTranslationCard(details = details)
                DrivingImpactSection(details = details)
                TechnicalSpecCard(details = details)
                EstimateTwinCards()
                DtcActionButtons(
                    onFindWorkshop = onNavigateToWorkshops,
                    onDownloadReport = onDownloadReport
                )
            }
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
    ShiftIQPageHeader(
        title = "Diagnóstico $dtcCode",
        subtitle = "Código DTC activo · telemetría OBD-II",
        badgeLabel = dtcCode,
        showBackButton = true,
        onBackPressed = onNavigateBack,
        modifier = modifier
    )
}

@Composable
private fun DtcAlertOverviewCard(
    dtcCode: String,
    details: DtcReferenceDetails,
    sourceLabel: String,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(spacing.strokeMedium, ShiftIQWarning),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
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
                            text = details.title,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                DtcUrgencyPill(severity = details.severity)
            }
            Spacer(modifier = Modifier.height(spacing.smallMedium))
            Text(
                text = "$sourceLabel · revisar en ${details.suggestedReviewKm} km",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = ShiftIQWarning
            )
        }
    }
}

@Composable
private fun DtcUrgencyPill(severity: String, modifier: Modifier = Modifier) {
    val spacing = MaterialTheme.spacing
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(spacing.radiusSmall))
            .background(ShiftIQWarning.copy(alpha = 0.15f))
            .padding(horizontal = spacing.small, vertical = spacing.extraSmall)
    ) {
        Text(
            text = severity.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = ShiftIQWarning
        )
    }
}

@Composable
private fun PlainLanguageTranslationCard(details: DtcReferenceDetails, modifier: Modifier = Modifier) {
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
                text = "¿Qué significa en lenguaje sencillo?",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(spacing.small))
            Text(
                text = details.plainLanguage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DrivingImpactSection(details: DtcReferenceDetails, modifier: Modifier = Modifier) {
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
                desc = details.consumptionImpact,
                modifier = Modifier.weight(1f)
            )
            ImpactCard(
                icon = Icons.Default.Warning,
                title = "Check Engine",
                desc = details.checkEngineImpact,
                modifier = Modifier.weight(1f)
            )
            ImpactCard(
                icon = Icons.Default.CheckCircle,
                title = "Seguridad",
                desc = details.safetyImpact,
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
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(spacing.strokeThin, ShiftIQBorderSlate),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
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
private fun TechnicalSpecCard(details: DtcReferenceDetails, modifier: Modifier = Modifier) {
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
                text = "Especificación Técnica OEM",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(spacing.small))
            Text(
                text = "Ubicación: ${details.location}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Componente a verificar: ${details.part}",
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
            value = "No disponible",
            icon = Icons.Default.Build,
            modifier = Modifier.weight(1f)
        )
        EstimateMetricCard(
            title = "Tiempo en Bahía",
            value = "Por confirmar",
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
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue, contentColor = Color.White)
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
            uiState = DtcDetailUiState(dtcCode = "P0135", isLoading = false),
            details = referenceDetailsForDtc("P0135"),
            onNavigateBack = {},
            onNavigateToWorkshops = {}
        )
    }
}
