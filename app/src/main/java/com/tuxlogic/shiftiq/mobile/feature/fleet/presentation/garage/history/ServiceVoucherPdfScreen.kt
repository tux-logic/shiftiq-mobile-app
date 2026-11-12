package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.garage.history

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.hilt.navigation.compose.hiltViewModel
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Navy900
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
fun ServiceVoucherPdfRoute(
    voucherId: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ServiceVoucherPdfViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    ServiceVoucherPdfScreen(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onZoomIn = viewModel::zoomIn,
        onZoomOut = viewModel::zoomOut,
        onDownloadPdf = viewModel::onDownloadPdf,
        onShare = {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "Comprobante electrónico ShiftIq: ${uiState.voucherNumber} por S/ ${uiState.totalPaid} PEN.")
                type = "text/plain"
            }
            context.startActivity(Intent.createChooser(sendIntent, "Compartir comprobante"))
        },
        onDismissConfirmation = viewModel::onDismissSavedConfirmation,
        modifier = modifier
    )
}

@Composable
fun ServiceVoucherPdfScreen(
    uiState: ServiceVoucherPdfUiState,
    onNavigateBack: () -> Unit,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onDownloadPdf: () -> Unit,
    onShare: () -> Unit,
    onDismissConfirmation: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        PdfTopBar(
            onNavigateBack = onNavigateBack,
            statusBarPadding = statusBarPadding
        )

        PdfViewerControlsBar(
            zoomPercent = uiState.zoomPercent,
            onZoomIn = onZoomIn,
            onZoomOut = onZoomOut
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(spacing.medium),
            verticalArrangement = Arrangement.spacedBy(spacing.medium)
        ) {
            FacturaSheetCard(uiState = uiState)
            Spacer(modifier = Modifier.height(spacing.medium))
        }

        PdfFixedActionFooter(
            onDownloadPdf = onDownloadPdf,
            onShare = onShare
        )
    }

    if (uiState.isSavedConfirmationVisible) {
        PdfSavedConfirmationDialog(
            fileName = uiState.savedFileName,
            filePath = uiState.savedFilePath,
            fileSizeText = uiState.fileSizeText,
            fileFormatText = uiState.fileFormatText,
            onDismiss = onDismissConfirmation
        )
    }
}

@Composable
private fun PdfTopBar(
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
                text = "Comprobante De Servicio",
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
private fun PdfViewerControlsBar(
    zoomPercent: Int,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Slate100)
            .border(spacing.strokeThin, Slate200)
            .padding(horizontal = spacing.medium, vertical = spacing.compact),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Description,
                contentDescription = null,
                tint = Slate500,
                modifier = Modifier.size(spacing.iconMedium)
            )
            Spacer(modifier = Modifier.width(spacing.compact))
            Column {
                Text(
                    text = "Página 1 de 1",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Slate700
                )
                Text(
                    text = "PDF/A • 218 KB",
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                    color = Slate500
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.small)
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(spacing.radiusFull))
                    .background(Color.White)
                    .border(spacing.strokeThin, Slate300, RoundedCornerShape(spacing.radiusFull))
                    .padding(horizontal = spacing.small, vertical = spacing.micro),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "−",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Slate700,
                    modifier = Modifier
                        .clickable(onClick = onZoomOut)
                        .padding(horizontal = spacing.extraSmall)
                )
                Spacer(modifier = Modifier.width(spacing.small))
                Text(
                    text = "$zoomPercent%",
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = Slate700
                )
                Spacer(modifier = Modifier.width(spacing.small))
                Text(
                    text = "+",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Slate700,
                    modifier = Modifier
                        .clickable(onClick = onZoomIn)
                        .padding(horizontal = spacing.extraSmall)
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(spacing.radiusExtraSmall))
                    .background(Color.White)
                    .border(spacing.strokeThin, Slate300, RoundedCornerShape(spacing.radiusExtraSmall))
                    .padding(spacing.compact),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Fullscreen,
                    contentDescription = null,
                    tint = Slate500,
                    modifier = Modifier.size(spacing.iconSmall)
                )
            }
        }
    }
}

@Composable
private fun FacturaSheetCard(
    uiState: ServiceVoucherPdfUiState,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(spacing.radiusSmall),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(spacing.strokeThin, Slate200),
        elevation = CardDefaults.cardElevation(defaultElevation = spacing.elevationSmall)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.medium),
            verticalArrangement = Arrangement.spacedBy(spacing.smallMedium)
        ) {
            VoucherCompanyHeader(uiState = uiState)
            HorizontalDivider(color = Slate200, thickness = spacing.strokeThin)
            ClientAndVehicleBlock(uiState = uiState)
            VoucherItemsTable(items = uiState.items)
            HorizontalDivider(color = Slate200, thickness = spacing.strokeThin)
            FinancialSummaryBlock(uiState = uiState)
            HorizontalDivider(color = Slate200, thickness = spacing.strokeThin)
            OfficialSunatStampSection(sunatHash = uiState.sunatHash)
        }
    }
}

