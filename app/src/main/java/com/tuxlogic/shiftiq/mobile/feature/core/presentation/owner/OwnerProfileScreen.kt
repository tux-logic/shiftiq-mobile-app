package com.tuxlogic.shiftiq.mobile.feature.core.presentation.owner

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tuxlogic.shiftiq.mobile.core.designsystem.components.ShiftIQButton
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQTheme
import com.tuxlogic.shiftiq.mobile.core.model.UserId
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.OwnerProfile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerProfileScreen(
    viewModel: OwnerProfileViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    OwnerProfileContent(
        uiState = uiState,
        onFirstNameChanged = viewModel::onFirstNameChanged,
        onLastNameChanged = viewModel::onLastNameChanged,
        onDocumentNumberChanged = viewModel::onDocumentNumberChanged,
        onPhoneChanged = viewModel::onPhoneChanged,
        onSubmit = viewModel::saveProfile,
        onNavigateBack = onNavigateBack,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerProfileContent(
    uiState: OwnerProfileUiState,
    onFirstNameChanged: (String) -> Unit,
    onLastNameChanged: (String) -> Unit,
    onDocumentNumberChanged: (String) -> Unit,
    onPhoneChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Perfil del Propietario",
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
            when {
                uiState.isLoading && uiState.profile == null -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                uiState.profile != null -> {
                    // Perfil ya existente: Modo visualización
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            modifier = Modifier
                                .size(88.dp)
                                .clip(CircleShape),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(44.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = uiState.profile.fullName,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Dueño de Taller Verificado",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    text = "Datos de Identidad",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Badge,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "${uiState.profile.documentType}: ${uiState.profile.documentNumber}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = "Documento de Identidad",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Phone,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = uiState.profile.phone,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = "Teléfono de Contacto",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        ShiftIQButton(
                            text = "Volver a Mis Talleres",
                            onClick = onNavigateBack,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                else -> {
                    // Modo formulario para crear perfil de dueño
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Registrar Datos del Dueño",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Completa tu información legal y de contacto para asociar tus talleres en ShiftIQ.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = uiState.firstName,
                            onValueChange = onFirstNameChanged,
                            label = { Text("Nombres") },
                            placeholder = { Text("Ej: Alan") },
                            isError = uiState.firstNameError != null,
                            supportingText = uiState.firstNameError?.let { { Text(it) } },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = uiState.lastName,
                            onValueChange = onLastNameChanged,
                            label = { Text("Apellidos") },
                            placeholder = { Text("Ej: Jara") },
                            isError = uiState.lastNameError != null,
                            supportingText = uiState.lastNameError?.let { { Text(it) } },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = uiState.documentNumber,
                            onValueChange = onDocumentNumberChanged,
                            label = { Text("DNI / Cédula") },
                            placeholder = { Text("72345678") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            isError = uiState.documentNumberError != null,
                            supportingText = uiState.documentNumberError?.let { { Text(it) } },
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
                            text = "Guardar Perfil de Dueño",
                            onClick = onSubmit,
                            isLoading = uiState.isLoading,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OwnerProfileViewPreview() {
    ShiftIQTheme {
        OwnerProfileContent(
            uiState = OwnerProfileUiState(
                profile = OwnerProfile(
                    id = "o1",
                    userId = UserId("u1"),
                    firstName = "Alan",
                    lastName = "Jara",
                    documentType = "DNI",
                    documentNumber = "72345678",
                    phone = "+51987654321"
                )
            ),
            onFirstNameChanged = {},
            onLastNameChanged = {},
            onDocumentNumberChanged = {},
            onPhoneChanged = {},
            onSubmit = {},
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OwnerProfileFormPreview() {
    ShiftIQTheme {
        OwnerProfileContent(
            uiState = OwnerProfileUiState(
                firstName = "Alan",
                lastName = "Jara",
                documentNumber = "72345678",
                phone = "+51987654321"
            ),
            onFirstNameChanged = {},
            onLastNameChanged = {},
            onDocumentNumberChanged = {},
            onPhoneChanged = {},
            onSubmit = {},
            onNavigateBack = {}
        )
    }
}
