package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.garage.history

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuxlogic.shiftiq.mobile.feature.billing.application.usecase.vouchers.GetVoucherByIdUseCase
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.VoucherId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ServiceVoucherPdfViewModel @Inject constructor(
    private val getVoucherByIdUseCase: GetVoucherByIdUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val voucherId: String = savedStateHandle["voucherId"] ?: "v-0004921"

    private val _uiState = MutableStateFlow(ServiceVoucherPdfUiState(isLoading = true))
    val uiState: StateFlow<ServiceVoucherPdfUiState> = _uiState.asStateFlow()

    init {
        loadVoucher(voucherId)
    }

    fun loadVoucher(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val cleanId = id.removePrefix("v-")
                val voucher = runCatching {
                    getVoucherByIdUseCase(VoucherId.fromString(cleanId)).getOrNull()
                }.getOrNull()

                if (voucher != null) {
                    val formattedDocNum = voucher.fiscalNumber ?: "${voucher.series ?: "F001"}-${voucher.correlative ?: "0004921"}"
                    val total = voucher.totalAmount.amount.toDouble()
                    val taxable = total / 1.18
                    val igv = total - taxable
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            voucherNumber = formattedDocNum,
                            clientName = voucher.customerName,
                            clientDni = voucher.customerDocumentNumber,
                            totalPaid = total,
                            taxableAmount = taxable,
                            igvAmount = igv
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun zoomIn() {
        _uiState.update { it.copy(zoomPercent = (it.zoomPercent + 10).coerceAtMost(150)) }
    }

    fun zoomOut() {
        _uiState.update { it.copy(zoomPercent = (it.zoomPercent - 10).coerceAtLeast(70)) }
    }

    fun onDownloadPdf() {
        _uiState.update { it.copy(isSavedConfirmationVisible = true) }
    }

    fun onDismissSavedConfirmation() {
        _uiState.update { it.copy(isSavedConfirmationVisible = false) }
    }
}
