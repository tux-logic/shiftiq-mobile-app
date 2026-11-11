package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.home.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Navy900
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.PrimaryBlue
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate100
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate200
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate300
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate700
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate800
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate900

// High-fidelity fallback sedan image URL with transparent / neutral studio backdrop
private const val DEFAULT_SEDAN_PHOTO_URL =
    "https://images.unsplash.com/photo-1552519507-da3b142c6e3d?auto=format&fit=crop&w=800&q=80"

@Composable
fun CarSilhouetteCanvas(
    photoUrl: String? = null,
    modifier: Modifier = Modifier
) {
    val imageModel = if (!photoUrl.isNullOrBlank()) photoUrl else DEFAULT_SEDAN_PHOTO_URL

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(Slate100, Color.White)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Detailed volumetric Canvas fallback with realistic depth & ground shadows
        Canvas(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .height(120.dp)
        ) {
            val w = size.width
            val h = size.height

            // 1. Sombra suave radial en el suelo (Drop Shadow Oval con gradiente)
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x550F172A),
                        Color(0x220F172A),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.5f, h * 0.88f),
                    radius = w * 0.45f
                ),
                topLeft = Offset(w * 0.05f, h * 0.82f),
                size = Size(w * 0.90f, h * 0.16f)
            )

            // 2. Chasis y Carrocería con reflejos aerodinámicos
            val bodyPath = Path().apply {
                moveTo(w * 0.04f, h * 0.72f) // Frente bajo (paragolpes)
                cubicTo(w * 0.04f, h * 0.58f, w * 0.12f, h * 0.50f, w * 0.22f, h * 0.48f) // Capó
                cubicTo(w * 0.30f, h * 0.46f, w * 0.36f, h * 0.20f, w * 0.50f, h * 0.18f) // Parabrisas inclinado
                lineTo(w * 0.72f, h * 0.18f) // Techo aerodinámico
                cubicTo(w * 0.80f, h * 0.18f, w * 0.86f, h * 0.38f, w * 0.92f, h * 0.50f) // Luneta trasera
                cubicTo(w * 0.95f, h * 0.54f, w * 0.98f, h * 0.64f, w * 0.97f, h * 0.72f) // Maletero
                lineTo(w * 0.04f, h * 0.72f)
                close()
            }

            // Pintura azul metálico con gradiente multi-tono
            drawPath(
                path = bodyPath,
                brush = Brush.horizontalGradient(
                    0.0f to Navy900,
                    0.3f to PrimaryBlue,
                    0.6f to Color(0xFF2563EB),
                    1.0f to Slate800
                ),
                style = Fill
            )

            // Línea de brillo superior en la carrocería (Highlights)
            val highlightPath = Path().apply {
                moveTo(w * 0.08f, h * 0.54f)
                cubicTo(w * 0.20f, h * 0.49f, w * 0.35f, h * 0.46f, w * 0.52f, h * 0.20f)
                lineTo(w * 0.70f, h * 0.20f)
            }
            drawPath(
                path = highlightPath,
                color = Color.White.copy(alpha = 0.35f),
                style = Stroke(width = 3f)
            )

            // 3. Ventanas tintadas con cristal degradado y reflejo
            val windowPath = Path().apply {
                moveTo(w * 0.36f, h * 0.45f)
                lineTo(w * 0.50f, h * 0.22f)
                lineTo(w * 0.70f, h * 0.22f)
                cubicTo(w * 0.76f, h * 0.22f, w * 0.82f, h * 0.38f, w * 0.86f, h * 0.45f)
                close()
            }
            drawPath(
                path = windowPath,
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF38BDF8).copy(alpha = 0.85f), Navy900)
                ),
                style = Fill
            )

            // Pilar central (B-Pillar en negro piano)
            drawLine(
                color = Slate900,
                start = Offset(w * 0.58f, h * 0.22f),
                end = Offset(w * 0.58f, h * 0.45f),
                strokeWidth = 5f
            )

            // 4. Faros LED con efecto de luz
            // Faro Delantero (Daytime Running Light blanco brillante)
            drawCircle(
                color = Color(0xFFFEF08A),
                radius = 5f,
                center = Offset(w * 0.06f, h * 0.55f)
            )
            drawCircle(
                color = Color.White,
                radius = 2.5f,
                center = Offset(w * 0.06f, h * 0.55f)
            )

            // Faro Trasero (LED Rojo deportivo)
            drawCircle(
                color = Color(0xFFDC2626),
                radius = 5f,
                center = Offset(w * 0.95f, h * 0.57f)
            )

            // 5. Ruedas de Aleación (Neumáticos + Rines pulidos + Cáliper de freno)
            val frontWheelCenter = Offset(w * 0.24f, h * 0.72f)
            val rearWheelCenter = Offset(w * 0.78f, h * 0.72f)
            val wheelRadius = h * 0.22f

            // Pasos de rueda oscuros (guardabarros)
            drawCircle(color = Color(0xFF0F172A), radius = wheelRadius * 1.12f, center = frontWheelCenter)
            drawCircle(color = Color(0xFF0F172A), radius = wheelRadius * 1.12f, center = rearWheelCenter)

            // Neumático de goma
            drawCircle(color = Color(0xFF1E293B), radius = wheelRadius, center = frontWheelCenter)
            drawCircle(color = Color(0xFF1E293B), radius = wheelRadius, center = rearWheelCenter)

            // Rin de aluminio pulido
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White, Slate300, Slate700),
                    center = frontWheelCenter,
                    radius = wheelRadius * 0.7f
                ),
                radius = wheelRadius * 0.68f,
                center = frontWheelCenter
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White, Slate300, Slate700),
                    center = rearWheelCenter,
                    radius = wheelRadius * 0.7f
                ),
                radius = wheelRadius * 0.68f,
                center = rearWheelCenter
            )

            // Cáliper de freno rojo deportivo
            drawCircle(color = Color(0xFFEF4444), radius = wheelRadius * 0.28f, center = Offset(frontWheelCenter.x - 6f, frontWheelCenter.y - 4f))
            drawCircle(color = Color(0xFFEF4444), radius = wheelRadius * 0.28f, center = Offset(rearWheelCenter.x - 6f, rearWheelCenter.y - 4f))

            // Tapa cubo central con emblema
            drawCircle(color = PrimaryBlue, radius = wheelRadius * 0.20f, center = frontWheelCenter)
            drawCircle(color = PrimaryBlue, radius = wheelRadius * 0.20f, center = rearWheelCenter)
            drawCircle(color = Color.White, radius = 3f, center = frontWheelCenter)
            drawCircle(color = Color.White, radius = 3f, center = rearWheelCenter)
        }

        // Carga de imagen real con Coil (si la URL está disponible o fallback)
        AsyncImage(
            model = imageModel,
            contentDescription = "Vehículo",
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .height(130.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop,
            alpha = 0.95f
        )
    }
}
