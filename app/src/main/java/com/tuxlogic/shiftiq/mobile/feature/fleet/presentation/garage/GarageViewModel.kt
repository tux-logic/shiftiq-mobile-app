package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.garage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuxlogic.shiftiq.mobile.core.database.dao.VehicleDao
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.B2CVehicleItem
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.MaintenanceHistoryItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class GarageViewModel @Inject constructor(
    private val vehicleDao: VehicleDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(GarageUiState(isLoading = true))
    val uiState: StateFlow<GarageUiState> = _uiState.asStateFlow()

    init {
        loadGarageData()
    }

    fun loadGarageData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val cached = vehicleDao.getAllVehicles().firstOrNull().orEmpty()
                val vehicleItems = if (cached.isNotEmpty()) {
                    cached.map {
                        B2CVehicleItem(
                            id = it.id,
                            brand = it.brand,
                            model = it.model,
                            year = it.year,
                            plateNumber = it.plateNumber,
                            vin = it.vin,
                            isMonitored = true,
                            batteryVoltage = 13.8,
                            isPrimary = it.isPrimary
                        )
                    }
                } else {
                    listOf(
                        B2CVehicleItem(
                            id = UUID.randomUUID().toString(),
                            brand = "Kia",
                            model = "Rio",
                            year = 2019,
                            plateNumber = "ABC-123",
                            vin = "9BWCA21102P012984",
                            isMonitored = true,
                            batteryVoltage = 13.8,
                            isPrimary = true
                        ),
                        B2CVehicleItem(
                            id = UUID.randomUUID().toString(),
                            brand = "Toyota",
                            model = "Yaris",
                            year = 2017,
                            plateNumber = "B8C-456",
                            vin = "JTDKB20U301294812",
                            isMonitored = false,
                            batteryVoltage = 12.4,
                            isPrimary = false
                        )
                    )
                }

                val history = listOf(
                    MaintenanceHistoryItem(
                        id = "hist-1",
                        date = "15 FEB 2026",
                        title = "Mantenimiento Preventivo 45,000 km",
                        mileage = 45102,
                        workshopName = "Taller Automotriz Silva",
                        description = "Cambio de aceite sintético 5W-30, cambio de filtro de aire/aceite y rotación de neumáticos en cruz.",
                        totalCostPen = 280.0,
                        voucherId = "v-0004921",
                        hasPdfVoucher = true,
                        hasTechnicalDetail = false
                    ),
                    MaintenanceHistoryItem(
                        id = "hist-2",
                        date = "10 NOV 2025",
                        title = "Alineación Computarizada y Balanceo",
                        mileage = 41200,
                        workshopName = "Taller Multimarca San Miguel",
                        description = "Calibración de combas delanteras y balanceo dinámico en las 4 ruedas.",
                        totalCostPen = 90.0,
                        voucherId = "v-0004922",
                        hasPdfVoucher = false,
                        hasTechnicalDetail = true
                    )
                )

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        vehicles = vehicleItems,
                        currentOdometerKm = 48120,
                        nextServiceKm = 50000,
                        batteryHealthVoltage = 13.8,
                        engineOilLifePercent = 78,
                        maintenanceHistory = history
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }
}
