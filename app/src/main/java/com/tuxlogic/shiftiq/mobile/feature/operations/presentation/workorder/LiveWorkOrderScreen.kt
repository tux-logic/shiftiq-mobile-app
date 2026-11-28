package com.tuxlogic.shiftiq.mobile.feature.operations.presentation.workorder

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import java.math.BigDecimal
import java.math.RoundingMode
import com.tuxlogic.shiftiq.mobile.core.designsystem.components.ShiftIQPageHeader
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.PrimaryBlue
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQBorderSlate
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQSuccess
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQTheme
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate100
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate500
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate700
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.spacing
import com.tuxlogic.shiftiq.mobile.core.localization.LocalizedText as Text
import com.tuxlogic.shiftiq.mobile.feature.inventory.domain.model.ProductItem
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.ReservedProduct
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.ServiceItem
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.TaskStatus
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.WorkOrder
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.WorkOrderStatus
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.isCancelled
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.isCompleted
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.isPaid
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.WorkOrderTask
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.DocumentType
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.PaymentMethod
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.VoucherType
import com.tuxlogic.shiftiq.mobile.feature.billing.presentation.VoucherCheckoutDemoData
import com.tuxlogic.shiftiq.mobile.feature.billing.presentation.VoucherDemoItemType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

@Composable
fun LiveWorkOrderRoute(
    workOrderId: String,
    onNavigateBack: () -> Unit,
    onOpenVoucher: (String) -> Unit = {},
    isOwnerAdmin: Boolean,
    canExecuteTasks: Boolean = true,
    modifier: Modifier = Modifier,
    viewModel: LiveWorkOrderViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(workOrderId) { viewModel.loadWorkOrder(workOrderId) }
    LiveWorkOrderScreen(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        isOwnerAdmin = isOwnerAdmin,
        canExecuteTasks = canExecuteTasks,
        onCanExecuteTask = viewModel::canExecuteTask,
        onStartTask = viewModel::startTask,
        onCompleteTask = viewModel::completeTask,
        onCompleteWorkOrder = viewModel::completeWorkOrder,
        onAddTask = viewModel::addTask,
        onReserveProduct = viewModel::reserveProduct,
        onUpdateProductQuantity = viewModel::updateProductQuantity,
        onRemoveProduct = viewModel::removeProduct,
        onUploadEvidence = viewModel::uploadEvidence,
        onRemoveEvidence = viewModel::removeEvidence,
        onEmitVoucher = viewModel::emitWorkOrderVoucher,
        onClearEmittedVoucher = viewModel::clearEmittedVoucher,
        onOpenVoucher = onOpenVoucher,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveWorkOrderScreen(
    uiState: LiveWorkOrderUiState,
    onNavigateBack: () -> Unit,
    isOwnerAdmin: Boolean,
    canExecuteTasks: Boolean,
    onCanExecuteTask: (String?) -> Boolean,
    onStartTask: (String) -> Unit,
    onCompleteTask: (String) -> Unit,
    onCompleteWorkOrder: () -> Unit,
    onAddTask: (String, String, String) -> Unit,
    onReserveProduct: (String, String, Int) -> Unit,
    onUpdateProductQuantity: (String, String, Int) -> Unit,
    onRemoveProduct: (String, String) -> Unit,
    onUploadEvidence: (String, ByteArray, String, String) -> Unit,
    onRemoveEvidence: (String, String) -> Unit,
    onEmitVoucher: (VoucherType, DocumentType, String, String) -> Unit,
    onClearEmittedVoucher: () -> Unit,
    onOpenVoucher: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    val order = uiState.workOrder
    var selectedTaskId by remember { mutableStateOf<String?>(null) }
    var showAddTaskDialog by remember { mutableStateOf(false) }
    var showCompleteDialog by remember { mutableStateOf(false) }
    var showCheckoutDialog by remember { mutableStateOf(false) }

    Column(modifier.fillMaxSize().background(Color(0xFFF8FAFC))) {
        ShiftIQPageHeader(
            title = "Orden de trabajo",
            subtitle = order?.formattedNumber.orEmpty(),
            showBackButton = true,
            onBackPressed = onNavigateBack
        )
        when {
            uiState.isLoading && order == null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
            order == null -> ErrorState(uiState.errorMessage ?: "No se encontró la orden.")
            else -> Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 28.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                uiState.errorMessage?.let { ErrorBanner(it) }
                WorkOrderSummaryCard(
                    order = order,
                    vehicleDescription = uiState.vehicleDescription,
                    customerName = uiState.customerName,
                    customerPhone = uiState.customerPhone
                )
                if (order.entryInspectionImages.isNotEmpty()) {
                    ImageStrip(title = "Inspección de recepción", urls = order.entryInspectionImages)
                }
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("TAREAS EN BAHÍA (${order.tasks.size})", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Slate700, modifier = Modifier.weight(1f))
                    if (isOwnerAdmin && !order.status.isCompleted) {
                        TextButton(onClick = { showAddTaskDialog = true }, enabled = !uiState.isSaving) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Text("Agregar tarea")
                        }
                    }
                }
                if (order.tasks.isEmpty()) {
                    MessageCard("Todavía no hay tareas registradas en esta orden.")
                } else {
                    order.tasks.forEach { task ->
                        val canExecute = canExecuteTasks && (isOwnerAdmin || onCanExecuteTask(task.assignedMechanicId?.toString()))
                        WorkOrderTaskCard(
                            task = task,
                            canExecute = canExecute && !order.status.isCompleted,
                            isSaving = uiState.isSaving,
                            onOpen = { selectedTaskId = task.id.toString() },
                            onStart = { onStartTask(task.id.toString()) },
                            onComplete = { onCompleteTask(task.id.toString()) }
                        )
                    }
                }
                FinancialSummary(order)
                if (order.status.isCompleted) {
                    WorkOrderPaymentStatusCard(isPaid = order.status.isPaid)
                }
                if (isOwnerAdmin && order.status.isCompleted && !order.status.isPaid) {
                    Button(
                        onClick = { showCheckoutDialog = true },
                        enabled = !uiState.isSaving,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("EMITIR COMPROBANTE", fontWeight = FontWeight.Bold)
                    }
                }
                if (isOwnerAdmin && !order.status.isCompleted && !order.status.isCancelled) {
                    Button(
                        onClick = { showCompleteDialog = true },
                        enabled = !uiState.isSaving,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0F172A),
                            contentColor = Color.White
                        )
                    ) {
                        if (uiState.isSaving) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp, color = Color.White)
                        else Icon(Icons.Default.CheckCircle, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("FINALIZAR Y LIQUIDAR (${order.totalAmount.formatted()})", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    val task = selectedTaskId?.let { id -> order?.tasks?.firstOrNull { it.id.toString() == id } }
    if (task != null && order != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedTaskId = null }
        ) {
            WorkOrderTaskDetailSheet(
                task = task,
                canExecute = canExecuteTasks && (isOwnerAdmin || onCanExecuteTask(task.assignedMechanicId?.toString())),
                isSaving = uiState.isSaving || order.status.isCompleted,
                availableProducts = uiState.availableProducts,
                onStart = { onStartTask(task.id.toString()) },
                onComplete = { onCompleteTask(task.id.toString()) },
                onAddProduct = { id, quantity -> onReserveProduct(task.id.toString(), id, quantity) },
                onUpdateProductQuantity = { id, quantity -> onUpdateProductQuantity(task.id.toString(), id, quantity) },
                onRemoveProduct = { id -> onRemoveProduct(task.id.toString(), id) },
                onUploadEvidence = { bytes, name, mime -> onUploadEvidence(task.id.toString(), bytes, name, mime) },
                onRemoveEvidence = { url -> onRemoveEvidence(task.id.toString(), url) }
            )
        }
    }

    if (showAddTaskDialog && order != null) {
        AddTaskDialog(
            services = uiState.availableServices,
            mechanics = uiState.availableMechanics,
            isSaving = uiState.isSaving,
            onDismiss = { showAddTaskDialog = false },
            onAdd = { serviceId, mechanicId, description ->
                showAddTaskDialog = false
                onAddTask(serviceId, mechanicId, description)
            }
        )
    }

    if (showCompleteDialog && order != null) {
        AlertDialog(
            onDismissRequest = { showCompleteDialog = false },
            title = { Text("Finalizar orden") },
            text = { Text("Se cerrará la orden ${order.formattedNumber} por un monto de ${order.totalAmount.formatted()}.") },
            confirmButton = {
                Button(onClick = { showCompleteDialog = false; onCompleteWorkOrder() }, enabled = !uiState.isSaving) {
                    Text("Confirmar y liquidar")
                }
            },
            dismissButton = { TextButton(onClick = { showCompleteDialog = false }) { Text("Cancelar") } }
        )
    }

    if (showCheckoutDialog && order != null) {
        WorkOrderCheckoutDialog(
            uiState = uiState,
            isSaving = uiState.isSaving,
            onDismiss = { if (!uiState.isSaving) showCheckoutDialog = false },
            onEmit = { type, documentType, documentNumber, customerName ->
                onEmitVoucher(type, documentType, documentNumber, customerName)
            }
        )
    }

    LaunchedEffect(uiState.emittedVoucher?.id) {
        uiState.emittedVoucher?.let { voucher ->
            showCheckoutDialog = false
            onClearEmittedVoucher()
            onOpenVoucher(voucher.id.toString())
        }
    }
}

@Composable
private fun WorkOrderPaymentStatusCard(isPaid: Boolean) {
    val statusColor = if (isPaid) PrimaryBlue else ShiftIQSuccess
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = statusColor.copy(alpha = 0.08f)),
        border = BorderStroke(1.dp, statusColor.copy(alpha = 0.32f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = statusColor)
            Spacer(Modifier.width(8.dp))
            Text(
                if (isPaid) "ORDEN PAGADA" else "ORDEN COMPLETADA · PENDIENTE DE PAGO",
                color = statusColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun WorkOrderCheckoutDialog(
    uiState: LiveWorkOrderUiState,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onEmit: (VoucherType, DocumentType, String, String) -> Unit
) {
    val order = uiState.workOrder ?: return
    var voucherType by remember { mutableStateOf(VoucherType.RECEIPT) }
    var documentType by remember { mutableStateOf(DocumentType.DNI) }
    var documentNumber by remember { mutableStateOf(VoucherCheckoutDemoData.dni) }
    var customerName by remember {
        mutableStateOf(uiState.customerName.takeIf(String::isNotBlank) ?: VoucherCheckoutDemoData.customerName)
    }
    var customerAddress by remember { mutableStateOf(VoucherCheckoutDemoData.fiscalAddress) }
    var paymentMethod by remember { mutableStateOf(PaymentMethod.CASH) }
    var validationMessage by remember { mutableStateOf<String?>(null) }
    val checkoutItems = remember(order) { VoucherCheckoutDemoData.itemsFor(order) }
    val total = checkoutItems.fold(BigDecimal.ZERO) { sum, item ->
        sum + BigDecimal.valueOf(item.totalPrice)
    }.setScale(2, RoundingMode.HALF_UP)
    val subtotal = total.divide(BigDecimal("1.18"), 2, RoundingMode.HALF_UP)
    val tax = total.subtract(subtotal)
    val vehicleLabel = order.vehiclePlate.takeIf { it.isNotBlank() && it != "S/P" }
        ?: VoucherCheckoutDemoData.vehicle
    val orderLabel = order.id.toString().take(8)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(Modifier.fillMaxSize().background(Color(0xFFF8FAFC))) {
            ShiftIQPageHeader(
                title = "Liquidación y facturación",
                subtitle = "Emisión de comprobante interno",
                showBackButton = true,
                onBackPressed = onDismiss
            )
            Column(
                Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = PrimaryBlue.copy(alpha = 0.08f)),
                    border = BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Build, contentDescription = null, tint = PrimaryBlue)
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text("Vehículo: $vehicleLabel", fontWeight = FontWeight.Bold)
                            Text("Orden de Trabajo: #$orderLabel", style = MaterialTheme.typography.bodySmall, color = Slate700)
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    CheckoutSectionTitle("TIPO DE COMPROBANTE")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = voucherType == VoucherType.RECEIPT,
                            onClick = {
                                voucherType = VoucherType.RECEIPT
                                documentType = DocumentType.DNI
                                if (documentNumber == VoucherCheckoutDemoData.ruc) {
                                    documentNumber = VoucherCheckoutDemoData.dni
                                    customerName = uiState.customerName.takeIf(String::isNotBlank)
                                        ?: VoucherCheckoutDemoData.customerName
                                }
                            },
                            label = { Text("Boleta de venta") },
                            modifier = Modifier.weight(1f)
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
                            label = { Text("Factura") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    CheckoutSectionTitle("DATOS DEL CLIENTE Y DOCUMENTO")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = documentType == DocumentType.DNI,
                            onClick = { documentType = DocumentType.DNI; documentNumber = documentNumber.take(8); validationMessage = null },
                            label = { Text("DNI") }
                        )
                        FilterChip(
                            selected = documentType == DocumentType.RUC,
                            onClick = { documentType = DocumentType.RUC; documentNumber = documentNumber.take(11); validationMessage = null },
                            label = { Text("RUC") }
                        )
                        FilterChip(
                            selected = documentType == DocumentType.CE,
                            onClick = { documentType = DocumentType.CE; validationMessage = null },
                            label = { Text("CE") }
                        )
                    }
                    androidx.compose.material3.OutlinedTextField(
                        value = documentNumber,
                        onValueChange = {
                            documentNumber = when (documentType) {
                                DocumentType.DNI -> it.filter(Char::isDigit).take(8)
                                DocumentType.RUC -> it.filter(Char::isDigit).take(11)
                                DocumentType.CE -> it.take(20)
                            }
                            validationMessage = null
                        },
                        label = { Text("Número de ${documentType.name}") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    androidx.compose.material3.OutlinedTextField(
                        value = customerName,
                        onValueChange = { customerName = it; validationMessage = null },
                        label = { Text("Nombre o razón social") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    androidx.compose.material3.OutlinedTextField(
                        value = customerAddress,
                        onValueChange = { customerAddress = it },
                        label = { Text("Dirección fiscal") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    CheckoutSectionTitle("DETALLE DE SERVICIOS Y REPUESTOS EN BAHÍA")
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, ShiftIQBorderSlate),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column {
                            checkoutItems.forEachIndexed { index, item ->
                                CheckoutLineItem(
                                    title = item.description,
                                    subtitle = "Cant: ${item.quantity} × ${com.tuxlogic.shiftiq.mobile.core.model.Money(item.unitPrice).formatted()}",
                                    amount = com.tuxlogic.shiftiq.mobile.core.model.Money(item.totalPrice).formatted(),
                                    icon = if (item.type == VoucherDemoItemType.SERVICE) Icons.Default.Build else Icons.Default.Inventory2
                                )
                                if (index < checkoutItems.lastIndex) HorizontalDivider(color = ShiftIQBorderSlate)
                            }
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    CheckoutSectionTitle("RESUMEN FISCAL")
                    Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(12.dp)) {
                        Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            CheckoutAmountRow("Subtotal", com.tuxlogic.shiftiq.mobile.core.model.Money(subtotal).formatted())
                            CheckoutAmountRow("IGV (18%)", com.tuxlogic.shiftiq.mobile.core.model.Money(tax).formatted())
                            HorizontalDivider(color = ShiftIQBorderSlate)
                            CheckoutAmountRow("Total", com.tuxlogic.shiftiq.mobile.core.model.Money(total).formatted(), emphasize = true)
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    CheckoutSectionTitle("MÉTODO DE PAGO Y COBRO EN CAJA")
                    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PaymentMethod.entries.forEach { method ->
                            FilterChip(
                                selected = paymentMethod == method,
                                onClick = { paymentMethod = method },
                                label = { Text(method.checkoutLabel()) }
                            )
                        }
                    }
                }

                validationMessage?.let { ErrorBanner(it) }
                uiState.errorMessage?.let { ErrorBanner(it) }
                Button(
                    enabled = !isSaving,
                    onClick = {
                        val documentError = when (documentType) {
                            DocumentType.DNI -> if (documentNumber.length == 8) null else "DNI requiere 8 dígitos."
                            DocumentType.RUC -> if (documentNumber.length == 11) null else "RUC requiere 11 dígitos."
                            DocumentType.CE -> if (documentNumber.isNotBlank()) null else "Ingresa el número de CE."
                        }
                        when {
                            customerName.isBlank() -> validationMessage = "Ingresa el nombre o razón social."
                            documentError != null -> validationMessage = documentError
                            else -> onEmit(voucherType, documentType, documentNumber, customerName)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = ShiftIQSuccess),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    if (isSaving) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp, color = Color.White)
                    else {
                        Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("EMITIR ${voucherType.checkoutLabel().uppercase()} ${com.tuxlogic.shiftiq.mobile.core.model.Money(total).formatted()}", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun CheckoutSectionTitle(title: String) {
    Text(title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Slate700)
}

@Composable
private fun CheckoutLineItem(title: String, subtitle: String, amount: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(icon, contentDescription = null, tint = PrimaryBlue)
        Column(Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Medium, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Slate500)
        }
        Text(amount, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun CheckoutAmountRow(label: String, amount: String, emphasize: Boolean = false) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = if (emphasize) Color(0xFF0F172A) else Slate700, fontWeight = if (emphasize) FontWeight.Bold else FontWeight.Normal)
        Text(amount, color = if (emphasize) PrimaryBlue else Color(0xFF0F172A), fontWeight = FontWeight.Bold)
    }
}

private fun PaymentMethod.checkoutLabel(): String = when (this) {
    PaymentMethod.CASH -> "Efectivo"
    PaymentMethod.CREDIT_CARD -> "Tarjeta de crédito"
    PaymentMethod.DEBIT_CARD -> "Tarjeta de débito"
    PaymentMethod.BANK_TRANSFER -> "Transferencia bancaria"
}

private fun VoucherType.checkoutLabel(): String = when (this) {
    VoucherType.RECEIPT -> "Boleta"
    VoucherType.INVOICE -> "Factura"
}

@Composable
private fun WorkOrderSummaryCard(
    order: WorkOrder,
    vehicleDescription: String,
    customerName: String,
    customerPhone: String
) {
    Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, ShiftIQBorderSlate)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(order.formattedNumber.ifBlank { "OT-${order.internalNumber}" }, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
            Text(vehicleDescription, style = MaterialTheme.typography.bodyMedium, color = Slate700)
            StatusPill(statusLabel(order.status.name), statusColor(order.status.name))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.PersonOutline, contentDescription = null, modifier = Modifier.size(16.dp), tint = Slate500)
                Spacer(Modifier.width(6.dp))
                Text(
                    "Cliente: $customerName",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = customerPhone.isBlank())
                )
                if (customerPhone.isNotBlank()) {
                    Spacer(Modifier.width(12.dp))
                    Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(14.dp), tint = Slate500)
                    Spacer(Modifier.width(4.dp))
                    Text(customerPhone, style = MaterialTheme.typography.bodySmall, maxLines = 1)
                }
            }
            order.diagnosticSummary.takeIf(String::isNotBlank)?.let {
                Text(it, style = MaterialTheme.typography.bodyMedium)
            }
            Text("Kilometraje de ingreso: ${order.mileageIn} km", style = MaterialTheme.typography.bodySmall, color = Slate700)
            order.createdAt.takeIf(String::isNotBlank)?.let {
                Text("Creada: ${it.take(16).replace('T', ' ')}", style = MaterialTheme.typography.labelSmall, color = Slate500)
            }
        }
    }
}

@Composable
private fun WorkOrderTaskCard(
    task: WorkOrderTask,
    canExecute: Boolean,
    isSaving: Boolean,
    onOpen: () -> Unit,
    onStart: () -> Unit,
    onComplete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onOpen),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, ShiftIQBorderSlate)
    ) {
        Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(if (task.status == TaskStatus.COMPLETED) Icons.Default.CheckCircle else Icons.Default.Build, contentDescription = null, tint = if (task.status == TaskStatus.COMPLETED) ShiftIQSuccess else PrimaryBlue)
                Column(Modifier.weight(1f)) {
                    Text(task.description.ifBlank { task.serviceName ?: "Tarea" }, fontWeight = FontWeight.Bold)
                    Text("${statusLabel(task.status.name)} · ${task.price.formatted()}", style = MaterialTheme.typography.bodySmall, color = Slate700)
                }
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Ver detalle", tint = Slate500)
            }
            task.assignedMechanicName?.takeIf(String::isNotBlank)?.let { Text("Mecánico: $it", style = MaterialTheme.typography.labelMedium, color = Slate700) }
            if (task.evidenceImages.isNotEmpty() || task.products.isNotEmpty()) {
                Text("${task.products.size} repuestos · ${task.evidenceImages.size} fotos de evidencia", style = MaterialTheme.typography.labelSmall, color = Slate500)
            }
            if (canExecute && task.status != TaskStatus.COMPLETED && task.status != TaskStatus.CANCELLED) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (task.status == TaskStatus.PENDING) {
                        OutlinedButton(onClick = onStart, enabled = !isSaving) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null)
                            Text("Iniciar")
                        }
                    } else if (task.status == TaskStatus.DOING) {
                        Button(onClick = onComplete, enabled = !isSaving) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null)
                            Text("Completar")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WorkOrderTaskDetailSheet(
    task: WorkOrderTask,
    canExecute: Boolean,
    isSaving: Boolean,
    availableProducts: List<ProductItem>,
    onStart: () -> Unit,
    onComplete: () -> Unit,
    onAddProduct: (String, Int) -> Unit,
    onUpdateProductQuantity: (String, Int) -> Unit,
    onRemoveProduct: (String) -> Unit,
    onUploadEvidence: (ByteArray, String, String) -> Unit,
    onRemoveEvidence: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showProductPicker by remember { mutableStateOf(false) }
    var selectedImage by remember { mutableStateOf<String?>(null) }
    var showEvidenceOptions by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) scope.launch {
            val payload = withContext(Dispatchers.IO) {
                val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                val name = uri.lastPathSegment?.substringAfterLast('/')?.takeIf(String::isNotBlank) ?: "evidence-${System.currentTimeMillis()}.jpg"
                val mime = context.contentResolver.getType(uri) ?: "image/jpeg"
                if (bytes == null) null else Triple(bytes, name, mime)
            }
            payload?.let { onUploadEvidence(it.first, it.second, it.third) }
        }
    }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
        if (bitmap != null) scope.launch {
            val bytes = withContext(Dispatchers.IO) {
                ByteArrayOutputStream().use { output ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, output)
                    output.toByteArray()
                }
            }
            onUploadEvidence(bytes, "evidence-${System.currentTimeMillis()}.jpg", "image/jpeg")
        }
    }

    Column(
        Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 18.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Detalle de tarea", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
        Text(task.description.ifBlank { task.serviceName ?: "Tarea" }, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text("Mecánico: ${task.assignedMechanicName ?: "Sin asignar"}", style = MaterialTheme.typography.bodyMedium, color = Slate700)
        Text("Estado: ${statusLabel(task.status.name)}", style = MaterialTheme.typography.bodyMedium, color = PrimaryBlue)
        task.startedAt?.let { Text("Iniciada: ${it.take(16).replace('T', ' ')}", style = MaterialTheme.typography.labelMedium, color = Slate500) }
        task.completedAt?.let { Text("Completada: ${it.take(16).replace('T', ' ')}", style = MaterialTheme.typography.labelMedium, color = Slate500) }

        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Repuestos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            if (canExecute && task.status != TaskStatus.COMPLETED && !isSaving) {
                TextButton(onClick = { showProductPicker = true }) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Text("Agregar")
                }
            }
        }
        if (task.products.isEmpty()) MessageCard("No hay repuestos asignados a esta tarea.")
        task.products.forEach { product ->
            ReservedProductRow(
                product = product,
                enabled = canExecute && task.status != TaskStatus.COMPLETED && !isSaving,
                onIncrement = { onUpdateProductQuantity(product.productId.toString(), product.quantity + 1) },
                onDecrement = { if (product.quantity > 1) onUpdateProductQuantity(product.productId.toString(), product.quantity - 1) },
                onRemove = { onRemoveProduct(product.productId.toString()) }
            )
        }

        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Evidencia (${task.evidenceImages.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            if (canExecute && task.status != TaskStatus.COMPLETED && !isSaving) {
                IconButton(onClick = { showEvidenceOptions = true }) { Icon(Icons.Default.Add, contentDescription = "Agregar evidencia") }
            }
        }
        if (task.evidenceImages.isEmpty()) MessageCard("Sin fotos de evidencia todavía.")
        if (task.evidenceImages.isNotEmpty()) {
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                task.evidenceImages.forEach { url ->
                    Box(Modifier.size(88.dp).clip(RoundedCornerShape(12.dp))) {
                        AsyncImage(model = url, contentDescription = "Foto de evidencia", modifier = Modifier.fillMaxSize().clickable { selectedImage = url })
                        if (canExecute && task.status != TaskStatus.COMPLETED && !isSaving) {
                            IconButton(onClick = { onRemoveEvidence(url) }, modifier = Modifier.align(Alignment.TopEnd).size(30.dp).background(Color.Black.copy(alpha = .65f), CircleShape)) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = "Quitar foto", tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }

        Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Slate100), border = BorderStroke(1.dp, ShiftIQBorderSlate)) {
            Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                AmountRow("Mano de obra", task.price.formatted())
                AmountRow("Repuestos", "S/ ${"%.2f".format(task.products.sumOf { it.totalPrice.amount })}")
                AmountRow("Total de tarea", "S/ ${"%.2f".format(task.price.amount + task.products.sumOf { it.totalPrice.amount })}", bold = true)
            }
        }

        if (canExecute && task.status == TaskStatus.PENDING && !isSaving) {
            Button(onClick = onStart, modifier = Modifier.fillMaxWidth().height(48.dp)) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Text("Iniciar tarea")
            }
        } else if (canExecute && task.status == TaskStatus.DOING && !isSaving) {
            Button(onClick = onComplete, modifier = Modifier.fillMaxWidth().height(48.dp)) {
                Icon(Icons.Default.CheckCircle, contentDescription = null)
                Text("Completar tarea")
            }
        }
        Spacer(Modifier.height(16.dp))
    }

    if (showEvidenceOptions) {
        AlertDialog(
            onDismissRequest = { showEvidenceOptions = false },
            title = { Text("Agregar foto de evidencia") },
            text = {
                Column {
                    TextButton(onClick = { showEvidenceOptions = false; cameraLauncher.launch(null) }, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null)
                        Text("Tomar foto con cámara")
                    }
                    TextButton(onClick = { showEvidenceOptions = false; galleryLauncher.launch("image/*") }, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.Image, contentDescription = null)
                        Text("Elegir de la galería")
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showEvidenceOptions = false }) { Text("Cerrar") } }
        )
    }

    if (showProductPicker) {
        ProductPickerDialog(
            products = availableProducts,
            isSaving = isSaving,
            onDismiss = { showProductPicker = false },
            onAdd = { productId, quantity -> showProductPicker = false; onAddProduct(productId, quantity) }
        )
    }

    selectedImage?.let { url ->
        AlertDialog(
            onDismissRequest = { selectedImage = null },
            text = { AsyncImage(model = url, contentDescription = "Evidencia ampliada", modifier = Modifier.fillMaxWidth().height(360.dp).clip(RoundedCornerShape(12.dp))) },
            confirmButton = { TextButton(onClick = { selectedImage = null }) { Text("Cerrar") } }
        )
    }
}

