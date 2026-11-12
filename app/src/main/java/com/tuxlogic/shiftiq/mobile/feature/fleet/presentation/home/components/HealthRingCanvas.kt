package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.home.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQSuccess
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQTheme
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.spacing

@Composable
fun HealthRingCanvas(
    score: Int,
    modifier: Modifier = Modifier,
    ringColor: Color = ShiftIQSuccess,
    trackColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh
) {
    val spacing = MaterialTheme.spacing
    Box(
        modifier = modifier.size(spacing.gaugeSmall),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(spacing.gaugeSmall)) {
            val strokeWidth = spacing.strokeGaugeSmall.toPx()
            drawCircle(
                color = trackColor,
                style = Stroke(width = strokeWidth)
            )
            val sweepAngle = 360f * (score.coerceIn(0, 100) / 100f)
            drawArc(
                color = ringColor,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        HealthRingScoreLabel(score = score)
    }
}

@Composable
private fun HealthRingScoreLabel(
    score: Int,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "$score%",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(spacing.micro))
        Text(
            text = "SCORE",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@PreviewLightDark
@PreviewFontScale
@Composable
private fun HealthRingCanvasPreview() {
    ShiftIQTheme {
        HealthRingCanvas(score = 94)
    }
}
