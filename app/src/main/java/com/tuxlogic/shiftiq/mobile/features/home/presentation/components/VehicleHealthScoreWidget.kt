package com.tuxlogic.shiftiq.mobile.features.home.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate400
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate900

@Composable
fun VehicleHealthScoreWidget(
    score: Int,
    modifier: Modifier = Modifier,
    size: Dp = 110.dp,
    strokeWidth: Dp = 9.dp
) {
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(score) {
        animatedProgress.animateTo(
            targetValue = (score.coerceIn(0, 100) / 100f),
            animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing)
        )
    }

    val progressColor = when {
        score >= 85 -> Color(0xFF10B981) // Verde esmeralda óptimo
        score >= 65 -> Color(0xFFF59E0B) // Ámbar preventivo
        else -> Color(0xFFEF4444)        // Rojo crítico
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val stroke = strokeWidth.toPx()
            val diameter = size.toPx() - stroke

            // Fondo circular
            drawCircle(
                color = Color(0xFFE2E8F0),
                radius = diameter / 2f,
                style = Stroke(width = stroke)
            )

            // Arco de progreso con inicio en la parte superior (-90 grados)
            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress.value,
                useCenter = false,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$score%",
                color = Slate900,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "SCORE",
                color = Slate400,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}
