package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.garage.external

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ExternalServiceRegistrationViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ExternalServiceRegistrationUiState())
    val uiState: StateFlow<ExternalServiceRegistrationUiState> = _uiState.asStateFlow()

    fun onServiceTypeChange(type: String) {
        _uiState.update { it.copy(serviceType = type) }
    }

    fun onWorkshopNameChange(name: String) {
        _uiState.update { it.copy(workshopName = name) }
    }

    fun onServiceDateChange(date: String) {
        _uiState.update { it.copy(serviceDate = date) }
    }

    fun onMileageChange(mileage: String) {
        val sanitized = mileage.filter { it.isDigit() }
        _uiState.update { it.copy(mileage = sanitized) }
    }

    fun onTotalCostChange(cost: String) {
        _uiState.update { it.copy(totalCostPen = cost) }
    }

    fun onNotesChange(notes: String) {
        _uiState.update { it.copy(notes = notes) }
    }

    fun onAttachReceipt(fileName: String) {
        _uiState.update { it.copy(attachedReceiptName = fileName) }
    }

    fun onRemoveReceipt() {
        _uiState.update { it.copy(attachedReceiptName = null) }
    }

    fun onToggleUpdateBaseOdometer(checked: Boolean) {
        _uiState.update { it.copy(updateBaseOdometer = checked) }
    }

    fun onToggleResetIntervalCounter(checked: Boolean) {
        _uiState.update { it.copy(resetIntervalCounter = checked) }
    }

    fun saveExternalService() {
        _uiState.update {
            it.copy(
                isLoading = false,
                isSuccess = false,
                errorMessage = "The backend does not provide an endpoint to save external service records yet. Your information has not been saved."
            )
        }
    }
}
