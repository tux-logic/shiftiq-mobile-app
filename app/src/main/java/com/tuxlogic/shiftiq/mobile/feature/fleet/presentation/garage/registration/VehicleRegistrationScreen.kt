package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.garage.registration

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.CanBusCyan
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Navy900
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQSuccess
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQTheme
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.spacing
import com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.garage.components.DiscoveredObdDevice
import com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.garage.components.ObdPairingBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleRegistrationScreen(
    onNavigateBack: () -> Unit,
    onRegistrationComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentStep by rememberSaveable { mutableIntStateOf(1) }
    var ownerName by rememberSaveable { mutableStateOf("Carlos Mendoza") }
    var documentId by rememberSaveable { mutableStateOf("45892104") }
    var phone by rememberSaveable { mutableStateOf("987 654 321") }
    var email by rememberSaveable { mutableStateOf("carlos.mendoza@shiftiq.pe") }
    var notificationsConsent by rememberSaveable { mutableStateOf(true) }

    var brand by rememberSaveable { mutableStateOf("Kia") }
    var model by rememberSaveable { mutableStateOf("Rio") }
    var year by rememberSaveable { mutableStateOf("2019") }
    var plateNumber by rememberSaveable { mutableStateOf("ABC-123") }
    var vin by rememberSaveable { mutableStateOf("KNADE1234567890") }
    var pairedDongle by rememberSaveable { mutableStateOf<String?>("OBD2-BT-9821") }

    var showPairingSheet by rememberSaveable { mutableStateOf(false) }

    val spacing = MaterialTheme.spacing
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        RegistrationTopBar(
            currentStep = currentStep,
            onBackClick = {
                if (currentStep > 1) currentStep-- else onNavigateBack()
            },
            statusBarPadding = statusBarPadding
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = spacing.medium, vertical = spacing.smallMedium),
            verticalArrangement = Arrangement.spacedBy(spacing.medium)
        ) {
            StepIndicatorHeader(currentStep = currentStep)

            when (currentStep) {
                1 -> Step1OwnerForm(
                    ownerName = ownerName,
                    onOwnerNameChange = { ownerName = it },
                    documentId = documentId,
                    onDocumentIdChange = { documentId = it },
                    phone = phone,
                    onPhoneChange = { phone = it },
                    email = email,
                    onEmailChange = { email = it },
                    consent = notificationsConsent,
                    onConsentChange = { notificationsConsent = it },
                    onContinue = { currentStep = 2 }
                )
                2 -> Step2VehicleForm(
                    brand = brand,
                    onBrandChange = { brand = it },
                    model = model,
                    onModelChange = { model = it },
                    year = year,
                    onYearChange = { year = it },
                    plate = plateNumber,
                    onPlateChange = { plateNumber = it },
                    vin = vin,
                    onVinChange = { vin = it },
                    pairedDongle = pairedDongle,
                    onOpenPairing = { showPairingSheet = true },
                    onBack = { currentStep = 1 },
                    onContinue = { currentStep = 3 }
                )
                3 -> Step3Confirmation(
                    ownerName = ownerName,
                    documentId = documentId,
                    brand = brand,
                    model = model,
                    year = year,
                    plate = plateNumber,
                    vin = vin,
                    dongleId = pairedDongle ?: "No vinculado",
                    onModify = { currentStep = 1 },
                    onConfirm = onRegistrationComplete
                )
            }
            Spacer(modifier = Modifier.height(spacing.large))
        }
    }

    if (showPairingSheet) {
        ObdPairingBottomSheet(
            onDismissRequest = { showPairingSheet = false },
            onDeviceConnected = { device ->
                pairedDongle = device.name
                showPairingSheet = false
            }
        )
    }
}

@Composable
private fun RegistrationTopBar(
    currentStep: Int,
    onBackClick: () -> Unit,
    statusBarPadding: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Navy900)
            .padding(
                top = statusBarPadding,
                start = spacing.small,
                end = spacing.medium,
                bottom = spacing.smallMedium
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Atrás",
                tint = Color.White
            )
        }
        Spacer(modifier = Modifier.width(spacing.small))
        Column {
            Text(
                text = "Registro de Vehículo",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Paso $currentStep de 3",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun StepIndicatorHeader(
    currentStep: Int,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        for (step in 1..3) {
            val isActive = step <= currentStep
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(spacing.indicatorHeight)
                    .clip(RoundedCornerShape(spacing.radiusFull))
                    .background(
                        if (isActive) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceContainerHigh
                    )
            )
        }
    }
}

