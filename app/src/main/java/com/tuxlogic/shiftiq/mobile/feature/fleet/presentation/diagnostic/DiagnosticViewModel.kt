package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.diagnostic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.DriverDtcAlert
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class DiagnosticViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(DiagnosticUiState())
    val uiState: StateFlow<DiagnosticUiState> = _uiState.asStateFlow()

    init {
        startTelemetryLoop()
    }

    private fun startTelemetryLoop() {
        viewModelScope.launch {
            while (true) {
                delay(2000L)
                if (!_uiState.value.isScanning) {
                    val rpmDelta = Random.nextInt(-20, 25)
                    val tempDelta = Random.nextInt(-1, 2)
                    val voltDelta = (Random.nextDouble(-0.1, 0.1) * 10).toInt() / 10.0
                    val loadDelta = Random.nextInt(-2, 3)

                    _uiState.update { current ->
                        current.copy(
                            telemetry = current.telemetry.copy(
                                rpm = (840 + rpmDelta).coerceIn(780, 920),
                                temperatureCelsius = (88 + tempDelta).coerceIn(86, 91),
                                batteryVoltage = (13.8 + voltDelta).coerceIn(13.6, 14.1),
                                isSynchronized = true
                            ),
                            engineLoadPercent = (22 + loadDelta).coerceIn(18, 28)
                        )
                    }
                }
            }
        }
    }

    fun startDiagnosticScan() {
        viewModelScope.launch {
            _uiState.update { it.copy(isScanning = true, scanProgress = 0f) }

            for (i in 1..10) {
                delay(150L)
                _uiState.update { it.copy(scanProgress = i / 10f) }
            }

            _uiState.update { current ->
                current.copy(
                    isScanning = false,
                    scanProgress = 1.0f,
                    activeDtcAlert = DriverDtcAlert(
                        code = "P0135",
                        title = "Alerta Leve: Sensor de Oxígeno",
                        severity = "Media",
                        suggestedReviewKm = 500,
                        description = "Ligera variación en la pre-calefacción de la mezcla de combustible. Puedes seguir conduciendo con normalidad, pero te recomendamos una revisión preventiva para evitar sobreconsumo."
                    )
                )
            }
        }
    }

    fun clearDtcCodes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isScanning = true, scanProgress = 0f) }
            delay(1000L)
            _uiState.update {
                it.copy(
                    isScanning = false,
                    scanProgress = 1.0f,
                    activeDtcAlert = null,
                    dtcResolvedMessage = "Todos los códigos DTC borrados. Módulos ECU restablecidos."
                )
            }
        }
    }

    fun dismissResolvedMessage() {
        _uiState.update { it.copy(dtcResolvedMessage = null) }
    }
}
