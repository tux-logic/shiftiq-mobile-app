package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.garage.external

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Surface
import androidx.compose.ui.platform.LocalContext
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.hilt.navigation.compose.hiltViewModel
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Navy900
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.PrimaryBlue
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQTheme
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate100
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate200
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate300
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate400
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate500
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate700
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.spacing

@Composable
fun ExternalServiceRegistrationRoute(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ExternalServiceRegistrationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val receiptPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            val name = context.contentResolver.query(uri, arrayOf(android.provider.OpenableColumns.DISPLAY_NAME), null, null, null)
                ?.use { cursor -> if (cursor.moveToFirst()) cursor.getString(0) else null }
                ?: uri.lastPathSegment.orEmpty()
            viewModel.onAttachReceipt(name)
        }
    }
    ExternalServiceRegistrationScreen(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onServiceTypeChange = viewModel::onServiceTypeChange,
        onWorkshopNameChange = viewModel::onWorkshopNameChange,
        onServiceDateChange = viewModel::onServiceDateChange,
        onMileageChange = viewModel::onMileageChange,
        onTotalCostChange = viewModel::onTotalCostChange,
        onNotesChange = viewModel::onNotesChange,
        onAttachReceipt = { receiptPicker.launch(arrayOf("application/pdf", "image/*")) },
        onRemoveReceipt = viewModel::onRemoveReceipt,
        onToggleUpdateBaseOdometer = viewModel::onToggleUpdateBaseOdometer,
        onToggleResetIntervalCounter = viewModel::onToggleResetIntervalCounter,
        onSaveService = viewModel::saveExternalService,
        modifier = modifier
    )
}

@Composable
fun ExternalServiceRegistrationScreen(
    uiState: ExternalServiceRegistrationUiState,
    onNavigateBack: () -> Unit,
    onServiceTypeChange: (String) -> Unit,
    onWorkshopNameChange: (String) -> Unit,
    onServiceDateChange: (String) -> Unit,
    onMileageChange: (String) -> Unit,
    onTotalCostChange: (String) -> Unit,
    onNotesChange: (String) -> Unit,
    onAttachReceipt: () -> Unit,
    onRemoveReceipt: () -> Unit,
    onToggleUpdateBaseOdometer: (Boolean) -> Unit,
    onToggleResetIntervalCounter: (Boolean) -> Unit,
    onSaveService: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ExternalServiceTopBar(
            onNavigateBack = onNavigateBack,
            statusBarPadding = statusBarPadding
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(spacing.medium),
            verticalArrangement = Arrangement.spacedBy(spacing.medium)
        ) {
            InfoBannerSection()

            ServiceTypeDropdownField(
                selectedType = uiState.serviceType,
                availableTypes = uiState.availableServiceTypes,
                onTypeSelected = onServiceTypeChange
            )

            WorkshopNameInputField(
                workshopName = uiState.workshopName,
                onWorkshopNameChange = onWorkshopNameChange
            )

            DateAndMileageRow(
                date = uiState.serviceDate,
                onDateChange = onServiceDateChange,
                mileage = uiState.mileage,
                onMileageChange = onMileageChange
            )

            TotalCostInputField(
                cost = uiState.totalCostPen,
                onCostChange = onTotalCostChange
            )

            NotesInputField(
                notes = uiState.notes,
                onNotesChange = onNotesChange
            )

            ReceiptUploadCard(
                attachedReceiptName = uiState.attachedReceiptName,
                onAttach = onAttachReceipt,
                onRemove = onRemoveReceipt
            )

            TelematicsCheckboxesSection(
                updateBaseOdometer = uiState.updateBaseOdometer,
                onToggleUpdateBaseOdometer = onToggleUpdateBaseOdometer,
                resetIntervalCounter = uiState.resetIntervalCounter,
                onToggleResetIntervalCounter = onToggleResetIntervalCounter,
                currentMileage = uiState.mileage
            )

            uiState.errorMessage?.let { message ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(spacing.radiusSmall)
                ) {
                    Text(message, modifier = Modifier.padding(spacing.smallMedium), color = MaterialTheme.colorScheme.onErrorContainer)
                }
            }

            ActionButtonsSection(
                isLoading = uiState.isLoading,
                onSave = onSaveService,
                onCancel = onNavigateBack
            )
            Spacer(modifier = Modifier.height(spacing.large))
        }
    }
}

@Composable
private fun ExternalServiceTopBar(
    onNavigateBack: () -> Unit,
    statusBarPadding: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Navy900)
            .padding(
                top = statusBarPadding + spacing.small,
                start = spacing.small,
                end = spacing.medium,
                bottom = spacing.smallMedium
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(spacing.extraSmall))
            Text(
                text = "Registrar Servicio Externo",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        Box(
            modifier = Modifier
                .size(spacing.badgeHeight + spacing.smallMedium)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(spacing.iconLarge)
            )
        }
    }
}

