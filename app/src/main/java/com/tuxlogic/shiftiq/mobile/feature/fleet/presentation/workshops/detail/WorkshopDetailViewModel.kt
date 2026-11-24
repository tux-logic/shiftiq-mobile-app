package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.workshops.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.GetBranchCatalogUseCase
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.GetSpecialtiesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkshopDetailViewModel @Inject constructor(
    private val getBranchCatalog: GetBranchCatalogUseCase,
    private val getSpecialties: GetSpecialtiesUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val workshopId: String = savedStateHandle["workshopId"] ?: ""

    private val _uiState = MutableStateFlow<WorkshopDetailUiState>(WorkshopDetailUiState.Loading)
    val uiState: StateFlow<WorkshopDetailUiState> = _uiState.asStateFlow()

    init {
        loadWorkshopDetail(workshopId)
    }

    fun loadWorkshopDetail(id: String) {
        _uiState.value = WorkshopDetailUiState.Loading
        viewModelScope.launch {
            if (id.isBlank()) {
                _uiState.value = WorkshopDetailUiState.Error("Workshop identifier is missing.")
                return@launch
            }

            val catalogItem = when (val result = getBranchCatalog()) {
                is AppResult.Success -> result.data.firstOrNull { it.branchId == id || it.workshopId == id }
                is AppResult.Failure -> {
                    _uiState.value = WorkshopDetailUiState.Error(result.error.message)
                    return@launch
                }
            }

            if (catalogItem == null) {
                _uiState.value = WorkshopDetailUiState.Error("Workshop branch was not found.")
                return@launch
            }

            val specialties = when (val result = getSpecialties(catalogItem.workshopId, activeOnly = true)) {
                is AppResult.Success -> result.data.map { specialty ->
                    WorkshopSpecialtyItem(
                        id = specialty.id,
                        name = specialty.name,
                        description = specialty.description.orEmpty(),
                        estimatedPricePen = 0.0,
                        estimatedDurationMinutes = 0,
                        warrantyDays = 0
                    )
                }
                is AppResult.Failure -> emptyList()
            }

            _uiState.value = WorkshopDetailUiState.Success(
                WorkshopDetailData(
                    id = catalogItem.branchId,
                    name = catalogItem.displayName,
                    rating = 0.0,
                    reviewCount = 0,
                    address = catalogItem.address,
                    district = catalogItem.address.substringAfterLast(",").trim().ifBlank { catalogItem.address },
                    phone = catalogItem.phone,
                    openingHours = "",
                    isOpenNow = true,
                    compatibilityNotice = "Branch available for booking in ShiftIQ.",
                    activeBaysCount = 0,
                    certifiedTechniciansCount = 0,
                    isFavorite = false,
                    specialties = specialties
                )
            )
        }
    }

    fun toggleFavorite() {
        val current = _uiState.value
        if (current is WorkshopDetailUiState.Success) {
            val updated = current.workshop.copy(isFavorite = !current.workshop.isFavorite)
            _uiState.value = WorkshopDetailUiState.Success(updated)
        }
    }
}