@Composable
private fun VoucherCompanyHeader(
    uiState: ServiceVoucherPdfUiState,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Business,
                    contentDescription = null,
                    tint = Navy900,
                    modifier = Modifier.size(spacing.iconMedium)
                )
                Spacer(modifier = Modifier.width(spacing.extraSmall))
                Text(
                    text = uiState.companyName,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            Text(
                text = uiState.tradeName,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = Slate700
            )
            Text(
                text = "RUC: ${uiState.ruc}",
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.Monospace,
                color = Slate500
            )
            Text(
                text = uiState.address,
                style = MaterialTheme.typography.labelSmall,
                color = Slate400
            )
        }

        Spacer(modifier = Modifier.width(spacing.small))

        Box(
            modifier = Modifier
                .width(spacing.slotCardWidth + spacing.large)
                .clip(RoundedCornerShape(spacing.micro))
                .border(spacing.strokeThin, Color.Black, RoundedCornerShape(spacing.micro))
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black)
                        .padding(vertical = spacing.micro),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.voucherType,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Column(
                    modifier = Modifier.padding(spacing.extraSmall),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = uiState.voucherNumber,
                        style = MaterialTheme.typography.labelMedium,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "Fecha: ${uiState.emissionDate}",
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        color = Slate500
                    )
                }
            }
        }
    }
}

@Composable
private fun ClientAndVehicleBlock(
    uiState: ServiceVoucherPdfUiState,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(spacing.radiusSmall),
        colors = CardDefaults.cardColors(containerColor = Slate100.copy(alpha = 0.5f)),
        border = BorderStroke(spacing.strokeThin, Slate200)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.smallMedium),
            verticalArrangement = Arrangement.spacedBy(spacing.extraSmall)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = uiState.clientName,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Slate700
                )
                Text(
                    text = "DNI: ${uiState.clientDni}",
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = Slate500
                )
            }
            HorizontalDivider(color = Slate200, thickness = spacing.strokeThin)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "VEHÍCULO / PLACA",
                        style = MaterialTheme.typography.labelSmall,
                        color = Slate400
                    )
                    Text(
                        text = uiState.vehicleInfo,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Slate700
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "ODÓMETRO REGISTRADO",
                        style = MaterialTheme.typography.labelSmall,
                        color = Slate400
                    )
                    Text(
                        text = "${uiState.odometerKm} km",
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = Slate700
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(spacing.radiusFull))
                    .background(Color.White)
                    .border(spacing.strokeThin, Slate300, RoundedCornerShape(spacing.radiusFull))
                    .padding(horizontal = spacing.small, vertical = spacing.micro)
            ) {
                Text(
                    text = "Motivo: ${uiState.serviceReason}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Slate700
                )
            }
        }
    }
}

@Composable
private fun VoucherItemsTable(
    items: List<VoucherTableItem>,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Slate100)
                .padding(vertical = spacing.extraSmall, horizontal = spacing.extraSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "CANT",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Slate500,
                modifier = Modifier.width(spacing.controlButtonSize)
            )
            Text(
                text = "DESCRIPCIÓN / ÍTEM",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Slate500,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "P. UNIT",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Slate500,
                textAlign = TextAlign.End,
                modifier = Modifier.width(spacing.extraExtraLarge + spacing.small)
            )
            Text(
                text = "TOTAL",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Slate500,
                textAlign = TextAlign.End,
                modifier = Modifier.width(spacing.extraExtraLarge + spacing.small)
            )
        }

        items.forEach { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = spacing.compact, horizontal = spacing.extraSmall),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${item.quantity}x",
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                    color = Slate500,
                    modifier = Modifier.width(spacing.controlButtonSize)
                )
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = Slate700,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "S/ ${String.format(java.util.Locale.US, "%.2f", item.unitPrice)}",
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                    color = Slate500,
                    textAlign = TextAlign.End,
                    modifier = Modifier.width(spacing.extraExtraLarge + spacing.small)
                )
                Text(
                    text = "S/ ${String.format(java.util.Locale.US, "%.2f", item.totalPrice)}",
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = Slate700,
                    textAlign = TextAlign.End,
                    modifier = Modifier.width(spacing.extraExtraLarge + spacing.small)
                )
            }
            HorizontalDivider(color = Slate100, thickness = spacing.strokeThin)
        }
    }
}