@Composable
private fun AddTaskDialog(
    services: List<ServiceItem>,
    mechanics: List<WorkOrderMechanicOption>,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onAdd: (String, String, String) -> Unit
) {
    var description by remember { mutableStateOf("") }
    var serviceMenuExpanded by remember { mutableStateOf(false) }
    var mechanicMenuExpanded by remember { mutableStateOf(false) }
    var selectedService by remember(services) { mutableStateOf(services.firstOrNull()) }
    var selectedMechanic by remember(mechanics) { mutableStateOf(mechanics.firstOrNull()) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar tarea") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descripción de la tarea") }, placeholder = { Text("Describe el trabajo a realizar") }, modifier = Modifier.fillMaxWidth())
                Box {
                    OutlinedButton(onClick = { serviceMenuExpanded = true }, modifier = Modifier.fillMaxWidth(), enabled = services.isNotEmpty()) {
                        Text(selectedService?.let { "${it.name} · ${it.price.formatted()}" } ?: "No hay servicios activos")
                    }
                    DropdownMenu(expanded = serviceMenuExpanded, onDismissRequest = { serviceMenuExpanded = false }) {
                        services.forEach { service ->
                            DropdownMenuItem(text = { Text("${service.name} · ${service.price.formatted()}") }, onClick = { selectedService = service; serviceMenuExpanded = false })
                        }
                    }
                }
                Box {
                    OutlinedButton(onClick = { mechanicMenuExpanded = true }, modifier = Modifier.fillMaxWidth(), enabled = mechanics.isNotEmpty()) {
                        Text(selectedMechanic?.name ?: "No hay mecánicos activos")
                    }
                    DropdownMenu(expanded = mechanicMenuExpanded, onDismissRequest = { mechanicMenuExpanded = false }) {
                        mechanics.forEach { mechanic ->
                            DropdownMenuItem(text = { Text(mechanic.name) }, onClick = { selectedMechanic = mechanic; mechanicMenuExpanded = false })
                        }
                    }
                }
                if (services.isEmpty() || mechanics.isEmpty()) {
                    Text("Para agregar una tarea se necesita un servicio y un mecánico activo en la sede.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { selectedService?.let { service -> selectedMechanic?.let { mechanic -> onAdd(service.id.toString(), mechanic.id, description) } } },
                enabled = !isSaving && description.trim().length >= 10 && selectedService != null && selectedMechanic != null
            ) { Text("Agregar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
private fun ProductPickerDialog(
    products: List<ProductItem>,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onAdd: (String, Int) -> Unit
) {
    var selectedProduct by remember { mutableStateOf<ProductItem?>(null) }
    var quantity by remember { mutableIntStateOf(1) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar repuesto") },
        text = {
            Column(Modifier.height(320.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                if (products.isEmpty()) Text("No hay productos disponibles en esta sede.")
                products.forEach { product ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { selectedProduct = product },
                        colors = CardDefaults.cardColors(containerColor = if (selectedProduct?.id == product.id) PrimaryBlue.copy(alpha = .10f) else Color.White),
                        border = BorderStroke(1.dp, if (selectedProduct?.id == product.id) PrimaryBlue else ShiftIQBorderSlate)
                    ) {
                        Row(Modifier.fillMaxWidth().padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Inventory2, contentDescription = null, tint = PrimaryBlue)
                            Column(Modifier.weight(1f).padding(start = 8.dp)) {
                                Text(product.name, fontWeight = FontWeight.SemiBold)
                                Text("Stock ${product.currentStock} · ${product.salePrice.formatted()}", style = MaterialTheme.typography.labelSmall, color = Slate700)
                            }
                        }
                    }
                }
                if (selectedProduct != null) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                        IconButton(onClick = { quantity = (quantity - 1).coerceAtLeast(1) }) { Icon(Icons.Default.Remove, contentDescription = "Menos") }
                        Text("Cantidad: $quantity", fontWeight = FontWeight.Bold)
                        IconButton(onClick = { quantity = (quantity + 1).coerceAtMost(selectedProduct!!.currentStock.coerceAtLeast(1)) }) { Icon(Icons.Default.Add, contentDescription = "Más") }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { selectedProduct?.let { onAdd(it.id.toString(), quantity) } }, enabled = !isSaving && selectedProduct != null) { Text("Agregar a tarea") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
private fun ReservedProductRow(
    product: ReservedProduct,
    enabled: Boolean,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onRemove: () -> Unit
) {
    Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, ShiftIQBorderSlate)) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(product.productName, fontWeight = FontWeight.SemiBold)
                Text("${product.quantity} × ${product.unitPrice.formatted()} = ${product.totalPrice.formatted()}", style = MaterialTheme.typography.labelSmall, color = Slate700)
            }
            IconButton(onClick = onDecrement, enabled = enabled && product.quantity > 1) { Icon(Icons.Default.Remove, contentDescription = "Reducir cantidad") }
            Text(product.quantity.toString(), fontWeight = FontWeight.Bold)
            IconButton(onClick = onIncrement, enabled = enabled) { Icon(Icons.Default.Add, contentDescription = "Aumentar cantidad") }
            IconButton(onClick = onRemove, enabled = enabled) { Icon(Icons.Default.DeleteOutline, contentDescription = "Quitar repuesto", tint = MaterialTheme.colorScheme.error) }
        }
    }
}

@Composable
private fun FinancialSummary(order: WorkOrder) {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, ShiftIQBorderSlate)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Resumen financiero", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            AmountRow("Servicios", "S/ ${"%.2f".format(order.tasks.sumOf { it.price.amount })}")
            AmountRow("Repuestos", "S/ ${"%.2f".format(order.tasks.sumOf { task -> task.products.sumOf { it.totalPrice.amount } })}")
            AmountRow("TOTAL GENERAL DE LA ORDEN", order.totalAmount.formatted(), bold = true)
        }
    }
}

