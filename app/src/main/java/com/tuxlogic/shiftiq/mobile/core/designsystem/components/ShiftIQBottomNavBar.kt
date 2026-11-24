package com.tuxlogic.shiftiq.mobile.core.designsystem.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Navy900
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQAccentSkyBlue
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQTheme
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate400
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.spacing

data class ShiftIQNavItemData(
    val title: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon
)

val DefaultB2CNavItems = listOf(
    ShiftIQNavItemData("Home", Icons.Default.Home),
    ShiftIQNavItemData("Diagnóstico", Icons.Default.Build),
    ShiftIQNavItemData("Talleres", Icons.Default.Storefront),
    ShiftIQNavItemData("Mi Garaje", Icons.Default.DirectionsCar)
)

private val NavDockEnd = Color(0xFF172554)
private val PillStart = Color(0x5260A5FA)
private val PillEnd = Color(0x2E2563EB)
private val DockShadow = Color(0x380F172A)
private val AccentShadow = Color(0x222563EB)

/**
 * Floating navy navigation dock aligned with Flutter [ShiftIQFloatingNavBar].
 */
@Composable
fun ShiftIQBottomNavBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    items: List<ShiftIQNavItemData> = DefaultB2CNavItems
) {
    if (items.isEmpty()) return

    val spacing = MaterialTheme.spacing
    val safeIndex = selectedTab.coerceIn(0, items.lastIndex)
    val haptics = LocalHapticFeedback.current
    val dockShape = RoundedCornerShape(28.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(start = spacing.medium, end = spacing.medium, bottom = spacing.smallMedium)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(spacing.bottomNavHeight)
                .shadow(
                    elevation = 16.dp,
                    shape = dockShape,
                    ambientColor = DockShadow,
                    spotColor = AccentShadow
                )
                .clip(dockShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Navy900, NavDockEnd)
                    )
                )
                .border(
                    width = spacing.strokeThin,
                    color = Color.White.copy(alpha = 36f / 255f),
                    shape = dockShape
                )
                .padding(6.dp)
        ) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val slotWidth = maxWidth / items.size
                val pillOffset = remember { Animatable(safeIndex.toFloat()) }

                LaunchedEffect(safeIndex) {
                    pillOffset.animateTo(
                        targetValue = safeIndex.toFloat(),
                        animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
                    )
                }

                Box(
                    modifier = Modifier
                        .offset(x = slotWidth * pillOffset.value)
                        .width(slotWidth)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            brush = Brush.linearGradient(colors = listOf(PillStart, PillEnd))
                        )
                        .border(
                            width = spacing.strokeThin,
                            color = Color.White.copy(alpha = 115f / 255f),
                            shape = RoundedCornerShape(22.dp)
                        )
                )

                Row(modifier = Modifier.fillMaxSize()) {
                    items.forEachIndexed { index, item ->
                        FloatingNavSlot(
                            item = item,
                            selected = index == safeIndex,
                            onSelect = {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onTabSelected(index)
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FloatingNavSlot(
    item: ShiftIQNavItemData,
    selected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val pressScale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (pressed) 0.92f else 1f,
        animationSpec = tween(140),
        label = "navPressScale"
    )
    val iconScale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (selected) 1.12f else 1f,
        animationSpec = tween(240),
        label = "navIconScale"
    )
    val tint = if (selected) Color.White else Slate400

    Column(
        modifier = modifier
            .fillMaxHeight()
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onSelect
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        Icon(
            imageVector = if (selected) item.selectedIcon else item.icon,
            contentDescription = item.title,
            tint = tint,
            modifier = Modifier
                .size(21.dp)
                .graphicsLayer {
                    scaleX = iconScale
                    scaleY = iconScale
                }
        )
        AnimatedVisibility(
            visible = selected,
            enter = fadeIn(tween(180)) + expandVertically(tween(220)),
            exit = fadeOut(tween(180)) + shrinkVertically(tween(220))
        ) {
            Text(
                text = item.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.1).sp,
                    color = Color.White
                ),
                modifier = Modifier.padding(top = 2.dp, start = 4.dp, end = 4.dp)
            )
        }
    }
}

@PreviewLightDark
@PreviewFontScale
@Composable
private fun ShiftIQBottomNavBarPreview() {
    ShiftIQTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(ShiftIQAccentSkyBlue.copy(alpha = 0.15f))
                .padding(top = 24.dp)
        ) {
            ShiftIQBottomNavBar(
                selectedTab = 0,
                onTabSelected = {}
            )
        }
    }
}
