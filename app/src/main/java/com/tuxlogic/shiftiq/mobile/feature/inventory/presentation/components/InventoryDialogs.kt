package com.tuxlogic.shiftiq.mobile.feature.inventory.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate100
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate200
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate500
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate600
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate800
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate900

@Composable
fun DuplicateSkuDialog(
    sku: String,
    onViewExisting: () -> Unit,
    onEditSku: () -> Unit,
    onDismiss: () -> Unit
) {
    InventoryModalScaffold(
        title = "El código SKU ya se encuentra registrado",
        subtitle = "No se puede registrar el repuesto con código $sku porque ya existe en el catálogo de esta sucursal.",
        icon = { WarningGlyph() },
        onDismiss = onDismiss
    ) {
        InfoNote(
            text = "Si deseas actualizar las existencias de este repuesto, ingresa un nuevo lote en lugar de crear un producto duplicado."
        )
        Spacer(modifier = Modifier.height(16.dp))
        PrimaryDarkButton(text = "Entendido", onClick = onEditSku)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(
            onClick = onViewExisting,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Slate200)
        ) {
            Text("Volver al catálogo", color = Slate800, fontWeight = FontWeight.SemiBold)
        }
        TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
            Text("Cerrar ventana", color = Slate500)
        }
    }
}

@Composable
fun ProductInUseDialog(
    productName: String,
    onDismiss: () -> Unit
) {
    InventoryModalScaffold(
        title = "No se puede eliminar el repuesto",
        subtitle = "El producto \"$productName\" tiene intervenciones técnicas asociadas y no puede retirarse del catálogo.",
        icon = { LockGlyph() },
        onDismiss = onDismiss
    ) {
        InfoNote(
            text = "Mantén el repuesto en catálogo para preservar la trazabilidad histórica. Solo se puede eliminar cuando no esté ligado a órdenes de trabajo."
        )
        Spacer(modifier = Modifier.height(16.dp))
        PrimaryDarkButton(text = "Mantener en catálogo", onClick = onDismiss)
        TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
            Text("Cancelar", color = Slate500)
        }
    }
}

@Composable
fun NegativeStockDialog(
    currentStock: Int,
    attemptedQty: Int,
    onCorrectQuantity: () -> Unit,
    onDismiss: () -> Unit
) {
    val resulting = currentStock - attemptedQty
    InventoryModalScaffold(
        title = "Ajuste no permitido: saldo negativo",
        subtitle = "No es posible procesar una deducción de $attemptedQty unidades porque excede las existencias físicas disponibles.",
        icon = { WarningGlyph() },
        onDismiss = onDismiss
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Slate100)
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                BalanceRow("Stock actual registrado", "$currentStock un.")
                BalanceRow("Cantidad a retirar", "$attemptedQty un.")
                BalanceRow("Saldo resultante", "$resulting un.", emphasize = true)
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        InfoNote(
            text = "Para regularizar diferencias superiores al registro, verifica el conteo físico de lotes o ingresa existencias pendientes de recepción."
        )
        Spacer(modifier = Modifier.height(16.dp))
        PrimaryDarkButton(text = "Corregir cantidad a retirar", onClick = onCorrectQuantity)
        TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
            Text("Cerrar ventana", color = Slate500)
        }
    }
}

@Composable
private fun InventoryModalScaffold(
    title: String,
    subtitle: String,
    icon: @Composable () -> Unit,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            tonalElevation = 0.dp,
            shadowElevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Slate500)
                    }
                }
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Slate100),
                    contentAlignment = Alignment.Center
                ) {
                    icon()
                }
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = title,
                    color = Slate900,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 24.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = subtitle,
                    color = Slate600,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                content()
            }
        }
    }
}

@Composable
private fun InfoNote(text: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Slate200)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = Slate500,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = text, color = Slate600, fontSize = 12.sp, lineHeight = 17.sp)
        }
    }
}

@Composable
private fun BalanceRow(label: String, value: String, emphasize: Boolean = false) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Slate600, fontSize = 12.sp)
        Text(
            value,
            color = if (emphasize) Slate900 else Slate800,
            fontSize = 12.sp,
            fontWeight = if (emphasize) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
private fun PrimaryDarkButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Slate900,
            contentColor = Color.White
        )
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 4.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun WarningGlyph() {
    Icon(Icons.Default.Warning, contentDescription = null, tint = Slate800, modifier = Modifier.size(24.dp))
}

@Composable
private fun LockGlyph() {
    Icon(Icons.Default.Lock, contentDescription = null, tint = Slate800, modifier = Modifier.size(24.dp))
}
