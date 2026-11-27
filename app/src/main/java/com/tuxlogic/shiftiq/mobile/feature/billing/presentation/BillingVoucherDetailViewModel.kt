package com.tuxlogic.shiftiq.mobile.feature.billing.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.feature.billing.application.usecase.vouchers.GetVoucherByIdUseCase
import com.tuxlogic.shiftiq.mobile.feature.billing.application.usecase.vouchers.RegisterPaymentUseCase
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.PaymentMethod
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.Voucher
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.VoucherId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BillingVoucherDetailUiState(
    val isLoading: Boolean = true,
    val isSavingPayment: Boolean = false,
    val voucher: Voucher? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class BillingVoucherDetailViewModel @Inject constructor(
    private val getVoucherById: GetVoucherByIdUseCase,
    private val registerPayment: RegisterPaymentUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(BillingVoucherDetailUiState())
    val uiState: StateFlow<BillingVoucherDetailUiState> = _uiState.asStateFlow()

    fun load(voucherId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            when (val result = getVoucherById(VoucherId(voucherId))) {
                is AppResult.Success -> _uiState.value = BillingVoucherDetailUiState(
                    isLoading = false,
                    voucher = result.data
                )
                is AppResult.Failure -> _uiState.value = BillingVoucherDetailUiState(
                    isLoading = false,
                    errorMessage = result.error.message
                )
            }
        }
    }

    fun collectPayment(method: PaymentMethod, onComplete: (AppResult<Voucher>) -> Unit) {
        val voucher = _uiState.value.voucher ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSavingPayment = true, errorMessage = null)
            val result = registerPayment(
                voucherId = voucher.id,
                amount = voucher.balance.amount.toDouble(),
                method = method
            )
            when (result) {
                is AppResult.Success -> _uiState.value = _uiState.value.copy(
                    isSavingPayment = false,
                    voucher = result.data,
                    errorMessage = null
                )
                is AppResult.Failure -> _uiState.value = _uiState.value.copy(
                    isSavingPayment = false,
                    errorMessage = result.error.message
                )
            }
            onComplete(result)
        }
    }
}
