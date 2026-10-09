package com.tuxlogic.shiftiq.mobile.feature.core.presentation.workshops

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tuxlogic.shiftiq.mobile.core.designsystem.components.ShiftIQButton
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWorkshopScreen(
    ownerId: String,
    viewModel: CreateWorkshopViewModel,
    onNavigateBack: () -> Unit,
    onWorkshopCreated: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(ownerId) {
        viewModel.setOwnerId(ownerId)
    }

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onWorkshopCreated()
        }
    }

    CreateWorkshopContent(
        uiState = uiState,
        onBusinessNameChanged = viewModel::onBusinessNameChanged,
        onBrandNameChanged = viewModel::onBrandNameChanged,
        onTaxIdChanged = viewModel::onTaxIdChanged,
        onMileageIntervalChanged = viewModel::onMileageIntervalChanged,
        onSubmit = viewModel::createWorkshop,
        onNavigateBack = onNavigateBack,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWorkshopContent(
    uiState: CreateWorkshopUiState,
    onBusinessNameChanged: (String) -> Unit,
    onBrandNameChanged: (String) -> Unit,
    onTaxIdChanged: (String) -> Unit,
    onMileageIntervalChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Registrar Taller",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                Text(
                    text = "Información Fiscal y Comercial",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Ingresa los datos de tu empresa para la facturación y presencia de tu taller.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = uiState.businessName,
                    onValueChange = onBusinessNameChanged,
                    label = { Text("Razón Social") },
                    placeholder = { Text("Ej: AutoServicios Perú S.A.C.") },
                    isError = uiState.businessNameError != null,
                    supportingText = uiState.businessNameError?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = uiState.brandName,
                    onValueChange = onBrandNameChanged,
                    label = { Text("Nombre Comercial / Marca") },
                    placeholder = { Text("Ej: ShiftIQ Taller Central") },
                    isError = uiState.brandNameError != null,
                    supportingText = uiState.brandNameError?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = uiState.taxId,
                    onValueChange = onTaxIdChanged,
                    label = { Text("RUC (11 dígitos)") },
                    placeholder = { Text("20601234567") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = uiState.taxIdError != null,
                    supportingText = uiState.taxIdError?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = uiState.mileageInterval,
                    onValueChange = onMileageIntervalChanged,
                    label = { Text("Intervalo sugerido de mantenimiento (km)") },
                    placeholder = { Text("5000") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = uiState.mileageIntervalError != null,
                    supportingText = uiState.mileageIntervalError?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth()
                )

                if (uiState.errorMessage != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = uiState.errorMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                ShiftIQButton(
                    text = "Registrar Taller",
                    onClick = onSubmit,
                    isLoading = uiState.isLoading,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateWorkshopScreenPreview() {
    ShiftIQTheme {
        CreateWorkshopContent(
            uiState = CreateWorkshopUiState(
                businessName = "TuxLogic Motors S.A.C.",
                brandName = "TuxLogic AutoCare",
                taxId = "20601234567"
            ),
            onBusinessNameChanged = {},
            onBrandNameChanged = {},
            onTaxIdChanged = {},
            onMileageIntervalChanged = {},
            onSubmit = {},
            onNavigateBack = {}
        )
    }
}
