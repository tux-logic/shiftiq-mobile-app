package com.tuxlogic.shiftiq.mobile.feature.billing.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.activity.compose.BackHandler
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.designsystem.components.ShiftIQPageHeader
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.PrimaryBlue
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQBorderSlate
import com.tuxlogic.shiftiq.mobile.core.localization.LocalizedText as Text
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.DocumentType
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.Quote
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.QuoteId
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.QuoteStatus
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.PaymentMethod
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.Voucher
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.VoucherType
import kotlinx.coroutines.launch

@Composable
fun BillingDashboardRoute(
    onNavigateBack: () -> Unit,
    onOpenVoucher: (String) -> Unit,
    viewModel: BillingDashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { viewModel.refresh() }
    BillingDashboardScreen(
        uiState = uiState,
        onRefresh = viewModel::refresh,
        onNavigateBack = onNavigateBack,
        onOpenVoucher = onOpenVoucher,
        onEmitVoucher = viewModel::emitVoucher
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillingDashboardScreen(
    uiState: BillingDashboardUiState,
    onRefresh: () -> Unit,
    onNavigateBack: () -> Unit,
    onOpenVoucher: (String) -> Unit,
    onEmitVoucher: (
        QuoteId,
        VoucherType,
        DocumentType,
        String,
        String,
        (AppResult<Voucher>) -> Unit
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedVoucherType by remember { mutableStateOf<VoucherType?>(null) }
    var showCheckout by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val issuedQuoteIds = remember(uiState.vouchers) { uiState.vouchers.map { it.quoteId }.toSet() }
    val emitEligibleQuotes = remember(uiState.quotes, issuedQuoteIds) {
        uiState.quotes.filter { it.status == QuoteStatus.APPROVED && it.id !in issuedQuoteIds }
    }
    val visibleVouchers = remember(uiState.vouchers, selectedVoucherType) {
        uiState.vouchers.filter { selectedVoucherType == null || it.type == selectedVoucherType }
    }

    BackHandler(enabled = showCheckout) { showCheckout = false }
    if (showCheckout) {
        CheckoutVoucherScreen(
            approvedQuotes = emitEligibleQuotes,
            isEmitting = uiState.isEmittingVoucher,
            backendError = uiState.errorMessage,
            onNavigateBack = { showCheckout = false },
            onEmit = { quoteId, type, documentType, documentNumber, customerName, onError ->
                onEmitVoucher(quoteId, type, documentType, documentNumber, customerName) { result ->
                    when (result) {
                        is AppResult.Success -> {
                            showCheckout = false
                            onOpenVoucher(result.data.id.toString())
                        }
                        is AppResult.Failure -> onError(result.error.message)
                    }
                }
            },
            modifier = modifier
        )
    } else Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showCheckout = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Emitir comprobante") },
                containerColor = PrimaryBlue,
                contentColor = androidx.compose.ui.graphics.Color.White
            )
        }
    ) { scaffoldPadding ->
        Column(Modifier.fillMaxSize().padding(scaffoldPadding)) {
            ShiftIQPageHeader(
                title = "Facturación SUNAT y Cotizaciones",
                subtitle = "Comprobantes y cotizaciones",
                showBackButton = true,
                onBackPressed = onNavigateBack
            )
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    label = { Text("Comprobantes (${uiState.vouchers.size})") }
                )
                FilterChip(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    label = { Text("Cotizaciones (${uiState.quotes.size})") }
                )
            }
            if (selectedTab == 0) {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    BillingMetric("Facturación hoy", "S/ ${"%.2f".format(uiState.totalBilledToday)}", Modifier.weight(1f))
                    BillingMetric("Por cobrar", "${uiState.pendingPaymentsCount} comprobantes", Modifier.weight(1f))
                }
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(selectedVoucherType == null, { selectedVoucherType = null }, label = { Text("Todos") })
                    FilterChip(selectedVoucherType == VoucherType.RECEIPT, { selectedVoucherType = VoucherType.RECEIPT }, label = { Text("Boletas (B001)") })
                    FilterChip(selectedVoucherType == VoucherType.INVOICE, { selectedVoucherType = VoucherType.INVOICE }, label = { Text("Facturas (F001)") })
                }
            }
            PullToRefreshBox(
                isRefreshing = uiState.isLoading,
                onRefresh = onRefresh,
                modifier = Modifier.weight(1f).fillMaxWidth()
            ) {
                when {
                    uiState.isLoading && uiState.vouchers.isEmpty() && uiState.quotes.isEmpty() -> Column(
                        Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) { CircularProgressIndicator(color = PrimaryBlue) }
                    uiState.errorMessage != null && uiState.vouchers.isEmpty() && uiState.quotes.isEmpty() -> Column(
                        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) { Text(uiState.errorMessage, color = MaterialTheme.colorScheme.error) }
                    selectedTab == 0 && visibleVouchers.isEmpty() -> BillingEmptyState("No se encontraron comprobantes emitidos")
                    selectedTab == 1 && uiState.quotes.isEmpty() -> BillingEmptyState("No hay cotizaciones registradas")
                    selectedTab == 0 -> LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 88.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(visibleVouchers, key = { it.id.toString() }) { voucher ->
                            BillingRecordCard(
                                title = "${voucher.series.orEmpty()}-${voucher.correlative.orEmpty()}".trim('-')
                                    .ifBlank { "Comprobante ${voucher.id.toString().take(8)}" },
                                subtitle = "${voucher.customerName} · ${voucher.status.name.replace('_', ' ')}",
                                amount = voucher.totalAmount.formatted(),
                                detail = "Saldo: ${voucher.balance.formatted()}",
                                onClick = { onOpenVoucher(voucher.id.toString()) }
                            )
                        }
                    }
                    else -> LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 88.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(uiState.quotes, key = { it.id.toString() }) { quote ->
                            BillingRecordCard(
                                title = "Cotización ${quote.id.toString().take(8)}",
                                subtitle = "Orden ${quote.workOrderId} · ${quote.status.name}",
                                amount = quote.totalAmount.formatted(),
                                detail = "${quote.items.size} conceptos · descuento ${quote.discountPercentage}%",
                                onClick = {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Cotización ${quote.id.toString().take(8)} · ${quote.totalAmount.formatted()}")
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

}

@Composable
private fun CheckoutVoucherScreen(
    approvedQuotes: List<Quote>,
    isEmitting: Boolean,
    backendError: String?,
    onNavigateBack: () -> Unit,
    onEmit: (
        QuoteId, VoucherType, DocumentType, String, String, (String) -> Unit
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedQuote by remember(approvedQuotes) { mutableStateOf(approvedQuotes.firstOrNull()) }
    var documentMenuExpanded by remember { mutableStateOf(false) }
    var voucherType by remember { mutableStateOf(VoucherType.RECEIPT) }
    var documentType by remember { mutableStateOf(DocumentType.DNI) }
    var documentNumber by remember { mutableStateOf(VoucherCheckoutDemoData.dni) }
    var customerName by remember { mutableStateOf(VoucherCheckoutDemoData.customerName) }
    var fiscalAddress by remember { mutableStateOf(VoucherCheckoutDemoData.fiscalAddress) }
    var paymentMethod by remember { mutableStateOf(PaymentMethod.CASH) }
    var validationMessage by remember { mutableStateOf<String?>(null) }
    val maxDigits = when (documentType) {
        DocumentType.DNI -> 8
        DocumentType.RUC -> 11
        DocumentType.CE -> 12
    }
    val total = VoucherCheckoutDemoData.items.sumOf { it.totalPrice }
    val subtotal = total / 1.18
    val tax = total - subtotal

    Column(modifier.fillMaxSize()) {
        ShiftIQPageHeader(
            title = "Liquidación y facturación",
            subtitle = "Emisión de comprobante interno",
            showBackButton = true,
            onBackPressed = onNavigateBack
        )
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text("ORDEN DE TRABAJO", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = PrimaryBlue.copy(alpha = 0.08f)),
                border = BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Build, contentDescription = null, tint = PrimaryBlue)
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text("Vehículo: ${VoucherCheckoutDemoData.vehicle}", fontWeight = FontWeight.Bold)
                        Text("Orden de Trabajo: #${VoucherCheckoutDemoData.workOrderNumber}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

                Text("TIPO DE COMPROBANTE", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = voucherType == VoucherType.RECEIPT,
                        onClick = {
                            voucherType = VoucherType.RECEIPT
                            documentType = DocumentType.DNI
                            if (documentNumber == VoucherCheckoutDemoData.ruc) {
                                documentNumber = VoucherCheckoutDemoData.dni
                                customerName = VoucherCheckoutDemoData.customerName
                            }
                        },
                        label = { Text("Boleta de venta") }
                    )
                    FilterChip(
                        selected = voucherType == VoucherType.INVOICE,
                        onClick = {
                            voucherType = VoucherType.INVOICE
                            documentType = DocumentType.RUC
                            if (documentNumber == VoucherCheckoutDemoData.dni) {
                                documentNumber = VoucherCheckoutDemoData.ruc
                                customerName = VoucherCheckoutDemoData.invoiceCustomerName
                            }
                        },
                        label = { Text("Factura") }
                    )
                }

                Text("DATOS DEL CLIENTE Y DOCUMENTO", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box {
                        OutlinedButton(onClick = { documentMenuExpanded = true }) { Text(documentType.name) }
                        DropdownMenu(expanded = documentMenuExpanded, onDismissRequest = { documentMenuExpanded = false }) {
                            DocumentType.entries.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type.name) },
                                    onClick = {
                                        documentType = type
                                        documentNumber = documentNumber.take(when (type) {
                                            DocumentType.DNI -> 8
                                            DocumentType.RUC -> 11
                                            DocumentType.CE -> 12
                                        })
                                        documentMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    OutlinedTextField(
                        value = documentNumber,
                        onValueChange = { documentNumber = it.filter(Char::isDigit).take(maxDigits); validationMessage = null },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        label = { Text("Número de documento") }
                    )
                }
                OutlinedTextField(
                    value = customerName,
                    onValueChange = { customerName = it; validationMessage = null },
                    modifier = Modifier.fillMaxWidth(), singleLine = true,
                    label = { Text("Nombre / razón social") }
                )
                OutlinedTextField(
                    value = fiscalAddress,
                    onValueChange = { fiscalAddress = it },
                    modifier = Modifier.fillMaxWidth(), singleLine = true,
                    label = { Text("Dirección fiscal") }
                )

                Text("DETALLE DE SERVICIOS Y REPUESTOS EN BAHÍA", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White),
                    border = BorderStroke(1.dp, ShiftIQBorderSlate),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    VoucherCheckoutDemoData.items.forEachIndexed { index, item ->
                        Row(
                            Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                if (item.type == VoucherDemoItemType.SERVICE) Icons.Default.Build else Icons.Default.Inventory2,
                                contentDescription = null,
                                tint = PrimaryBlue
                            )
                            Column(Modifier.weight(1f)) {
                                Text(item.description, fontWeight = FontWeight.Medium)
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text("Cant:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("${item.quantity} x ${item.unitPrice.currencyText()}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Text(item.totalPrice.currencyText(), fontWeight = FontWeight.Bold)
                        }
                        if (index < VoucherCheckoutDemoData.items.lastIndex) androidx.compose.material3.HorizontalDivider()
                    }
                }
                FiscalSummary(subtotal = subtotal, tax = tax, total = total)

                Text("MÉTODO DE PAGO Y COBRO EN CAJA", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                Row(
                    Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PaymentMethod.entries.forEach { method ->
                        FilterChip(
                            selected = paymentMethod == method,
                            onClick = { paymentMethod = method },
                            label = { Text(method.displayName()) }
                        )
                    }
                }
                (validationMessage ?: backendError)?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
                Button(
                    enabled = !isEmitting,
                    onClick = {
                        when {
                            customerName.isBlank() -> validationMessage = "Ingresa el nombre o razón social."
                            documentNumber.isBlank() -> validationMessage = "Ingresa el número de documento."
                            documentType == DocumentType.DNI && documentNumber.length != 8 -> validationMessage = "DNI requiere 8 dígitos."
                            documentType == DocumentType.RUC && documentNumber.length != 11 -> validationMessage = "RUC requiere 11 dígitos."
                            voucherType == VoucherType.INVOICE && documentType != DocumentType.RUC -> validationMessage = "La factura requiere un RUC válido."
                            selectedQuote == null -> validationMessage = "No hay una cotización aprobada para emitir el comprobante."
                            else -> onEmit(selectedQuote!!.id, voucherType, documentType, documentNumber, customerName) { validationMessage = it }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 50.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    if (isEmitting) CircularProgressIndicator(Modifier.padding(end = 8.dp), strokeWidth = 2.dp)
                    if (isEmitting) Text("Emitiendo comprobante…") else Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = null)
                        Text(if (voucherType == VoucherType.RECEIPT) "EMITIR BOLETA" else "EMITIR FACTURA")
                        Text(total.currencyText())
                    }
                }
        }
    }
}

@Composable
private fun FiscalSummary(subtotal: Double, tax: Double, total: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White),
        border = BorderStroke(1.dp, ShiftIQBorderSlate),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SummaryRow("Subtotal", subtotal.currencyText())
            SummaryRow("IGV (18%)", tax.currencyText())
            androidx.compose.material3.HorizontalDivider()
            SummaryRow("Total", total.currencyText(), bold = true)
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String, bold: Boolean = false) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal)
        Text(value, fontWeight = FontWeight.Bold)
    }
}