@Composable
private fun FinancialSummaryBlock(
    uiState: ServiceVoucherPdfUiState,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.extraSmall)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Op. Gravada:", style = MaterialTheme.typography.bodySmall, color = Slate500)
            Text(
                text = "S/ ${String.format(java.util.Locale.US, "%.2f", uiState.taxableAmount)} PEN",
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.Monospace,
                color = Slate700
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "I.G.V. (18%):", style = MaterialTheme.typography.bodySmall, color = Slate500)
            Text(
                text = "S/ ${String.format(java.util.Locale.US, "%.2f", uiState.igvAmount)} PEN",
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.Monospace,
                color = Slate700
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(spacing.radiusSmall))
                .background(PrimaryBlue.copy(alpha = 0.08f))
                .border(spacing.strokeThin, PrimaryBlue.copy(alpha = 0.25f), RoundedCornerShape(spacing.radiusSmall))
                .padding(spacing.smallMedium)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.micro)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TOTAL PAGADO:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Navy900
                    )
                    Text(
                        text = "S/ ${String.format(java.util.Locale.US, "%.2f", uiState.totalPaid)} PEN",
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.ExtraBold,
                        color = Navy900
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CreditCard,
                        contentDescription = null,
                        tint = Slate500,
                        modifier = Modifier.size(spacing.iconSmall)
                    )
                    Spacer(modifier = Modifier.width(spacing.extraSmall))
                    Text(
                        text = uiState.paymentInfo,
                        style = MaterialTheme.typography.labelSmall,
                        color = Slate500
                    )
                }
            }
        }
    }
}

@Composable
private fun OfficialSunatStampSection(
    sunatHash: String,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing.smallMedium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(spacing.qrCodeSize)
                .clip(RoundedCornerShape(spacing.radiusExtraSmall))
                .border(spacing.strokeThin, Slate300, RoundedCornerShape(spacing.radiusExtraSmall))
                .padding(spacing.extraSmall),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.QrCode2,
                contentDescription = "Código QR SUNAT",
                tint = Color.Black,
                modifier = Modifier.fillMaxSize()
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Verified,
                    contentDescription = null,
                    tint = ShiftIQSuccess,
                    modifier = Modifier.size(spacing.iconSmall)
                )
                Spacer(modifier = Modifier.width(spacing.extraSmall))
                Text(
                    text = "EMISIÓN SUNAT VÁLIDA",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            Text(
                text = "HASH: $sunatHash",
                style = MaterialTheme.typography.labelSmall,
                fontFamily = FontFamily.Monospace,
                color = Slate500
            )
            Text(
                text = "Certificado Digital ShiftIq • Garantía 6 meses o 5,000 km",
                style = MaterialTheme.typography.labelSmall,
                color = Slate400
            )
        }
    }
}

@Composable
private fun PdfFixedActionFooter(
    onDownloadPdf: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .border(spacing.strokeThin, Slate200)
            .padding(spacing.medium),
        verticalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        Button(
            onClick = onDownloadPdf,
            shape = RoundedCornerShape(spacing.radiusSmall),
            colors = ButtonDefaults.buttonColors(containerColor = Navy900, contentColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .height(spacing.buttonHeight)
        ) {
            Icon(
                imageVector = Icons.Default.Download,
                contentDescription = null,
                modifier = Modifier.size(spacing.iconMedium)
            )
            Spacer(modifier = Modifier.width(spacing.small))
            Text(
                text = "Descargar Archivo PDF en Dispositivo ↓",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
        }
        OutlinedButton(
            onClick = onShare,
            shape = RoundedCornerShape(spacing.radiusSmall),
            border = BorderStroke(spacing.strokeThin, Navy900),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Navy900),
            modifier = Modifier
                .fillMaxWidth()
                .height(spacing.buttonHeight)
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = null,
                modifier = Modifier.size(spacing.iconMedium)
            )
            Spacer(modifier = Modifier.width(spacing.small))
            Text(
                text = "Compartir por Correo o WhatsApp",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@PreviewLightDark
@PreviewFontScale
@Composable
private fun ServiceVoucherPdfScreenPreview() {
    ShiftIQTheme {
        ServiceVoucherPdfScreen(
            uiState = ServiceVoucherPdfUiState(),
            onNavigateBack = {},
            onZoomIn = {},
            onZoomOut = {},
            onDownloadPdf = {},
            onShare = {},
            onDismissConfirmation = {}
        )
    }
}