@Composable
private fun AmountRow(label: String, amount: String, bold: Boolean = false) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = if (bold) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodySmall, fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal)
        Text(amount, style = if (bold) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodySmall, fontWeight = if (bold) FontWeight.Bold else FontWeight.Medium, color = if (bold) PrimaryBlue else MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun ImageStrip(title: String, urls: List<String>) {
    var selected by remember { mutableStateOf<String?>(null) }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            urls.forEach { url ->
                AsyncImage(model = url, contentDescription = title, modifier = Modifier.size(82.dp).clip(RoundedCornerShape(10.dp)).clickable { selected = url })
            }
        }
    }
    selected?.let { url ->
        AlertDialog(onDismissRequest = { selected = null }, text = { AsyncImage(model = url, contentDescription = title, modifier = Modifier.fillMaxWidth().height(360.dp).clip(RoundedCornerShape(12.dp))) }, confirmButton = { TextButton(onClick = { selected = null }) { Text("Cerrar") } })
    }
}

@Composable
private fun StatusPill(label: String, color: Color) {
    Text(label, color = color, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, modifier = Modifier.clip(RoundedCornerShape(999.dp)).background(color.copy(alpha = .12f)).padding(horizontal = 10.dp, vertical = 5.dp))
}

@Composable
private fun MessageCard(message: String) {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, ShiftIQBorderSlate)) {
        Text(message, Modifier.fillMaxWidth().padding(16.dp), style = MaterialTheme.typography.bodyMedium, color = Slate700)
    }
}

