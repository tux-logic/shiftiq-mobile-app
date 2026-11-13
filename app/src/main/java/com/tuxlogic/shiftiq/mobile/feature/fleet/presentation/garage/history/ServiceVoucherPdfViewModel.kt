package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.garage.history

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.model.VehicleId
import com.tuxlogic.shiftiq.mobile.feature.billing.application.usecase.quotes.GetQuoteByIdUseCase
import com.tuxlogic.shiftiq.mobile.feature.billing.application.usecase.vouchers.GetVoucherByIdUseCase
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.VoucherId
import com.tuxlogic.shiftiq.mobile.feature.core.domain.repository.CoreRepository
import com.tuxlogic.shiftiq.mobile.feature.iot.application.usecase.vehicles.GetVehicleByIdUseCase
import com.tuxlogic.shiftiq.mobile.feature.operations.application.usecase.GetWorkOrderByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ServiceVoucherPdfViewModel @Inject constructor(
    private val getVoucherById: GetVoucherByIdUseCase,
    private val getQuoteById: GetQuoteByIdUseCase,
    private val getWorkOrderById: GetWorkOrderByIdUseCase,
    private val getVehicleById: GetVehicleByIdUseCase,
    private val coreRepository: CoreRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val initialVoucherId: String = savedStateHandle["voucherId"] ?: ""
    private val _uiState = MutableStateFlow(ServiceVoucherPdfUiState())
    val uiState: StateFlow<ServiceVoucherPdfUiState> = _uiState.asStateFlow()

    init {
        if (initialVoucherId.isNotBlank()) loadVoucher(initialVoucherId)
    }

    fun loadVoucher(id: String) {
        val uuid = runCatching { UUID.fromString(id.removePrefix("v-")) }.getOrNull()
        if (uuid == null) {
            _uiState.value = ServiceVoucherPdfUiState(
                isLoading = false,
                errorMessage = "This voucher has no valid server identifier."
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = ServiceVoucherPdfUiState(isLoading = true)
            when (val voucherResult = getVoucherById(VoucherId(uuid))) {
                is AppResult.Failure -> _uiState.value = ServiceVoucherPdfUiState(
                    isLoading = false,
                    errorMessage = voucherResult.error.message
                )

                is AppResult.Success -> {
                    val voucher = voucherResult.data
                    val quote = (getQuoteById(voucher.quoteId) as? AppResult.Success)?.data
                    val order = quote?.let { (getWorkOrderById(it.workOrderId) as? AppResult.Success)?.data }
                    val vehicle = order?.let { (getVehicleById(it.vehicleId) as? AppResult.Success)?.data }
                    val branch = quote?.let { (coreRepository.getBranchById(it.branchId.value.toString()) as? AppResult.Success)?.data }
                    val workshop = branch?.let { (coreRepository.getWorkshopById(it.workshopId) as? AppResult.Success)?.data }
                    val number = voucher.fiscalNumber
                        ?: listOfNotNull(voucher.series, voucher.correlative).takeIf { it.isNotEmpty() }?.joinToString("-")
                        .orEmpty()

                    _uiState.value = ServiceVoucherPdfUiState(
                        voucherId = voucher.id.toString(),
                        companyName = workshop?.businessName.orEmpty(),
                        tradeName = workshop?.brandName.orEmpty(),
                        ruc = workshop?.taxId.orEmpty(),
                        address = branch?.address.orEmpty(),
                        voucherType = voucher.type.name,
                        voucherNumber = number,
                        status = voucher.status.name,
                        clientName = voucher.customerName,
                        clientDni = voucher.customerDocumentNumber,
                        vehicleInfo = vehicle?.let { "${it.brand} ${it.model} ${it.year} • ${it.plateNumber.value}" }.orEmpty(),
                        odometerKm = order?.mileageIn,
                        serviceReason = order?.diagnosticSummary.orEmpty(),
                        items = quote?.items.orEmpty().map { item ->
                            VoucherTableItem(
                                quantity = item.quantity,
                                description = item.name,
                                unitPrice = item.unitPrice.amount.toDouble(),
                                totalPrice = item.total.amount.toDouble()
                            )
                        },
                        totalPaid = voucher.totalPaid.amount.toDouble(),
                        balance = voucher.balance.amount.toDouble(),
                        paymentInfo = voucher.payments.joinToString { payment ->
                            "${payment.method.name} ${payment.amount.formatted()}"
                        },
                        pdfUrl = voucher.pdfUrl,
                        isLoading = false,
                        isLoaded = true
                    )
                }
            }
        }
    }

    fun zoomIn() = _uiState.update { it.copy(zoomPercent = (it.zoomPercent + 10).coerceAtMost(150)) }
    fun zoomOut() = _uiState.update { it.copy(zoomPercent = (it.zoomPercent - 10).coerceAtLeast(70)) }
    fun onPdfSaved(fileName: String, filePath: String, fileSizeText: String) = _uiState.update {
        it.copy(
            isSavedConfirmationVisible = true,
            savedFileName = fileName,
            savedFilePath = filePath,
            fileSizeText = fileSizeText,
            fileFormatText = "PDF"
        )
    }
    fun onDismissSavedConfirmation() = _uiState.update { it.copy(isSavedConfirmationVisible = false) }
}
