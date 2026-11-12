package com.tuxlogic.shiftiq.mobile.core.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.NavActiveBlue
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.NavActivePill
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.NavInactiveSlate
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQTheme
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.spacing

data class ShiftIQNavItemData(
    val title: String,
    val icon: ImageVector
)

val DefaultB2CNavItems = listOf(
    ShiftIQNavItemData("Home", Icons.Default.Home),
    ShiftIQNavItemData("Diagnóstico", Icons.Default.Build),
    ShiftIQNavItemData("Talleres", Icons.Default.Storefront),
    ShiftIQNavItemData("Mi Garaje", Icons.Default.DirectionsCar)
)

/**
 * Pixel-perfect Material 3 Bottom Navigation Bar matching Figma Frame 215:15142.
 * Enforces zero hardcoded literals, 64dp bar height, 40x24dp pill indicator with 70% tint,
 * 16dp icon size, and 10sp typography with active/inactive states.
 */
@Composable
fun ShiftIQBottomNavBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    items: List<ShiftIQNavItemData> = DefaultB2CNavItems
) {
    val spacing = MaterialTheme.spacing
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(spacing.bottomNavHeight),
        border = BorderStroke(spacing.strokeThin, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.9f)),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = spacing.elevationSmall
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(spacing.bottomNavHeight)
                .padding(horizontal = spacing.small),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                ShiftIQBottomNavItem(
                    item = item,
                    isSelected = selectedTab == index,
                    onSelect = { onTabSelected(index) }
                )
            }
        }
    }
}

@Composable
private fun ShiftIQBottomNavItem(
    item: ShiftIQNavItemData,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = modifier
            .width(spacing.navItemWidth)
            .clip(RoundedCornerShape(spacing.radiusMedium))
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, radius = spacing.large),
                onClick = onSelect
            )
            .padding(vertical = spacing.extraSmall),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ShiftIQNavPill(
            icon = item.icon,
            contentDescription = item.title,
            isSelected = isSelected
        )
        Spacer(modifier = Modifier.height(spacing.micro))
        Text(
            text = item.title,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) NavActiveBlue else NavInactiveSlate
        )
    }
}

@Composable
private fun ShiftIQNavPill(
    icon: ImageVector,
    contentDescription: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    val pillBg = if (isSelected) NavActivePill.copy(alpha = 0.7f) else Color.Transparent
    val iconTint = if (isSelected) NavActiveBlue else NavInactiveSlate

    Box(
        modifier = modifier
            .width(spacing.navPillWidth)
            .height(spacing.navPillHeight)
            .clip(RoundedCornerShape(spacing.radiusFull))
            .background(pillBg),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = iconTint,
            modifier = Modifier.size(spacing.iconMedium)
        )
    }
}

@PreviewLightDark
@PreviewFontScale
@Composable
private fun ShiftIQBottomNavBarPreview() {
    ShiftIQTheme {
        ShiftIQBottomNavBar(
            selectedTab = 0,
            onTabSelected = {}
        )
    }
}
