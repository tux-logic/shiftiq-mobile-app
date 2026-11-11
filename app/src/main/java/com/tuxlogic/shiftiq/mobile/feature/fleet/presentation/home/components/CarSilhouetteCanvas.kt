package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.home.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Navy900
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.PrimaryBlue
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate100
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate300
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate700
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate800

@Composable
fun CarSilhouetteCanvas(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(130.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(Slate100, Color.White)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(85.dp)
        ) {
            val w = size.width
            val h = size.height

            // 1. Sombra suave inferior
            drawRoundRect(
                color = Slate300.copy(alpha = 0.5f),
                topLeft = Offset(w * 0.08f, h * 0.88f),
                size = Size(w * 0.84f, h * 0.08f),
                cornerRadius = CornerRadius(10f, 10f)
            )

            // 2. Carrocería del sedán moderno
            val bodyPath = Path().apply {
                moveTo(w * 0.05f, h * 0.72f) // Frente bajo
                cubicTo(w * 0.05f, h * 0.60f, w * 0.12f, h * 0.52f, w * 0.22f, h * 0.50f) // Capó
                cubicTo(w * 0.32f, h * 0.48f, w * 0.38f, h * 0.22f, w * 0.52f, h * 0.20f) // Parabrisas
                lineTo(w * 0.72f, h * 0.20f) // Techo
                cubicTo(w * 0.80f, h * 0.20f, w * 0.86f, h * 0.40f, w * 0.92f, h * 0.52f) // Luneta trasera
                cubicTo(w * 0.95f, h * 0.55f, w * 0.98f, h * 0.65f, w * 0.97f, h * 0.72f) // Maletero
                lineTo(w * 0.05f, h * 0.72f) // Fondo
                close()
            }

            // Relleno de carrocería en azul metálico
            drawPath(
                path = bodyPath,
                brush = Brush.horizontalGradient(
                    colors = listOf(Navy900, PrimaryBlue, Slate800)
                ),
                style = Fill
            )

            // 3. Ventanas estilizadas (tintadas oscuras con reflejo celeste)
            val windowPath = Path().apply {
                moveTo(w * 0.38f, h * 0.46f)
                lineTo(w * 0.52f, h * 0.25f)
                lineTo(w * 0.70f, h * 0.25f)
                cubicTo(w * 0.76f, h * 0.25f, w * 0.82f, h * 0.40f, w * 0.86f, h * 0.46f)
                close()
            }
            drawPath(
                path = windowPath,
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF38BDF8).copy(alpha = 0.7f), Navy900)
                ),
                style = Fill
            )

            // Pilar central (B-Pillar)
            drawLine(
                color = Slate800,
                start = Offset(w * 0.60f, h * 0.25f),
                end = Offset(w * 0.60f, h * 0.46f),
                strokeWidth = 4f
            )

            // 4. Faros LED delantero y trasero
            drawCircle(
                color = Color(0xFFFDE047),
                radius = 4f,
                center = Offset(w * 0.07f, h * 0.56f)
            )
            drawCircle(
                color = Color(0xFFEF4444),
                radius = 4f,
                center = Offset(w * 0.95f, h * 0.58f)
            )

            // 5. Ruedas (Llantas con aros plateados)
            val frontWheelCenter = Offset(w * 0.24f, h * 0.72f)
            val rearWheelCenter = Offset(w * 0.78f, h * 0.72f)
            val wheelRadius = h * 0.22f

            // Neumático exterior
            drawCircle(color = Color(0xFF1E293B), radius = wheelRadius, center = frontWheelCenter)
            drawCircle(color = Color(0xFF1E293B), radius = wheelRadius, center = rearWheelCenter)

            // Rin plateado
            drawCircle(color = Slate300, radius = wheelRadius * 0.60f, center = frontWheelCenter, style = Fill)
            drawCircle(color = Slate300, radius = wheelRadius * 0.60f, center = rearWheelCenter, style = Fill)

            // Tapa cubo central
            drawCircle(color = Navy900, radius = wheelRadius * 0.25f, center = frontWheelCenter)
            drawCircle(color = Navy900, radius = wheelRadius * 0.25f, center = rearWheelCenter)

            // Borde de llantas
            drawCircle(color = Slate700, radius = wheelRadius, center = frontWheelCenter, style = Stroke(width = 2f))
            drawCircle(color = Slate700, radius = wheelRadius, center = rearWheelCenter, style = Stroke(width = 2f))
        }
    }
}
