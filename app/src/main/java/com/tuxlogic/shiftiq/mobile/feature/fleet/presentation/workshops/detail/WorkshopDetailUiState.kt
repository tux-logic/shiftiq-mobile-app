package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.workshops.detail

data class WorkshopSpecialtyItem(
    val id: String,
    val name: String,
    val description: String,
    val estimatedPricePen: Double,
    val estimatedDurationMinutes: Int,
    val warrantyDays: Int
)

data class WorkshopDetailData(
    val id: String,
    val name: String,
    val rating: Double,
    val reviewCount: Int,
    val address: String,
    val district: String,
    val phone: String,
    val openingHours: String,
    val isOpenNow: Boolean,
    val compatibilityNotice: String,
    val activeBaysCount: Int,
    val certifiedTechniciansCount: Int,
    val isFavorite: Boolean,
    val specialties: List<WorkshopSpecialtyItem>
)

sealed interface WorkshopDetailUiState {
    data object Loading : WorkshopDetailUiState
    data class Success(val workshop: WorkshopDetailData) : WorkshopDetailUiState
    data class Error(val message: String) : WorkshopDetailUiState
}
