package com.tuxlogic.shiftiq.mobile.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import com.tuxlogic.shiftiq.mobile.core.localization.LocalizedText as Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tuxlogic.shiftiq.mobile.core.localization.AppLanguage
import com.tuxlogic.shiftiq.mobile.core.localization.AppLanguageStore
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Navy900

@Composable
fun AppLanguageToggle(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val selected = AppLanguageStore.current
    val description = if (selected == AppLanguage.SPANISH) "Idioma: Español" else "Language: English"

    Row(
        modifier = modifier
            .semantics { contentDescription = description }
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.16f))
            .border(1.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
            .padding(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LanguageOption(
            label = "ES",
            selected = selected == AppLanguage.SPANISH,
            onClick = { AppLanguageStore.set(context, AppLanguage.SPANISH) }
        )
        LanguageOption(
            label = "EN",
            selected = selected == AppLanguage.ENGLISH,
            onClick = { AppLanguageStore.set(context, AppLanguage.ENGLISH) }
        )
    }
}

@Composable
private fun LanguageOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Text(
        text = label,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) Color.White else Color.Transparent)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 9.dp, vertical = 4.dp),
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = if (selected) Navy900 else Color.White.copy(alpha = 0.78f)
    )
}