@Composable
private fun ErrorBanner(message: String) {
    Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF1F2)), border = BorderStroke(1.dp, Color(0xFFFECACA))) {
        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Color(0xFFDC2626))
            Text(message, style = MaterialTheme.typography.bodySmall, color = Color(0xFFB91C1C))
        }
    }
}

@Composable
private fun ErrorState(message: String) {
    Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(36.dp))
            Text(message, color = MaterialTheme.colorScheme.error)
        }
    }
}

private fun statusLabel(status: String): String = when (status.uppercase()) {
    "PENDING" -> "Pendiente"
    "IN_PROGRESS", "DOING" -> "En progreso"
    "COMPLETED" -> "Completada"
    "PAID" -> "Pagada"
    "DELETED", "CANCELLED", "CANCELED" -> "Cancelada"
    else -> status.replace('_', ' ').lowercase().replaceFirstChar(Char::uppercase)
}

private fun statusColor(status: String): Color = when (status.uppercase()) {
    "COMPLETED" -> ShiftIQSuccess
    "PAID" -> PrimaryBlue
    "DELETED", "CANCELLED", "CANCELED" -> Color(0xFFDC2626)
    "IN_PROGRESS", "DOING" -> Color(0xFF0D9488)
    else -> Color(0xFFD97706)
}

@PreviewLightDark
@PreviewFontScale
@Composable
private fun LiveWorkOrderScreenPreview() {
    ShiftIQTheme {
        LiveWorkOrderScreen(
            uiState = LiveWorkOrderUiState(errorMessage = "Vista previa"),
            onNavigateBack = {},
            isOwnerAdmin = false,
            canExecuteTasks = false,
            onCanExecuteTask = { true },
            onStartTask = {},
            onCompleteTask = {},
            onCompleteWorkOrder = {},
            onAddTask = { _, _, _ -> },
            onReserveProduct = { _, _, _ -> },
            onUpdateProductQuantity = { _, _, _ -> },
            onRemoveProduct = { _, _ -> },
            onUploadEvidence = { _, _, _, _ -> },
            onRemoveEvidence = { _, _ -> },
            onEmitVoucher = { _, _, _, _ -> },
            onClearEmittedVoucher = {},
            onOpenVoucher = {}
        )
    }
}
