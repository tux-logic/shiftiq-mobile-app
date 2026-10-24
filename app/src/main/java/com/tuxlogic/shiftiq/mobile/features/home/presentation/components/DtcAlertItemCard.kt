package com.tuxlogic.shiftiq.mobile.features.home.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.arrowForward
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.warningAmber
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.PrimaryBlue
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate200
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate600
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate900
import com.tuxlogic.shiftiq.mobile.features.home.domain.model.AlertSeverity
import com.tuxlogic.shiftiq.mobile.features.home.domain.model.DtcAlert

@Composable
fun DtcAlertItemCard(
    alert: DtcAlert,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = when (alert.severity) {
        AlertSeverity.LOW -> Color(0xFF3B82F6)     // Azul informativo
        AlertSeverity.MEDIUM -> Color(0xFFF59E0B)  // Ámbar
        AlertSeverity.HIGH -> Color(0xFFEA580C)    // Naranja intenso
        AlertSeverity.CRITICAL -> Color(0xFFEF4444)// Carmesí crítico
    }

    val badgeBg = when (alert.severity) {
        AlertSeverity.LOW -> Color(0xFFEFF6FF)
        AlertSeverity.MEDIUM -> Color(0xFFFEF3C7)
        AlertSeverity.HIGH -> Color(0xFFFFEDD5)
        AlertSeverity.CRITICAL -> Color(0xFFFEE2E2)
    }

    val badgeTextColor = when (alert.severity) {
        AlertSeverity.LOW -> Color(0xFF1E40AF)
        AlertSeverity.MEDIUM -> Color(0xFF92400E)
        AlertSeverity.HIGH -> Color(0xFF9A3412)
        AlertSeverity.CRITICAL -> Color(0xFF991B1B)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Slate200),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Barra lateral con acento de color de severidad
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .height(130.dp)
                    .background(accentColor)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                // Fila superior: Icono + Título + Código DTC
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(badgeBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = warningAmber,
                                contentDescription = null,
                                tint = accentColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = alert.title,
                            color = Slate900,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }

                    // Badge con el código de falla OBD-II (ej. P0135)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFF1F5F9))
                            .border(BorderStroke(1.dp, Color(0xFFCBD5E1)), RoundedCornerShape(6.dp))
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = alert.code,
                            color = Slate900,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Badge de severidad y sugerencia de revisión en km
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(badgeBg)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "${alert.severityLabel} • Revisión sugerida en ${alert.suggestedReviewKm} km",
                        color = badgeTextColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Breve descripción
                Text(
                    text = alert.description,
                    color = Slate600,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Enlace a diagnóstico
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Ver Diagnóstico Completo",
                        color = PrimaryBlue,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = arrowForward,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}
