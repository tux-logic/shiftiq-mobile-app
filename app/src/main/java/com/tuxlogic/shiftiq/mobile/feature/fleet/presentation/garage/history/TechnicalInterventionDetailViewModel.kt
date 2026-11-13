package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.garage.history

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.feature.billing.application.usecase.quotes.GetQuotesUseCase
import com.tuxlogic.shiftiq.mobile.feature.billing.application.usecase.vouchers.GetVouchersUseCase
import com.tuxlogic.shiftiq.mobile.feature.core.domain.repository.CoreRepository
import com.tuxlogic.shiftiq.mobile.feature.operations.application.usecase.GetWorkOrderByIdUseCase
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.WorkOrderId
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.WorkOrderStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TechnicalInterventionDetailViewModel @Inject constructor(
    private val getWorkOrderById: GetWorkOrderByIdUseCase,
    private val coreRepository: CoreRepository,
    private val getQuotes: GetQuotesUseCase,
    private val getVouchers: GetVouchersUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val interventionId: String = savedStateHandle["id"] ?: ""
    private val _uiState = MutableStateFlow(TechnicalInterventionDetailUiState(isLoading = true))
    val uiState: StateFlow<TechnicalInterventionDetailUiState> = _uiState.asStateFlow()

    init {
        loadInterventionDetail(interventionId)
    }

    fun loadInterventionDetail(id: String) {
        val uuid = runCatching { UUID.fromString(id) }.getOrNull()
        if (uuid == null) {
            _uiState.value = TechnicalInterventionDetailUiState(
                isLoading = false,
                errorMessage = "This service record has no valid server identifier."
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = TechnicalInterventionDetailUiState(isLoading = true)
            when (val result = getWorkOrderById(WorkOrderId(uuid))) {
                is AppResult.Failure -> _uiState.value = TechnicalInterventionDetailUiState(
                    isLoading = false,
                    errorMessage = result.error.message
                )

                is AppResult.Success -> {
                    val order = result.data
                    val branch = (coreRepository.getBranchById(order.branchId.value.toString()) as? AppResult.Success)?.data
                    val technician = order.tasks.firstNotNullOfOrNull { it.assignedMechanicName?.takeIf(String::isNotBlank) }
                    val quote = (getQuotes(order.branchId) as? AppResult.Success)?.data
                        ?.firstOrNull { it.workOrderId == order.id }
                    val voucher = quote?.let { matchingQuote ->
                        (getVouchers(order.branchId) as? AppResult.Success)?.data
                            ?.firstOrNull { it.quoteId == matchingQuote.id }
                    }

                    _uiState.value = TechnicalInterventionDetailUiState(
                        expNumber = order.formattedNumber,
                        isSealed = order.status == WorkOrderStatus.COMPLETED,
                        statusText = order.status.name,
                        odometerKm = order.mileageIn,
                        serviceTitle = order.diagnosticSummary,
                        executionDate = order.completedAt ?: order.updatedAt,
                        workshopName = branch?.name.orEmpty(),
                        workshopAddress = branch?.address.orEmpty(),
                        technicianName = technician.orEmpty(),
                        totalCostPen = order.totalAmount.amount.toDouble(),
                        protocolOperations = order.tasks.map { task ->
                            ProtocolOperationItem(
                                title = task.description.ifBlank { task.serviceName.orEmpty() },
                                description = "${task.status.name} · ${task.price.formatted()}"
                            )
                        },
                        usedSupplies = order.tasks.flatMap { task ->
                            task.products.map { product ->
                                UsedSupplyItem(
                                    name = product.productName,
                                    description = "${product.quantity} × ${product.unitPrice.formatted()}"
                                )
                            }
                        },
                        voucherId = voucher?.id?.toString().orEmpty(),
                        isLoading = false,
                        isLoaded = true
                    )
                }
            }
        }
    }
}