private fun Double.currencyText(): String = "S/ ${"%.2f".format(this)}"

private fun PaymentMethod.displayName(): String = when (this) {
    PaymentMethod.CASH -> "Efectivo en Caja"
    PaymentMethod.CREDIT_CARD -> "Tarjeta de Crédito"
    PaymentMethod.DEBIT_CARD -> "Tarjeta de Débito"
    PaymentMethod.BANK_TRANSFER -> "Transferencia Bancaria"
}

@Composable
private fun BillingMetric(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White), border = BorderStroke(1.dp, ShiftIQBorderSlate)) {
        Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = PrimaryBlue)
        }
    }
}

@Composable
private fun BillingRecordCard(title: String, subtitle: String, amount: String, detail: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White),
        border = BorderStroke(1.dp, ShiftIQBorderSlate)
    ) {
        Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = null, tint = PrimaryBlue)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, fontWeight = FontWeight.Bold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(detail, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(amount, fontWeight = FontWeight.Bold, color = PrimaryBlue)
        }
    }
}

@Composable
private fun BillingEmptyState(message: String) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = null, tint = PrimaryBlue)
        Text(message, modifier = Modifier.padding(top = 10.dp), fontWeight = FontWeight.SemiBold)
        Text("Desliza hacia abajo para actualizar.", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