@Composable
private fun InfoBannerSection(modifier: Modifier = Modifier) {
    val spacing = MaterialTheme.spacing
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(spacing.radiusMedium),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(spacing.strokeThin, Slate200)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.medium),
            horizontalArrangement = Arrangement.spacedBy(spacing.smallMedium)
        ) {
            Box(
                modifier = Modifier
                    .size(spacing.stepperNodeSize)
                    .clip(RoundedCornerShape(spacing.radiusSmall))
                    .background(PrimaryBlue.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(spacing.iconLarge)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Mantén tu historial vehicular al día",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(spacing.micro))
                Text(
                    text = "Registra los mantenimientos preventivos o correctivos realizados en talleres externos independientes para sincronizar el odómetro telemático y las alertas de vida de aceite.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ServiceTypeDropdownField(
    selectedType: String,
    availableTypes: List<String>,
    onTypeSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Tipo de Servicio Realizado *",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = Slate700
        )
        Spacer(modifier = Modifier.height(spacing.extraSmall))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(spacing.radiusSmall))
                .background(Color.White)
                .border(spacing.strokeThin, Slate300, RoundedCornerShape(spacing.radiusSmall))
                .clickable { expanded = true }
                .padding(horizontal = spacing.medium, vertical = spacing.smallMedium)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedType,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = Slate700
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Slate500,
                    modifier = Modifier.size(spacing.iconMedium)
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                availableTypes.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type, style = MaterialTheme.typography.bodyMedium) },
                        onClick = {
                            onTypeSelected(type)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun WorkshopNameInputField(
    workshopName: String,
    onWorkshopNameChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Nombre del Taller o Mecánico",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = Slate700
        )
        Spacer(modifier = Modifier.height(spacing.extraSmall))
        OutlinedTextField(
            value = workshopName,
            onValueChange = onWorkshopNameChange,
            placeholder = { Text("Ej. Lubricentro San Antonio / Taller Particular", style = MaterialTheme.typography.bodyMedium) },
            singleLine = true,
            shape = RoundedCornerShape(spacing.radiusSmall),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = PrimaryBlue,
                unfocusedBorderColor = Slate300
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun DateAndMileageRow(
    date: String,
    onDateChange: (String) -> Unit,
    mileage: String,
    onMileageChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing.smallMedium)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Fecha del Servicio *",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Slate700
            )
            Spacer(modifier = Modifier.height(spacing.extraSmall))
            OutlinedTextField(
                value = date,
                onValueChange = onDateChange,
                singleLine = true,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.size(spacing.iconMedium),
                        tint = Slate500
                    )
                },
                shape = RoundedCornerShape(spacing.radiusSmall),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = Slate300
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Kilometraje Exacto *",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Slate700
            )
            Spacer(modifier = Modifier.height(spacing.extraSmall))
            OutlinedTextField(
                value = mileage,
                onValueChange = onMileageChange,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = null,
                        modifier = Modifier.size(spacing.iconMedium),
                        tint = Slate500
                    )
                },
                shape = RoundedCornerShape(spacing.radiusSmall),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = Slate300
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun TotalCostInputField(
    cost: String,
    onCostChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Costo Total Invertido (PEN)",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = Slate700
        )
        Spacer(modifier = Modifier.height(spacing.extraSmall))
        OutlinedTextField(
            value = cost,
            onValueChange = onCostChange,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Paid,
                    contentDescription = null,
                    tint = Slate500,
                    modifier = Modifier.size(spacing.iconMedium)
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            shape = RoundedCornerShape(spacing.radiusSmall),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = PrimaryBlue,
                unfocusedBorderColor = Slate300
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun NotesInputField(
    notes: String,
    onNotesChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Repuestos o notas de la intervención",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = Slate700
        )
        Spacer(modifier = Modifier.height(spacing.extraSmall))
        OutlinedTextField(
            value = notes,
            onValueChange = onNotesChange,
            placeholder = { Text("Ej. Aceite sintético 5W-30 Shell Helix, filtro Bosch...", style = MaterialTheme.typography.bodySmall) },
            minLines = 3,
            maxLines = 5,
            shape = RoundedCornerShape(spacing.radiusSmall),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = PrimaryBlue,
                unfocusedBorderColor = Slate300
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ReceiptUploadCard(
    attachedReceiptName: String?,
    onAttach: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(spacing.radiusMedium),
        colors = CardDefaults.cardColors(containerColor = Slate100.copy(alpha = 0.5f)),
        border = BorderStroke(spacing.strokeThin, Slate300)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.medium),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing.small)
        ) {
            Box(
                modifier = Modifier
                    .size(spacing.controlButtonSize + spacing.small)
                    .clip(CircleShape)
                    .background(PrimaryBlue.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Receipt,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(spacing.iconLarge)
                )
            }
            Text(
                text = "Adjuntar Comprobante o Factura (Opcional)",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Formatos soportados: JPG, PNG, PDF (Máx. 10 MB)",
                style = MaterialTheme.typography.labelSmall,
                color = Slate400
            )

            if (attachedReceiptName != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(spacing.radiusSmall))
                        .background(Color.White)
                        .border(spacing.strokeThin, Slate200, RoundedCornerShape(spacing.radiusSmall))
                        .padding(horizontal = spacing.medium, vertical = spacing.small),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AttachFile,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(spacing.iconMedium)
                        )
                        Spacer(modifier = Modifier.width(spacing.small))
                        Text(
                            text = attachedReceiptName,
                            style = MaterialTheme.typography.labelMedium,
                            color = Slate700
                        )
                    }
                    IconButton(onClick = onRemove, modifier = Modifier.size(spacing.badgeHeight)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remover",
                            tint = Slate500,
                            modifier = Modifier.size(spacing.iconSmall)
                        )
                    }
                }
            } else {
                Button(
                    onClick = onAttach,
                    shape = RoundedCornerShape(spacing.radiusSmall),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = PrimaryBlue),
                    border = BorderStroke(spacing.strokeThin, PrimaryBlue.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        modifier = Modifier.size(spacing.iconMedium)
                    )
                    Spacer(modifier = Modifier.width(spacing.small))
                    Text(
                        text = "[ + Tomar Foto o Subir Archivo ]",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun TelematicsCheckboxesSection(
    updateBaseOdometer: Boolean,
    onToggleUpdateBaseOdometer: (Boolean) -> Unit,
    resetIntervalCounter: Boolean,
    onToggleResetIntervalCounter: (Boolean) -> Unit,
    currentMileage: String,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        TelematicsCheckboxCard(
            checked = updateBaseOdometer,
            onCheckedChange = onToggleUpdateBaseOdometer,
            title = "Actualizar odómetro base telemático con este registro",
            subtitle = "Sincroniza la telemetría del Garaje a $currentMileage km."
        )
        TelematicsCheckboxCard(
            checked = resetIntervalCounter,
            onCheckedChange = onToggleResetIntervalCounter,
            title = "Reiniciar contador de intervalo de próximo cambio",
            subtitle = "Establece nueva alerta para dentro de 5,000 km o 6 meses."
        )
    }
}

@Composable
private fun TelematicsCheckboxCard(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) },
        shape = RoundedCornerShape(spacing.radiusSmall),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(spacing.strokeThin, Slate200)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.smallMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(checkedColor = Navy900)
            )
            Spacer(modifier = Modifier.width(spacing.extraSmall))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Slate700
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate400
                )
            }
        }
    }
}

