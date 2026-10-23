package com.tuxlogic.shiftiq.mobile.features.onboarding.presentation

import androidx.annotation.DrawableRes
import com.tuxlogic.shiftiq.mobile.R

data class OnboardingSlide(
    @DrawableRes val imageRes: Int,
    val title: String,
    val subtitle: String
)

data class OnboardingUiState(
    val currentPage: Int = 0,
    val slides: List<OnboardingSlide> = listOf(
        OnboardingSlide(
            imageRes = R.drawable.onboarding1,
            title = "Tu auto bajo control inteligente",
            subtitle = "Telemetría preventiva OBD-II y diagnósticos precisos."
        ),
        OnboardingSlide(
            imageRes = R.drawable.onboarding2,
            title = "Detección temprana de fallas DTC",
            subtitle = "Monitorea la salud de tu motor en tiempo real antes de averías costosas."
        ),
        OnboardingSlide(
            imageRes = R.drawable.onboarding3,
            title = "Conexión directa con talleres certificados",
            subtitle = "Historial técnico transparente, presupuestos claros y reservas en un toque."
        )
    ),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
