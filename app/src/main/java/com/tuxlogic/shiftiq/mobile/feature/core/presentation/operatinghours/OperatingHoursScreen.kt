package com.tuxlogic.shiftiq.mobile.feature.core.presentation.operatinghours

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tuxlogic.shiftiq.mobile.core.designsystem.components.ShiftIQPageHeader
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.PrimaryBlue
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQBorderSlate
import com.tuxlogic.shiftiq.mobile.core.localization.LocalizedText as Text
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.OperatingHoursDay
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.OperatingHoursExceptionInput
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.OperatingHoursScope
import java.time.LocalDate

@Composable
fun OperatingHoursRoute(
    onNavigateBack: () -> Unit,
    viewModel: OperatingHoursViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    OperatingHoursScreen(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onSelectScope = viewModel::selectScope,
        onUpdateDay = viewModel::updateDay,
        onSave = viewModel::saveSchedule,
        onReset = viewModel::resetBranchHours,
        onAddException = viewModel::addException,
        onDeleteException = viewModel::deleteException,
        onRetry = viewModel::load,
        onClearMessages = viewModel::clearMessages
    )
}

@Composable
fun OperatingHoursScreen(
    uiState: OperatingHoursUiState,
    onNavigateBack: () -> Unit,
    onSelectScope: (OperatingHoursScope) -> Unit,
    onUpdateDay: (String, (OperatingHoursDay) -> OperatingHoursDay) -> Unit,
    onSave: () -> Unit,
    onReset: () -> Unit,
    onAddException: (OperatingHoursExceptionInput) -> Unit,
    onDeleteException: (com.tuxlogic.shiftiq.mobile.feature.core.domain.model.OperatingHoursException) -> Unit,
    onRetry: () -> Unit,
    onClearMessages: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showExceptionDialog by remember { mutableStateOf(false) }
    Column(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        ShiftIQPageHeader(
            title = "Horarios y Feriados",
            subtitle = uiState.branchName.ifBlank { "Configura el horario del taller" },
            showBackButton = true,
            onBackPressed = onNavigateBack
        )
        when {
            uiState.isLoading && uiState.days.isEmpty() -> Column(
                Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) { CircularProgressIndicator(color = PrimaryBlue) }
            uiState.errorMessage != null && uiState.days.isEmpty() -> Column(
                Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(uiState.errorMessage, color = MaterialTheme.colorScheme.error)
            }
            else -> Column(
                Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (uiState.canEditWorkshop) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = uiState.scope == OperatingHoursScope.BRANCH,
                            onClick = { onSelectScope(OperatingHoursScope.BRANCH) },
                            label = { Text("Esta sede") }
                        )
                        FilterChip(
                            selected = uiState.scope == OperatingHoursScope.WORKSHOP,
                            onClick = { onSelectScope(OperatingHoursScope.WORKSHOP) },
                            label = { Text("Todo el taller") }
                        )
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, ShiftIQBorderSlate),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Schedule, null, tint = PrimaryBlue)
                            Spacer(Modifier.width(8.dp))
                            Text("Horario semanal", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.height(8.dp))
                        uiState.days.forEach { day ->
                            OperatingHoursDayRow(day = day, onChanged = { updated -> onUpdateDay(day.dayOfWeek) { updated } })
                        }
                    }
                }

                uiState.errorMessage?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
                uiState.noticeMessage?.let {
                    Text(it, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall)
                }

                Button(onClick = onSave, enabled = !uiState.isSaving, modifier = Modifier.fillMaxWidth()) {
                    if (uiState.isSaving) CircularProgressIndicator(Modifier.height(18.dp).width(18.dp), strokeWidth = 2.dp)
                    else Text("Guardar horario")
                }
                if (uiState.scope == OperatingHoursScope.BRANCH && uiState.canEditWorkshop) {
                    OutlinedButton(onClick = onReset, enabled = !uiState.isSaving, modifier = Modifier.fillMaxWidth()) {
                        Text("Usar horario del taller")
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, ShiftIQBorderSlate),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Feriados y excepciones", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            TextButton(onClick = { showExceptionDialog = true }, enabled = !uiState.isSaving) {
                                Icon(Icons.Default.Add, null)
                                Text("Agregar")
                            }
                        }
                        if (uiState.exceptions.isEmpty()) {
                            Text("No hay excepciones registradas.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        } else {
                            uiState.exceptions.forEach { exception ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Column(Modifier.weight(1f)) {
                                        Text(exception.date, fontWeight = FontWeight.SemiBold)
                                        Text(
                                            if (exception.open) "Abierto ${exception.openTime.orEmpty()} - ${exception.closeTime.orEmpty()}" else "Cerrado",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        exception.reason?.takeIf(String::isNotBlank)?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                                    }
                                    TextButton(onClick = { onDeleteException(exception) }, enabled = !uiState.isSaving) {
                                        Icon(Icons.Default.DeleteOutline, contentDescription = "Eliminar excepción", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(18.dp))
            }
        }
    }
    if (showExceptionDialog) {
        OperatingHoursExceptionDialog(
            onDismiss = { showExceptionDialog = false },
            onCreate = { exception -> onAddException(exception); showExceptionDialog = false }
        )
    }
}

@Composable
private fun OperatingHoursDayRow(day: OperatingHoursDay, onChanged: (OperatingHoursDay) -> Unit) {
    val dayLabel = when (day.dayOfWeek.uppercase()) {
        "MONDAY" -> "Lunes"; "TUESDAY" -> "Martes"; "WEDNESDAY" -> "Miércoles"
        "THURSDAY" -> "Jueves"; "FRIDAY" -> "Viernes"; "SATURDAY" -> "Sábado"; else -> "Domingo"
    }
    Column(Modifier.fillMaxWidth().padding(vertical = 5.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(dayLabel, modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium)
            Text(if (day.open) "Abierto" else "Cerrado", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Switch(checked = day.open, onCheckedChange = { onChanged(day.copy(open = it)) })
        }
        if (day.open) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                TimeButton("Apertura", day.openTime ?: "08:30") { onChanged(day.copy(openTime = it)) }
                TimeButton("Cierre", day.closeTime ?: "18:00") { onChanged(day.copy(closeTime = it)) }
                TextButton(onClick = { onChanged(day.copy(slotDurationMinutes = if (day.slotDurationMinutes >= 120) 30 else day.slotDurationMinutes + 15)) }) {
                    Text("${day.slotDurationMinutes} min")
                }
            }
        }
    }
}

