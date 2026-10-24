package com.tuxlogic.shiftiq.mobile.core.designsystem.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Better UI Principles:
 * 1. Press scales to 0.96 over 150ms with ease-out.
 * 2. High-frequency / disabled interactions skip scaling.
 */
fun Modifier.scaleOnPress(
    enabled: Boolean = true,
    targetScale: Float = 0.96f,
    interactionSource: MutableInteractionSource? = null,
    onClick: (() -> Unit)? = null
): Modifier = composed {
    if (!enabled) return@composed this

    val source = interactionSource ?: remember { MutableInteractionSource() }
    val isPressed by source.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) targetScale else 1f,
        animationSpec = tween(
            durationMillis = 150,
            easing = FastOutSlowInEasing
        ),
        label = "better_ui_scale_on_press"
    )

    val modifier = this.scale(scale)
    if (onClick != null) {
        modifier.clickable(
            interactionSource = source,
            indication = null, // Custom scale provides tactile feedback
            onClick = onClick
        )
    } else {
        modifier
    }
}

/**
 * Better UI concentric radius calculation:
 * Outer radius = Inner radius + padding (for padding <= 24.dp)
 */
fun concentricOuterRadius(innerRadius: Dp, padding: Dp): Dp {
    return if (padding <= 24.dp) {
        innerRadius + padding
    } else {
        innerRadius
    }
}

fun concentricInnerRadius(outerRadius: Dp, padding: Dp): Dp {
    return if (padding <= 24.dp && outerRadius > padding) {
        outerRadius - padding
    } else {
        outerRadius
    }
}
