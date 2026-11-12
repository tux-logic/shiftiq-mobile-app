package com.tuxlogic.shiftiq.mobile.feature.operations.presentation.workorder

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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Navy900
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQSuccess
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQTheme
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQWarning
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.WhatsAppGreen
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveWorkOrderRoute(
    workOrderId: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    LiveWorkOrderScreen(
        workOrderId = workOrderId,
        onNavigateBack = onNavigateBack,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveWorkOrderScreen(
    workOrderId: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAdditionalTaskSheet by rememberSaveable { mutableStateOf(false) }
    var isAdditionalTaskApproved by rememberSaveable { mutableStateOf(false) }
    var showPaymentSuccessDialog by rememberSaveable { mutableStateOf(false) }

    val spacing = MaterialTheme.spacing
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LiveOrderTopBar(onNavigateBack = onNavigateBack, statusBarPadding = statusBarPadding)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = spacing.medium, vertical = spacing.smallMedium),
            verticalArrangement = Arrangement.spacedBy(spacing.medium)
        ) {
            OrderSummaryCard(orderId = workOrderId)
            OrderProgressStepperSection(activeStep = 3)
            AssignedMechanicCard()
            AuthorizedTasksSection(isAdditionalApproved = isAdditionalTaskApproved)
            CostBreakdownCard(isAdditionalApproved = isAdditionalTaskApproved)
            LiveOrderActionButtons(
                onOpenAdditionalTask = { showAdditionalTaskSheet = true },
                onPayNow = { showPaymentSuccessDialog = true }
            )
            Spacer(modifier = Modifier.height(spacing.medium))
        }
    }

    if (showAdditionalTaskSheet) {
        AdditionalTaskBottomSheet(
            onDismiss = { showAdditionalTaskSheet = false },
            onApprove = {
                isAdditionalTaskApproved = true
                showAdditionalTaskSheet = false
            },
            onReject = { showAdditionalTaskSheet = false }
        )
    }

    if (showPaymentSuccessDialog) {
        PaymentSuccessVoucherDialog(
            orderId = workOrderId,
            amountPen = if (isAdditionalTaskApproved) 253.70 else 212.40,
            onDismiss = { showPaymentSuccessDialog = false }
        )
    }
}

