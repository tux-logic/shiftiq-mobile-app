package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.diagnostic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.database.dao.TelemetryDao
import com.tuxlogic.shiftiq.mobile.core.datastore.SessionDataStore
import com.tuxlogic.shiftiq.mobile.core.model.CustomerId
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.GetCustomerByUserIdUseCase
import com.tuxlogic.shiftiq.mobile.feature.iot.application.usecase.vehicles.GetCustomerVehiclesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

data class OfflineTelemetryUiState(
    val isLoading: Boolean = true,
    val frames: List<BufferedFrameItem> = emptyList(),
    val frameCount: Int = 0,
    val memoryKb: Double = 0.0,
    val message: String? = null
)

@HiltViewModel
class OfflineTelemetryViewModel @Inject constructor(
    private val sessionDataStore: SessionDataStore,
    private val getCustomerByUserId: GetCustomerByUserIdUseCase,
    private val getCustomerVehicles: GetCustomerVehiclesUseCase,
    private val telemetryDao: TelemetryDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(OfflineTelemetryUiState())
    val uiState: StateFlow<OfflineTelemetryUiState> = _uiState.asStateFlow()

    init {
        loadBufferedFrames()
    }

    fun loadBufferedFrames() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, message = null) }
            val userId = sessionDataStore.userId.firstOrNull()?.value?.toString()
            if (userId.isNullOrBlank()) {
                _uiState.update {
                    it.copy(isLoading = false, message = "Sign in to inspect the local telemetry buffer.")
                }
                return@launch
            }

            val customer = (getCustomerByUserId(userId) as? AppResult.Success)?.data
            val vehicleId = customer?.let {
                (getCustomerVehicles(CustomerId(it.id)) as? AppResult.Success)?.data?.firstOrNull()?.id?.toString()
            }

            if (vehicleId.isNullOrBlank()) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        frames = emptyList(),
                        frameCount = 0,
                        memoryKb = 0.0,
                        message = "No vehicle is available to read buffered telemetry."
                    )
                }
                return@launch
            }

            val snapshots = telemetryDao.getTelemetryHistory(vehicleId, limit = 50).firstOrNull().orEmpty()
            val now = System.currentTimeMillis()
            val frames = snapshots.flatMap { snapshot ->
                listOf(
                    BufferedFrameItem("PID \$010C", "Engine RPM", "${snapshot.rpm} RPM", relativeTime(now, snapshot.timestamp)),
                    BufferedFrameItem("PID \$0105", "Coolant temperature", "${snapshot.temperatureCelsius} °C", relativeTime(now, snapshot.timestamp)),
                    BufferedFrameItem("PID \$010D", "Vehicle speed", "${snapshot.speedKmh} km/h", relativeTime(now, snapshot.timestamp)),
                    BufferedFrameItem("PID \$012F", "Fuel level", "${snapshot.fuelLevelPercent} %", relativeTime(now, snapshot.timestamp))
                )
            }.take(20)

            _uiState.update {
                it.copy(
                    isLoading = false,
                    frames = frames,
                    frameCount = snapshots.size,
                    memoryKb = snapshots.size * 0.15,
                    message = if (frames.isEmpty()) {
                        "No locally buffered telemetry frames yet. Open Home or Diagnostics while online to cache readings."
                    } else null
                )
            }
        }
    }

    private fun relativeTime(now: Long, timestamp: Long): String {
        val seconds = TimeUnit.MILLISECONDS.toSeconds(now - timestamp).coerceAtLeast(0)
        return when {
            seconds < 60 -> "${seconds}s ago"
            seconds < 3600 -> "${seconds / 60}m ago"
            else -> "${seconds / 3600}h ago"
        }
    }
}
