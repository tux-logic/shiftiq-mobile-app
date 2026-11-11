package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.home.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate200
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate500
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate900
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.TelematicsGreen

@Composable
fun HealthRingCanvas(
    score: Int = 94,
    modifier: Modifier = Modifier,
    ringColor: Color = TelematicsGreen,
    trackColor: Color = Slate200
) {
    Box(
        modifier = modifier.size(130.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(120.dp)) {
            val strokeWidth = 10.dp.toPx()
            // Track de fondo completo (360 grados)
            drawCircle(
                color = trackColor,
                style = Stroke(width = strokeWidth)
            )
            // Arco de progreso correspondiente al porcentaje (iniciando en -90 grados / arriba)
            val sweepAngle = 360f * (score.coerceIn(0, 100) / 100f)
            drawArc(
                color = ringColor,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$score%",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Slate900
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "SCORE",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = Slate500
            )
        }
    }
}