@Composable
private fun LiveOrderTopBar(
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
                top = statusBarPadding,
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
            Column {
                Text(
                    text = "Orden de Trabajo en Vivo",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Seguimiento en tiempo real",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(spacing.radiusSmall))
                .background(ShiftIQSuccess)
                .padding(horizontal = spacing.small, vertical = spacing.extraSmall)
        ) {
            Text(
                text = "EN RAMPA",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun OrderSummaryCard(
    orderId: String,
    modifier: Modifier = Modifier
) {
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
            Column {
                Text(
                    text = orderId,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Kia Rio 2019 • Placa ABC-123",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(spacing.radiusSmall))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(horizontal = spacing.smallMedium, vertical = spacing.extraSmall)
            ) {
                Text(
                    text = "Bahía 02",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun OrderProgressStepperSection(
    activeStep: Int,
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
            verticalArrangement = Arrangement.spacedBy(spacing.smallMedium)
        ) {
            Text(
                text = "Progreso de Bahía",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            StepperStepRow(stepNumber = 1, title = "Ingreso y Checklist Digital", isDone = true, isActive = false)
            StepperStepRow(stepNumber = 2, title = "Diagnóstico y Desmontaje", isDone = true, isActive = false)
            StepperStepRow(stepNumber = 3, title = "En Rampa: Reemplazo Sensor O2", isDone = false, isActive = true)
            StepperStepRow(stepNumber = 4, title = "Calibración ECU y Entrega", isDone = false, isActive = false)
        }
    }
}

@Composable
private fun StepperStepRow(
    stepNumber: Int,
    title: String,
    isDone: Boolean,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(spacing.badgeHeight)
                .clip(CircleShape)
                .background(
                    when {
                        isDone -> ShiftIQSuccess
                        isActive -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.surfaceContainerHigh
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isDone) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(spacing.iconSmall)
                )
            } else {
                Text(
                    text = stepNumber.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isActive) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(modifier = Modifier.width(spacing.smallMedium))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isActive || isDone) FontWeight.Bold else FontWeight.Normal,
            color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun AssignedMechanicCard(modifier: Modifier = Modifier) {
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
                        .size(spacing.avatarMedium)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(spacing.iconLarge)
                    )
                }
                Spacer(modifier = Modifier.width(spacing.smallMedium))
                Column {
                    Text(
                        text = "Jorge Ramírez",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Técnico Líder • Bahía 02 Certificada",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.small)) {
                IconButton(
                    onClick = {},
                    modifier = Modifier
                        .size(spacing.badgeHeight + spacing.small)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                ) {
                    Icon(Icons.Default.Call, contentDescription = "Llamar", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(spacing.iconMedium))
                }
                IconButton(
                    onClick = {},
                    modifier = Modifier
                        .size(spacing.badgeHeight + spacing.small)
                        .clip(CircleShape)
                        .background(WhatsAppGreen.copy(alpha = 0.15f))
                ) {
                    Icon(Icons.Default.Call, contentDescription = "WhatsApp", tint = WhatsAppGreen, modifier = Modifier.size(spacing.iconMedium))
                }
            }
        }
    }
}

@Composable
private fun AuthorizedTasksSection(
    isAdditionalApproved: Boolean,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Tareas y Evidencia Fotográfica",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(spacing.small))
        Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
            TaskCard(
                title = "Desmontaje e inspección sensor O2",
                isComplete = true,
                hasEvidence = true
            )
            TaskCard(
                title = "Instalación sensor O2 OEM Bosch",
                isComplete = false,
                progress = 0.75f
            )
            if (isAdditionalApproved) {
                TaskCard(
                    title = "Reemplazo filtro de aire de motor",
                    isComplete = true,
                    hasEvidence = true
                )
            }
        }
    }
}

@Composable
private fun TaskCard(
    title: String,
    isComplete: Boolean,
    hasEvidence: Boolean = false,
    progress: Float = 1.0f,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(spacing.radiusMedium),
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
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (isComplete) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ShiftIQSuccess, modifier = Modifier.size(spacing.iconMedium))
                }
            }
            if (!isComplete) {
                Spacer(modifier = Modifier.height(spacing.small))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(spacing.indicatorHeight)
                        .clip(RoundedCornerShape(spacing.radiusFull)),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            if (hasEvidence) {
                Spacer(modifier = Modifier.height(spacing.small))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Image, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(spacing.iconSmall))
                    Spacer(modifier = Modifier.width(spacing.extraSmall))
                    Text("Evidencia fotográfica adjunta", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
private fun CostBreakdownCard(
    isAdditionalApproved: Boolean,
    modifier: Modifier = Modifier
) {
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
                .padding(spacing.medium),
            verticalArrangement = Arrangement.spacedBy(spacing.extraSmall)
        ) {
            Text(
                text = "Desglose Transparente de Costos",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(spacing.extraSmall))
            CostItemRow(label = "Mano de obra calificada", amount = "S/ 60.00")
            CostItemRow(label = "Sensor O2 OEM Bosch", amount = "S/ 120.00")
            CostItemRow(label = "Calibración ECU (Bonif. ShiftIQ)", amount = "S/ 0.00", isDiscount = true)
            if (isAdditionalApproved) {
                CostItemRow(label = "Filtro de aire de motor", amount = "S/ 35.00")
            }
            CostItemRow(
                label = "IGV (18%)",
                amount = if (isAdditionalApproved) "S/ 38.70" else "S/ 32.40"
            )
            Spacer(modifier = Modifier.height(spacing.extraSmall))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total Garantizado",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (isAdditionalApproved) "S/ 253.70" else "S/ 212.40",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun CostItemRow(
    label: String,
    amount: String,
    isDiscount: Boolean = false,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = if (isDiscount) ShiftIQSuccess else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = amount,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = if (isDiscount) ShiftIQSuccess else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun LiveOrderActionButtons(
    onOpenAdditionalTask: () -> Unit,
    onPayNow: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        OutlinedButton(
            onClick = onOpenAdditionalTask,
            modifier = Modifier
                .fillMaxWidth()
                .height(spacing.buttonHeight),
            shape = RoundedCornerShape(spacing.radiusMedium)
        ) {
            Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(spacing.iconSmall))
            Spacer(modifier = Modifier.width(spacing.extraSmall))
            Text("Revisar Tarea Adicional Detectada")
        }
        Button(
            onClick = onPayNow,
            modifier = Modifier
                .fillMaxWidth()
                .height(spacing.buttonHeight),
            shape = RoundedCornerShape(spacing.radiusMedium),
            colors = ButtonDefaults.buttonColors(containerColor = Navy900, contentColor = Color.White)
        ) {
            Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(spacing.iconMedium))
            Spacer(modifier = Modifier.width(spacing.small))
            Text("Pagar con Yape / Tarjeta", fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdditionalTaskBottomSheet(
    onDismiss: () -> Unit,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.medium),
            verticalArrangement = Arrangement.spacedBy(spacing.smallMedium)
        ) {
            Text(
                text = "Tarea Adicional Requerida",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "El técnico detectó saturación severa en el filtro de aire durante la prueba de flujo. Reemplazo sugerido: S/ 35.00.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(
                onClick = onApprove,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(spacing.buttonHeight),
                shape = RoundedCornerShape(spacing.radiusMedium),
                colors = ButtonDefaults.buttonColors(containerColor = Navy900, contentColor = Color.White)
            ) {
                Text("Aprobar Tarea Adicional (+S/ 35.00)", fontWeight = FontWeight.Bold)
            }
            OutlinedButton(
                onClick = onReject,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(spacing.buttonHeight),
                shape = RoundedCornerShape(spacing.radiusMedium)
            ) {
                Text("Rechazar y Mantener Alcance Original")
            }
            Spacer(modifier = Modifier.height(spacing.medium))
        }
    }
}

@Composable
private fun PaymentSuccessVoucherDialog(
    orderId: String,
    amountPen: Double,
    onDismiss: () -> Unit
) {
    val spacing = MaterialTheme.spacing
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "¡Pago Procesado Exitosamente!",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.extraSmall)) {
                Text("Comprobante Digital Emitido:", style = MaterialTheme.typography.bodySmall)
                Text("Orden: $orderId", fontWeight = FontWeight.Bold)
                Text("Monto Total: S/ $amountPen", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.ExtraBold)
                Text("Tu vehículo está listo para retiro en Bahía 02.", style = MaterialTheme.typography.bodySmall)
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Aceptar")
            }
        },
        shape = RoundedCornerShape(spacing.radiusLarge)
    )
}

@PreviewLightDark
@PreviewFontScale
@Composable
private fun LiveWorkOrderScreenPreview() {
    ShiftIQTheme {
        LiveWorkOrderScreen(
            workOrderId = "OT-2026-0418",
            onNavigateBack = {}
        )
    }
}
