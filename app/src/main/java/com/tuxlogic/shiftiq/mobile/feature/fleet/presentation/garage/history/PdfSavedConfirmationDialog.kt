package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.garage.history

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Navy900
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQSuccess
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate100
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate200
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate300
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate400
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate500
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate700
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.spacing

@Composable
fun PdfSavedConfirmationDialog(
    fileName: String,
    filePath: String,
    fileSizeText: String,
    fileFormatText: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(spacing.radiusLarge),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(spacing.strokeThin, Slate200)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(spacing.large),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(spacing.medium)
            ) {
                Box(
                    modifier = Modifier
                        .size(spacing.extraExtraLarge)
                        .clip(RoundedCornerShape(spacing.radiusMedium))
                        .background(Navy900),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(spacing.iconExtraLarge)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(spacing.extraSmall)
                ) {
                    Text(
                        text = "¡Archivo PDF Guardado!",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "El comprobante se guardó correctamente en la ubicación que elegiste.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate500,
                        textAlign = TextAlign.Center
                    )
                }

                FileMetadataCard(
                    fileName = fileName,
                    fileSizeText = fileSizeText,
                    fileFormatText = fileFormatText
                )

                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(spacing.radiusSmall),
                    border = BorderStroke(spacing.strokeThin, Slate300),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Slate700),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(spacing.buttonHeight)
                ) {
                    Text(
                        text = "Volver",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Folder,
                        contentDescription = null,
                        tint = Slate400,
                        modifier = Modifier.size(spacing.iconSmall)
                    )
                    Spacer(modifier = Modifier.width(spacing.extraSmall))
                    Text(
                        text = filePath,
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        color = Slate400
                    )
                }
            }
        }
    }
}

@Composable
private fun FileMetadataCard(
    fileName: String,
    fileSizeText: String,
    fileFormatText: String,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(spacing.radiusMedium),
        colors = CardDefaults.cardColors(containerColor = Slate100.copy(alpha = 0.5f)),
        border = BorderStroke(spacing.strokeThin, Slate200)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.medium),
            verticalArrangement = Arrangement.spacedBy(spacing.small)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    tint = Navy900,
                    modifier = Modifier.size(spacing.iconMedium)
                )
                Spacer(modifier = Modifier.width(spacing.small))
                Text(
                    text = fileName,
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = Slate700
                )
            }
            Text(
                text = "Tamaño: $fileSizeText • Formato: $fileFormatText",
                style = MaterialTheme.typography.labelSmall,
                color = Slate500
            )
            HorizontalDivider(color = Slate200, thickness = spacing.strokeThin)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "GUARDADO EN DISPOSITIVO",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Slate500
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(spacing.radiusExtraSmall))
                        .background(Slate200)
                        .padding(horizontal = spacing.compact, vertical = spacing.micro)
                ) {
                    Text(
                        text = "PDF",
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = Slate700
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Verified,
                    contentDescription = null,
                    tint = ShiftIQSuccess,
                    modifier = Modifier.size(spacing.iconSmall)
                )
                Spacer(modifier = Modifier.width(spacing.extraSmall))
                Text(
                    text = "Archivo disponible en la ubicación seleccionada",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = Slate700
                )
            }
        }
    }
}
