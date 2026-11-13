package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.workshops.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkshopDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val workshopId: String = savedStateHandle["workshopId"] ?: "w1"

    private val _uiState = MutableStateFlow<WorkshopDetailUiState>(WorkshopDetailUiState.Loading)
    val uiState: StateFlow<WorkshopDetailUiState> = _uiState.asStateFlow()

    init {
        loadWorkshopDetail(workshopId)
    }

    fun loadWorkshopDetail(id: String) {
        _uiState.value = WorkshopDetailUiState.Loading
        viewModelScope.launch {
            delay(250)
            val isExpress = id == "w2"
            _uiState.value = WorkshopDetailUiState.Success(
                WorkshopDetailData(
                    id = id,
                    name = if (isExpress) "AutoService Express - Mecánica Rápida" else "Taller Automotriz Silva - Sede San Miguel",
                    rating = if (isExpress) 4.6 else 4.8,
                    reviewCount = if (isExpress) 89 else 115,
                    address = if (isExpress) "Av. Sucre 820, Pueblo Libre, Lima" else "Av. La Marina 2450, San Miguel, Lima",
                    district = if (isExpress) "Pueblo Libre" else "San Miguel",
                    phone = if (isExpress) "+51 987 111 222" else "+51 987 654 321",
                    openingHours = "Lun - Sáb: 08:00 AM - 06:30 PM",
                    isOpenNow = true,
                    compatibilityNotice = "Taller de demostración para el flujo de reservas.",
                    activeBaysCount = if (isExpress) 2 else 3,
                    certifiedTechniciansCount = if (isExpress) 3 else 4,
                    isFavorite = false,
                    specialties = defaultSpecialties()
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

    private fun defaultSpecialties(): List<WorkshopSpecialtyItem> = listOf(
        WorkshopSpecialtyItem(
            id = "s1",
            name = "Diagnóstico Electrónico OBD-II y Borrado DTC",
            description = "Lectura de flujo de datos en vivo, análisis de códigos de falla y verificación de sensores.",
            estimatedPricePen = 80.0,
            estimatedDurationMinutes = 45,
            warrantyDays = 30
        ),
        WorkshopSpecialtyItem(
            id = "s2",
            name = "Inyección Electrónica y Calibración de Inyectores",
            description = "Limpieza por ultrasonido, prueba de hermeticidad y calibración de cuerpo de aceleración.",
            estimatedPricePen = 140.0,
            estimatedDurationMinutes = 90,
            warrantyDays = 60
        ),
        WorkshopSpecialtyItem(
            id = "s3",
            name = "Reemplazo de Sensores O2 (Lambda) / MAF",
            description = "Sustitución técnica de sondas con escaneo de resistencia y calibración de mezcla aire-combustible.",
            estimatedPricePen = 160.0,
            estimatedDurationMinutes = 60,
            warrantyDays = 90
        ),
        WorkshopSpecialtyItem(
            id = "s4",
            name = "Mantenimiento Preventivo 20,000 / 40,000 km",
            description = "Cambio de aceite sintético, filtros, inspección integral de suspensión y frenos en 4 ruedas.",
            estimatedPricePen = 220.0,
            estimatedDurationMinutes = 120,
            warrantyDays = 90
        )
    )
}