@Composable
private fun ActionButtonsSection(
    isLoading: Boolean,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        Button(
            onClick = onSave,
            enabled = !isLoading,
            shape = RoundedCornerShape(spacing.radiusSmall),
            colors = ButtonDefaults.buttonColors(containerColor = Navy900, contentColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .height(spacing.buttonHeight)
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier.size(spacing.iconMedium)
            )
            Spacer(modifier = Modifier.width(spacing.small))
            Text(
                text = "Guardar Registro en Mi Garaje",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
        }
        TextButton(
            onClick = onCancel,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Cancelar y volver a Mi Garaje",
                style = MaterialTheme.typography.labelMedium,
                color = Slate700
            )
        }
    }
}

@PreviewLightDark
@PreviewFontScale
@Composable
private fun ExternalServiceRegistrationScreenPreview() {
    ShiftIQTheme {
        ExternalServiceRegistrationScreen(
            uiState = ExternalServiceRegistrationUiState(
                serviceType = "Mantenimiento preventivo",
                workshopName = "Taller registrado por el usuario",
                serviceDate = "12/11/2026",
                mileage = "48000",
                totalCostPen = "250.00"
            ),
            onNavigateBack = {},
            onServiceTypeChange = {},
            onWorkshopNameChange = {},
            onServiceDateChange = {},
            onMileageChange = {},
            onTotalCostChange = {},
            onNotesChange = {},
            onAttachReceipt = {},
            onRemoveReceipt = {},
            onToggleUpdateBaseOdometer = {},
            onToggleResetIntervalCounter = {},
            onSaveService = {}
        )
    }
}