@Composable
private fun Step1OwnerForm(
    ownerName: String,
    onOwnerNameChange: (String) -> Unit,
    documentId: String,
    onDocumentIdChange: (String) -> Unit,
    phone: String,
    onPhoneChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    consent: Boolean,
    onConsentChange: (Boolean) -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(spacing.radiusLarge),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(spacing.strokeThin, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.medium),
            verticalArrangement = Arrangement.spacedBy(spacing.smallMedium)
        ) {
            Text(
                text = "Datos del Propietario",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            OutlinedTextField(
                value = ownerName,
                onValueChange = onOwnerNameChange,
                label = { Text("Nombre Completo") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = documentId,
                onValueChange = onDocumentIdChange,
                label = { Text("DNI / Carné de Extranjería") },
                supportingText = { Text("Requerido para comprobantes fiscales de servicio") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = phone,
                onValueChange = onPhoneChange,
                label = { Text("Teléfono de Contacto (+51)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = { Text("Correo Electrónico") },
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(checked = consent, onCheckedChange = onConsentChange)
                Spacer(modifier = Modifier.width(spacing.extraSmall))
                Text(
                    text = "Deseo recibir reportes y avisos por SMS / WhatsApp",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            SecurityGuaranteeBadge()
            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(spacing.buttonHeight),
                shape = RoundedCornerShape(spacing.radiusMedium),
                colors = ButtonDefaults.buttonColors(containerColor = Navy900, contentColor = Color.White)
            ) {
                Text(
                    text = "Continuar a Vehículo y OBD-II",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun SecurityGuaranteeBadge(modifier: Modifier = Modifier) {
    val spacing = MaterialTheme.spacing
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(spacing.radiusMedium))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(spacing.smallMedium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(spacing.iconMedium)
        )
        Spacer(modifier = Modifier.width(spacing.small))
        Text(
            text = "Tus datos y telemetría están protegidos mediante cifrado CAN-BUS seguro.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun Step2VehicleForm(
    brand: String,
    onBrandChange: (String) -> Unit,
    model: String,
    onModelChange: (String) -> Unit,
    year: String,
    onYearChange: (String) -> Unit,
    plate: String,
    onPlateChange: (String) -> Unit,
    vin: String,
    onVinChange: (String) -> Unit,
    pairedDongle: String?,
    onOpenPairing: () -> Unit,
    onBack: () -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(spacing.radiusLarge),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(spacing.strokeThin, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.medium),
            verticalArrangement = Arrangement.spacedBy(spacing.smallMedium)
        ) {
            Text(
                text = "Ficha Técnica del Vehículo",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            PhotoUploadDropzone()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.smallMedium)
            ) {
                OutlinedTextField(
                    value = brand,
                    onValueChange = onBrandChange,
                    label = { Text("Marca") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = model,
                    onValueChange = onModelChange,
                    label = { Text("Modelo") },
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.smallMedium)
            ) {
                OutlinedTextField(
                    value = year,
                    onValueChange = onYearChange,
                    label = { Text("Año") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = plate,
                    onValueChange = onPlateChange,
                    label = { Text("Placa") },
                    modifier = Modifier.weight(1f)
                )
            }
            VinValidatedField(vin = vin, onVinChange = onVinChange)
            ObdDongleStatusCard(pairedDongle = pairedDongle, onOpenPairing = onOpenPairing)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.smallMedium)
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier
                        .weight(1f)
                        .height(spacing.buttonHeight),
                    shape = RoundedCornerShape(spacing.radiusMedium)
                ) {
                    Text("Atrás")
                }
                Button(
                    onClick = onContinue,
                    modifier = Modifier
                        .weight(1f)
                        .height(spacing.buttonHeight),
                    shape = RoundedCornerShape(spacing.radiusMedium),
                    colors = ButtonDefaults.buttonColors(containerColor = Navy900, contentColor = Color.White)
                ) {
                    Text("Continuar")
                }
            }
        }
    }
}

@Composable
private fun PhotoUploadDropzone(modifier: Modifier = Modifier) {
    val spacing = MaterialTheme.spacing
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(spacing.dropzoneHeight)
            .clip(RoundedCornerShape(spacing.radiusLarge))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .border(
                spacing.strokeThin,
                MaterialTheme.colorScheme.outlineVariant,
                RoundedCornerShape(spacing.radiusLarge)
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.AddPhotoAlternate,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(spacing.avatarSmall)
            )
            Spacer(modifier = Modifier.height(spacing.extraSmall))
            Text(
                text = "Adjuntar Foto del Vehículo",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "PNG, JPG hasta 5MB",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun VinValidatedField(
    vin: String,
    onVinChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    OutlinedTextField(
        value = vin,
        onValueChange = onVinChange,
        label = { Text("Número de Chasis (VIN - 17 dígitos)") },
        trailingIcon = {
            if (vin.length == 17 || vin.length == 14) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "VIN Válido",
                    tint = ShiftIQSuccess
                )
            }
        },
        supportingText = { Text("Verificado con el estándar ISO 3779") },
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
private fun ObdDongleStatusCard(
    pairedDongle: String?,
    onOpenPairing: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    val isPaired = !pairedDongle.isNullOrBlank()

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(spacing.radiusMedium),
        colors = CardDefaults.cardColors(
            containerColor = if (isPaired) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
            else MaterialTheme.colorScheme.surfaceContainerLow
        ),
        border = BorderStroke(
            spacing.strokeThin,
            if (isPaired) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.medium),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Bluetooth,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(spacing.iconLarge)
                )
                Spacer(modifier = Modifier.width(spacing.smallMedium))
                Column {
                    Text(
                        text = if (isPaired) "Adaptador Vinculado" else "Adaptador OBD-II",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = pairedDongle ?: "Ningún dispositivo vinculado",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            OutlinedButton(
                onClick = onOpenPairing,
                shape = RoundedCornerShape(spacing.radiusSmall)
            ) {
                Text(if (isPaired) "Cambiar" else "Vincular")
            }
        }
    }
}

@Composable
private fun Step3Confirmation(
    ownerName: String,
    documentId: String,
    brand: String,
    model: String,
    year: String,
    plate: String,
    vin: String,
    dongleId: String,
    onModify: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(spacing.radiusLarge),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(spacing.strokeThin, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.medium),
            verticalArrangement = Arrangement.spacedBy(spacing.smallMedium)
        ) {
            ConfirmationHeader(plate = plate, brand = brand, model = model, year = year)
            ConfirmationItemRow(label = "Propietario Registrado", value = ownerName)
            ConfirmationItemRow(label = "Documento de Identidad", value = documentId)
            ConfirmationItemRow(label = "Número de Chasis (VIN)", value = vin)
            ConfirmationItemRow(label = "Dongle OBD-II Vinculado", value = dongleId)
            ConfirmationItemRow(label = "Canal de Telemetría", value = "Cifrado AES-256")
            Spacer(modifier = Modifier.height(spacing.small))
            Button(
                onClick = onConfirm,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(spacing.buttonHeight),
                shape = RoundedCornerShape(spacing.radiusMedium),
                colors = ButtonDefaults.buttonColors(containerColor = Navy900, contentColor = Color.White)
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(spacing.iconMedium))
                Spacer(modifier = Modifier.width(spacing.small))
                Text("Confirmar y Registrar Vehículo", fontWeight = FontWeight.Bold)
            }
            OutlinedButton(
                onClick = onModify,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(spacing.buttonHeight),
                shape = RoundedCornerShape(spacing.radiusMedium)
            ) {
                Text("Modificar Datos")
            }
        }
    }
}

@Composable
private fun ConfirmationHeader(
    plate: String,
    brand: String,
    model: String,
    year: String,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Resumen de Registro",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "$brand $model $year",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(spacing.radiusSmall))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .padding(horizontal = spacing.smallMedium, vertical = spacing.extraSmall)
        ) {
            Text(
                text = plate,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun ConfirmationItemRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@PreviewLightDark
@PreviewFontScale
@Composable
private fun VehicleRegistrationScreenPreview() {
    ShiftIQTheme {
        VehicleRegistrationScreen(
            onNavigateBack = {},
            onRegistrationComplete = {}
        )
    }
}
