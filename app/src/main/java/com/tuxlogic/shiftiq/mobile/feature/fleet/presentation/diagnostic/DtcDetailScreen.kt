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
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import com.tuxlogic.shiftiq.mobile.core.presentation.pdf.PdfDocumentExporter
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Navy900
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQSuccess
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQTheme
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQWarning
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.spacing

private data class DtcDemoDetails(
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

private fun demoDetailsForDtc(code: String): DtcDemoDetails = when (code.uppercase()) {
    "P0135" -> DtcDemoDetails(
        title = "Circuito calefactor del sensor de oxígeno",
        severity = "Media · preventiva",
        suggestedReviewKm = 500,
        plainLanguage = "La simulación representa una falla del circuito que precalienta el sensor de oxígeno. No es una lectura real del vehículo.",
        consumptionImpact = "Puede aumentar en frío",
        checkEngineImpact = "Luz activa simulada",
        safetyImpact = "Sin riesgo simulado inmediato",
        location = "Escape, Banco 1 · Sensor 1",
        part = "Sensor de oxígeno / circuito calefactor",
        recommendation = "Verificar el sensor y su circuito con un escáner OBD-II real."
    )
    "P0420" -> DtcDemoDetails(
        title = "Eficiencia del catalizador baja",
        severity = "Media · revisar",
        suggestedReviewKm = 250,
        plainLanguage = "La simulación indica que el catalizador trabaja por debajo del umbral esperado. El código es ficticio y no confirma una falla.",
        consumptionImpact = "Emisiones elevadas simuladas",
        checkEngineImpact = "Luz activa simulada",
        safetyImpact = "Revisión recomendada",
        location = "Sistema de escape · catalizador",
        part = "Catalizador y sensores de oxígeno",
        recommendation = "Comprobar sensores y escape antes de reemplazar piezas."
    )
    "P0301" -> DtcDemoDetails(
        title = "Falla de combustión en cilindro 1",
        severity = "Alta · atención pronta",
        suggestedReviewKm = 100,
        plainLanguage = "La simulación detectó una combustión irregular en el cilindro 1. Es un dato ficticio para probar las alertas.",
        consumptionImpact = "Consumo irregular simulado",
        checkEngineImpact = "Luz activa simulada",
        safetyImpact = "Evita exigir el motor",
        location = "Motor · cilindro 1",
        part = "Bujía, bobina e inyector del cilindro 1",
        recommendation = "Solicitar una inspección técnica; no sustituir piezas solo por esta demo."
    )
    "P0171" -> DtcDemoDetails(
        title = "Mezcla de combustible demasiado pobre",
        severity = "Media · revisar",
        suggestedReviewKm = 300,
        plainLanguage = "La simulación representa una mezcla con más aire del esperado. No es una lectura real del motor.",
        consumptionImpact = "Rendimiento irregular simulado",
        checkEngineImpact = "Luz activa simulada",
        safetyImpact = "Revisión preventiva",
        location = "Admisión · Banco 1",
        part = "Admisión de aire y sensores de mezcla",
        recommendation = "Revisar admisión y sensores con equipo de diagnóstico."
    )
    else -> DtcDemoDetails(
        title = "Código de diagnóstico $code",
        severity = "Demo · validar",
        suggestedReviewKm = 500,
        plainLanguage = "Este código se muestra como ejemplo IoT. Confírmalo con una lectura OBD-II real antes de tomar decisiones.",
        consumptionImpact = "No disponible en demo",
        checkEngineImpact = "Código de ejemplo",
        safetyImpact = "No evaluado",
        location = "No disponible para este código",
        part = "No se asigna una pieza automáticamente",
        recommendation = "Confirmar la definición del código con un escáner OBD-II real."
    )
}

@Composable
fun DtcDetailRoute(
    dtcCode: String,
    onNavigateBack: () -> Unit,
    onNavigateToWorkshops: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val details = demoDetailsForDtc(dtcCode)
    val reportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { destination ->
        if (destination != null) {
            val reportLines = listOf(
                "Fecha: ${java.time.LocalDateTime.now()}",
                "Origen: datos IoT ficticios de demostración; no son una lectura real del vehículo.",
                "Código DTC: $dtcCode",
                "Descripción: ${details.title}",
                "Severidad de demostración: ${details.severity}.",
                "Ventana sugerida de revisión: próximos ${details.suggestedReviewKm} km.",
                "Interpretación: ${details.plainLanguage}",
                "Recomendación: ${details.recommendation}"
            )
            PdfDocumentExporter.write(context, destination, "Informe DTC $dtcCode", reportLines)
                .onSuccess {
                    Toast.makeText(context, "Informe PDF guardado", Toast.LENGTH_LONG).show()
                }
                .onFailure {
                    Toast.makeText(context, "No se pudo guardar el PDF: ${it.localizedMessage.orEmpty()}", Toast.LENGTH_LONG).show()
                }
        }
    }
    DtcDetailScreen(
        dtcCode = dtcCode,
        onNavigateBack = onNavigateBack,
        onNavigateToWorkshops = onNavigateToWorkshops,
        onDownloadReport = {
            reportLauncher.launch("ShiftIQ_DTC_${dtcCode}_${System.currentTimeMillis()}.pdf")
        },
        modifier = modifier
    )
}

@Composable
fun DtcDetailScreen(
    dtcCode: String,
    onNavigateBack: () -> Unit,
    onNavigateToWorkshops: () -> Unit,
    onDownloadReport: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    val details = demoDetailsForDtc(dtcCode)
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
            DtcAlertOverviewCard(dtcCode = dtcCode, details = details)
            PlainLanguageTranslationCard(details = details)
            DrivingImpactSection(details = details)
            TechnicalSpecCard(details = details)
            EstimateTwinCards()
            DtcActionButtons(
                onFindWorkshop = onNavigateToWorkshops,
                onDownloadReport = onDownloadReport
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
    details: DtcDemoDetails,
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
                text = "Lectura ficticia · revisar en ${details.suggestedReviewKm} km",
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
private fun PlainLanguageTranslationCard(details: DtcDemoDetails, modifier: Modifier = Modifier) {
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
                text = details.plainLanguage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DrivingImpactSection(details: DtcDemoDetails, modifier: Modifier = Modifier) {
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
private fun TechnicalSpecCard(details: DtcDemoDetails, modifier: Modifier = Modifier) {
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