@Composable
private fun TimeButton(label: String, value: String, onValue: (String) -> Unit) {
    val context = LocalContext.current
    OutlinedButton(onClick = {
        val hour = value.take(2).toIntOrNull() ?: 8
        val minute = value.drop(3).take(2).toIntOrNull() ?: 30
        TimePickerDialog(context, { _, selectedHour, selectedMinute -> onValue("%02d:%02d".format(selectedHour, selectedMinute)) }, hour, minute, true).show()
    }) { Text("$label $value", style = MaterialTheme.typography.labelSmall) }
}

@Composable
private fun OperatingHoursExceptionDialog(
    onDismiss: () -> Unit,
    onCreate: (OperatingHoursExceptionInput) -> Unit
) {
    val context = LocalContext.current
    var date by remember { mutableStateOf(LocalDate.now().plusDays(1)) }
    var specialHours by remember { mutableStateOf(false) }
    var openTime by remember { mutableStateOf("09:00") }
    var closeTime by remember { mutableStateOf("13:00") }
    var reason by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva excepción") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = {
                    DatePickerDialog(context, { _, year, month, day -> date = LocalDate.of(year, month + 1, day) }, date.year, date.monthValue - 1, date.dayOfMonth).apply {
                        datePicker.minDate = System.currentTimeMillis()
                    }.show()
                }) { Text(date.toString()) }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Abrir con horario especial", modifier = Modifier.weight(1f))
                    Switch(checked = specialHours, onCheckedChange = { specialHours = it })
                }
                if (specialHours) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TimeButton("Apertura", openTime) { openTime = it }
                        TimeButton("Cierre", closeTime) { closeTime = it }
                    }
                }
                OutlinedTextField(value = reason, onValueChange = { reason = it }, label = { Text("Motivo") }, singleLine = true)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onCreate(OperatingHoursExceptionInput(date.toString(), specialHours, if (specialHours) openTime else null, if (specialHours) closeTime else null, reason))
                onDismiss()
            }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
