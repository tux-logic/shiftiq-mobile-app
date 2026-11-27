package com.tuxlogic.shiftiq.mobile.feature.billing.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.designsystem.components.ShiftIQPageHeader
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.PrimaryBlue
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQBorderSlate
import com.tuxlogic.shiftiq.mobile.core.localization.LocalizedText as Text
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.PaymentMethod
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.Voucher
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.VoucherStatus
import kotlinx.coroutines.launch

@Composable
fun BillingVoucherDetailRoute(
    voucherId: String,
    onNavigateBack: () -> Unit,
    viewModel: BillingVoucherDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(voucherId) { viewModel.load(voucherId) }
    BillingVoucherDetailScreen(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onCollect = viewModel::collectPayment
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillingVoucherDetailScreen(
    uiState: BillingVoucherDetailUiState,
    onNavigateBack: () -> Unit,
    onCollect: (PaymentMethod, (AppResult<Voucher>) -> Unit) -> Unit
) {
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showPaymentDialog by remember { mutableStateOf(false) }
    var paymentMethod by remember { mutableStateOf(PaymentMethod.CASH) }
    val voucher = uiState.voucher

    Scaffold(snackbarHost = { SnackbarHost(snackbar) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            ShiftIQPageHeader(
                title = voucher?.let { "${voucher.type.label()} ${voucher.code()}" } ?: "Detalle de comprobante",
                subtitle = "Comprobante electrónico SUNAT",
                showBackButton = true,
                onBackPressed = onNavigateBack,
                actions = {
                    IconButton(onClick = {
                        scope.launch { snackbar.showSnackbar("Enlace de descarga copiado al portapapeles") }
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Compartir por WhatsApp", tint = androidx.compose.ui.graphics.Color.White)
                    }
                }
            )

            when {
                uiState.isLoading -> Column(
                    Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) { CircularProgressIndicator(color = PrimaryBlue) }
                voucher == null -> Column(
                    Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) { Text(uiState.errorMessage ?: "No se encontró el comprobante", color = MaterialTheme.colorScheme.error) }
                else -> Column(
                    Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    VoucherStatusCard(
                        voucher = voucher,
                        canCollect = voucher.status != VoucherStatus.PAID && voucher.balance.amount.signum() > 0,
                        onCollect = { showPaymentDialog = true }
                    )
                    VoucherCustomerCard(voucher)
                    VoucherAmountsCard(voucher)
                    if (voucher.payments.isNotEmpty()) {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White),
                            border = BorderStroke(1.dp, ShiftIQBorderSlate)
                        ) {
                            Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("Cobros registrados", fontWeight = FontWeight.Bold)
                                voucher.payments.forEach { payment ->
                                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("${payment.method.label()}${payment.paidAt?.let { " · ${it.take(16).replace('T', ' ')}" }.orEmpty()}")
                                        Text(payment.amount.formatted(), fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        }
                    }
                    uiState.errorMessage?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedButton(
                            modifier = Modifier.weight(1f),
                            onClick = { scope.launch { snackbar.showSnackbar("Enviando ticket a impresora térmica Bluetooth...") } }
                        ) {
                            Icon(Icons.Default.Print, contentDescription = null)
                            Text("Ticket térmico", modifier = Modifier.padding(start = 6.dp))
                        }
                        Button(
                            modifier = Modifier.weight(1f),
                            onClick = { scope.launch { snackbar.showSnackbar("Descargando PDF oficial SUNAT (${voucher.code()}.pdf)...") } }
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null)
                            Text("PDF SUNAT", modifier = Modifier.padding(start = 6.dp))
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }

    if (showPaymentDialog && voucher != null) {
        AlertDialog(
            onDismissRequest = { if (!uiState.isSavingPayment) showPaymentDialog = false },
            title = { Text("Registrar cobro en caja") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Selecciona el método de pago para liquidar este comprobante.")
                    PaymentMethod.entries.forEach { method ->
                        FilterChip(
                            selected = paymentMethod == method,
                            onClick = { paymentMethod = method },
                            label = { Text(method.label()) }
                        )
                    }
                    Text("Importe: ${voucher.balance.formatted()}", fontWeight = FontWeight.Bold)
                    uiState.errorMessage?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                }
            },
            confirmButton = {
                Button(
                    enabled = !uiState.isSavingPayment,
                    onClick = {
                        onCollect(paymentMethod) { result ->
                            when (result) {
                                is AppResult.Success -> {
                                    showPaymentDialog = false
                                    scope.launch { snackbar.showSnackbar("¡Cobro registrado exitosamente!") }
                                }
                                is AppResult.Failure -> Unit
                            }
                        }
                    }
                ) {
                    if (uiState.isSavingPayment) CircularProgressIndicator(Modifier.padding(end = 8.dp), strokeWidth = 2.dp)
                    Text(if (uiState.isSavingPayment) "Registrando…" else "Confirmar cobro")
                }
            },
            dismissButton = { TextButton(onClick = { showPaymentDialog = false }, enabled = !uiState.isSavingPayment) { Text("Cancelar") } }
        )
    }
}

@Composable
private fun VoucherStatusCard(voucher: Voucher, canCollect: Boolean, onCollect: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White),
        border = BorderStroke(1.dp, ShiftIQBorderSlate)
    ) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(voucher.code(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = PrimaryBlue)
            Text("Estado: ${voucher.status.name.replace('_', ' ')}", fontWeight = FontWeight.SemiBold)
            if (canCollect) {
                Button(onClick = onCollect, modifier = Modifier.fillMaxWidth()) { Text("Cobrar") }
            }
        }
    }
}

@Composable
private fun VoucherCustomerCard(voucher: Voucher) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White),
        border = BorderStroke(1.dp, ShiftIQBorderSlate)
    ) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Datos del adquiriente / cliente", fontWeight = FontWeight.Bold)
            VoucherInfoRow("Cliente / razón social", voucher.customerName)
            VoucherInfoRow(voucher.customerDocumentType.name, voucher.customerDocumentNumber)
            VoucherInfoRow("Tipo", voucher.type.label())
        }
    }
}

@Composable
private fun VoucherAmountsCard(voucher: Voucher) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White),
        border = BorderStroke(1.dp, ShiftIQBorderSlate)
    ) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Resumen fiscal", fontWeight = FontWeight.Bold)
            VoucherInfoRow("Total", voucher.totalAmount.formatted())
            VoucherInfoRow("Pagado", voucher.totalPaid.formatted())
            VoucherInfoRow("Saldo pendiente", voucher.balance.formatted())
            voucher.fiscalNumber?.takeIf(String::isNotBlank)?.let { VoucherInfoRow("Número fiscal", it) }
        }
    }
}

@Composable
private fun VoucherInfoRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontWeight = FontWeight.SemiBold)
    }
}

private fun Voucher.code(): String = listOfNotNull(series, correlative).joinToString("-").ifBlank { "Comprobante ${id.toString().take(8)}" }

private fun com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.VoucherType.label(): String =
    when (this) {
        com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.VoucherType.RECEIPT -> "Boleta"
        com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.VoucherType.INVOICE -> "Factura"
    }

private fun PaymentMethod.label(): String = when (this) {
    PaymentMethod.CASH -> "Efectivo"
    PaymentMethod.CREDIT_CARD -> "Tarjeta de crédito"
    PaymentMethod.DEBIT_CARD -> "Tarjeta de débito"
    PaymentMethod.BANK_TRANSFER -> "Transferencia bancaria"
}
