package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.garage.history

import android.content.Intent
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.LaunchedEffect
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
import com.tuxlogic.shiftiq.mobile.core.presentation.pdf.PdfDocumentExporter
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
    LaunchedEffect(voucherId) { viewModel.loadVoucher(voucherId) }
    val downloadLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { destination ->
        if (destination != null) {
            val safeVoucherNumber = uiState.voucherNumber
                .replace(Regex("[^A-Za-z0-9._-]"), "_")
                .ifBlank { "servicio" }
            val fallbackName = "ShiftIQ_Comprobante_$safeVoucherNumber.pdf"
            val lines = buildList {
                add("Comprobante: ${uiState.voucherNumber.ifBlank { "No disponible" }}")
                add("Tipo: ${uiState.voucherType.ifBlank { "No disponible" }}")
                add("Estado: ${uiState.status.ifBlank { "No disponible" }}")
                add("Fecha de emisión: ${uiState.emissionDate.ifBlank { "No disponible" }}")
                add("Emisor: ${listOf(uiState.companyName, uiState.tradeName).filter(String::isNotBlank).joinToString(" · ").ifBlank { "No disponible" }}")
                if (uiState.ruc.isNotBlank()) add("RUC: ${uiState.ruc}")
                if (uiState.address.isNotBlank()) add("Dirección: ${uiState.address}")
                add("Cliente: ${uiState.clientName.ifBlank { "No disponible" }}")
                if (uiState.clientDni.isNotBlank()) add("Documento del cliente: ${uiState.clientDni}")
                if (uiState.vehicleInfo.isNotBlank()) add("Vehículo: ${uiState.vehicleInfo}")
                uiState.odometerKm?.let { add("Odómetro: $it km") }
                if (uiState.serviceReason.isNotBlank()) add("Servicio: ${uiState.serviceReason}")
                add("")
                add("Detalle del comprobante")
                if (uiState.items.isEmpty()) {
                    add("El backend no devolvió el detalle de productos o servicios.")
                } else {
                    uiState.items.forEach { item ->
                        add("${item.quantity} × ${item.description} · Unitario S/ ${"%.2f".format(java.util.Locale.US, item.unitPrice)} · Total S/ ${"%.2f".format(java.util.Locale.US, item.totalPrice)}")
                    }
                }
                uiState.taxableAmount?.let { add("Base imponible: S/ ${"%.2f".format(java.util.Locale.US, it)}") }
                uiState.igvAmount?.let { add("IGV: S/ ${"%.2f".format(java.util.Locale.US, it)}") }
                uiState.totalPaid?.let { add("Total pagado: S/ ${"%.2f".format(java.util.Locale.US, it)}") }
                uiState.balance?.let { add("Saldo pendiente: S/ ${"%.2f".format(java.util.Locale.US, it)}") }
                if (uiState.paymentInfo.isNotBlank()) add("Pagos: ${uiState.paymentInfo}")
                if (uiState.sunatHash.isNotBlank()) add("Hash SUNAT: ${uiState.sunatHash}")
                add("")
                add("Copia PDF generada en la app a partir de los datos recibidos del backend.")
            }
            PdfDocumentExporter.write(context, destination, "Comprobante de servicio", lines)
                .onSuccess {
                    val savedName = context.contentResolver.query(
                        destination,
                        arrayOf(OpenableColumns.DISPLAY_NAME),
                        null,
                        null,
                        null
                    )?.use { cursor ->
                        if (cursor.moveToFirst()) cursor.getString(0) else null
                    } ?: fallbackName
                    val sizeBytes = context.contentResolver.openFileDescriptor(destination, "r")?.use { it.statSize } ?: -1L
                    val sizeText = if (sizeBytes >= 0) "${(sizeBytes / 1024).coerceAtLeast(1)} KB" else "No disponible"
                    viewModel.onPdfSaved(savedName, destination.toString(), sizeText)
                }
                .onFailure {
                    Toast.makeText(context, "No se pudo guardar el PDF: ${it.localizedMessage.orEmpty()}", Toast.LENGTH_LONG).show()
                }
        }
    }

    ServiceVoucherPdfScreen(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onZoomIn = viewModel::zoomIn,
        onZoomOut = viewModel::zoomOut,
        onDownloadPdf = {
            val safeVoucherNumber = uiState.voucherNumber
                .replace(Regex("[^A-Za-z0-9._-]"), "_")
                .ifBlank { "servicio" }
            downloadLauncher.launch("ShiftIQ_Comprobante_$safeVoucherNumber.pdf")
        },
        onShare = {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "Comprobante ${uiState.voucherNumber} · ${uiState.status} · ${uiState.totalPaid?.let { "S/ $it" } ?: "monto no disponible"} ${uiState.pdfUrl.orEmpty()}")
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

        when {
            uiState.isLoading -> Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                androidx.compose.material3.CircularProgressIndicator(color = PrimaryBlue)
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
                VoucherDetailsCard(uiState)
                Spacer(modifier = Modifier.height(spacing.medium))
            }
        }

        if (uiState.isLoaded) {
            PdfFixedActionFooter(onDownloadPdf = onDownloadPdf, onShare = onShare)
        }
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
private fun VoucherDetailsCard(state: ServiceVoucherPdfUiState) {
    val spacing = MaterialTheme.spacing
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(spacing.radiusMedium),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(spacing.strokeThin, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(spacing.medium),
            verticalArrangement = Arrangement.spacedBy(spacing.small)
        ) {
            Text("Comprobante de servicio", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(listOf(state.companyName, state.tradeName).filter(String::isNotBlank).joinToString(" · ").ifBlank { "Emisor no disponible" }, style = MaterialTheme.typography.bodyMedium)
            if (state.ruc.isNotBlank()) Text("RUC: ${state.ruc}", style = MaterialTheme.typography.bodySmall, color = Slate500)
            if (state.address.isNotBlank()) Text(state.address, style = MaterialTheme.typography.bodySmall, color = Slate500)
            HorizontalDivider(color = Slate200)
            Text("${state.voucherType.ifBlank { "Comprobante" }} ${state.voucherNumber}".trim(), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Text("Estado: ${state.status}", style = MaterialTheme.typography.bodySmall, color = PrimaryBlue)
            Text("Cliente: ${state.clientName.ifBlank { "No disponible" }}", style = MaterialTheme.typography.bodySmall)
            if (state.clientDni.isNotBlank()) Text("Documento: ${state.clientDni}", style = MaterialTheme.typography.bodySmall)
            if (state.vehicleInfo.isNotBlank()) Text("Vehículo: ${state.vehicleInfo}", style = MaterialTheme.typography.bodySmall)
            state.odometerKm?.let { Text("Odómetro: $it km", style = MaterialTheme.typography.bodySmall) }
            if (state.serviceReason.isNotBlank()) Text("Servicio: ${state.serviceReason}", style = MaterialTheme.typography.bodySmall)

            if (state.items.isNotEmpty()) {
                HorizontalDivider(color = Slate200)
                Text("Detalle", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                state.items.forEach { item ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("${item.quantity} × ${item.description}", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
                        Text("S/ ${String.format(java.util.Locale.US, "%.2f", item.totalPrice)}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                    }
                }
            } else {
                Text("The backend did not return line items for this voucher.", style = MaterialTheme.typography.bodySmall, color = Slate500)
            }

            HorizontalDivider(color = Slate200)
            state.totalPaid?.let { Text("Pagado: S/ ${String.format(java.util.Locale.US, "%.2f", it)}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold) }
            state.balance?.let { Text("Saldo: S/ ${String.format(java.util.Locale.US, "%.2f", it)}", style = MaterialTheme.typography.bodySmall) }
            if (state.paymentInfo.isNotBlank()) Text("Pagos: ${state.paymentInfo}", style = MaterialTheme.typography.bodySmall, color = Slate500)
            if (state.pdfUrl.isNullOrBlank()) Text("No hay un PDF adjunto en el backend; puedes guardar una copia con los datos del comprobante.", style = MaterialTheme.typography.bodySmall, color = Slate500)
        }
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
            uiState = ServiceVoucherPdfUiState(
                companyName = "Taller de demostración",
                tradeName = "Vista previa",
                ruc = "20600000000",
                address = "Dirección de ejemplo",
                voucherType = "INVOICE",
                voucherNumber = "F001-0001",
                status = "PAID",
                clientName = "Cliente de ejemplo",
                clientDni = "00000000",
                vehicleInfo = "Vehículo de ejemplo",
                odometerKm = 45102,
                serviceReason = "Mantenimiento preventivo de ejemplo",
                items = listOf(VoucherTableItem(1, "Servicio de ejemplo", 180.0, 180.0)),
                totalPaid = 180.0,
                balance = 0.0,
                isLoading = false,
                isLoaded = true
            ),
            onNavigateBack = {},
            onZoomIn = {},
            onZoomOut = {},
            onDownloadPdf = {},
            onShare = {},
            onDismissConfirmation = {}
        )
    }
}
