package com.tuxlogic.shiftiq.mobile.feature.operations.presentation.workorder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.model.EmployeeId
import com.tuxlogic.shiftiq.mobile.feature.billing.application.usecase.quotes.ApproveQuoteUseCase
import com.tuxlogic.shiftiq.mobile.feature.billing.application.usecase.quotes.CreateQuoteUseCase
import com.tuxlogic.shiftiq.mobile.feature.billing.application.usecase.quotes.GetQuotesUseCase
import com.tuxlogic.shiftiq.mobile.feature.billing.application.usecase.vouchers.EmitVoucherUseCase
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.DocumentType
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.QuoteStatus
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.VoucherType
import com.tuxlogic.shiftiq.mobile.feature.core.domain.repository.CoreRepository
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.repository.FleetRepository
import com.tuxlogic.shiftiq.mobile.feature.inventory.application.usecase.GetProductsUseCase
import com.tuxlogic.shiftiq.mobile.feature.iot.application.usecase.vehicles.GetVehicleByIdUseCase
import com.tuxlogic.shiftiq.mobile.feature.operations.application.usecase.AddTaskToWorkOrderUseCase
import com.tuxlogic.shiftiq.mobile.feature.operations.application.usecase.CompleteTaskUseCase
import com.tuxlogic.shiftiq.mobile.feature.operations.application.usecase.CompleteWorkOrderUseCase
import com.tuxlogic.shiftiq.mobile.feature.operations.application.usecase.GetServicesUseCase
import com.tuxlogic.shiftiq.mobile.feature.operations.application.usecase.GetWorkOrderByIdUseCase
import com.tuxlogic.shiftiq.mobile.feature.operations.application.usecase.RemoveProductFromTaskUseCase
import com.tuxlogic.shiftiq.mobile.feature.operations.application.usecase.ReserveProductForTaskUseCase
import com.tuxlogic.shiftiq.mobile.feature.operations.application.usecase.StartTaskUseCase
import com.tuxlogic.shiftiq.mobile.feature.operations.application.usecase.UpdateTaskProductQuantityUseCase
import com.tuxlogic.shiftiq.mobile.feature.operations.application.usecase.UpdateTaskUseCase
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.ServiceId
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.ProductId
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.TaskId
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.WorkOrder
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.WorkOrderId
import com.tuxlogic.shiftiq.mobile.feature.shared.domain.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LiveWorkOrderViewModel @Inject constructor(
    private val getWorkOrderById: GetWorkOrderByIdUseCase,
    private val getVehicleById: GetVehicleByIdUseCase,
    private val startTaskUseCase: StartTaskUseCase,
    private val completeTaskUseCase: CompleteTaskUseCase,
    private val completeWorkOrderUseCase: CompleteWorkOrderUseCase,
    private val addTaskUseCase: AddTaskToWorkOrderUseCase,
    private val getServices: GetServicesUseCase,
    private val getProducts: GetProductsUseCase,
    private val updateTask: UpdateTaskUseCase,
    private val reserveProductUseCase: ReserveProductForTaskUseCase,
    private val updateProductQuantityUseCase: UpdateTaskProductQuantityUseCase,
    private val removeProductUseCase: RemoveProductFromTaskUseCase,
    private val mediaRepository: MediaRepository,
    private val coreRepository: CoreRepository,
    private val fleetRepository: FleetRepository,
    private val getQuotes: GetQuotesUseCase,
    private val createQuote: CreateQuoteUseCase,
    private val approveQuote: ApproveQuoteUseCase,
    private val emitVoucher: EmitVoucherUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LiveWorkOrderUiState(isLoading = true))
    val uiState: StateFlow<LiveWorkOrderUiState> = _uiState.asStateFlow()

    fun loadWorkOrder(id: String) {
        val uuid = runCatching { UUID.fromString(id) }.getOrNull()
        if (uuid == null) {
            _uiState.value = LiveWorkOrderUiState(errorMessage = "La orden no tiene un identificador válido.")
            return
        }

        viewModelScope.launch {
            _uiState.value = LiveWorkOrderUiState(isLoading = true)
            when (val result = getWorkOrderById(WorkOrderId(uuid))) {
                is AppResult.Failure -> _uiState.value = LiveWorkOrderUiState(errorMessage = result.error.message)
                is AppResult.Success -> {
                    val order = result.data
                    val vehicleDescription = when (val vehicleResult = getVehicleById(order.vehicleId)) {
                        is AppResult.Success -> "${vehicleResult.data.brand} ${vehicleResult.data.model} ${vehicleResult.data.year} • ${vehicleResult.data.plateNumber.value}"
                        is AppResult.Failure -> "No se pudo cargar la información del vehículo."
                    }
                    val identity = loadEmployeeIdentity(order.branchId.value)
                    val customer = (coreRepository.getCustomerById(order.customerId.toString()) as? AppResult.Success)?.data
                    val services = (getServices(order.branchId) as? AppResult.Success)?.data.orEmpty()
                    val products = (getProducts(branchId = order.branchId) as? AppResult.Success)?.data.orEmpty()
                    _uiState.value = LiveWorkOrderUiState(
                        workOrder = order,
                        vehicleDescription = vehicleDescription,
                        customerName = customer?.displayName.orEmpty(),
                        customerDocumentType = customer?.documentType?.uppercase()?.takeIf { it == "RUC" } ?: "DNI",
                        customerDocumentNumber = customer?.documentNumber.orEmpty(),
                        currentEmployeeIds = identity.first,
                        employeeIdentityLoaded = identity.second,
                        availableServices = services,
                        availableProducts = products,
                        availableMechanics = loadMechanicOptions(order.branchId.value)
                    )
                }
            }
        }
    }

    fun canExecuteTask(taskAssignedMechanicId: String?): Boolean {
        val state = _uiState.value
        if (!state.employeeIdentityLoaded) return false
        return taskAssignedMechanicId != null && taskAssignedMechanicId in state.currentEmployeeIds
    }

    fun startTask(taskId: String) = mutateTask(taskId) { startTaskUseCase(TaskId(taskId)) }

    fun completeTask(taskId: String) = mutateTask(taskId) { completeTaskUseCase(TaskId(taskId)) }

    fun completeWorkOrder() {
        val order = _uiState.value.workOrder ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            val result = completeWorkOrderUseCase(order.id)
            _uiState.update { state ->
                when (result) {
                    is AppResult.Success -> state.copy(workOrder = result.data, isSaving = false)
                    is AppResult.Failure -> state.copy(errorMessage = result.error.message, isSaving = false)
                }
            }
        }
    }

    fun emitWorkOrderVoucher(
        type: VoucherType,
        documentType: DocumentType,
        documentNumber: String,
        customerName: String
    ) {
        val order = _uiState.value.workOrder ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null, emittedVoucher = null) }
            val existingQuotes = getQuotes(order.branchId)
            val approvedQuote = (existingQuotes as? AppResult.Success)?.data
                ?.firstOrNull { it.workOrderId == order.id && it.status == QuoteStatus.APPROVED }

            val quoteResult = if (approvedQuote != null) {
                AppResult.Success(approvedQuote)
            } else {
                when (val created = createQuote(order.id, order.branchId)) {
                    is AppResult.Failure -> created
                    is AppResult.Success -> if (created.data.status == QuoteStatus.APPROVED) created
                        else approveQuote(created.data.id)
                }
            }

            when (quoteResult) {
                is AppResult.Failure -> _uiState.update { it.copy(isSaving = false, errorMessage = quoteResult.error.message) }
                is AppResult.Success -> when (val result = emitVoucher(
                    quoteId = quoteResult.data.id,
                    type = type,
                    customerDocumentType = documentType,
                    customerDocumentNumber = documentNumber,
                    customerName = customerName
                )) {
                    is AppResult.Success -> _uiState.update { it.copy(isSaving = false, emittedVoucher = result.data) }
                    is AppResult.Failure -> _uiState.update { it.copy(isSaving = false, errorMessage = result.error.message) }
                }
            }
        }
    }

    fun clearEmittedVoucher() {
        _uiState.update { it.copy(emittedVoucher = null) }
    }

    fun addTask(serviceId: String, mechanicId: String, description: String) {
        val order = _uiState.value.workOrder ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            val result = addTaskUseCase(
                workOrderId = order.id,
                serviceId = ServiceId(serviceId),
                assignedMechanicId = EmployeeId(mechanicId),
                description = description
            )
            applyOrderResult(result)
        }
    }

    fun reserveProduct(taskId: String, productId: String, quantity: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            val result = reserveProductUseCase(TaskId(taskId), ProductId(productId), quantity)
            applyOrderResult(result)
        }
    }

    fun updateProductQuantity(taskId: String, productId: String, quantity: Int) {
        if (quantity <= 0) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            val result = updateProductQuantityUseCase(TaskId(taskId), ProductId(productId), quantity)
            applyOrderResult(result)
        }
    }

    fun removeProduct(taskId: String, productId: String) {
        val order = _uiState.value.workOrder ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            when (val result = removeProductUseCase(TaskId(taskId), ProductId(productId))) {
                is AppResult.Failure -> _uiState.update { it.copy(errorMessage = result.error.message, isSaving = false) }
                is AppResult.Success -> reloadOrder(order.id)
            }
        }
    }

    fun uploadEvidence(taskId: String, bytes: ByteArray, fileName: String, mimeType: String) {
        val order = _uiState.value.workOrder ?: return
        val task = order.tasks.firstOrNull { it.id.toString() == taskId } ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            when (val upload = mediaRepository.uploadImage(bytes, fileName, mimeType, "workorders")) {
                is AppResult.Failure -> _uiState.update { it.copy(errorMessage = upload.error.message, isSaving = false) }
                is AppResult.Success -> {
                    val result = updateTask(
                        workOrderId = order.id,
                        taskId = task.id,
                        evidenceImages = task.evidenceImages + upload.data.url
                    )
                    applyOrderResult(result)
                }
            }
        }
    }

    fun removeEvidence(taskId: String, imageUrl: String) {
        val order = _uiState.value.workOrder ?: return
        val task = order.tasks.firstOrNull { it.id.toString() == taskId } ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            val result = updateTask(
                workOrderId = order.id,
                taskId = task.id,
                evidenceImages = task.evidenceImages.filterNot { it == imageUrl }
            )
            applyOrderResult(result)
        }
    }

    private fun mutateTask(taskId: String, action: suspend () -> AppResult<WorkOrder>) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            applyOrderResult(action())
        }
    }

    private fun applyOrderResult(result: AppResult<WorkOrder>) {
        _uiState.update { state ->
            when (result) {
                is AppResult.Success -> state.copy(workOrder = result.data, isSaving = false)
                is AppResult.Failure -> state.copy(errorMessage = result.error.message, isSaving = false)
            }
        }
    }

    private suspend fun reloadOrder(id: WorkOrderId) {
        when (val result = getWorkOrderById(id)) {
            is AppResult.Success -> _uiState.update { it.copy(workOrder = result.data, isSaving = false) }
            is AppResult.Failure -> _uiState.update { it.copy(errorMessage = result.error.message, isSaving = false) }
        }
    }

    private suspend fun loadEmployeeIdentity(branchId: UUID): Pair<Set<String>, Boolean> {
        val profile = coreRepository.getProfileMe() as? AppResult.Success ?: return emptySet<String>() to false
        val employeeId = profile.data.employee?.employeeId?.takeIf { it.isNotBlank() }
            ?: return emptySet<String>() to true
        val identifiers = mutableSetOf(employeeId)
        val parsedEmployeeId = runCatching { UUID.fromString(employeeId) }.getOrNull()
            ?: return identifiers to true
        val registrations = fleetRepository.getEmployeeRegistrations(
            branchId = branchId,
            status = "ACTIVE",
            employeeId = parsedEmployeeId
        ) as? AppResult.Success
        registrations?.data?.forEach { identifiers += it.id.toString() }
        return identifiers to true
    }

    private suspend fun loadMechanicOptions(branchId: UUID): List<WorkOrderMechanicOption> {
        val registrations = (fleetRepository.getEmployeeRegistrations(
            branchId = branchId,
            status = "ACTIVE"
        ) as? AppResult.Success)?.data.orEmpty()
        return registrations.map { registration ->
            val profile = coreRepository.getEmployeeById(registration.employeeId.toString()) as? AppResult.Success
            val displayName = profile?.data?.fullName?.takeIf { it.isNotBlank() }
                ?: registration.specialityName?.takeIf { it.isNotBlank() }
                ?: "Empleado ${registration.employeeId.toString().take(8)}"
            WorkOrderMechanicOption(registration.id.toString(), displayName)
        }
    }
}
