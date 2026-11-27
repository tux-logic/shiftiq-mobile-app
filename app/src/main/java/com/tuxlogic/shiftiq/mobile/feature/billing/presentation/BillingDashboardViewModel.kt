package com.tuxlogic.shiftiq.mobile.feature.billing.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.datastore.SessionDataStore
import com.tuxlogic.shiftiq.mobile.feature.billing.application.usecase.quotes.GetQuotesUseCase
import com.tuxlogic.shiftiq.mobile.feature.billing.application.usecase.vouchers.EmitVoucherUseCase
import com.tuxlogic.shiftiq.mobile.feature.billing.application.usecase.vouchers.GetVouchersUseCase
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.DocumentType
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.QuoteId
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.Quote
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.VoucherType
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.Voucher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId
import javax.inject.Inject

data class BillingDashboardUiState(
    val isLoading: Boolean = true,
    val isEmittingVoucher: Boolean = false,
    val vouchers: List<Voucher> = emptyList(),
    val quotes: List<Quote> = emptyList(),
    val errorMessage: String? = null
) {
    val pendingPaymentsCount: Int get() = vouchers.count { it.balance.amount.signum() > 0 }
    val totalBilledToday: Double
        get() {
            val today = LocalDate.now()
            return vouchers.flatMap { it.payments }.filter { payment ->
                payment.paidAt?.let(::toLocalDate) == today
            }.sumOf { it.amount.amount.toDouble() }
        }

    private fun toLocalDate(value: String): LocalDate? = runCatching {
        runCatching { Instant.parse(value).atZone(ZoneId.systemDefault()).toLocalDate() }
            .getOrElse { OffsetDateTime.parse(value).toLocalDate() }
    }.getOrNull()
}

@HiltViewModel
class BillingDashboardViewModel @Inject constructor(
    private val getQuotes: GetQuotesUseCase,
    private val getVouchers: GetVouchersUseCase,
    private val emitVoucherUseCase: EmitVoucherUseCase,
    private val sessionDataStore: SessionDataStore
) : ViewModel() {
    private val _uiState = MutableStateFlow(BillingDashboardUiState())
    val uiState: StateFlow<BillingDashboardUiState> = _uiState.asStateFlow()

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val branchId = sessionDataStore.activeBranchId.firstOrNull()
            if (branchId == null) {
                _uiState.value = BillingDashboardUiState(
                    isLoading = false,
                    errorMessage = "Selecciona una sede activa para consultar la facturación."
                )
                return@launch
            }
            val quotesResult = getQuotes(branchId)
            val vouchersResult = getVouchers(branchId)
            val quotes = (quotesResult as? AppResult.Success)?.data.orEmpty()
            val vouchers = (vouchersResult as? AppResult.Success)?.data.orEmpty()
            val error = (quotesResult as? AppResult.Failure)?.error?.message
                ?: (vouchersResult as? AppResult.Failure)?.error?.message
            _uiState.value = BillingDashboardUiState(
                isLoading = false,
                quotes = quotes,
                vouchers = vouchers,
                errorMessage = error
            )
        }
    }

    fun emitVoucher(
        quoteId: QuoteId,
        type: VoucherType,
        documentType: DocumentType,
        documentNumber: String,
        customerName: String,
        onComplete: (AppResult<Voucher>) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isEmittingVoucher = true, errorMessage = null)
            val result = emitVoucherUseCase(
                quoteId = quoteId,
                type = type,
                customerDocumentType = documentType,
                customerDocumentNumber = documentNumber,
                customerName = customerName
            )
            when (result) {
                is AppResult.Success -> _uiState.value = _uiState.value.copy(
                    isEmittingVoucher = false,
                    vouchers = listOf(result.data) + _uiState.value.vouchers,
                    errorMessage = null
                )
                is AppResult.Failure -> _uiState.value = _uiState.value.copy(
                    isEmittingVoucher = false,
                    errorMessage = result.error.message
                )
            }
            onComplete(result)
        }
    }
}
