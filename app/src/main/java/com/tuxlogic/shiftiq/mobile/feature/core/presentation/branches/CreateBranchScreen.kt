package com.tuxlogic.shiftiq.mobile.feature.core.presentation.branches

import androidx.compose.foundation.background
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import com.tuxlogic.shiftiq.mobile.core.localization.LocalizedText as Text
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
import com.tuxlogic.shiftiq.mobile.core.designsystem.components.ShiftIQPageHeader
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQTheme

@Composable
fun CreateBranchScreen(
    workshopId: String,
    viewModel: CreateBranchViewModel,
    onNavigateBack: () -> Unit,
    onBranchCreated: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(workshopId) {
        viewModel.setWorkshopId(workshopId)
    }

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onBranchCreated()
        }
    }

    CreateBranchContent(
        uiState = uiState,
        onCodeChanged = viewModel::onCodeChanged,
        onNameChanged = viewModel::onNameChanged,
        onAddressChanged = viewModel::onAddressChanged,
        onPhoneChanged = viewModel::onPhoneChanged,
        onSubmit = viewModel::createBranch,
        onNavigateBack = onNavigateBack,
        modifier = modifier
    )
}

@Composable
fun CreateBranchContent(
    uiState: CreateBranchUiState,
    onCodeChanged: (String) -> Unit,
    onNameChanged: (String) -> Unit,
    onAddressChanged: (String) -> Unit,
    onPhoneChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ShiftIQPageHeader(
            title = "Registrar Sede",
            subtitle = "Nueva sede",
            showBackButton = true,
            onBackPressed = onNavigateBack
        )
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 32.dp)
            ) {
                Text(
                    text = "Datos de Ubicación y Contacto",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Cada sede física atiende citas, gestiona repuestos y emite comprobantes.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = uiState.code,
                    onValueChange = onCodeChanged,
                    label = { Text("Código de Sede") },
                    placeholder = { Text("Ej: SEDE-SUR-01") },
                    isError = uiState.codeError != null,
                    supportingText = uiState.codeError?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = onNameChanged,
                    label = { Text("Nombre de la Sede") },
                    placeholder = { Text("Ej: Sede Surco Principal") },
                    isError = uiState.nameError != null,
                    supportingText = uiState.nameError?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = uiState.address,
                    onValueChange = onAddressChanged,
                    label = { Text("Dirección") },
                    placeholder = { Text("Ej: Av. Benavides 1850, Surco, Lima") },
                    isError = uiState.addressError != null,
                    supportingText = uiState.addressError?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = uiState.phone,
                    onValueChange = onPhoneChanged,
                    label = { Text("Teléfono de Contacto") },
                    placeholder = { Text("+51987654321") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    isError = uiState.phoneError != null,
                    supportingText = uiState.phoneError?.let { { Text(it) } },
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
                    text = "Registrar Sede",
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
fun CreateBranchScreenPreview() {
    ShiftIQTheme {
        CreateBranchContent(
            uiState = CreateBranchUiState(
                code = "SEDE-LIMA-01",
                name = "Sede San Isidro",
                address = "Av. Javier Prado Este 1234, Lima",
                phone = "+5114223344"
            ),
            onCodeChanged = {},
            onNameChanged = {},
            onAddressChanged = {},
            onPhoneChanged = {},
            onSubmit = {},
            onNavigateBack = {}
        )
    }
}
