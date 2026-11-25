package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Navy900
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQPrimary
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQSecondary
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQWarning
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.spacing

/**
 * Welcome hero card mirrored from Flutter [_buildWelcomeHero].
 */
@Composable
fun CustomerWelcomeHero(
    driverName: String,
    driverEmail: String,
    driverRole: String,
    healthPercent: Int,
    healthRatio: Float,
    dtcCount: Int,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    val healthColor = if (dtcCount > 0) ShiftIQWarning else ShiftIQSecondary
    val shape = RoundedCornerShape(20.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Navy900, ShiftIQPrimary)
                )
            )
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = driverRole.ifBlank { "CLIENTE" }.uppercase(),
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.4.sp,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(ShiftIQSecondary.copy(alpha = 45f / 255f))
                    .border(
                        width = 1.dp,
                        color = ShiftIQSecondary.copy(alpha = 120f / 255f),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Salud $healthPercent%",
                color = healthColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "¡Hola, $driverName!",
            color = Color.White,
            fontSize = 21.sp,
            fontWeight = FontWeight.ExtraBold
        )

        if (driverEmail.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = driverEmail,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.5.sp
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        LinearProgressIndicator(
            progress = { healthRatio.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(7.dp)
                .clip(RoundedCornerShape(999.dp)),
            color = healthColor,
            trackColor = Color.White.copy(alpha = 32f / 255f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (dtcCount == 0) {
                "Tus vehículos operan sin alertas OBD-II activas."
            } else {
                "Se detectaron $dtcCount alerta(s) DTC. Revisa Diagnóstico."
            },
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 12.sp
        )
    }
}
